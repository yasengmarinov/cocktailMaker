package cocktailMaker.server.db;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import org.apache.log4j.Logger;
import org.hibernate.Criteria;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.criterion.Restrictions;
import cocktailMaker.server.LogType;
import cocktailMaker.server.Utils;
import cocktailMaker.server.db.entities.*;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

/**
 * Created by yasen.marinov on 6/10/2017.
 */
@Singleton
public class SqlDAO implements DAO {

    private static final Logger logger = Logger.getLogger(SqlDAO.class);
    private static SessionFactory sessionFactory;
    private static Session session;

    @Inject
    private SqlDAO() {
    }

    @Override
    public void init() {
        logger.info("Initializing SqlDAO");
        final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
                .configure(SqlDAO.class.getClassLoader().getResource("config/hibernate/hibernate.cfg.xml"))
                .build();
        sessionFactory = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        session = sessionFactory.openSession();
        session.setFlushMode(FlushMode.ALWAYS);
        prepopulateData();
    }

    @Override
    public <T> List<T> getAll(Class<T> clazz) {
        Criteria criteria = session.createCriteria(clazz);
        return criteria.list();
    }

    private void prepopulateData() {
        if (getAll(User.class).size() == 0)
            persist(new User("admin", Utils.md5("admin"), true));
    }

    @Override
    public synchronized boolean persist(Object object) {
        logger.info("Persisting object in the DB: \n" + object.toString());
        try {
            session.beginTransaction();
            session.save(object);
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            return false;
        }
    }

    public synchronized boolean persistCocktailIngredient(CocktailIngredient cocktailIngredient) {
        boolean toReturn = persist(cocktailIngredient);
        cocktailIngredient.getCocktail().getCocktailIngredients().add(cocktailIngredient);
        session.persist(cocktailIngredient.getCocktail());
        return toReturn;
    }

    @Override
    public synchronized boolean update(Object object) {
        logger.info("Updating object " + object);
        try {
            session.beginTransaction();
            session.update(object);
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public synchronized boolean delete(Object object) {
        logger.info("Deleting object " + object);
        try {
            session.beginTransaction();
            session.delete(object);
            session.getTransaction().commit();
            return true;
        } catch (Exception ex) {
            session.getTransaction().rollback();
            return false;
        }
    }

    @Override
    public User getUser(String username) {
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username.toLowerCase()));
        return criteria.list().isEmpty() ? null : (User) criteria.list().get(0);
    }

    @Override
    public User getUser(String username, String password) {
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("username", username.toLowerCase()));
        criteria.add(Restrictions.eq("password", Utils.md5(password)));
        return criteria.list().isEmpty() ? null : (User) criteria.list().get(0);
    }

    @Override
    public User getUserByCard(String card) {
        Criteria criteria = session.createCriteria(User.class);
        criteria.add(Restrictions.eq("magneticCard", Utils.md5(card)));
        return criteria.list().isEmpty() ? null : (User) criteria.list().get(0);
    }

    @Override
    public List<CocktailIngredient> getCocktailIngredients(Cocktail cocktail) {
        Criteria criteria = session.createCriteria(CocktailIngredient.class);
        criteria.add(Restrictions.eq("cocktail", cocktail));
        return criteria.list().isEmpty() ? null : criteria.list();
    }

    @Override
    public Dispenser getDispenserByIngredient(Ingredient ingredient) {
        Criteria criteria = session.createCriteria(Dispenser.class);
        criteria.add(Restrictions.eq("ingredient", ingredient));
        criteria.add(Restrictions.eq("enabled", true));
        return criteria.list().isEmpty() ? null : (Dispenser) criteria.list().get(0);
    }

    @Override
    public synchronized Dispenser getDispenserByCocktailIngredient(CocktailIngredient cocktailIngredient) {
        Criteria criteria = session.createCriteria(Dispenser.class);
        criteria.add(Restrictions.eq("ingredient", cocktailIngredient.getIngredient()));
        criteria.add(Restrictions.eq("enabled", true));
        criteria.add(Restrictions.ge("millilitresLeft", cocktailIngredient.getMillilitres()));
        return criteria.list().isEmpty() ? null : (Dispenser) criteria.list().get(0);
    }

    @Override
    public List<Dispenser> getEnabledDispensers() {
        Criteria criteria = session.createCriteria(Dispenser.class);
        criteria.add(Restrictions.eq("enabled", true));
        return criteria.list();
    }

    @Override
    public List<CocktailLog> getCocktailLog() {
        Criteria criteria = session.createCriteria(CocktailLog.class);
        criteria.add(Restrictions.in("type", LogType.TYPE_COCKTAIL));
        return criteria.list();
    }

    @Override
    public List<CocktailLog> getIngredientsLog(LocalDate from, LocalDate to) {
        Date fromDate = Date.from(from.atStartOfDay(ZoneId.systemDefault()).toInstant());
        Date toDate = Date.from(to.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant());

        Criteria criteria = session.createCriteria(CocktailLog.class);
        criteria.add(Restrictions.in("type", LogType.TYPE_INGREDIENTS));
        criteria.add(Restrictions.between("eventDate", fromDate, toDate));
        return criteria.list();
    }

    @Override
    public List<Cocktail> getCocktailsByGroup(CocktailGroup cocktailGroup) {
        Criteria criteria = session.createCriteria(Cocktail.class);
        criteria.add(Restrictions.eq("cocktailGroup", cocktailGroup));
        return criteria.list();
    }


}

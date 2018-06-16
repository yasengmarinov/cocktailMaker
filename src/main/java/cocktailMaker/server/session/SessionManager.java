package cocktailMaker.server.session;

import cocktailMaker.server.db.DAO;
import cocktailMaker.server.db.entities.User;
import com.google.inject.Inject;
import com.google.inject.Singleton;

/**
 * Created by yasen.marinov on 6/22/2017.
 */
@Singleton
public class SessionManager {

    private static Session session = null;
    protected final DAO dao;

    @Inject
    public SessionManager(DAO dao) {
        this.dao = dao;
    }

    public final Session createSession(String username, String password) {

        if (username.isEmpty() || password.isEmpty())
            return null;
        User user = dao.getUser(username, password);

        if (user != null) {
            session = new Session(user);
        }
        return session;
    }

    public final Session createSession(User user) {

        session = new Session(user);

        return session;
    }

    public final Session getSession() {
        return session;
    }

    public void sessionInvalidate() {
        session = null;
    }
}

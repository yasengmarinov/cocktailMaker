package cocktailMaker.server.db.entities;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import cocktailMaker.server.db.entities.interfaces.NamedEntity;

import javax.persistence.*;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by yasen.marinov on 6/16/2017.
 */
@Entity
@Table(name = "COCKTAILS")
public class Cocktail implements NamedEntity, Comparable<Cocktail> {
    private Integer id;
    private String name;
    private CocktailGroup cocktailGroup;
    private List<CocktailIngredient> cocktailIngredients;

    public Cocktail() {
        cocktailIngredients = new LinkedList<>();
    }

    public Cocktail(String name, CocktailGroup cocktailGroup) {
        this();

        this.name = name;
        this.cocktailGroup = cocktailGroup;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "NAME", unique = true)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Cocktail that = (Cocktail) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "GROUP_ID", referencedColumnName = "ID", nullable = false)
    public CocktailGroup getCocktailGroup() {
        return cocktailGroup;
    }

    public void setCocktailGroup(CocktailGroup cocktailGroupByGroupId) {
        this.cocktailGroup = cocktailGroupByGroupId;
    }

    @OneToMany(mappedBy = "cocktail")
    @Cascade(CascadeType.SAVE_UPDATE)
    public List<CocktailIngredient> getCocktailIngredients() {
        return cocktailIngredients;
    }

    public void setCocktailIngredients(List<CocktailIngredient> cocktailIngredientById) {
        this.cocktailIngredients = cocktailIngredientById;
    }

    @Override
    public int compareTo(Cocktail o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}

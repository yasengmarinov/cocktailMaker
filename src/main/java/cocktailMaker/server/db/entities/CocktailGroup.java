package cocktailMaker.server.db.entities;

import cocktailMaker.server.db.entities.interfaces.NamedEntity;

import javax.persistence.*;

/**
 * Created by yasen.marinov on 6/16/2017.
 */
@Entity
@Table(name = "COCKTAIL_GROUPS")
public class CocktailGroup implements NamedEntity, Comparable<CocktailGroup> {
    private Integer id;
    private String name;

    public CocktailGroup() {
    }

    public CocktailGroup(String name) {
        this.name = name;
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

        CocktailGroup that = (CocktailGroup) o;

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

    @Override
    public int compareTo(CocktailGroup o) {
        return this.name.compareTo(o.getName());
    }

    @Override
    public String toString() {
        return name;
    }
}

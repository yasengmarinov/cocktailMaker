package cocktailMaker.server.db.entities;

import javax.persistence.*;

/**
 * Created by yasen.marinov on 6/18/2017.
 */
@Entity
@Table(name = "DISPENSERS")
public class Dispenser implements Comparable<Dispenser> {
    private Integer id;
    private Ingredient ingredient;
    private Integer millilitresLeft;
    private Boolean enabled;

    public Dispenser() {

    }

    public Dispenser(Integer id, Boolean enabled) {
        this.id = id;
        this.enabled = enabled;
    }

    public Dispenser(Integer id, Ingredient ingredient, Integer millilitresLeft, Boolean enabled) {
        this.id = id;
        this.ingredient = ingredient;
        this.millilitresLeft = millilitresLeft;
        this.enabled = enabled;
    }

    @Id
    @Column(name = "ID")
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    @Basic
    @Column(name = "MILLILITRES")
    public Integer getMillilitresLeft() {
        return millilitresLeft;
    }

    public void setMillilitresLeft(Integer millilitresLeft) {
        this.millilitresLeft = millilitresLeft;
    }

    @Basic
    @Column(name = "ENABLED")
    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Dispenser dispenser = (Dispenser) o;

        if (id != null ? !id.equals(dispenser.id) : dispenser.id != null) return false;
        if (enabled != null ? !enabled == (dispenser.enabled) : dispenser.enabled != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (enabled != null ? enabled.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID", referencedColumnName = "ID")
    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }


    @Override
    public int compareTo(Dispenser o) {
        return Integer.compare(this.getId(), o.getId());
    }
}

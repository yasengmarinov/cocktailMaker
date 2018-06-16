package cocktailMaker.server.db.entities;

import javax.persistence.*;

/**
 * Created by yasen.marinov on 6/16/2017.
 */
@Entity
@Table(name = "COCKTAILS_INGREDIENTS")
public class CocktailIngredient implements Comparable<CocktailIngredient> {
    private Integer id;
    private Integer millilitres;
    private Cocktail cocktail;
    private Ingredient ingredient;

    public CocktailIngredient() {
    }

    public CocktailIngredient(Cocktail cocktail, Ingredient ingredient, Integer millilitres) {
        this.millilitres = millilitres;
        this.cocktail = cocktail;
        this.ingredient = ingredient;
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
    @Column(name = "MILLILITRES")
    public Integer getMillilitres() {
        return millilitres;
    }

    public void setMillilitres(Integer millilitres) {
        this.millilitres = millilitres;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CocktailIngredient that = (CocktailIngredient) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (millilitres != null ? !millilitres.equals(that.millilitres) : that.millilitres != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (millilitres != null ? millilitres.hashCode() : 0);
        return result;
    }

    @ManyToOne
    @JoinColumn(name = "COCKTAIL_ID", referencedColumnName = "ID", nullable = false)
    public Cocktail getCocktail() {
        return cocktail;
    }

    public void setCocktail(Cocktail cocktailByCocktailId) {
        this.cocktail = cocktailByCocktailId;
    }

    @ManyToOne
    @JoinColumn(name = "INGREDIENT_ID", referencedColumnName = "ID", nullable = false)
    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredientByIngredientId) {
        this.ingredient = ingredientByIngredientId;
    }

    @Override
    public int compareTo(CocktailIngredient o) {
        return this.ingredient.compareTo(o.ingredient);
    }
}

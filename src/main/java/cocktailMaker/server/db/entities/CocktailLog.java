package cocktailMaker.server.db.entities;

import cocktailMaker.server.session.SessionManager;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.Calendar;

/**
 * Created by yasen.marinov on 6/22/2017.
 */
@Entity
@Table(name = "COCKTAIL_LOG")
public class CocktailLog implements Comparable<CocktailLog> {

    private Integer id;
    private Integer type;
    private String message;
    private String username;
    private Timestamp eventDate;

    public CocktailLog() {

    }

    public CocktailLog(Integer type, String message, String username) {
        this.type = type;
        this.message = message;
        this.username = username;
        this.eventDate = new Timestamp(Calendar.getInstance().getTime().getTime());
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
    @Column(name = "TYPE")
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    @Basic
    @Column(name = "MESSAGE")
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Basic
    @Column(name = "USERNAME")
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @Basic
    @Column(name = "EVENT_DATE")
    public Timestamp getEventDate() {
        return eventDate;
    }

    public void setEventDate(Timestamp eventDate) {
        this.eventDate = eventDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CocktailLog that = (CocktailLog) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        if (type != null ? !type.equals(that.type) : that.type != null) return false;
        if (message != null ? !message.equals(that.message) : that.message != null) return false;
        if (username != null ? !username.equals(that.username) : that.username != null) return false;
        if (eventDate != null ? !eventDate.equals(that.eventDate) : that.eventDate != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (message != null ? message.hashCode() : 0);
        result = 31 * result + (username != null ? username.hashCode() : 0);
        result = 31 * result + (eventDate != null ? eventDate.hashCode() : 0);
        return result;
    }

    @Override
    public int compareTo(CocktailLog o) {
        return o.getEventDate().compareTo(this.getEventDate());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Log Entry type ");
        builder.append(type);
        builder.append(": ");
        builder.append(message);
        builder.append(" by ");
        builder.append(username);
        return builder.toString();
    }
}

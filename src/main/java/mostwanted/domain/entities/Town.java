package mostwanted.domain.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity(name = "towns")
public class Town extends BaseEntity {
    private String name;

    public Town() {
    }

    @Column(name = "names",unique = true,nullable = false)
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

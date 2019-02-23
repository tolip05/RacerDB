package mostwanted.domain.entities;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "races")
public class Race extends BaseEntity{
    private Integer laps;
    private District district;
    private List<RaceEntry> raceEntries;

    public Race() {
        this.raceEntries = new ArrayList<>();
         this.laps = 0;
    }

    @Column(name = "laps",nullable = false)
    public Integer getLaps() {
        return this.laps;
    }

    public void setLaps(Integer laps) {
        this.laps = laps;
    }

    @ManyToOne(targetEntity = District.class)
    @JoinColumn(
            name = "district_id",
            referencedColumnName = "id",
            nullable = false
    )
    public District getDistrict() {
        return this.district;
    }

    public void setDistrict(District district) {
        this.district = district;
    }

    @OneToMany(targetEntity = RaceEntry.class,mappedBy = "race")
    public List<RaceEntry> getRaceEntries() {
        return this.raceEntries;
    }

    public void setRaceEntries(List<RaceEntry> raceEntries) {
        this.raceEntries = raceEntries;
    }
}

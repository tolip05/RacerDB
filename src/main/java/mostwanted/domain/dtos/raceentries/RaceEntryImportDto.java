package mostwanted.domain.dtos.raceentries;

import mostwanted.util.FileUtil;

import javax.xml.bind.annotation.*;

@XmlRootElement(name = "race-entry")
@XmlAccessorType(XmlAccessType.FIELD)
public class RaceEntryImportDto {
    @XmlAttribute(name = "has-finished")
    private Boolean hasFinished;
    @XmlAttribute(name = "finish-time")
    private Double finishTime;
    @XmlAttribute(name = "car-id")
    private Integer carId;
    @XmlElement(name = "racer")
    private String racer;

    public RaceEntryImportDto() {
    }

    public Boolean getHasFinished() {
        return this.hasFinished;
    }

    public void setHasFinished(Boolean hasFinished) {
        this.hasFinished = hasFinished;
    }

    public Double getFinishTime() {
        return this.finishTime;
    }

    public void setFinishTime(Double finishTime) {
        this.finishTime = finishTime;
    }

    public Integer getCarId() {
        return this.carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getRacer() {
        return this.racer;
    }

    public void setRacer(String racer) {
        this.racer = racer;
    }
}

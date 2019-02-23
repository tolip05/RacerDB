package mostwanted.service;

import mostwanted.domain.entities.RaceEntry;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

public interface RaceEntryService {

    Boolean raceEntriesAreImported();

    String readRaceEntriesXmlFile() throws IOException;

    String importRaceEntries() throws JAXBException, FileNotFoundException;
    RaceEntry findById(Integer id);

    void saveAll(List<RaceEntry> raceEntries);
}

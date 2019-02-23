package mostwanted.service;

import mostwanted.domain.entities.Racer;

import java.io.IOException;
import java.util.Optional;

public interface RacerService {

    Boolean racersAreImported();

    String readRacersJsonFile() throws IOException;

    String importRacers(String racersFileContent);

    String exportRacingCars();

    Racer getRacer(String racerName);
}

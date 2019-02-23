package mostwanted.service;

import mostwanted.common.Constants;
import mostwanted.domain.dtos.raceentries.RaceEntryRootImportDto;
import mostwanted.domain.entities.Car;
import mostwanted.domain.entities.RaceEntry;
import mostwanted.domain.entities.Racer;
import mostwanted.repository.RaceEntryRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import mostwanted.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class RaceEntryServiceImpl implements RaceEntryService {
    private final String FILE_XML_RACE_PATH = System.getProperty("user.dir")
            + "/src/main/resources/files/race-entries.xml";
    private final RaceEntryRepository raceEntryRepository;
    private final RacerService racerService;
    private final CarService carService;
    private final FileUtil fileUtil;
    private final XmlParser xmlParser;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public RaceEntryServiceImpl(RaceEntryRepository raceEntryRepository, RacerService racerService,
                                CarService carService, FileUtil fileUtil,
                                XmlParser xmlParser,
                                ValidationUtil validationUtil, ModelMapper modelMapper) {
        this.raceEntryRepository = raceEntryRepository;


        this.racerService = racerService;
        this.carService = carService;
        this.fileUtil = fileUtil;
        this.xmlParser = xmlParser;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean raceEntriesAreImported() {
        return this.raceEntryRepository.count() > 0;
    }

    @Override
    public String readRaceEntriesXmlFile() throws IOException {
        return this.fileUtil.readFile(FILE_XML_RACE_PATH);
    }

    @Override
    public String importRaceEntries() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        RaceEntryRootImportDto raceEntryRootImportDto =
                this.xmlParser.parseXml(RaceEntryRootImportDto.class, FILE_XML_RACE_PATH);

        Arrays.stream(raceEntryRootImportDto.getRaceEntryImportDtos())
                .forEach(raceEntryImportDto -> {
                    Racer racer = this.racerService
                            .getRacer(raceEntryImportDto.getRacer());
                    Car car = this.carService.getCarById(raceEntryImportDto.getCarId());
                    if (racer == null || car == null) {
                        sb.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());
                        return;
                    }
                    RaceEntry raceEntry = this.modelMapper.map(raceEntryImportDto, RaceEntry.class);
                    raceEntry.setCar(car);
                    raceEntry.setRacer(racer);
                    raceEntry.setRace(null);
                    raceEntry = this.raceEntryRepository.saveAndFlush(raceEntry);
                    sb.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,raceEntry.getClass().getSimpleName(),
                            raceEntry.getId())).append(System.lineSeparator());
                });


        return sb.toString().trim();
    }

    @Override
    public RaceEntry findById(Integer id) {
        return this.raceEntryRepository.findById(id).orElse(null);
    }

    @Override
    public void saveAll(List<RaceEntry> raceEntries) {
        this.raceEntryRepository.saveAll(raceEntries);
    }
}

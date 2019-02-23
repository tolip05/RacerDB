package mostwanted.service;

import mostwanted.common.Constants;
import mostwanted.domain.dtos.races.RaceImportRootDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Race;
import mostwanted.domain.entities.RaceEntry;
import mostwanted.repository.RaceRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import mostwanted.util.XmlParser;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RaceServiceImpl implements RaceService {
    private final String FILE_XML_RACES_PATH = System.getProperty("user.dir")
            + "/src/main/resources/files/races.xml";

    private final RaceRepository raceRepository;
    private final DistrictService districtService;
    private final FileUtil fileUtil;
    private final XmlParser parser;
    private final ValidationUtil validationUtil;
    private final RaceEntryService raceEntryService;
    private final ModelMapper modelMapper;

    @Autowired
    public RaceServiceImpl(RaceRepository raceRepository,
                           DistrictService districtService,
                           FileUtil fileUtil,
                           XmlParser parser, ValidationUtil validationUtil,
                           RaceEntryService raceEntryService, ModelMapper modelMapper) {
        this.raceRepository = raceRepository;
        this.districtService = districtService;
        this.fileUtil = fileUtil;
        this.parser = parser;
        this.validationUtil = validationUtil;
        this.raceEntryService = raceEntryService;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean racesAreImported() {
        return this.raceRepository.count() > 0;
    }

    @Override
    public String readRacesXmlFile() throws IOException {
        return this.fileUtil.readFile(FILE_XML_RACES_PATH);
    }

    @Override
    public String importRaces() throws JAXBException, FileNotFoundException {
        StringBuilder sb = new StringBuilder();
        RaceImportRootDto raceImportRootDto =
                this.parser.parseXml(RaceImportRootDto.class,FILE_XML_RACES_PATH);

        Arrays.stream(raceImportRootDto.getRaceImportDtos())
                .forEach(raceImportDto -> {
                    District districtEntity =
                            this.districtService.findDistrictByName(raceImportDto.getDistrict());
                    if (!this.validationUtil.isValid(raceImportDto) || districtEntity == null){
                        sb.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());
                        return;
                    }
                    Race raceEntity = this.modelMapper.map(raceImportDto,Race.class);
                    raceEntity.setDistrict(districtEntity);

                    List<RaceEntry> raceEntryList = new ArrayList<>();
                    Arrays.stream(raceImportDto.getEntryImportRootDto().getEntryImportDtos())
                            .forEach(entryImportDto -> {
                                RaceEntry raceEntryEntity = this.raceEntryService.findById(entryImportDto.getId());
                                if (raceEntryEntity == null){
                                    return;
                                }
                               raceEntryEntity.setRace(raceEntity);
                                raceEntryList.add(raceEntryEntity);
                            });
                    Race race = this.raceRepository.saveAndFlush(raceEntity);
                    this.raceEntryService.saveAll(raceEntryList);
                    sb.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,race.getClass().getSimpleName(),race.getId()))
                            .append(System.lineSeparator());
                });
        return sb.toString().trim();
    }
}

package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.RacerImportDto;
import mostwanted.domain.entities.Racer;
import mostwanted.domain.entities.Town;
import mostwanted.repository.RacerRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class RacerServiceImpl implements RacerService{
    private final String FILE_JSON_RACER_PATH = System.getProperty("user.dir")
            + "/src/main/resources/files/racers.json";

    private final RacerRepository racerRepository;
    private final FileUtil fileUtil;
    private final TownService townService;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public RacerServiceImpl(RacerRepository racerRepository,
                            FileUtil fileUtil, TownService townService,ValidationUtil validationUtil,
                            ModelMapper modelMapper, Gson gson) {
        this.racerRepository = racerRepository;


        this.fileUtil = fileUtil;
        this.townService = townService;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public Boolean racersAreImported() {
        return this.racerRepository.count() > 0;
    }

    @Override
    public String readRacersJsonFile() throws IOException {
        return this.fileUtil.readFile(FILE_JSON_RACER_PATH);
    }

    @Override
    public String importRacers(String racersFileContent) {

        StringBuilder sb = new StringBuilder();
        RacerImportDto[] racerImportDtos = this.gson.fromJson(racersFileContent, RacerImportDto[].class);
        Arrays.stream(racerImportDtos).forEach(racerImportDto -> {
            Racer racerEntity = this.racerRepository
                    .findByName(racerImportDto.getName()).orElse(null);
            if (racerEntity != null){
                sb.append(Constants.DUPLICATE_DATA_MESSAGE)
                        .append(System.lineSeparator());
                return;
            }
            Town townEntity = this.townService.getTown(racerImportDto.getHomeTown());
            if (!this.validationUtil.isValid(racerImportDto) || townEntity == null){
                sb.append(Constants.INCORRECT_DATA_MESSAGE)
                        .append(System.lineSeparator());
                return;
            }
            racerEntity = this.modelMapper.map(racerImportDto,Racer.class);
            racerEntity.setHomeTown(townEntity);
            this.racerRepository.saveAndFlush(racerEntity);
            sb.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,racerEntity.getClass().getSimpleName()
            ,racerEntity.getName())).append(System.lineSeparator());
        });
        return sb.toString().trim();
    }

    @Override
    public String exportRacingCars() {
        return null;
    }

    @Override
    public Racer getRacer(String racerName) {
        return this.racerRepository.findByName(racerName).orElse(null);
    }
}

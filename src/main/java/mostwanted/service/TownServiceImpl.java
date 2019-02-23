package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.TownImportDto;
import mostwanted.domain.entities.Town;
import mostwanted.repository.TownRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class TownServiceImpl implements TownService {
    private final String FILE_JSON_TOWN_PATH = System.getProperty("user.dir")
            + "/src/main/resources/files/towns.json";

    private final TownRepository townRepository;
    private final FileUtil fileUtil;
    private final ValidationUtil validationUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;

    @Autowired
    public TownServiceImpl(TownRepository townRepository, FileUtil fileUtil, ValidationUtil validationUtil, ModelMapper modelMapper, Gson gson) {
        this.townRepository = townRepository;
        this.fileUtil = fileUtil;
        this.validationUtil = validationUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
    }

    @Override
    public Boolean townsAreImported() {
      return this.townRepository.count() > 0;
    }

    @Override
    public String readTownsJsonFile() throws IOException {
        return this.fileUtil.readFile(FILE_JSON_TOWN_PATH);
    }

    @Override
    public String importTowns(String townsFileContent) {
        StringBuilder sb = new StringBuilder();
        TownImportDto[] townImportDtos =
                this.gson.fromJson(townsFileContent,TownImportDto[].class);
        Arrays.stream(townImportDtos).forEach(townImportDto -> {
            Town town = this.getTown(townImportDto.getName());
             if (town != null){
                 sb.append(Constants.DUPLICATE_DATA_MESSAGE).append(System.lineSeparator());
                 return;
             }
             if (!this.validationUtil.isValid(townImportDto)){
                 sb.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());
               return;
             }
            town = this.modelMapper.map(townImportDto,Town.class);
             this.townRepository.saveAndFlush(town);

             sb.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,town.getClass().getSimpleName(),town.getName()))
                     .append(System.lineSeparator());
        });
        return sb.toString().trim();
    }

    @Override
    public String exportRacingTowns() {
        return null;
    }

    @Override
    public Town getTown(String name) {
        return this.townRepository.findByName(name).orElse(null);
    }
}

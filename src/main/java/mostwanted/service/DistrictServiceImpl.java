package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.DistrictImportDto;
import mostwanted.domain.entities.District;
import mostwanted.domain.entities.Town;
import mostwanted.repository.DistrictRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class DistrictServiceImpl implements DistrictService {
    private final String FILE_DISTRICT_CARS_PATH = System.getProperty("user.dir")
            + "/src/main/resources/files/districts.json";
    private final DistrictRepository districtRepository;
    private final FileUtil fileUtil;
    private final TownService townService;
    private final ValidationUtil validationUtil;
    private final Gson gson;
    private final ModelMapper modelMapper;

    @Autowired
    public DistrictServiceImpl(DistrictRepository districtRepository, FileUtil fileUtil, TownService townService, ValidationUtil validationUtil, Gson gson, ModelMapper modelMapper) {
        this.districtRepository = districtRepository;
        this.fileUtil = fileUtil;
        this.townService = townService;
        this.validationUtil = validationUtil;
        this.gson = gson;
        this.modelMapper = modelMapper;
    }

    @Override
    public Boolean districtsAreImported() {
        return this.districtRepository.count() > 0;
    }

    @Override
    public String readDistrictsJsonFile() throws IOException {
        return this.fileUtil.readFile(FILE_DISTRICT_CARS_PATH);
    }

    @Override
    public String importDistricts(String districtsFileContent) {
        StringBuilder sb = new StringBuilder();
        DistrictImportDto[] districtImportDtos = this.gson
                .fromJson(districtsFileContent, DistrictImportDto[].class);
        Arrays.stream(districtImportDtos).forEach(districtImportDto -> {
            District districtEntity = this.districtRepository.findByName(districtImportDto.getName()).orElse(null);
            if (districtEntity != null) {
                sb.append(Constants.DUPLICATE_DATA_MESSAGE).append(System.lineSeparator());
                return;
            }
            Town town = this.townService.getTown(districtImportDto.getTownName());
            if (!this.validationUtil.isValid(districtImportDto) || town == null) {
                sb.append(Constants.INCORRECT_DATA_MESSAGE).append(System.lineSeparator());
                return;
            }
            districtEntity = this.modelMapper.map(districtImportDto, District.class);
            districtEntity.setTown(town);
            this.districtRepository.saveAndFlush(districtEntity);
             sb.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,districtEntity.getClass().getSimpleName(),
                     districtEntity.getName())).append(System.lineSeparator());
        });

        return sb.toString().trim();
    }

    @Override
    public District findDistrictByName(String name) {
        return this.districtRepository.findByName(name).orElse(null);
    }

    public District getDistrict(String name) {
        return this.districtRepository.findByName(name).orElse(null);
    }
}

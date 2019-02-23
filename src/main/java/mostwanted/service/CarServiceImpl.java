package mostwanted.service;

import com.google.gson.Gson;
import mostwanted.common.Constants;
import mostwanted.domain.dtos.CarImportDto;
import mostwanted.domain.entities.Car;
import mostwanted.domain.entities.Racer;
import mostwanted.repository.CarRepository;
import mostwanted.util.FileUtil;
import mostwanted.util.ValidationUtil;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Arrays;

@Service
public class CarServiceImpl implements CarService{
    private final String FILE_JSON_CARS_PATH = System.getProperty("user.dir")
            + "/src/main/resources/files/cars.json";
    private final CarRepository carRepository;
    private final FileUtil fileUtil;
    private final ModelMapper modelMapper;
    private final Gson gson;
    private final ValidationUtil validationUtil;
    private final RacerService racerService;

    @Autowired
    public CarServiceImpl(CarRepository carRepository, FileUtil fileUtil,
                          ModelMapper modelMapper, Gson gson,
                          ValidationUtil validationUtil, RacerService racerService) {
        this.carRepository = carRepository;
        this.fileUtil = fileUtil;
        this.modelMapper = modelMapper;
        this.gson = gson;
        this.validationUtil = validationUtil;
        this.racerService = racerService;
    }

    @Override
    public Boolean carsAreImported() {
        return this.carRepository.count() > 0;
    }

    @Override
    public String readCarsJsonFile() throws IOException {
        return this.fileUtil.readFile(FILE_JSON_CARS_PATH);
    }

    @Override
    public String importCars(String carsFileContent)
    {
        StringBuilder sb = new StringBuilder();

        Arrays.stream(this.gson.fromJson(carsFileContent, CarImportDto[].class))
                .forEach(carImportDto -> {
                    Racer racerEntity = this.racerService
                            .getRacer(carImportDto.getRacerName());
                    if (!this.validationUtil.isValid(carImportDto) || racerEntity == null){
                        sb.append(Constants.INCORRECT_DATA_MESSAGE)
                                .append(System.lineSeparator());
                        return;
                    }
                    Car carEntity = this.modelMapper.map(carImportDto,Car.class);
                    carEntity.setRacer(racerEntity);
                    String carImportResult = String.format("%s %s @ %d",carEntity.getBrand(),carEntity.getModel()
                            ,carEntity.getYearOfProduction());
                    this.carRepository.saveAndFlush(carEntity);
                    sb.append(String.format(Constants.SUCCESSFUL_IMPORT_MESSAGE,carEntity
                            .getClass().getSimpleName(),carImportResult))
                            .append(System.lineSeparator());

                });
        return sb.toString().trim();
    }

    @Override
    public Car getCarById(Integer id) {
        return this.carRepository.findById(id).orElse(null);
    }
}

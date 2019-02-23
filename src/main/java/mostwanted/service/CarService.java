package mostwanted.service;

import mostwanted.domain.entities.Car;

import java.io.IOException;

public interface CarService {

    Boolean carsAreImported();

    String readCarsJsonFile() throws IOException;

    String importCars(String carsFileContent);

    Car getCarById(Integer id);
}

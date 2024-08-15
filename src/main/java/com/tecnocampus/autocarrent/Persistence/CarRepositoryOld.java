package com.tecnocampus.autocarrent.Persistence;

import com.tecnocampus.autocarrent.Domain.Car;
import com.tecnocampus.autocarrent.Utilities.NotFoundException;

import java.util.List;
import java.util.ArrayList;

public class CarRepositoryOld {

    static List<Car> cars;

    public CarRepositoryOld(){
        if(cars == null) cars = new ArrayList<>();
    }

    public void saveCar(Car car) throws NotFoundException {
        if(cars.stream().anyMatch(x -> x.getId().equals(car.getId())) ||
                cars.stream().anyMatch(y -> y.getLicensePlate().equals(car.getLicensePlate()))){
            throw new NotFoundException();
        }else{
            cars.add(car);
        }
    }

    public Car getCar(String id) throws NotFoundException {
        for (Car car : cars) {
            if (car.getId().equals(id)) {
                return car;
            }
        }
        throw new NotFoundException();
    }

    public List<Car> getCars(){return cars;}

    public void deleteCar(String carId) throws NotFoundException {
        try {
            cars.removeIf(x -> x.getId().equals(carId));
        }catch(Exception e){
            throw new NotFoundException();
        }
    }

    public void clear(){
        cars.clear();
    }
}

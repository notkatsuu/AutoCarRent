package com.tecnocampus.autocarrent.Application;

import com.tecnocampus.autocarrent.Application.DTO.BookingDTO;
import com.tecnocampus.autocarrent.Application.DTO.CarDTO;
import com.tecnocampus.autocarrent.Domain.Car;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientBookingRepository;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientCarRepository;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;
import com.tecnocampus.autocarrent.Utilities.NotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CarController {

    private final ClientCarRepository carRepository;
    private final ClientBookingRepository bookingRepository;

    public CarController(@Qualifier("clientCarRepository")ClientCarRepository carRepository, @Qualifier("clientBookingRepository") ClientBookingRepository bookingRepository) {
        this.carRepository = carRepository;
        this.bookingRepository = bookingRepository;
    }

    public CarDTO createCar(CarDTO carDTO) throws Exception {

        Car car = new Car(carDTO);
        carRepository.create(car);
        return new CarDTO(car);

    }

    public CarDTO getCar(String id) throws Throwable {
        Car car = (Car) carRepository.findById(id).orElseThrow(NotFoundException::new);
        return new CarDTO(car);
    }

    public void checkCarAvailability(String id, LocalDateTime datePickUp, LocalDateTime dateReturn) throws Throwable {
        getCar(id); //If exception thrown, car doesn't exist
        List<BookingDTO> bookingDTOs = bookingRepository.findAllByCarId(id).stream().map(x -> new BookingDTO(x)).toList();
        bookingDTOs.stream().filter(x -> x.getDatePickUp().compareTo(String.valueOf(datePickUp))>0&&x.getDateReturn().compareTo(String.valueOf(datePickUp))<0
                                || x.getDatePickUp().compareTo(String.valueOf(dateReturn))>0&&x.getDateReturn().compareTo(String.valueOf(dateReturn))<0);

        if(!bookingDTOs.isEmpty())
            throw new InvalidParamsException("Car is not available during the specified period");

    }

    public void setCarCondition(String id, double condition) throws Throwable {
        Car car = (Car)carRepository.findById(id).orElseThrow(InvalidParamsException::new);
        car.setCondition(condition);
        carRepository.update(car);
    }


    public List<CarDTO> getCars() {
        List<Car> cars = carRepository.findAll();
        return cars.stream().map(x -> new CarDTO(x)).collect(Collectors.toList());
    }

    public void deleteCar(String carId) throws InvalidParamsException {
        carRepository.delete(carId);
    }

    public void removeAll(){
        carRepository.removeAll();
    }
}
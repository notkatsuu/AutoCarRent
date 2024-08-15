package com.tecnocampus.autocarrent.api;


import com.tecnocampus.autocarrent.Application.CarController;
import com.tecnocampus.autocarrent.Application.DTO.CarDTO;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
public class CarRestController {
    
    private final CarController carController;
    
    public CarRestController(CarController carController){this.carController = carController;}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CarDTO createCar(@RequestBody CarDTO carDTO) throws Exception {
        return carController.createCar(carDTO);
    }

    @GetMapping
    public List<CarDTO> getCars() {
        return carController.getCars();
    }

    @GetMapping("/{carId}")
    public CarDTO getCar(@PathVariable String carId) throws Throwable {
        return carController.getCar(carId);
    }

    @DeleteMapping("/{carId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCar(@PathVariable String carId) throws Exception {
        carController.deleteCar(carId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAll(){
        carController.removeAll();
    }
}

package com.tecnocampus.autocarrent.Application.DTO;


import com.tecnocampus.autocarrent.Domain.Car;

public class CarDTO {

    private String  id, licensePlate, brand, model, category;
    private double condition;


    public CarDTO(){
    }


    public CarDTO(Car car)
    {
        this.id = car.getId();
        this.licensePlate = car.getLicensePlate();
        this.brand = car.getBrand();
        this.model = car.getModel();
        this.category = car.getCategory();
        this.condition = car.getCondition();
    }

    public String getId() {return id;}
    public String getLicensePlate() {return licensePlate;}
    public String getBrand() {return brand;}
    public String getModel() {return model;}
    public String getCategory() {return category;}
    public double getCondition() {return this.condition;}

}

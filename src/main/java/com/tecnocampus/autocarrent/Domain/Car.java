package com.tecnocampus.autocarrent.Domain;


import com.tecnocampus.autocarrent.Application.DTO.CarDTO;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
public class Car {

    private String id = UUID.randomUUID().toString(), brand, model, category, licensePlate;

    private double condition = 100;

    public Car(){}

    public Car(CarDTO carDTO) throws Exception
    {
        checkInput(carDTO);
        this.licensePlate = carDTO.getLicensePlate();
        this.brand = carDTO.getBrand();
        this.model = carDTO.getModel();
        this.category = carDTO.getCategory();

    }

    public Car(ResultSet resultSet) throws SQLException {
        this.id = resultSet.getString("id");
        this.licensePlate = resultSet.getString("licensePlate");
        this.brand = resultSet.getString("brand");
        this.model = resultSet.getString("model");
        this.category = resultSet.getString("category");
        this.condition = resultSet.getDouble("carCondition");
    }

    private void checkInput(CarDTO carDTO) throws Exception {
        checkLicensePlate(carDTO.getLicensePlate());
        checkBrand(carDTO.getBrand());
        checkModel(carDTO.getModel());
        checkCategory(carDTO.getCategory());
    }

    private void checkLicensePlate(String licensePlate) throws Exception
    {
        if(licensePlate == null) throw new InvalidParamsException();
        if(licensePlate.matches("^[0-9]{4}-[A-Z]{3}$"))
            return;
        throw new InvalidParamsException();
    }

    private void checkCategory(String category) throws Exception
    {
        if(category == null) throw new InvalidParamsException();
        CarCategories.Categories.valueOf(category.toUpperCase());
    }

    private void checkBrand(String brand) throws Exception
    {
        if(brand == null) throw new InvalidParamsException();
    }

    private void checkModel(String model) throws Exception
    {
        if(model == null) throw new InvalidParamsException();
    }

    public void setCondition(double condition) throws Exception
    {
        if(condition < 0 || condition > 100)
            throw new InvalidParamsException();
        this.condition = condition;
    }

    public String getId() {
        return id;
    }
    public String getLicensePlate() {
        return licensePlate;
    }
    public String getBrand() {
        return brand;
    }
    public String getModel() {
        return model;
    }
    public String getCategory() {
        return category;
    }
    public double getCondition() {
        return condition;
    }
}

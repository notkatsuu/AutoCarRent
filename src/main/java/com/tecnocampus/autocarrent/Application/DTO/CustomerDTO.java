package com.tecnocampus.autocarrent.Application.DTO;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tecnocampus.autocarrent.Domain.Customer;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class CustomerDTO {
    private String id, name, surname, address, driverLicense;
    double totalSpent;

    private String  birthDate;

    public CustomerDTO() {
    }


    public CustomerDTO(Customer customer) {
        this.id = customer.getId();
        this.name = customer.getName();
        this.surname = customer.getSurname();
        this.address = customer.getAddress();
        this.birthDate = customer.getBirthDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        this.driverLicense = customer.getDriverLicense();
        this.totalSpent = customer.getTotalSpent();
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return this.name;
    }

    public String getSurname() {
        return this.surname;
    }

    public String getAddress() {
        return this.address;
    }


    public LocalDate getBirthDate() throws InvalidParamsException {

        if (birthDate == null) throw new InvalidParamsException();

        LocalDate date = LocalDate.parse(birthDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        if (date.isAfter(LocalDate.now())) throw new InvalidParamsException();
        return date;

    }

    public String getDriverLicense() {
        return this.driverLicense;
    }

    public double getTotalSpent() {
        return this.totalSpent;
    }
}

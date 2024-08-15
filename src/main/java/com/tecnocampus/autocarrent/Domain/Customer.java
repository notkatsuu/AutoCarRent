package com.tecnocampus.autocarrent.Domain;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.tecnocampus.autocarrent.Application.CustomerController;
import com.tecnocampus.autocarrent.Application.DTO.CustomerDTO;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;

import java.time.LocalDate;
import java.util.UUID;

public class Customer {
    String name, surname, address, driverLicense;

    @JsonFormat(pattern="yyyy-MM-dd")
    LocalDate birthDate;

    String id = UUID.randomUUID().toString();

    double totalSpent;

    public Customer(){}

    public Customer(CustomerDTO customerDTO) throws InvalidParamsException {
        checkDateOfBirth(customerDTO.getBirthDate());
        checkDriverLicenseLength(customerDTO.getDriverLicense());
        checkDriverLicense(customerDTO.getDriverLicense());

        this.name = customerDTO.getName();
        this.surname = customerDTO.getSurname();
        this.address = customerDTO.getAddress();
        this.birthDate = customerDTO.getBirthDate();
        this.driverLicense = customerDTO.getDriverLicense();
        this.totalSpent = customerDTO.getTotalSpent();

    }

    private void checkDateOfBirth(LocalDate birthDate) throws InvalidParamsException {

        if (birthDate == null) {

            throw new InvalidParamsException();
        }

        LocalDate limit = LocalDate.now().minusYears(18);
        if (birthDate.isAfter(limit)) {
            throw new InvalidParamsException();
        }
    }


    private void checkDriverLicenseLength(String driverLicense) throws InvalidParamsException {
        if (driverLicense.length() != 9) {
            throw new InvalidParamsException();
        }
    }

    private void checkDriverLicense(String driverLicense) throws InvalidParamsException {
        for (int i = 0; i < driverLicense.length(); i++) {
            //Character.isDigit() DONE
            if (!Character.isDigit(driverLicense.charAt(i))) {
                throw new InvalidParamsException();
            }
        }
        if (driverLicense.charAt(8) < 48 || driverLicense.charAt(8) > 57) {
            throw new InvalidParamsException();
        }
        this.driverLicense = driverLicense;
    }

    public void payBill(double amount) {
        this.totalSpent += amount;
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

    public LocalDate getBirthDate() {
        return this.birthDate;
    }

    public String getDriverLicense() {
        return this.driverLicense;
    }

    public String getId() {
        return id;
    }

    public double getTotalSpent() {
        return this.totalSpent;
    }

    public void returnDeposit(double amount) {
        this.totalSpent -= amount;
    }
}

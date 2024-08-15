package com.tecnocampus.autocarrent.Application.DTO;


import com.tecnocampus.autocarrent.Domain.Booking;

import java.time.LocalDateTime;

public class BookingDTO {
    private String id;
    private CarDTO car;
    private CustomerDTO customer;
    private String datePickUp, dateReturn;
    private double bill, deposit;

    private String state;


    public BookingDTO(){
    }


    public BookingDTO(Booking booking) {
        this.id = booking.getId();
        this.datePickUp = booking.getDatePickUp().toString();
        this.dateReturn = booking.getDateReturn().toString();
        this.car = new CarDTO(booking.getCar());
        this.customer = new CustomerDTO(booking.getCustomer());
        this.bill = booking.getBill();
        this.deposit = booking.getDeposit();
        this.state = booking.getState();
    }

    // Getters and setters
    public String getId() { return id; }
    public String getDatePickUp() { return datePickUp; }
    public String getDateReturn() { return dateReturn; }
    public CarDTO getCar() { return car; }
    public CustomerDTO getCustomer() { return customer; }
    public double getBill() { return bill; }
    public double getDeposit() { return deposit; }
    public String getState() { return state; }

    public void setId(String id) { this.id = id; }
    public void setDatePickUp(LocalDateTime datePickUp) { this.datePickUp = String.valueOf(datePickUp); }
    public void setDateReturn(LocalDateTime dateReturn) { this.dateReturn = String.valueOf(dateReturn); }
    public void setCar(CarDTO car) { this.car = car; }
    public void setCustomer(CustomerDTO customer) { this.customer = customer; }
    public void setBill(double bill) { this.bill = bill; }
    public void setDeposit(double deposit) { this.deposit = deposit; }
    public void setState(String state) { this.state = state; }
}

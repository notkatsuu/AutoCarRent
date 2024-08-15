package com.tecnocampus.autocarrent.Domain;

import com.tecnocampus.autocarrent.Application.DTO.BookingDTO;
import com.tecnocampus.autocarrent.Persistence.CarRepositoryOld;
import com.tecnocampus.autocarrent.Persistence.CustomerRepositoryOld;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientCarRepository;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.DayOfWeek;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.UUID;

public class Booking {

    private String id = UUID.randomUUID().toString();
    private LocalDateTime datePickUp;
    private LocalDateTime dateReturn;
    private Car bookedCar;
    private Customer booker;

    private enum State {
        PENDING, IN_PROGRESS, COMPLETED, CANCELLED
    }

    private State state;

    public Booking(){}

    public Booking(Car car, Customer customer, LocalDateTime datePickUp, LocalDateTime dateReturn) throws InvalidParamsException {
        this.datePickUp = datePickUp;
        this.dateReturn = dateReturn;
        validateDates(datePickUp, dateReturn);
        this.bookedCar = car;
        this.booker = customer;
        this.state = State.PENDING;
    }

    public Booking(Car car, Customer customer, ResultSet resultSet) throws SQLException{
        this.id = resultSet.getString("id");
        this.datePickUp = resultSet.getTimestamp("datePickUp").toLocalDateTime();
        this.dateReturn = resultSet.getTimestamp("dateReturn").toLocalDateTime();
        this.bookedCar = car;
        this.booker = customer;
        this.state = State.valueOf(resultSet.getString("state"));

    }

    void validateDates(LocalDateTime datePickUp, LocalDateTime dateReturn) throws InvalidParamsException{

        if(datePickUp.isAfter(dateReturn)){
            throw new InvalidParamsException("Pick up must be before return");
        }
        if(datePickUp.isBefore(LocalDateTime.now()))
            throw new InvalidParamsException("Pick up must be in the future");

        int daysBetween = (int) ChronoUnit.DAYS.between(datePickUp, dateReturn);
        if (daysBetween < 1) throw new InvalidParamsException();
        if (daysBetween > 30) throw new InvalidParamsException();
    }

    public LocalDateTime getDatePickUp() {
        return datePickUp;
    }

    public LocalDateTime getDateReturn() {
        return dateReturn;
    }


    public Customer getCustomer() {
        return booker;
    }

    public String getId() {
        return id;
    }

    public Car getCar() {
        return bookedCar;
    }


    public double getDeposit() {
        return CarCategories.getDepositPrice(bookedCar.getCategory());
    }

    public String getState() {
        return this.state.name();
    }

    public boolean isWeekend(LocalDateTime date) {
        return date.getDayOfWeek() == DayOfWeek.FRIDAY || date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public double getBill() {
        double total = 0;
        int daysBetween = (int) ChronoUnit.DAYS.between(datePickUp, dateReturn);
        for (int i = 0; i < daysBetween; i++) {
            double todayPrice = CarCategories.getDailyPrice(bookedCar.getCategory());
            total += (isWeekend(datePickUp.plusDays(i))) ? todayPrice * 1.25 : todayPrice;
        }
        return total;
    }

    public void cancelBooking() throws Exception {
        if (LocalDateTime.now().isAfter(datePickUp.minusHours(24))) {
            throw new InvalidParamsException("Cancel time exceded");
        }
        state = State.CANCELLED;
    }

    public void setInProgress() {
        state = State.IN_PROGRESS;
    }

    public void setCompleted() {
        state = State.COMPLETED;
    }

    public boolean isInProgress() {
        return state == State.IN_PROGRESS;
    }

    public boolean isCompleted() {
        return state == State.COMPLETED;
    }

    public boolean isCancelled() {
        return state == State.CANCELLED;
    }

    public boolean isPending() {
        return state == State.PENDING;
    }
}

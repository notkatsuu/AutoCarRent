package com.tecnocampus.autocarrent.Persistence.jdbc;

import com.tecnocampus.autocarrent.Domain.Booking;
import com.tecnocampus.autocarrent.Domain.Car;
import com.tecnocampus.autocarrent.Domain.Customer;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientBookingRepository implements IRepository<Booking>{

    private final JdbcClient jdbcClient;
    private final ClientCarRepository carRepository;
    private final ClientCustomerRepository customerRepository;

    public ClientBookingRepository(JdbcClient jdbcClient, ClientCarRepository carRepository, ClientCustomerRepository customerRepository){
        this.jdbcClient = jdbcClient;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
    }

    @Override
    public List<Booking> findAll() {
        return jdbcClient.sql("SELECT * FROM Booking")
                .query((rs, rowNum) -> {
                    Car car = carRepository.findById(rs.getString("bookedCar")).get();
                    Customer customer = customerRepository.findById(rs.getString("booker")).get();
                    return new Booking(car,customer,rs);
                }).list();
    }

    @Override
    public List<Booking> findAll(int limit) {
        return jdbcClient.sql("SELECT * FROM Booking LIMIT :limit")
                .param("limit",limit)
                .query(Booking.class).list();
    }

    public List<Booking> findAllByCarId(String carId){
        return jdbcClient.sql("SELECT * FROM Booking WHERE bookedCar = ?")
                .param(carId)
                .query((rs, rowNum) -> {
                    Car car = carRepository.findById(rs.getString("bookedCar")).get();
                    Customer customer = customerRepository.findById(rs.getString("booker")).get();
                    return new Booking(car,customer,rs);
                }).list();
    }


    public List<Booking> findAllByCustomerId(String customerId){
        return jdbcClient.sql("SELECT * FROM Booking WHERE booker = ?")
                .param(customerId)
                .query((rs, rowNum) -> {
                    Car car = carRepository.findById(rs.getString("bookedCar")).get();
                    Customer customer = customerRepository.findById(rs.getString("booker")).get();
                    return new Booking(car,customer,rs);
                }).list();
    }



    @Override
    public Optional<Booking> findById(String id){
        return jdbcClient.sql("SELECT * FROM Booking WHERE id = ?")
                .param(id)
                .query((rs, rowNum) -> {
                    Car car = carRepository.findById(rs.getString("bookedCar")).get();
                    Customer customer = customerRepository.findById(rs.getString("booker")).get();
                    return new Booking(car,customer,rs);
                }).optional();

    }


    @Override
    public void create(Booking booking) throws InvalidParamsException {
        int update = jdbcClient.sql("INSERT INTO Booking VALUES (?,?,?,?,?,?)")
                .params(List.of(booking.getId(),booking.getDatePickUp(),booking.getDateReturn(),booking.getCar().getId(),booking.getCustomer().getId(),booking.getState()))
                .update();
        if(update != 1) throw new InvalidParamsException();
    }

    @Override
    public void update(Booking booking) throws InvalidParamsException {
        int update = jdbcClient.sql("UPDATE Booking SET state = ? WHERE id = ?")
                .params(List.of(booking.getState().toString(),booking.getId()))
                .update();

        if(update != 1) throw new InvalidParamsException("Cannot update the booking");
    }

    @Override
    public void delete(String id) throws InvalidParamsException {
        int update = jdbcClient.sql("DELETE FROM Booking WHERE id = ?")
                .param(id)
                .update();
        if(update != 1) throw new InvalidParamsException();
    }



    public void removeAll(){
        jdbcClient.sql("DELETE FROM booking").update();
    }
}

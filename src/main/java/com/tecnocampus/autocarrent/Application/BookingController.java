package com.tecnocampus.autocarrent.Application;

import com.tecnocampus.autocarrent.Domain.Car;
import com.tecnocampus.autocarrent.Domain.Customer;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientBookingRepository;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientCarRepository;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientCustomerRepository;
import com.tecnocampus.autocarrent.Utilities.NotFoundException;
import com.tecnocampus.autocarrent.Application.DTO.BookingDTO;
import com.tecnocampus.autocarrent.Domain.Booking;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingController {

    private final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
    private final ClientBookingRepository bookingRepository;
    private final ClientCarRepository carRepository;
    private final ClientCustomerRepository customerRepository;
    private final CustomerController customerController;

    public BookingController(@Qualifier("clientBookingRepository") ClientBookingRepository bookingRepository, @Qualifier("clientCarRepository") ClientCarRepository carRepository, @Qualifier("clientCustomerRepository") ClientCustomerRepository customerRepository, CustomerController customerController) {
        this.bookingRepository = bookingRepository;
        this.carRepository = carRepository;
        this.customerRepository = customerRepository;
        this.customerController = customerController;
    }

    public BookingDTO createBookingRequest(String carId, String customerId, String pickUpDate, String returnDate) throws Throwable {
        Car car = carRepository.findById(carId).orElseThrow(NotFoundException::new);
        Customer customer = customerRepository.findById(customerId).orElseThrow(NotFoundException::new);
        Booking booking = new Booking(car, customer, LocalDateTime.parse(pickUpDate, formatter), LocalDateTime.parse(returnDate, formatter));
        bookingRepository.create(booking);
        return getBooking(booking.getId());
    }

    public List<BookingDTO> getBookings() {
        List<Booking> bookings = bookingRepository.findAll();
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDTOS.add(new BookingDTO(booking));
        }
        return bookingDTOS;
    }

    private Booking findById(String bookingId)throws Throwable{
        return bookingRepository.findById(bookingId).orElseThrow(NotFoundException::new);
    }

    public BookingDTO getBooking(String bookingId) throws Throwable {
        Booking booking = findById(bookingId);
        return new BookingDTO(booking);
    }

    public BookingDTO cancelBooking(String bookingId) throws Throwable {
        Booking booking = findById(bookingId);
        String customerId = booking.getCustomer().getId();
        booking.cancelBooking();
        bookingRepository.update(booking);
        customerController.payBill(customerId, -(booking.getBill() + booking.getDeposit()));
        return new BookingDTO(booking);
    }

    public BookingDTO pickUpBooking(String bookingId) throws Throwable {
        Booking booking = findById(bookingId);
        if (LocalDateTime.now().isBefore(booking.getDatePickUp())) throw new InvalidParamsException("Not in booking period");
        if(LocalDateTime.now().isAfter(booking.getDateReturn())) throw new InvalidParamsException("Booking period expired");
        if (!booking.isPending()) throw new InvalidParamsException("Car already picked up or booking canceled");
        booking.setInProgress();
        bookingRepository.update(booking);
        return new BookingDTO(booking);
    }

    public BookingDTO returnBooking(String bookingId) throws Throwable {
        Booking booking = findById(bookingId);
        booking.setCompleted();
        bookingRepository.update(booking);
        return new BookingDTO(booking);
    }

    public List<BookingDTO> getBookingsByCustomerId(String customerId) {
        List<Booking> bookings = bookingRepository.findAllByCustomerId(customerId);
        List<BookingDTO> bookingDTOS = new ArrayList<>();
        for(Booking booking : bookings)
        {
            bookingDTOS.add(new BookingDTO(booking));
        }
        return bookingDTOS;
    }

    public void removeAll(){
        bookingRepository.removeAll();
    }
}

package com.tecnocampus.autocarrent.api;

import com.tecnocampus.autocarrent.Application.BookingController;
import com.tecnocampus.autocarrent.Application.CarController;
import com.tecnocampus.autocarrent.Application.CustomerController;
import com.tecnocampus.autocarrent.Application.DTO.BookingDTO;
import org.json.JSONObject;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;


@RestController
@RequestMapping("/bookings")
public class BookingRestController {

    private final BookingController bookingController;
    private final CustomerController customerController;
    private final CarController carController;

    private DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;


    public BookingRestController(BookingController bookingController, CustomerController customerController, CarController carController){
        this.bookingController = bookingController;
        this.customerController = customerController;
        this.carController = carController;
    }

    @PostMapping()
    @ResponseStatus(HttpStatus.CREATED)
    public BookingDTO createBooking(@RequestBody String json) throws Throwable {
        JSONObject JSON = new JSONObject(json);

        carController.checkCarAvailability(JSON.getString("CarId"), LocalDateTime.parse(JSON.getString("pickUpDate"), formatter),LocalDateTime.parse(JSON.getString("returnDate"), formatter));
        BookingDTO booking =  bookingController.createBookingRequest(JSON.getString("CarId"), JSON.getString("CustomerId"), JSON.getString("pickUpDate"), JSON.getString("returnDate"));

        customerController.payBill(booking.getCustomer().getId(), booking.getBill());

        return bookingController.getBooking(booking.getId());

    }

    @GetMapping
    public List<BookingDTO> getBookings() {
        return bookingController.getBookings();
    }

    @GetMapping("/{bookingId}")
    public BookingDTO getBooking(@PathVariable String bookingId) throws Throwable {
        return bookingController.getBooking(bookingId);
    }

    @PostMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public BookingDTO cancelBooking(@PathVariable String bookingId) throws Throwable {
        return bookingController.cancelBooking(bookingId);
    }

    @PostMapping("/{bookingId}/pickUp")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookingDTO pickUpCar(@PathVariable String bookingId) throws Throwable {

        String customerId = bookingController.getBooking(bookingId).getCustomer().getId();

        customerController.payBill(customerId, bookingController.getBooking(bookingId).getDeposit());

        return bookingController.pickUpBooking(bookingId);
    }


    @PostMapping("/{bookingId}/return")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public BookingDTO returnCar(@PathVariable String bookingId, @RequestBody String json) throws Throwable {
        JSONObject JSONObject = new JSONObject(json);

        double carCondition = JSONObject.getDouble("condition");
        BookingDTO booking = bookingController.returnBooking(bookingId);

        carController.setCarCondition(booking.getCar().getId(), carCondition);
        customerController.returnDeposit(booking.getCustomer().getId(), booking.getDeposit() * (carCondition / 100.0));

        return booking;
    }

    @DeleteMapping
    public void removeAll(){
        bookingController.removeAll();
    }

}

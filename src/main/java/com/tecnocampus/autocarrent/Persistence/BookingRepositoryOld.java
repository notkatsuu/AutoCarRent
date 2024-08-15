package com.tecnocampus.autocarrent.Persistence;

import com.tecnocampus.autocarrent.Domain.Booking;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;

import java.util.List;
import java.util.ArrayList;

public class BookingRepositoryOld {

    static List<Booking> bookingList;

    public BookingRepositoryOld(){
        if(bookingList == null) bookingList = new ArrayList<>();
    }

    public void saveBooking(Booking booking) throws InvalidParamsException {
        if(bookingList.stream().anyMatch(x -> x.getId().equals(booking.getId()))){
            throw new InvalidParamsException();
        }else{
            bookingList.add(booking);
        }
    }

    public Booking getBooking(String id) throws InvalidParamsException {
        try{
            return bookingList.stream().filter(x -> x.getId().equals(id)).findAny().get();
        }catch(Exception e){
            throw new InvalidParamsException();
        }
    }

    public List<Booking> getBookings() {
        return bookingList;
    }
}

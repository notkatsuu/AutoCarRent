package com.tecnocampus.autocarrent.Persistence;

import com.tecnocampus.autocarrent.Domain.Customer;
import com.tecnocampus.autocarrent.Utilities.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class CustomerRepositoryOld {

    static List<Customer> customers;

    public CustomerRepositoryOld(){
        if(customers == null) customers = new ArrayList<>();
    }

    public void saveCustomer(Customer customer) throws NotFoundException {
        if(customers.stream().anyMatch(x -> x.getId().equals(customer.getId())) ||
            customers.stream().anyMatch(y -> y.getDriverLicense().equals(customer.getDriverLicense()))){
            throw new NotFoundException();
        }else{
            customers.add(customer);
        }
    }

    public Customer getCustomer(String id) throws NotFoundException {
        for (Customer customer : customers) {
            if (customer.getId().equals(id)) {
                return customer;
            }
        }
        throw new NotFoundException();
    }

    public List<Customer> getCustomers(){return customers;}

    public void deleteCustomer(String id) throws NotFoundException {
        try {
            customers.removeIf(x -> x.getId().equals(id));
        }catch(Exception e){
            throw new NotFoundException();
        }
    }

    public void clear(){
        customers.clear();
    }
}

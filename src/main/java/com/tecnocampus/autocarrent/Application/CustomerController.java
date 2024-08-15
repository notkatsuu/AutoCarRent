package com.tecnocampus.autocarrent.Application;

import com.tecnocampus.autocarrent.Application.DTO.CustomerDTO;
import com.tecnocampus.autocarrent.Domain.Customer;
import com.tecnocampus.autocarrent.Persistence.jdbc.ClientCustomerRepository;
import com.tecnocampus.autocarrent.Utilities.NotFoundException;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerController {

    private final ClientCustomerRepository customerRepository;



    public CustomerController(@Qualifier("clientCustomerRepository") ClientCustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerDTO createCustomer(CustomerDTO customerDTO) throws Exception {
        Customer customer = new Customer(customerDTO);
        customerRepository.create(customer);
        return new CustomerDTO(customer);

    }

    public CustomerDTO getCustomer(String id) throws Throwable {
        Customer customer = findById(id);
        return new CustomerDTO(customer);
    }

    public List<CustomerDTO> getCustomers() throws Exception {
        List<CustomerDTO> customerDTOs = new ArrayList<>();
        List<Customer> customers = customerRepository.findAll();
        for (Customer customer : customers) {
            customerDTOs.add(new CustomerDTO(customer));
        }
        return customerDTOs;
    }

    public void deleteCustomer(String id) throws Exception {
        customerRepository.delete(id);
    }

    public void payBill(String id, double amount) throws Throwable {
        Customer customer = findById(id);
        customer.payBill(amount);
        customerRepository.update(customer);

    }

    public void returnDeposit(String id, double amount) throws Throwable {
        Customer customer = findById(id);
        customer.returnDeposit(amount);
        customerRepository.update(customer);
    }

    private Customer findById(String id) throws Throwable{
       return customerRepository.findById(id).orElseThrow(NotFoundException::new);
    }

    public void removeAll(){
        customerRepository.removeAll();
    }
}
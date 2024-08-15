package com.tecnocampus.autocarrent.api;


import com.tecnocampus.autocarrent.Application.CustomerController;
import com.tecnocampus.autocarrent.Application.DTO.CustomerDTO;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/customers")
public class CustomerRestController {

    private final CustomerController customerController;
    
    public CustomerRestController(CustomerController customerController){this.customerController = customerController;}

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CustomerDTO createCustomer(@RequestBody  CustomerDTO customerDTO) throws Exception {
        return customerController.createCustomer(customerDTO);
    }

    @GetMapping
    public List<CustomerDTO> getCustomers() throws Exception {
        return customerController.getCustomers();
    }

    @GetMapping("/{customerId}")
    public CustomerDTO getCustomer(@PathVariable String customerId) throws Throwable {
        return customerController.getCustomer(customerId);
    }

    @DeleteMapping("/{customerId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCustomer(@PathVariable String customerId) throws Exception {
        customerController.deleteCustomer(customerId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void removeAll(){
        customerController.removeAll();
    }
}

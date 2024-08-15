package com.tecnocampus.autocarrent;

import com.tecnocampus.autocarrent.Application.DTO.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CustomerApiTest {

    @Autowired
    private MockMvc mockMvc;
    @Test
    public void testValidCustomerRegistration() throws Exception {
        String customerJson = "{ \"name\": \"John\", \"surname\": \"Doe\", \"address\": \"123 Main St\", \"birthDate\": \"2000-01-01\", \"driverLicense\": \"111111111\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("totalSpent").exists());
    }

    @Test
    public void testInvalidCustomerRegistrationUnderage() throws Exception {
        String customerJson = "{ \"name\": \"John\", \"surname\": \"Doe\", \"address\": \"123 Main St\", \"birthDate\": \"2010-01-01\", \"driverLicense\": \"222222222\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testInvalidCustomerRegistrationMissingFields() throws Exception {
        String customerJson = "{ \"fullName\": \"John Doe\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(customerJson))
                .andExpect(status().is4xxClientError());
    }

    @Test
    public void testReadCustomer() throws Exception {

        String customerJson = "{ \"name\": \"John\", \"surname\": \"Doe\", \"address\": \"123 Main St\", \"birthDate\": \"2000-01-01\", \"driverLicense\": \"333333333\" }";

        String json=mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson)).andReturn().getResponse().getContentAsString();

        CustomerDTO customerDTO = new ObjectMapper().readValue(json, CustomerDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(customerDTO.getId()));
    }

    @Test
    public void testDeleteCustomer() throws Exception {
        String customerJson = "{ \"name\": \"John\", \"surname\": \"Doe\", \"address\": \"123 Main St\", \"birthDate\": \"2000-01-01\", \"driverLicense\": \"444444444\" }";

        String json=mockMvc.perform(MockMvcRequestBuilders.post("/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(customerJson)).andReturn().getResponse().getContentAsString();

        CustomerDTO customerDTO = new ObjectMapper().readValue(json, CustomerDTO.class);


        mockMvc.perform(MockMvcRequestBuilders.delete("/customers/{id}", customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/customers/{id}", customerDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

}

package com.tecnocampus.autocarrent;

import com.tecnocampus.autocarrent.Application.DTO.CarDTO;
import com.tecnocampus.autocarrent.Application.DTO.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class CarApiTest {

    @Autowired
    private MockMvc mockMvc;

    private CustomerDTO customerDTO;
    private CarDTO carDTO;

    // Removed the @BeforeEach as, in our Project, we're checking for repetition of driverLicenses and licensePlates,
    // so we can't have the same customer or car twice

    @Test
    public void testDeleteCar() throws Exception {
        String carJson = "{ \"licensePlate\": \"1234-ABC\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"ECONOMY\" }";
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson)).andReturn().getResponse().getContentAsString();
        carDTO = new ObjectMapper().readValue(json, CarDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.delete("/cars/{id}", carDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        mockMvc.perform(MockMvcRequestBuilders.get("/cars/{id}", carDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }


    @Test
    public void testReadCar() throws Exception {
        String carJson = "{ \"licensePlate\": \"1234-BBC\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"ECONOMY\" }";
        String json = mockMvc.perform(MockMvcRequestBuilders.post("/cars")
                .contentType(MediaType.APPLICATION_JSON)
                .content(carJson)).andReturn().getResponse().getContentAsString();
        carDTO = new ObjectMapper().readValue(json, CarDTO.class);

        mockMvc.perform(MockMvcRequestBuilders.get("/cars/{id}", carDTO.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(carDTO.getId()));
    }


    @Test
    public void testValidCarRegistration() throws Exception {
        String carJson = "{ \"licensePlate\": \"1234-CDR\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"MINI\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("category", new TypeSafeMatcher<String>() {

                    @Override
                    public void describeTo(Description description) {}

                    @Override
                    protected boolean matchesSafely(String item) {
                        return Arrays.asList("Economy", "Luxury", "Family", "Mini").stream()
                                .anyMatch(value -> value.equalsIgnoreCase(item));

                    }
                }));
    }

    @Test
    public void testInvalidCarRegistrationMissingFields() throws Exception {
        String carJson = "{ \"licencePlate\": \"1284-ABC\", \"brand\": \"Toyota\" }";

        mockMvc.perform(MockMvcRequestBuilders.post("/cars")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(carJson))
                .andExpect(status().is4xxClientError());
    }
}



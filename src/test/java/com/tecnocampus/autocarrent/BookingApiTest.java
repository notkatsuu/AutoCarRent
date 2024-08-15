package com.tecnocampus.autocarrent;

import static org.hamcrest.Matchers.is;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tecnocampus.autocarrent.Application.DTO.BookingDTO;
import com.tecnocampus.autocarrent.Application.DTO.CarDTO;
import com.tecnocampus.autocarrent.Application.DTO.CustomerDTO;
import com.fasterxml.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.springframework.http.RequestEntity.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
public class BookingApiTest {
	@Autowired
	private MockMvc mockMvc;
	private CustomerDTO customerDTO;
	private CarDTO carDTO;

	@BeforeEach
	public void setUp() throws Exception {

		String customerJson = "{ \"name\": \"John\", \"surname\": \"Doe\", \"address\": \"123 Main St\", \"birthDate\": \"2000-01-01\", \"driverLicense\": \"111111111\" }";
		String json = mockMvc.perform(MockMvcRequestBuilders.post("/customers")
				.contentType(MediaType.APPLICATION_JSON)
				.content(customerJson)).andReturn().getResponse().getContentAsString();

		customerDTO = new ObjectMapper().readValue(json, CustomerDTO.class);

		String carJson = "{ \"licensePlate\": \"1234-BBC\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"ECONOMY\" }";
		json = mockMvc.perform(MockMvcRequestBuilders.post("/cars")
				.contentType(MediaType.APPLICATION_JSON)
				.content(carJson)).andReturn().getResponse().getContentAsString();
		carDTO = new ObjectMapper().readValue(json, CarDTO.class);
	}

	@AfterEach
	public void tearDown() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.delete("/bookings"));
		mockMvc.perform(MockMvcRequestBuilders.delete("/customers"));
		mockMvc.perform(MockMvcRequestBuilders.delete("/cars"));
	}

	@Test
	public void testValidBooking() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);



		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.id").exists());
	}

	@Test
	public void testInvalidBookingInvalidCar() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		LocalDateTime dateReturn = datePickUp.plusDays(5);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = "";

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testInvalidBookingExceedsMaxDays() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		LocalDateTime dateReturn = datePickUp.plusDays(32); // 21 days, which exceeds the limit

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testBookingWithPastStartDate() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().minusDays(1);
		LocalDateTime dateReturn = datePickUp.plusDays(5);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();


		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testPricingCalculationEconomy() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		System.out.println(datePickUp); // 2021-06-01T00:00:00
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.bill").value(232.5)); // 4 weekdays * 30 + 3 weekend * 30 * 1.25 = 232,5


		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent").value(232.5));

	}

	@Test
	public void testPricingCalculationLuxury() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		System.out.println(datePickUp); // 2021-06-01T00:00:00
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String carJson = "{ \"licensePlate\": \"1234-LUX\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"LUXURY\" }";
		String json = mockMvc.perform(MockMvcRequestBuilders.post("/cars")
				.contentType(MediaType.APPLICATION_JSON)
				.content(carJson)).andReturn().getResponse().getContentAsString();

		carDTO = new ObjectMapper().readValue(json, CarDTO.class);


		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);
		
		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.bill").value(620)); // 4 weekdays * 80 + 3 weekend * 80 * 1.25 = 620


		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent").value(620));
	}

	@Test
	public void testPricingCalculationFamily() throws Exception {

		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		System.out.println(datePickUp); // 2021-06-01T00:00:00
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String carJson = "{ \"licensePlate\": \"1234-FAM\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"FAMILY\" }";
		String json = mockMvc.perform(MockMvcRequestBuilders.post("/cars")
				.contentType(MediaType.APPLICATION_JSON)
				.content(carJson)).andReturn().getResponse().getContentAsString();

		carDTO = new ObjectMapper().readValue(json, CarDTO.class);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.bill").value(387.5)); // 4 weekdays * 50 + 3 weekend * 50 * 1.25 = 387.5

		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent").value(387.5));
	}

	@Test
	public void testPricingCalculationMini() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		System.out.println(datePickUp); // 2021-06-01T00:00:00
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String carJson = "{ \"licensePlate\": \"1234-MIN\", \"brand\": \"Toyota\", \"model\": \"Corolla\", \"category\": \"MINI\" }";
		String json = mockMvc.perform(MockMvcRequestBuilders.post("/cars")
				.contentType(MediaType.APPLICATION_JSON)
				.content(carJson)).andReturn().getResponse().getContentAsString();

		carDTO = new ObjectMapper().readValue(json, CarDTO.class);


		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.bill").value(155)); // 4 weekdays * 20 + 3 weekend * 20 * 1.25 = 155


		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerId))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent").value(155));
	}


	@Test
	public void testCarAvailabilityOverlap() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		LocalDateTime dateReturn = datePickUp.plusDays(5);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);

		// First booking
		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(bookingJson))
				.andExpect(status().isCreated());

		// Attempt overlapping booking
		LocalDateTime overlapDatePickUp = datePickUp.plusDays(3);
		LocalDateTime overlapDateReturn = dateReturn.plusDays(4);

		String overlapDatePickUpStr = overlapDatePickUp.toString();
		String overlapDateReturnStr = overlapDateReturn.toString();

		String overlapCustomerId = customerDTO.getId();
		String overlapCarId = carDTO.getId();

		String overlapBookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				overlapCustomerId,overlapCarId,overlapDatePickUpStr, overlapDateReturnStr);


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
						.contentType(MediaType.APPLICATION_JSON)
						.content(overlapBookingJson))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testBookingCancellationWithinAllowedTimeframe() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusDays(2);
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		String json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(bookingJson)).andReturn().getResponse().getContentAsString();

		BookingDTO bookingDTO = mapper.readValue(json, BookingDTO.class);

		String bookingId = bookingDTO.getId();


		mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{bookingId}",bookingId)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	public void testBookingCancellationLessThan24HoursBeforeStart() throws Exception {
		LocalDateTime datePickUp = LocalDateTime.now().plusHours(23);
		LocalDateTime dateReturn = datePickUp.plusDays(5);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		String json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(bookingJson)).andReturn().getResponse().getContentAsString();

		BookingDTO bookingDTO = mapper.readValue(json, BookingDTO.class);

		String deleteJson = String.format("{ \"bookingId\":\"%s\"}", bookingDTO.getId());


		mockMvc.perform(MockMvcRequestBuilders.put("/bookings/cancel")
						.contentType(MediaType.APPLICATION_JSON)
						.content(deleteJson))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void testCarPickup() throws Exception {
		// Create booking
		LocalDateTime datePickUp = LocalDateTime.now().plusSeconds(2);
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		String json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(bookingJson)).andReturn().getResponse().getContentAsString();

		BookingDTO bookingDTO = mapper.readValue(json, BookingDTO.class);

		String bookingId = bookingDTO.getId();

		Thread.sleep(3000); //Wait till valid pick up time
		// Create pick up car
		json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{bookingId}/pickUp",bookingId)
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		bookingDTO = mapper.readValue(json, BookingDTO.class);

		assertThat(bookingDTO.getDeposit()).isEqualTo(500);
		Assertions.assertEquals("IN_PROGRESS",bookingDTO.getState());

		// Verify the amount expended is updated
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerDTO.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent", is(732.5))); // 232.5 + 500 from LUXURY Deposit
	}


	@Test
	public void testCarReturn() throws Exception {
		// Create booking
		LocalDateTime datePickUp = LocalDateTime.now().plusSeconds(10);
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		// CREATE BOOKING

		String json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(bookingJson)).andReturn().getResponse().getContentAsString();

		BookingDTO bookingDTO = mapper.readValue(json, BookingDTO.class);


		String bookingId = bookingDTO.getId();

		// PICKUP CAR FROM BOOKING
		json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{bookingId}/pickUp",bookingId)
				.contentType(MediaType.APPLICATION_JSON)).
				andReturn().getResponse().getContentAsString();

		bookingDTO = mapper.readValue(json, BookingDTO.class);

		assertThat(bookingDTO.getDeposit()).isEqualTo(500);
		assertThat(bookingDTO.getState().equalsIgnoreCase("IN_PROGRESS")).isTrue();

		// Verify the amount expended is updated
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerDTO.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent", is(732.5))); // 232.5 + 500 from LUXURY Deposit


		// PICKUP DONE. NOW RETURN THE CAR

		// Return car
		String returnJson = String.format("{\"condition\": 20}");
		json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{bookingId}/return",bookingId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(returnJson))
				.andReturn().getResponse().getContentAsString();



		bookingDTO = mapper.readValue(json, BookingDTO.class);

		Assertions.assertEquals("COMPLETED",bookingDTO.getState());

		// Verify the amount expended is updated
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerDTO.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent", is(232.5+500*0.8)));
	}


	@Test
	public void testCarReturnLowerDamage() throws Exception {
		// Create booking
		LocalDateTime datePickUp = LocalDateTime.now().plusSeconds(10);
		LocalDateTime dateReturn = datePickUp.plusDays(7);

		String datePickUpStr = datePickUp.toString();
		String dateReturnStr = dateReturn.toString();

		String customerId = customerDTO.getId();
		String carId = carDTO.getId();

		String bookingJson = String.format("{ \"CustomerId\":\"%s\",\"CarId\":\"%s\", \"pickUpDate\": \"%s\", \"returnDate\": \"%s\" }",
				customerId,carId,datePickUpStr, dateReturnStr);

		ObjectMapper mapper = new ObjectMapper();
		mapper.registerModule(new JavaTimeModule());

		// CREATE BOOKING

		String json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings")
				.contentType(MediaType.APPLICATION_JSON)
				.content(bookingJson)).andReturn().getResponse().getContentAsString();

		BookingDTO bookingDTO = mapper.readValue(json, BookingDTO.class);


		String bookingId = bookingDTO.getId();

		// PICKUP CAR FROM BOOKING
		json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{bookingId}/pickUp",bookingId)
				.contentType(MediaType.APPLICATION_JSON))
				.andReturn().getResponse().getContentAsString();

		bookingDTO = mapper.readValue(json, BookingDTO.class);

		assertThat(bookingDTO.getDeposit()).isEqualTo(500);
		assertThat(bookingDTO.getState().equalsIgnoreCase("IN_PROGRESS")).isTrue();

		// Verify the amount expended is updated
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerDTO.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent", is(732.5))); // 232.5 + 500 from LUXURY Deposit


		// PICKUP DONE. NOW RETURN THE CAR

		// Return car
		String returnJson = String.format("{ \"condition\": 80}");
		json = mockMvc.perform(MockMvcRequestBuilders.post("/bookings/{bookingId}/return",bookingId)
						.contentType(MediaType.APPLICATION_JSON)
						.content(returnJson))
				.andReturn().getResponse().getContentAsString();



		bookingDTO = mapper.readValue(json, BookingDTO.class);

		assertThat(bookingDTO.getState().equalsIgnoreCase("COMPLETED")).isTrue();

		// Verify the amount expended is updated
		mockMvc.perform(MockMvcRequestBuilders.get("/customers/{customerId}", customerDTO.getId()))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalSpent", is(232.5+500*0.2)));
	}
}

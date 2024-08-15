# AutoCarRent

### API RESTful for car rental management system. 
### Connected to a MySQL database using jdbc.

> [!IMPORTANT]
> It is necessary to have a MySQL database running on the local machine with the following configurations:
> - Database name: `autocarrent`
> - User: `root`
> - Password: `root`

## Prerequisites

- Java 11+
- Maven
- MySQL

## Authors

@notkatsuu
@shadowloker

## Features

- User registration by driver's license
- Car registration by license plate
- Booking and reservation management
- Car pick-up and return control
- Car condition control


## Endpoints

### BookingRestController

- **POST /bookings**
    - Creates a new booking.
    - Request Body: JSON containing `CarId`, `CustomerId`, `pickUpDate`, `returnDate`.
    - Response: `BookingDTO`
    - Status: `201 Created`
  

- **GET /bookings**
    - Retrieves a list of all bookings.
    - Response: List of `BookingDTO`
  

- **GET /bookings/{bookingId}**
    - Retrieves a specific booking by `bookingId`.
    - Response: `BookingDTO`
  

- **PUT /bookings/cancel**
    - Cancels a booking.
    - Request Body: JSON containing `bookingId`.
    - Response: `BookingDTO`
    - Status: `204 No Content`


- **PUT /bookings/pickUp**
    - Marks a car as picked up for a booking.
    - Request Body: JSON containing `bookingId`.
    - Response: `BookingDTO`
    - Status: `202 Accepted`


- **PUT /bookings/return**
    - Marks a car as returned for a booking.
    - Request Body: JSON containing `bookingId`, `condition`.
    - Response: `BookingDTO`
    - Status: `202 Accepted`


- **DELETE /bookings**
  - ⚠️ WARNING: This operation will clear the bookings from the database.
  - Removes all bookings.

### CarRestController

- **POST /cars**
    - Creates a new car.
    - Request Body: `CarDTO`
    - Response: `CarDTO`
    - Status: `201 Created`


- **GET /cars**
    - Retrieves a list of all cars.
    - Response: List of `CarDTO`


- **GET /cars/{carId}**
    - Retrieves a specific car by `carId`.
    - Response: `CarDTO`


- **DELETE /cars/{carId}**
    - Deletes a specific car by `carId`.
    - Status: `204 No Content`


- **DELETE /cars**
    - ⚠️ WARNING: This operation will clear the cars from the database.
    - Status: `204 No Content`


### CustomerRestController

- **POST /customers**
    - Creates a new customer.
    - Request Body: `CustomerDTO`
    - Response: `CustomerDTO`
    - Status: `201 Created`


- **GET /customers**
    - Retrieves a list of all customers.
    - Response: List of `CustomerDTO`


- **GET /customers/{customerId}**
    - Retrieves a specific customer by `customerId`.
    - Response: `CustomerDTO`


- **DELETE /customers/{customerId}**
    - Deletes a specific customer by `customerId`.
    - Status: `204 No Content`


- **DELETE /customers**
    - ⚠️ WARNING: This operation will clear the customers from the database.
    - Status: `204 No Content`



## Technologies

- Java
- Spring Boot
- MySQL
- JDBC



package com.tecnocampus.autocarrent.Persistence.jdbc;

import com.tecnocampus.autocarrent.Domain.Customer;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public class ClientCustomerRepository implements IRepository<Customer> {

    private final JdbcClient jdbcClient;

    public ClientCustomerRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Customer> findAll() {
        return jdbcClient.sql("SELECT * FROM Customer").query(Customer.class).list();
    }

    @Override
    public List<Customer> findAll(int limit) {
        return jdbcClient.sql("SELECT * FROM Customer LIMIT :limit")
                .param("limit", limit)
                .query(Customer.class).list();
    }
    @Override
    public Optional<Customer> findById(String id) {
        return jdbcClient.sql("SELECT * FROM Customer WHERE id = ?")
                .param(id)
                .query(Customer.class).optional();
    }


    @Override
    public void create(Customer customer) throws InvalidParamsException {
        int update = jdbcClient.sql("INSERT INTO Customer VALUES (?,?,?,?,?,?,?)")
                .params(List.of(customer.getId(), customer.getName(), customer.getSurname(), customer.getAddress(),
                        customer.getDriverLicense(), customer.getBirthDate(), customer.getTotalSpent()))
                .update();

        if (update != 1) throw new InvalidParamsException();
    }

    @Override
    public void update(Customer customer) throws InvalidParamsException {
        int update = jdbcClient.sql("UPDATE Customer SET name = ?, surname = ?, address = ?, totalSpent = ? WHERE id = ?")
                .params(List.of(customer.getName(), customer.getSurname(), customer.getAddress(), customer.getTotalSpent(), customer.getId())).update();
        if (update != 1) throw new InvalidParamsException();
    }

    @Override
    public void delete(String id) throws InvalidParamsException {
        int update = jdbcClient.sql("DELETE FROM Customer WHERE id = :id")
                .param("id", id)
                .update();
        if (update != 1) throw new InvalidParamsException();
    }

    public void removeAll(){
        jdbcClient.sql("DELETE FROM customer").update();
    }
}

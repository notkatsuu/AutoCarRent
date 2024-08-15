package com.tecnocampus.autocarrent.Persistence.jdbc;

import com.tecnocampus.autocarrent.Domain.Car;
import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

@Repository
public class ClientCarRepository implements IRepository<Car> {

    private final JdbcClient jdbcClient;

    public ClientCarRepository(JdbcClient jdbcClient) {
        this.jdbcClient = jdbcClient;
    }

    @Override
    public List<Car> findAll() {
        return jdbcClient.sql("SELECT id, licensePlate, brand, model, category FROM Car")
                .query(Car.class).list();
    }

    @Override
    public List<Car> findAll(int limit) {
        return jdbcClient.sql("SELECT * FROM Car LIMIT :limit")
                .param("limit", limit)
                .query(Car.class).list();
    }

    @Override
    public Optional<Car> findById(String id) {
        return jdbcClient.sql("SELECT * FROM Car WHERE id = ?")
                .param(id)
                .query(new RowMapper<Car>(){
                    @Override
                    public Car mapRow(ResultSet rs, int rowNum) throws SQLException {
                        return new Car(rs);
                    }
                }).optional();
    }

    @Override
    public void create(Car car) throws InvalidParamsException {
        int update = jdbcClient.sql("INSERT INTO Car VALUES (?,?,?,?,?,?)")
                .params(List.of(car.getId(), car.getBrand(), car.getModel(), car.getCategory(), car.getLicensePlate(), car.getCondition()))
                .update();

        if (update != 1) throw new InvalidParamsException();
    }

    @Override
    public void update(Car car) throws InvalidParamsException {
        int update = jdbcClient.sql("UPDATE car SET carCondition = ? WHERE id = ?")
                .params(List.of(car.getCondition(),car.getId()))
                .update();

        if(update != 1) throw new InvalidParamsException("Cannot update the car condition");
    }

    @Override
    public void delete(String id) throws InvalidParamsException {
        int update = jdbcClient.sql("DELETE FROM Car WHERE id = :id")
                .param("id", id)
                .update();

        if (update != 1) throw new InvalidParamsException();
    }

    public void removeAll(){
        jdbcClient.sql("DELETE FROM car").update();
    }
}

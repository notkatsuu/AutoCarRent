package com.tecnocampus.autocarrent.Persistence.jdbc;

import com.tecnocampus.autocarrent.Utilities.InvalidParamsException;
import com.tecnocampus.autocarrent.Utilities.NotFoundException;

import java.util.List;
import java.util.Optional;

public interface IRepository<E> {

    List<E> findAll();

    List<E> findAll(int limit);

    Optional<E> findById(String id);

    void create(E element) throws InvalidParamsException;

    void update(E element) throws InvalidParamsException;

    void delete(String id) throws InvalidParamsException;
}

package com.svalero.transportFleet.repository;

import com.svalero.transportFleet.domain.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends CrudRepository<User, Long> {
    List<User> findAll();
    List<User> findByUsername(String username);
    List<User> findUserByName(String name);
    List<User> findByBirthDateBefore(java.time.LocalDate date);
    List<User> findByActiveFalse();
}

package com.svalero.transportFleet.repository;

import com.svalero.transportFleet.domain.Station;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StationRepository extends CrudRepository<Station, Long> {
    List<Station> findAll();
    List<Station> findByCategory(String category);
    List<Station> findByNameContainingIgnoreCase(String name);
    List<Station> findByDisabledAccessTrue();
    List<Station> findByPostalCode(int postalCode);
}

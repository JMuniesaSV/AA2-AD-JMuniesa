package com.svalero.transportFleet.repository;

import com.svalero.transportFleet.domain.Route;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RouteRepository extends CrudRepository<Route, Long> {
    List<Route> findAll();
    List<Route> findByCategory(String category);
    List<Route> findByStation_Name(String stationName);
    List<Route> findByPriceLessThanEqualOrderByPriceAsc(Float price);
}

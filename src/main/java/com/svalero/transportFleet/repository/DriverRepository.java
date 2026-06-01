package com.svalero.transportFleet.repository;

import com.svalero.transportFleet.domain.Driver;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DriverRepository extends CrudRepository<Driver, Long> {
    List<Driver> findAll();
    List<Driver> findByType(String type);
    List<Driver> findByLicenseCategory(String licenseCategory);
    List<Driver> findByActiveTrue();
    List<Driver> findAllByOrderByExperienceDesc();
}

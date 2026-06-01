package com.svalero.transportFleet.service;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.dto.DriverOutDto;
import com.svalero.transportFleet.exception.DriverNotFoundException;
import com.svalero.transportFleet.repository.DriverRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DriverService {

    @Autowired
    private DriverRepository driverRepository;
    @Autowired
    private ModelMapper modelMapper;

    public Driver add(Driver driver) {
        return driverRepository.save(driver);
    }

    public void delete(long id) throws DriverNotFoundException {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(DriverNotFoundException::new);
        driverRepository.delete(driver);
    }

    public List<DriverOutDto> findAll(String type, Boolean active, Boolean orderByExperience) {
        List<Driver> allDrivers;

        if (type != null && !type.isEmpty()) {
            allDrivers = driverRepository.findByType(type);
        } else if (active != null && active) {
            allDrivers = driverRepository.findByActiveTrue();
        } else if (orderByExperience != null && orderByExperience) {
            allDrivers = driverRepository.findAllByOrderByExperienceDesc();
        } else {
            allDrivers = driverRepository.findAll();
        }

        return modelMapper.map(allDrivers, new TypeToken<List<DriverOutDto>>() {}.getType());
    }

    public Driver findDriverById(long id) throws DriverNotFoundException {
        return driverRepository.findById(id)
                .orElseThrow(DriverNotFoundException::new);
    }

    public List<Driver> findAllDriversById(List<Long> ids) {
        return (List<Driver>) driverRepository.findAllById(ids);
    }

    public Driver modify(long id, Driver driver) throws DriverNotFoundException {
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(DriverNotFoundException::new);

        modelMapper.map(driver, existingDriver);
        existingDriver.setId(id);

        return driverRepository.save(existingDriver);
    }
}

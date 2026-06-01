package com.svalero.transportFleet.controller;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.dto.DriverOutDto;
import com.svalero.transportFleet.exception.DriverNotFoundException;
import com.svalero.transportFleet.exception.ErrorResponse;
import com.svalero.transportFleet.service.DriverService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class DriverController {

    @Autowired
    private DriverService driverService;

    @GetMapping("/drivers")
    public ResponseEntity<List<DriverOutDto>> getAll(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "orderByExperience", required = false) Boolean orderByExperience) {

        List<DriverOutDto> allDriversOutDto = driverService.findAll(type, active, orderByExperience);
        return ResponseEntity.ok(allDriversOutDto);
    }

    @GetMapping("/drivers/{id}")
    public ResponseEntity<Driver> getDriverById(@PathVariable long id) throws DriverNotFoundException {
        Driver driver = driverService.findDriverById(id);
        return ResponseEntity.ok(driver);
    }

    @PostMapping("/drivers")
    public ResponseEntity<Driver> addDriver(@Valid @RequestBody Driver driver) {
        Driver newDriver = driverService.add(driver);
        return new ResponseEntity<>(newDriver, HttpStatus.CREATED);
    }

    @PutMapping("/drivers/{id}")
    public ResponseEntity<Driver> modifyDriver(@PathVariable long id, @RequestBody Driver driver) throws DriverNotFoundException {
        Driver newDriver = driverService.modify(id, driver);
        return ResponseEntity.ok(newDriver);
    }

    @DeleteMapping("/drivers/{id}")
    public ResponseEntity<Void> deleteDriver(@PathVariable long id) throws DriverNotFoundException {
        driverService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(DriverNotFoundException dnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The driver does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });
        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        ErrorResponse errorResponse = ErrorResponse.internalServerError();
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

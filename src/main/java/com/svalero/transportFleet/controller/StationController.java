package com.svalero.transportFleet.controller;

import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.StationOutDto;
import com.svalero.transportFleet.exception.ErrorResponse;
import com.svalero.transportFleet.exception.StationNotFoundException;
import com.svalero.transportFleet.service.StationService;
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
public class StationController {

    @Autowired
    private StationService stationService;

    @GetMapping("/stations")
    public ResponseEntity<List<StationOutDto>> getAll(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "disabledAccess", required = false) Boolean disabledAccess,
            @RequestParam(value = "postalCode", required = false) Integer postalCode) {

        List<StationOutDto> allStationsOutDto = stationService.findAll(category, disabledAccess, postalCode);
        return ResponseEntity.ok(allStationsOutDto);
    }

    @GetMapping("/stations/{id}")
    public ResponseEntity<Station> getStationById(@PathVariable long id) throws StationNotFoundException {
        Station station = stationService.findById(id);
        return ResponseEntity.ok(station);
    }

    @PostMapping("/stations")
    public ResponseEntity<Station> addStation(@Valid @RequestBody Station station) {
        Station newStation = stationService.add(station);
        return new ResponseEntity<>(newStation, HttpStatus.CREATED);
    }

    @PutMapping("/stations/{id}")
    public ResponseEntity<Station> modifyStation(@PathVariable long id, @RequestBody Station station) throws StationNotFoundException {
        Station newStation = stationService.modify(id, station);
        return ResponseEntity.ok(newStation);
    }

    @DeleteMapping("/stations/{id}")
    public ResponseEntity<Void> deleteStation(@PathVariable long id) throws StationNotFoundException {
        stationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(StationNotFoundException snfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The station does not exist");
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

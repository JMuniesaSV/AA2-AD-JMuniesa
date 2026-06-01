package com.svalero.transportFleet.controller;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.Station;
import com.svalero.transportFleet.dto.RouteInDto;
import com.svalero.transportFleet.dto.RouteModifyInDto;
import com.svalero.transportFleet.dto.RouteOutDto;
import com.svalero.transportFleet.exception.ErrorResponse;
import com.svalero.transportFleet.exception.RouteNotFoundException;
import com.svalero.transportFleet.exception.StationNotFoundException;
import com.svalero.transportFleet.service.DriverService;
import com.svalero.transportFleet.service.RouteService;
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
public class RouteController {

    @Autowired
    private RouteService routeService;
    @Autowired
    private StationService stationService;
    @Autowired
    private DriverService driverService;

    @GetMapping("/routes")
    public ResponseEntity<List<RouteOutDto>> getAll(
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "stationName", required = false) String stationName,
            @RequestParam(value = "price", required = false) Float price) {

        List<RouteOutDto> allRoutesOutDto = routeService.findAll(category, stationName, price);
        return ResponseEntity.ok(allRoutesOutDto);
    }

    @GetMapping("/routes/{id}")
    public ResponseEntity<Route> getRouteById(@PathVariable long id) throws RouteNotFoundException {
        Route route = routeService.findById(id);
        return ResponseEntity.ok(route);
    }

    @PostMapping("/routes")
    public ResponseEntity<Route> addRoute(@Valid @RequestBody RouteInDto routeInDto) throws StationNotFoundException {
        Station station = null;
        if (routeInDto.getStationId() != 0) {
            station = stationService.findById(routeInDto.getStationId());
        }

        List<Driver> drivers = null;
        if (routeInDto.getDriversIds() != null && !routeInDto.getDriversIds().isEmpty()) {
            drivers = driverService.findAllDriversById(routeInDto.getDriversIds());
        }

        Route newRoute = routeService.add(station, routeInDto, drivers);
        return new ResponseEntity<>(newRoute, HttpStatus.CREATED);
    }

    @PutMapping("/routes/{id}")
    public ResponseEntity<Route> modifyRoute(@PathVariable long id, @RequestBody RouteModifyInDto routeModifyInDto)
            throws RouteNotFoundException, StationNotFoundException {

        Station station = null;
        if (routeModifyInDto.getStationId() != 0) {
            station = stationService.findById(routeModifyInDto.getStationId());
        }

        List<Driver> drivers = null;
        if (routeModifyInDto.getDriversIds() != null && !routeModifyInDto.getDriversIds().isEmpty()) {
            drivers = driverService.findAllDriversById(routeModifyInDto.getDriversIds());
        }

        Route newRoute = routeService.modify(id, routeModifyInDto, station, drivers);
        return ResponseEntity.ok(newRoute);
    }

    @DeleteMapping("/routes/{id}")
    public ResponseEntity<Void> deleteRoute(@PathVariable long id) throws RouteNotFoundException {
        routeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RouteNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The route does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(StationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(StationNotFoundException snfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(404, "not-found", "The station does not exist");
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
        ErrorResponse errorResponse = ErrorResponse.generalError(500, "internal-error", e.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}

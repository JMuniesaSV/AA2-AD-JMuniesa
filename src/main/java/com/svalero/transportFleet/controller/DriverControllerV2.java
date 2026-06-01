package com.svalero.transportFleet.controller;

import com.svalero.transportFleet.domain.Driver;
import com.svalero.transportFleet.dto.DriverInV2Dto;
import com.svalero.transportFleet.dto.DriverOutDto;
import com.svalero.transportFleet.dto.DriverSummaryV2Dto;
import com.svalero.transportFleet.exception.DriverNotFoundException;
import com.svalero.transportFleet.exception.ErrorResponse;
import com.svalero.transportFleet.service.DriverService;
import jakarta.validation.Valid;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/v2/drivers")
public class DriverControllerV2 {

    @Autowired
    private DriverService driverService;

    @Autowired
    private ModelMapper modelMapper;

    // GET modificado: Devuelve un DriverSummaryV2Dto en lugar de DriverOutDto, reduciendo la cantidad de datos
    @GetMapping
    public ResponseEntity<List<DriverSummaryV2Dto>> getAll(
            @RequestParam(value = "type", required = false) String type,
            @RequestParam(value = "active", required = false) Boolean active,
            @RequestParam(value = "orderByExperience", required = false) Boolean orderByExperience) {

        List<DriverOutDto> allDriversOutDto = driverService.findAll(type, active, orderByExperience);
        List<DriverSummaryV2Dto> summaryList = allDriversOutDto.stream()
                .map(d -> {
                    DriverSummaryV2Dto summary = new DriverSummaryV2Dto();
                    summary.setId(d.getId());
                    summary.setFullName(d.getName() + " " + d.getSurname());
                    summary.setLicenseCategory(d.getLicenseCategory());
                    summary.setActive(d.isActive());
                    return summary;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(summaryList);
    }

    // POST modificado: Utiliza un DriverInV2Dto específico para entrada con validaciones distintas y retorna un resumen envuelto
    @PostMapping
    public ResponseEntity<Map<String, Object>> addDriver(@Valid @RequestBody DriverInV2Dto driverInV2Dto) {
        Driver driver = modelMapper.map(driverInV2Dto, Driver.class);
        Driver newDriver = driverService.add(driver);

        DriverSummaryV2Dto summary = new DriverSummaryV2Dto();
        summary.setId(newDriver.getId());
        summary.setFullName(newDriver.getName() + " " + newDriver.getSurname());
        summary.setLicenseCategory(newDriver.getLicenseCategory());
        summary.setActive(newDriver.isActive());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Driver created successfully in V2");
        response.put("data", summary);

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // PUT modificado: Recibe DriverInV2Dto en lugar del dominio entero y devuelve respuesta estandarizada
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> modifyDriver(@PathVariable long id, @Valid @RequestBody DriverInV2Dto driverInV2Dto) throws DriverNotFoundException {
        Driver driver = modelMapper.map(driverInV2Dto, Driver.class);
        Driver newDriver = driverService.modify(id, driver);

        DriverSummaryV2Dto summary = new DriverSummaryV2Dto();
        summary.setId(newDriver.getId());
        summary.setFullName(newDriver.getName() + " " + newDriver.getSurname());
        summary.setLicenseCategory(newDriver.getLicenseCategory());
        summary.setActive(newDriver.isActive());

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Driver updated successfully in V2");
        response.put("data", summary);

        return ResponseEntity.ok(response);
    }

    // DELETE modificado: En lugar de 204 No Content, devuelve un JSON de confirmación explícito 200 OK
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteDriver(@PathVariable long id) throws DriverNotFoundException {
        driverService.delete(id);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Driver physically deleted successfully");
        response.put("deletedId", String.valueOf(id));
        return ResponseEntity.ok(response);
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

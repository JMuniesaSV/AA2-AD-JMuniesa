package com.svalero.transportFleet.controller;

import com.svalero.transportFleet.domain.Feedback;
import com.svalero.transportFleet.domain.Route;
import com.svalero.transportFleet.domain.User;
import com.svalero.transportFleet.dto.FeedbackInDto;
import com.svalero.transportFleet.dto.FeedbackModifyInDto;
import com.svalero.transportFleet.dto.FeedbackOutDto;
import com.svalero.transportFleet.exception.ErrorResponse;
import com.svalero.transportFleet.exception.FeedbackNotFoundException;
import com.svalero.transportFleet.exception.RouteNotFoundException;
import com.svalero.transportFleet.exception.UserNotFoundException;
import com.svalero.transportFleet.service.FeedbackService;
import com.svalero.transportFleet.service.RouteService;
import com.svalero.transportFleet.service.UserService;
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
public class FeedbackController {

    @Autowired
    private FeedbackService feedbackService;
    @Autowired
    private RouteService routeService;
    @Autowired
    private UserService userService;

    @GetMapping("/feedbacks")
    public ResponseEntity<List<FeedbackOutDto>> getAll(
            @RequestParam(value = "username", required = false) String username,
            @RequestParam(value = "routeName", required = false) String routeName,
            @RequestParam(value = "rating", required = false) Float rating) {

        List<FeedbackOutDto> allFeedbacksOutDto = feedbackService.findAll(username, routeName, rating);
        return ResponseEntity.ok(allFeedbacksOutDto);
    }

    @GetMapping("/feedbacks/{id}")
    public ResponseEntity<Feedback> getFeedbackById(@PathVariable long id) throws FeedbackNotFoundException {
        Feedback feedback = feedbackService.getFeedbackById(id);
        return ResponseEntity.ok(feedback);
    }

    @PostMapping("/feedbacks")
    public ResponseEntity<Feedback> addFeedback(@Valid @RequestBody FeedbackInDto feedbackInDto)
            throws RouteNotFoundException, UserNotFoundException {

        Route route = routeService.findById(feedbackInDto.getRouteId());
        User user = userService.findUserById(feedbackInDto.getUserId());

        Feedback newFeedback = feedbackService.add(feedbackInDto, route, user);
        return new ResponseEntity<>(newFeedback, HttpStatus.CREATED);
    }

    @PutMapping("/feedbacks/{id}")
    public ResponseEntity<Feedback> modifyFeedback(@PathVariable long id, @RequestBody FeedbackModifyInDto feedbackModifyInDto)
            throws FeedbackNotFoundException, RouteNotFoundException, UserNotFoundException {

        Route route = routeService.findById(feedbackModifyInDto.getRouteId());
        User user = userService.findUserById(feedbackModifyInDto.getUserId());

        Feedback newFeedback = feedbackService.modify(id, feedbackModifyInDto, route, user);
        return ResponseEntity.ok(newFeedback);
    }

    @DeleteMapping("/feedbacks/{id}")
    public ResponseEntity<Void> deleteFeedback(@PathVariable long id) throws FeedbackNotFoundException {
        feedbackService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @ExceptionHandler(FeedbackNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(FeedbackNotFoundException fnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The feedback does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(RouteNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RouteNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The route does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The user does not exist");
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

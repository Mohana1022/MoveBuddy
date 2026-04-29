package com.alpha.MoveBuddy.exception;

import com.alpha.MoveBuddy.ResponseStructure;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // ---- Domain Exceptions ----

    @ExceptionHandler(DriverNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleDriverNotFound(DriverNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Driver not found", ex.getMessage());
    }

    @ExceptionHandler(CustomerNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleCustomerNotFound(CustomerNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Customer with given mobile number not found", ex.getMessage());
    }

    @ExceptionHandler(MobileAlreadyRegisteredException.class)
    public ResponseEntity<ResponseStructure<String>> handleMobileAlreadyRegistered(MobileAlreadyRegisteredException ex) {
        return buildResponse(HttpStatus.CONFLICT, "Mobile number already registered", ex.getMessage());
    }

    @ExceptionHandler(VehicleNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleVehicleNotFound(VehicleNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Vehicle not found", ex.getMessage());
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleBookingNotFound(BookingNotFoundException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "Booking not found", ex.getMessage());
    }

    @ExceptionHandler(NoCurrentBookingException.class)
    public ResponseEntity<ResponseStructure<String>> handleNoCurrentBooking(NoCurrentBookingException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, "No active or past booking found", "No Bookings");
    }

    @ExceptionHandler(InvalidLocationException.class)
    public ResponseEntity<ResponseStructure<String>> handleInvalidLocation(InvalidLocationException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, "Invalid or unrecognized location provided", ex.getMessage());
    }

    @ExceptionHandler(CoordinatesNotFoundException.class)
    public ResponseEntity<ResponseStructure<String>> handleCoordinatesError(CoordinatesNotFoundException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Failed to resolve coordinates", ex.getMessage());
    }

    @ExceptionHandler(DistanceCalculationFailedException.class)
    public ResponseEntity<ResponseStructure<String>> handleDistanceError(DistanceCalculationFailedException ex) {
        return buildResponse(HttpStatus.SERVICE_UNAVAILABLE, "Failed to calculate distance", ex.getMessage());
    }

    // ---- Security Exceptions ----

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ResponseStructure<String>> handleBadCredentials(BadCredentialsException ex) {
        return buildResponse(HttpStatus.UNAUTHORIZED, "Invalid mobile number or password", "UNAUTHORIZED");
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ResponseStructure<String>> handleAccessDenied(AccessDeniedException ex) {
        return buildResponse(HttpStatus.FORBIDDEN, "You do not have permission to access this resource", "FORBIDDEN");
    }

    // ---- Bean Validation Exceptions ----

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ResponseStructure<Map<String, String>>> handleValidation(
            MethodArgumentNotValidException ex) {

        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        ResponseStructure<Map<String, String>> response = new ResponseStructure<>();
        response.setStatuscode(HttpStatus.BAD_REQUEST.value());
        response.setMessage("Validation failed");
        response.setData(errors);

        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // ---- Generic Fallback ----

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ResponseStructure<String>> handleRuntimeException(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), "ERROR");
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ResponseStructure<String>> handleGenericException(Exception ex) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred", ex.getMessage());
    }

    // ---- Helper ----

    private ResponseEntity<ResponseStructure<String>> buildResponse(
            HttpStatus status, String message, String data) {

        ResponseStructure<String> response = new ResponseStructure<>();
        response.setStatuscode(status.value());
        response.setMessage(message);
        response.setData(data);
        return new ResponseEntity<>(response, status);
    }
}

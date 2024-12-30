package com.sky.api.weatherapiservice.Exception;

import com.sky.api.weatherapiservice.DTO.ErrorDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.Date;
import java.util.List;
import java.util.Set;

@ControllerAdvice
@Configuration
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Generic Exception Handler
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(HttpServletRequest request, Exception exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.INTERNAL_SERVER_ERROR.value())
                .path(request.getServletPath())
                .build();

        // Add the exception message to the errors
        errorDTO.addErrors(exception.getMessage() != null ? exception.getMessage() : "Internal Server Error");

        return new ResponseEntity<>(errorDTO, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // Validation Exception Handler (Override Default)
    @Override
    public ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request) {

        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request instanceof ServletWebRequest ?
                        ((ServletWebRequest) request).getRequest().getServletPath() : "Unknown Path")
                .build();

        // Extract field-level errors
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        fieldErrors.forEach(fieldError ->{
             errorDTO.addErrors(fieldError.getDefaultMessage());
        });


        return new ResponseEntity<>(errorDTO, headers, status);
    }

    // Specific Handler for IllegalArgumentException
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgumentException(HttpServletRequest request, IllegalArgumentException exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getServletPath())
                .build();

        errorDTO.addErrors(exception.getMessage());
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    // LocationNotFound Exception Handler
    @ExceptionHandler(LocationNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleLocationNotFoundException(HttpServletRequest request, LocationNotFoundException exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.NOT_FOUND.value())
                .path(request.getServletPath())
                .build();

        // Add the exception message to the errors
        errorDTO.addErrors(exception.getMessage() != null ? exception.getMessage() : "LocationNotFoundException Error");

        return new ResponseEntity<>(errorDTO, HttpStatus.NOT_FOUND);
    }

    // GeoLocation Exception Handler
    @ExceptionHandler(GeoLocationException.class)
    public ResponseEntity<ErrorDTO> handleGeoLocationNotFoundException(HttpServletRequest request, GeoLocationException exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getServletPath())
                .build();

        // Add the exception message to the errors
        errorDTO.addErrors(exception.getMessage() != null ? exception.getMessage() : "GeoLocationException Error");

        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HourlyWeatherForecastException.class)
    public ResponseEntity<ErrorDTO> handleHourlyWeatherForecastException(HttpServletRequest request, HourlyWeatherForecastException exception) {
        ErrorDTO errorDTO = ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getServletPath())
                .build();

        // Add the exception message to the errors
        errorDTO.addErrors(exception.getMessage() != null ? exception.getMessage() : "You have encountered an hourlyweatherforecastException.");
        return new ResponseEntity<>(errorDTO, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ErrorDTO handleConstraintViolationException(ConstraintViolationException exception,HttpServletRequest request) {
        ErrorDTO error=ErrorDTO.builder()
                .timestamp(new Date())
                .status(HttpStatus.BAD_REQUEST.value())
                .path(request.getServletPath())
                .build();
        Set<ConstraintViolation<?>> constraintViolationSet=exception.getConstraintViolations();
        constraintViolationSet.forEach( constraintViolation -> {
            error.addErrors(constraintViolation.getPropertyPath()+": "+constraintViolation.getMessage());
        });
        return error;
    }
}

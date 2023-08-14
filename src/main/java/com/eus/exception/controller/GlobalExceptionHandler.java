package com.eus.exception.controller;

import com.eus.dto.CustomResponse;
import com.eus.dto.ValidationErrorResponse;
import com.eus.exception.ResourceNotFoundException;
import com.eus.exception.ServiceUnavailableException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

import static com.eus.dto.CustomResponse.error;
import static com.eus.enums.ErrorType.FRAMEWORK_VALIDATION_ERROR;
import static com.eus.enums.ErrorType.REGULAR;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<CustomResponse<ValidationErrorResponse>> handleValidationException(Exception ex) {
        List<String> errorMessages = ex instanceof MethodArgumentNotValidException ?
                ((MethodArgumentNotValidException) ex).getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList()) :
                ((BindException) ex).getBindingResult().getFieldErrors().stream()
                        .map(FieldError::getDefaultMessage)
                        .collect(Collectors.toList());

        ValidationErrorResponse validationErrorResponse = new ValidationErrorResponse(errorMessages);
        CustomResponse<ValidationErrorResponse> response = error(FRAMEWORK_VALIDATION_ERROR, validationErrorResponse);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({ResourceNotFoundException.class})
    public ResponseEntity<CustomResponse<Object>> handleResourceNotFoundException(Exception ex) {
        CustomResponse<Object> response = error(REGULAR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler({ServiceUnavailableException.class})
    public ResponseEntity<CustomResponse<Object>> handleServiceUnavailableException(Exception ex) {
        CustomResponse<Object> response = error(REGULAR, ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}

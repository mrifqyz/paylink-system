package net.ryzen.paylinksystem.exception;

import lombok.extern.slf4j.Slf4j;
import net.ryzen.paylinksystem.dto.ErrorResponseDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class CustomExceptionsHandler {
    @Value("${limit.log.error:20}")
    private Integer limitLogError;

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<Object> handleAllExceptions(Exception ex) {
        log.error("Handle Exception => {}\n{} ", ex.getMessage(), this.getLogException(ex));

        return setResponse(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_SERVER_ERROR");
    }

    @ExceptionHandler(RestClientException.class)
    public final ResponseEntity<Object> handleRestClientException(RestClientException ex) {
        log.error("Handle RestClientException => {}\n{} ", ex.getMessage(), this.getLogException(ex));

        return setResponse(ex.getStatus(), ex.getMessage());
    }

    private String getLogException(Exception e) {
        return Arrays.stream(e.getStackTrace()).limit(limitLogError).map(StackTraceElement::toString).collect(Collectors.joining("\n"));
    }

    private ResponseEntity<Object> setResponse(HttpStatus httpStatus, String messageException) {

        return ResponseEntity
                .status(httpStatus)
                .body(ErrorResponseDTO.builder()
                        .errorMessage(messageException)
                        .build());
    }
}

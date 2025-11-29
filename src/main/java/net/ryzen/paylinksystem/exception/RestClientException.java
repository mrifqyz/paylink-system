package net.ryzen.paylinksystem.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
@Getter
@Setter
public class RestClientException extends RuntimeException{
    private String message;
    private HttpStatus status;

    public RestClientException(String message, HttpStatus status) {
        this.status = status;
        this.message = message;
    }

    public RestClientException(String message) {
        this.message = message;
    }
}

package net.ryzen.paylinksystem.exception;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class InvalidDataException extends RuntimeException{
    private String message;

    public InvalidDataException(String message) {
        this.message = message;
    }
}

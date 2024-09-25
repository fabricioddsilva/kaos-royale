package com.kaos.mongoroyale.resources.exceptions;

import com.kaos.mongoroyale.services.exceptions.ConnectionNotSucessfulException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.Instant;

@ControllerAdvice
public class ResourceExceptionHandler {

    @ExceptionHandler(ConnectionNotSucessfulException.class)
    public ResponseEntity<StandardError> objectNotFoundException(ConnectionNotSucessfulException e, HttpServletRequest request){

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Não foi possível se conecta a API do Clash Royale, por favor verifique a chave de autorização",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }
}

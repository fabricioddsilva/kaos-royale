package com.kaos.mongoroyale.resources.exceptions;

import com.kaos.mongoroyale.services.exceptions.ObjectNotFoundException;
import com.kaos.mongoroyale.services.exceptions.ConnectionNotSucessfulException;
import com.kaos.mongoroyale.services.exceptions.LoadBattlesException;
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
                "Não foi possível coletar os dados desse jogador",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(LoadBattlesException.class)
    public ResponseEntity<StandardError> loadBattlesException(LoadBattlesException e, HttpServletRequest request){

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Não foi possível coletar os dados de batalhar deste jogador",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }

    @ExceptionHandler(ObjectNotFoundException.class)
    public ResponseEntity<StandardError> cardNotFoundException(ObjectNotFoundException e, HttpServletRequest request){

        HttpStatus status = HttpStatus.NOT_FOUND;

        StandardError err = new StandardError(
                Instant.now(),
                status.value(),
                "Não foi possível encontrar registros",
                e.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(err);
    }


}

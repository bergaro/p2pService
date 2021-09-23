package ru.netology.p2p.exceptions;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    private final Logger logger = LogManager.getLogger(this.getClass());

    @ExceptionHandler(ErrorTransfer.class)
    public ResponseEntity<String> ErrorTransferHandler(ErrorTransfer ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(ErrorInputData.class)
    public ResponseEntity<String> ErrorInputDataHandler(ErrorInputData ex) {
        logger.error(ex.getMessage());
        return new ResponseEntity<>(ex.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

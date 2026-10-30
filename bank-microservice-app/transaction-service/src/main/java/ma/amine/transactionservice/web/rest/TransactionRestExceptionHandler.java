package ma.amine.transactionservice.web.rest;

import ma.amine.transactionservice.service.exception.AccountNotActiveException;
import ma.amine.transactionservice.service.exception.AccountNotFoundException;
import ma.amine.transactionservice.service.exception.CurrencyMismatchException;
import ma.amine.transactionservice.service.exception.InsufficientBalanceException;
import ma.amine.transactionservice.service.exception.InvalidTransactionException;
import ma.amine.transactionservice.service.exception.TransactionAmountExceededException;
import ma.amine.transactionservice.service.exception.TransactionNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Map;

@RestControllerAdvice(basePackages = "ma.amine.transactionservice.web.rest")
public class TransactionRestExceptionHandler {

    @ExceptionHandler({TransactionNotFoundException.class, AccountNotFoundException.class})
    public ResponseEntity<Map<String, Object>> handleNotFound(RuntimeException ex) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage());
    }

    @ExceptionHandler({
            InvalidTransactionException.class,
            TransactionAmountExceededException.class,
            AccountNotActiveException.class,
            CurrencyMismatchException.class,
            InsufficientBalanceException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<Map<String, Object>> handleBadRequest(RuntimeException ex) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage());
    }

    private ResponseEntity<Map<String, Object>> buildResponse(HttpStatus status, String message) {
        return ResponseEntity.status(status).body(Map.of(
                "timestamp", Instant.now().toString(),
                "status", status.value(),
                "error", status.getReasonPhrase(),
                "message", message
        ));
    }
}

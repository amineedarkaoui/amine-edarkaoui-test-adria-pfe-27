package ma.amine.transactionservice.service.exception;

public class TransactionAmountExceededException extends RuntimeException {
    public TransactionAmountExceededException(String message) {
        super(message);
    }
}

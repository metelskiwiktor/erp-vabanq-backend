package pl.vabanq.erp.domain.error;

public class DomainException extends RuntimeException {
    private final ErrorCode errorCode;

    public DomainException(ErrorCode errorCode, String... parameters) {
        super(String.format(errorCode.getMessage(), parameters));
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }
}

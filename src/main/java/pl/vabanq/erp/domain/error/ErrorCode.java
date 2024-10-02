package pl.vabanq.erp.domain.error;

public enum ErrorCode {
    INVALID_VALUE("Pole: '%s' ma nieprawidłową wartość: '%s'"),
    NOT_FOUND("Nie znaleziono");

    private final String message;

    ErrorCode(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }
}

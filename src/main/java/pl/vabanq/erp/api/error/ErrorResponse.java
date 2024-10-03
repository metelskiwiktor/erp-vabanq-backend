package pl.vabanq.erp.api.error;

public record ErrorResponse(
        String errorCode,
        String message
) {}

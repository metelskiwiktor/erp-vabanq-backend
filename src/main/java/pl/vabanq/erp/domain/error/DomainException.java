package pl.vabanq.erp.domain.error;

import java.util.Collections;
import java.util.List;

public class DomainException extends RuntimeException {
    private final List<ErrorCode> errorCodes;

    public DomainException(ErrorCode errorCode, String... parameters) {
        super(String.format(errorCode.getMessage(), parameters));
        this.errorCodes = Collections.singletonList(errorCode);
    }

    public List<ErrorCode> getErrorCodes() {
        return errorCodes;
    }
}

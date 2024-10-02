package pl.vabanq.erp.domain.error;

import org.springframework.http.HttpStatus;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class DomainException extends RuntimeException {
    private final List<ErrorCode> errorCodes;
    private final HttpStatus httpStatus;

    public DomainException(List<ErrorCode> errorCodes, HttpStatus httpStatus) {
        super(errorCodes.stream()
                .map(ErrorCode::getMessage)
                .collect(Collectors.joining(", ")));
        this.errorCodes = errorCodes;
        this.httpStatus = httpStatus;
    }

    public DomainException(ErrorCode errorCode, HttpStatus httpStatus) {
        super(errorCode.getMessage());
        this.errorCodes = Collections.singletonList(errorCode);
        this.httpStatus = httpStatus;
    }

    public DomainException(ErrorCode errorCode, String... parameters) {
        super(String.format(errorCode.getMessage(), parameters));
        this.errorCodes = Collections.singletonList(errorCode);
        this.httpStatus = HttpStatus.BAD_REQUEST;
    }

    public List<ErrorCode> getErrorCodes() {
        return errorCodes;
    }

    public HttpStatus httpStatus() {
        return httpStatus;
    }
}

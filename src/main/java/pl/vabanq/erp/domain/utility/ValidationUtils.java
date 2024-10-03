package pl.vabanq.erp.domain.utility;

import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;

import java.math.BigDecimal;

public class ValidationUtils {

    public static void validateName(String name) {
        if (!isNameValid(name)) {
            throw new DomainException(ErrorCode.INVALID_VALUE, "name", name);
        }
    }

    public static boolean isNameValid(String name) {
        return name != null && name.trim().length() >= 3;
    }

    public static void validatePrice(String unit, String price) {
        if (!isPriceValid(price)) {
            throw new DomainException(ErrorCode.INVALID_VALUE, unit, price);
        }
    }

    public static boolean isPriceValid(String price) {
        if (price == null) {
            return false;
        }
        try {
            BigDecimal bd = new BigDecimal(price);
            return bd.compareTo(BigDecimal.ZERO) > 0 && bd.scale() <= 2;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public static boolean isDescriptionValid(String description) {
        return description != null && description.trim().length() >= 10;
    }
}

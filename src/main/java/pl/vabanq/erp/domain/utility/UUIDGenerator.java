package pl.vabanq.erp.domain.utility;

import java.util.UUID;

public class UUIDGenerator {
    public static String generateUUID() {
        return UUID.randomUUID().toString();
    }
}

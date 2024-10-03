package pl.vabanq.erp.api.response;

import java.math.BigDecimal;

public record FilamentAccessoryResponse(
        String id,
        String name,
        String producer,
        String filamentType,
        double printTemperature,
        double deskTemperature,
        BigDecimal pricePerKg,
        String color,
        String description,
        double quantity
) {}
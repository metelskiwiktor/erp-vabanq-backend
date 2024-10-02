package pl.vabanq.erp.domain.accessory.model;


import pl.vabanq.erp.domain.Identifiable;

import java.math.BigDecimal;

public record FilamentAccessory(
        String id,
        String name,
        String producer,
        String filamentType,
        double printTemperature,
        double deskTemperature,
        BigDecimal pricePerKg,
        String color,
        String description
) implements Identifiable {
}

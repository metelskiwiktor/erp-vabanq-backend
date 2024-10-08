package pl.vabanq.erp.domain.products.accessory.model;

import pl.vabanq.erp.domain.Identifiable;

import java.math.BigDecimal;

public record FastenersAccessory(
    String id,
    String name,
    BigDecimal netPricePerQuantity,
    double quantity,
    String description
) implements Identifiable {
}

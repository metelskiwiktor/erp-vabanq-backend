package pl.vabanq.erp.domain.accessory.model;

import pl.vabanq.erp.domain.Identifiable;

import java.math.BigDecimal;

public record FastenersAccessory(
    String id,
    String name,
    BigDecimal netPricePerQuantity
) implements Identifiable {
}

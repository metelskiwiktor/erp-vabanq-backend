package pl.vabanq.erp.domain.products.accessory.model;

import pl.vabanq.erp.domain.Identifiable;

import java.math.BigDecimal;

public record PackagingAccessory(
    String id,
    String name,
    String packagingSize,
    String dimensions, //__x__x__
    BigDecimal netPricePerQuantity,
    double quantity
) implements Identifiable {
}

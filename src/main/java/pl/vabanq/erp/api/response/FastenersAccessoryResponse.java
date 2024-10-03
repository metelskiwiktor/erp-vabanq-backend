package pl.vabanq.erp.api.response;

import java.math.BigDecimal;

public record FastenersAccessoryResponse(
        String id,
        String name,
        BigDecimal netPricePerQuantity,
        double quantity
) {}
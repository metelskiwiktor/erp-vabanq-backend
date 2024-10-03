package pl.vabanq.erp.api.response;

import java.math.BigDecimal;

public record PackagingAccessoryResponse(
        String id,
        String name,
        String packagingSize,
        String dimensions,
        BigDecimal netPricePerQuantity,
        double quantity
) {}
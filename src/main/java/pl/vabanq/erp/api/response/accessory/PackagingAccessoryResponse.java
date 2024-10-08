package pl.vabanq.erp.api.response.accessory;

import java.math.BigDecimal;

public record PackagingAccessoryResponse(
        String id,
        String name,
        String packagingSize,
        String dimensions,
        BigDecimal netPricePerQuantity,
        double quantity,
        String description
) {}
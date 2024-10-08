package pl.vabanq.erp.api.request.accessory;

public record PackagingAccessoryRequest(
        String name,
        String packagingSize,
        String dimensions,
        String netPricePerQuantity,
        String quantity,
        String description
) {}

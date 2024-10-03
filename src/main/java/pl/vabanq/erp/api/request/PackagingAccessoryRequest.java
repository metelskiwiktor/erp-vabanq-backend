package pl.vabanq.erp.api.request;

public record PackagingAccessoryRequest(
        String name,
        String packagingSize,
        String dimensions,
        String netPricePerQuantity,
        String quantity
) {}

package pl.vabanq.erp.api.request.accessory;

public record FastenersAccessoryRequest(
        String name,
        String netPricePerQuantity,
        String quantity,
        String description
        ) {}
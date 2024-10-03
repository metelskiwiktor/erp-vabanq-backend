package pl.vabanq.erp.api.request;

public record FastenersAccessoryRequest(
        String name,
        String netPricePerQuantity,
        String quantity
) {}
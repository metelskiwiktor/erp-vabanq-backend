package pl.vabanq.erp.api.request;

public record FilamentAccessoryRequest(
        String name,
        String producer,
        String filamentType,
        String printTemperature,
        String deskTemperature,
        String pricePerKg,
        String color,
        String description,
        String quantity
) {}
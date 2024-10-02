package pl.vabanq.erp.domain.change;

public record ChangeDetail(
    String fieldName,
    String oldValue,
    String newValue
) {}

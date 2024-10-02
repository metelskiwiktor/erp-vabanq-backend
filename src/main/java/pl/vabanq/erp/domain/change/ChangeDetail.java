package pl.vabanq.erp.domain.change;

/**
 * Reprezentuje pojedynczą zmianę w polu encji.
 */
public record ChangeDetail(
    String fieldName,
    String oldValue,
    String newValue
) {}

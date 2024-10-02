package pl.vabanq.erp.domain.change;

import java.time.LocalDateTime;
import java.util.List;

public record ChangeLog(
    Long id,
//    String entityName, // TEMPLATE lub ACCESSORY
    String entityId,
    String operationType, // CREATE, UPDATE, DELETE
    LocalDateTime timestamp,
    List<ChangeDetail> details // Szczegóły zmiany jako lista ChangeDetail
) {}

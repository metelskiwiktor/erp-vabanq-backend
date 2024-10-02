package pl.vabanq.erp.domain.change;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

public record ChangeLog(
        Long id,
        String entityName,
        String entityId,
        String operationType, // CREATE, UPDATE, DELETE
        LocalDateTime timestamp,
        List<ChangeDetail> details // Szczegóły zmiany jako lista ChangeDetail
) {
    public ChangeLog(Long id, String entityName, String entityId, String operationType,
                     LocalDateTime timestamp, List<ChangeDetail> details) {
        this.id = id;
        this.entityName = entityName;
        this.entityId = entityId;
        this.operationType = operationType;
        this.timestamp = timestamp;
        // Make an unmodifiable copy of the list to prevent external modifications
        this.details = details != null ? List.copyOf(details) : Collections.emptyList();
    }

    @Override
    public List<ChangeDetail> details() {
        return List.copyOf(details);
    }
}

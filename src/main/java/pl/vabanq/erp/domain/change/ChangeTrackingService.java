package pl.vabanq.erp.domain.change;


import pl.vabanq.erp.domain.Identifiable;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

public class ChangeTrackingService {
    private final List<ChangeLog> changeLogs = new CopyOnWriteArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public void logCreate(Identifiable identifiable) {
        ChangeLog log = new ChangeLog(
                idGenerator.getAndIncrement(),
                identifiable.name(),
                identifiable.id(),
                "CREATE",
                LocalDateTime.now(),
                Collections.emptyList() // Brak szczegółów dla operacji CREATE
        );
        changeLogs.add(log);
    }

    public void logDelete(Identifiable identifiable) {
        ChangeLog log = new ChangeLog(
                idGenerator.getAndIncrement(),
                identifiable.name(),
                identifiable.id(),
                "DELETE",
                LocalDateTime.now(),
                Collections.emptyList() // Brak szczegółów dla operacji DELETE
        );
        changeLogs.add(log);
    }

    public void logUpdate(Identifiable oldObj, Identifiable newObj) {
        List<ChangeDetail> details = compareRecords(oldObj, newObj);
        if (!details.isEmpty()) {
            ChangeLog log = new ChangeLog(
                    idGenerator.getAndIncrement(),
                    oldObj.name(),
                    oldObj.id(),
                    "UPDATE",
                    LocalDateTime.now(),
                    details
            );
            changeLogs.add(log);
        }
    }

    private List<ChangeDetail> compareRecords(Object oldObj, Object newObj) {
        List<ChangeDetail> changes = new ArrayList<>();

        if (!oldObj.getClass().equals(newObj.getClass())) {
            throw new IllegalArgumentException("Obiekty muszą być tego samego typu.");
        }

        RecordComponent[] components = oldObj.getClass().getRecordComponents();

        try {
            for (RecordComponent component : components) {
                String fieldName = component.getName();
                Object oldValue = component.getAccessor().invoke(oldObj);
                Object newValue = component.getAccessor().invoke(newObj);

                if (oldValue == null && newValue == null) {
                    continue;
                }

                if (oldValue == null || !oldValue.equals(newValue)) {
                    changes.add(new ChangeDetail(
                            fieldName,
                            oldValue != null ? oldValue.toString() : null,
                            newValue != null ? newValue.toString() : null
                    ));
                }
            }
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR);
        }

        return Collections.unmodifiableList(changes);
    }

}

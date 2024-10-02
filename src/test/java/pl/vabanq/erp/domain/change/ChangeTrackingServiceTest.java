package pl.vabanq.erp.domain.change;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ChangeTrackingServiceTest {
    private ChangeTrackingService changeTrackingService;

    @BeforeEach
    void setUp() {
        changeTrackingService = new ChangeTrackingService();
    }

    @Test
    void testLogCreate() {
//        FilamentAccessory filamentAccessory1 = new FilamentAccessory(UUIDGenerator.generateUUID(), "name1",
//                BigDecimal.valueOf(12.5), "description");
//        changeTrackingService.logCreate(filamentAccessory1);
//
//        System.out.println(changeTrackingService.getAllChangeLogs());
    }

    @Test
    void testLogUpdate() {
//        FilamentAccessory filamentAccessory1 = new FilamentAccessory(UUIDGenerator.generateUUID(), "name1",
//                BigDecimal.valueOf(12.5), "description");
//        FilamentAccessory filamentAccessory2 = new FilamentAccessory(filamentAccessory1.id(), "name1",
//                BigDecimal.valueOf(12.8), "description");
//        changeTrackingService.logUpdate(filamentAccessory1, filamentAccessory2);
//
//        System.out.println(changeTrackingService.getAllChangeLogs());
    }
}
package pl.vabanq.erp.domain.accessory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.vabanq.erp.domain.products.accessory.AccessoryService;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.products.accessory.model.PackagingAccessory;
import pl.vabanq.erp.domain.change.ChangeTrackingService;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;
import pl.vabanq.erp.infrastructure.database.accessory.AccessoryRepositoryJPA;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class AccessoryServiceTest {

    private AccessoryService accessoryService;

    @Autowired
    private AccessoryRepositoryJPA accessoryRepository;

    @BeforeEach
    void setUp() {
        ChangeTrackingService changeTrackingService = new ChangeTrackingService();
        accessoryService = new AccessoryService(changeTrackingService, accessoryRepository);
        accessoryRepository.cleanUp();
    }

    @Test
    @DisplayName("Should save filament successfully for valid data")
    void shouldSaveFilamentSuccessfully() {
        // given
        String name = "PLA 1kg";
        String producer = "XYZ";
        String filamentType = "PLA";
        String printTemperature = "200.0";
        String deskTemperature = "60.0";
        String pricePerKg = "19.99";
        String color = "#FFFFFF";
        String description = "High-quality PLA filament";
        String quantity = "10.0";

        // when
        accessoryService.saveFilament(name, producer, filamentType, printTemperature,
                deskTemperature, pricePerKg, color, description, quantity);

        // then
        List<FilamentAccessory> filaments = accessoryService.getAllFilaments();
        assertEquals(1, filaments.size());
        FilamentAccessory savedFilament = filaments.getFirst();

        assertEquals(name, savedFilament.name());
        assertEquals(producer, savedFilament.producer());
        assertEquals(filamentType, savedFilament.filamentType());
        assertEquals(Double.parseDouble(printTemperature), savedFilament.printTemperature());
        assertEquals(Double.parseDouble(deskTemperature), savedFilament.deskTemperature());
        assertEquals(new BigDecimal(pricePerKg), savedFilament.pricePerKg());
        assertEquals(color, savedFilament.color());
        assertEquals(description, savedFilament.description());
        assertEquals(Double.parseDouble(quantity), savedFilament.quantity());
    }

    @Test
    @DisplayName("Should save multiple filaments and validate repository content")
    void shouldSaveMultipleFilaments() {
        // given
        String name1 = "PLA 1kg";
        String producer1 = "ABC";
        String filamentType1 = "PLA";
        String printTemperature1 = "200.0";
        String deskTemperature1 = "60.0";
        String pricePerKg1 = "19.99";
        String color1 = "#FF0000";
        String description1 = "Red PLA filament";
        String quantity1 = "15.0";

        String name2 = "ABS 1kg";
        String producer2 = "DEF";
        String filamentType2 = "ABS";
        String printTemperature2 = "230.0";
        String deskTemperature2 = "80.0";
        String pricePerKg2 = "29.99";
        String color2 = "#00FF00";
        String description2 = "Green ABS filament";
        String quantity2 = "20.0";

        // when
        accessoryService.saveFilament(name1, producer1, filamentType1, printTemperature1,
                deskTemperature1, pricePerKg1, color1, description1, quantity1);
        accessoryService.saveFilament(name2, producer2, filamentType2, printTemperature2,
                deskTemperature2, pricePerKg2, color2, description2, quantity2);

        // then
        List<FilamentAccessory> filaments = accessoryService.getAllFilaments();
        assertEquals(2, filaments.size());

        FilamentAccessory filament1 = filaments.getFirst();
        FilamentAccessory filament2 = filaments.get(1);

        assertEquals(name1, filament1.name());
        assertEquals(producer1, filament1.producer());
        assertEquals(filamentType1, filament1.filamentType());
        assertEquals(Double.parseDouble(printTemperature1), filament1.printTemperature());
        assertEquals(Double.parseDouble(deskTemperature1), filament1.deskTemperature());
        assertEquals(new BigDecimal(pricePerKg1), filament1.pricePerKg());
        assertEquals(color1, filament1.color());
        assertEquals(description1, filament1.description());
        assertEquals(Double.parseDouble(quantity1), filament1.quantity());

        assertEquals(name2, filament2.name());
        assertEquals(producer2, filament2.producer());
        assertEquals(filamentType2, filament2.filamentType());
        assertEquals(Double.parseDouble(printTemperature2), filament2.printTemperature());
        assertEquals(Double.parseDouble(deskTemperature2), filament2.deskTemperature());
        assertEquals(new BigDecimal(pricePerKg2), filament2.pricePerKg());
        assertEquals(color2, filament2.color());
        assertEquals(description2, filament2.description());
        assertEquals(Double.parseDouble(quantity2), filament2.quantity());
    }

    @ParameterizedTest
    @MethodSource("invalidFilamentDataProvider")
    @DisplayName("Should throw DomainException for invalid filament data")
    void shouldThrowDomainExceptionForInvalidFilamentData(String name, String producer, String filamentType,
                                                          String printTemperature, String deskTemperature,
                                                          String pricePerKg, String color, String description,
                                                          String quantity, String expectedField, String expectedValue) {
        // given
        // invalid data passed from MethodSource

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.saveFilament(name, producer, filamentType, printTemperature,
                        deskTemperature, pricePerKg, color, description, quantity)
        );
        assertTrue(exception.getMessage().contains(expectedField));
        assertTrue(exception.getMessage().contains(expectedValue));
    }

    @ParameterizedTest
    @MethodSource("filamentUpdateTestCases")
    @DisplayName("Should update filament fields correctly based on provided input")
    void shouldUpdateFilamentCorrectly(
            String initialName,
            String initialProducer,
            String initialFilamentType,
            double initialPrintTemperature,
            double initialDeskTemperature,
            BigDecimal initialPricePerKg,
            String initialColor,
            String initialDescription,
            double initialQuantity,
            String updatedName,
            String updatedProducer,
            String updatedFilamentType,
            String updatedPrintTemperature,
            String updatedDeskTemperature,
            String updatedPricePerKg,
            String updatedColor,
            String updatedDescription,
            String updatedQuantity,
            String expectedName,
            String expectedProducer,
            String expectedFilamentType,
            double expectedPrintTemperature,
            double expectedDeskTemperature,
            BigDecimal expectedPricePerKg,
            String expectedColor,
            String expectedDescription,
            double expectedQuantity
    ) {
        // given
        accessoryService.saveFilament(
                initialName,
                initialProducer,
                initialFilamentType,
                String.valueOf(initialPrintTemperature),
                String.valueOf(initialDeskTemperature),
                initialPricePerKg.toString(),
                initialColor,
                initialDescription,
                String.valueOf(initialQuantity)
        );
        FilamentAccessory savedFilament = accessoryService.getAllFilaments().getFirst();
        assertEquals(1, accessoryService.getAllFilaments().size());

        // when
        accessoryService.updateFilament(
                savedFilament.id(),
                updatedName,
                updatedProducer,
                updatedFilamentType,
                updatedPrintTemperature,
                updatedDeskTemperature,
                updatedPricePerKg,
                updatedColor,
                updatedDescription,
                updatedQuantity
        );

        // then
        FilamentAccessory updatedFilament = accessoryService.getAllFilaments().getFirst();
        assertEquals(1, accessoryService.getAllFilaments().size());
        assertEquals(expectedName, updatedFilament.name());
        assertEquals(expectedProducer, updatedFilament.producer());
        assertEquals(expectedFilamentType, updatedFilament.filamentType());
        assertEquals(expectedPrintTemperature, updatedFilament.printTemperature());
        assertEquals(expectedDeskTemperature, updatedFilament.deskTemperature());
        assertEquals(expectedPricePerKg, updatedFilament.pricePerKg());
        assertEquals(expectedColor, updatedFilament.color());
        assertEquals(expectedDescription, updatedFilament.description());
        assertEquals(expectedQuantity, updatedFilament.quantity());
    }

    @Test
    @DisplayName("Should save packaging accessory successfully for valid data")
    void shouldSavePackagingAccessorySuccessfully() {
        // given
        String name = "Box A";
        String packagingSize = "S";
        String dimensions = "10x20x30";
        String netPricePerQuantity = "5.50";
        String quantity = "50.0";

        // when
        accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity, quantity);

        // then
        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        assertEquals(1, packagingAccessories.size());
        PackagingAccessory savedPackagingAccessory = packagingAccessories.getFirst();

        assertEquals(name, savedPackagingAccessory.name());
        assertEquals(packagingSize, savedPackagingAccessory.packagingSize());
        assertEquals(dimensions, savedPackagingAccessory.dimensions());
        assertEquals(new BigDecimal(netPricePerQuantity), savedPackagingAccessory.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity), savedPackagingAccessory.quantity());
    }

    @Test
    @DisplayName("Should save multiple packaging accessories and validate repository content")
    void shouldSaveMultiplePackagingAccessories() {
        // given
        String name1 = "Box A";
        String packagingSize1 = "S";
        String dimensions1 = "10x20x30";
        String netPricePerQuantity1 = "5.50";
        String quantity1 = "50.0";

        String name2 = "Box B";
        String packagingSize2 = "M";
        String dimensions2 = "15x25x35";
        String netPricePerQuantity2 = "7.75";
        String quantity2 = "30.0";

        // when
        accessoryService.savePackagingAccessory(name1, packagingSize1, dimensions1, netPricePerQuantity1, quantity1);
        accessoryService.savePackagingAccessory(name2, packagingSize2, dimensions2, netPricePerQuantity2, quantity2);

        // then
        List<PackagingAccessory> packagingAccessories = accessoryRepository.getAllPackagingAccessories();
        assertEquals(2, packagingAccessories.size());

        PackagingAccessory packaging1 = packagingAccessories.getFirst();
        PackagingAccessory packaging2 = packagingAccessories.get(1);

        assertEquals(name1, packaging1.name());
        assertEquals(packagingSize1, packaging1.packagingSize());
        assertEquals(dimensions1, packaging1.dimensions());
        assertEquals(new BigDecimal(netPricePerQuantity1), packaging1.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity1), packaging1.quantity());

        assertEquals(name2, packaging2.name());
        assertEquals(packagingSize2, packaging2.packagingSize());
        assertEquals(dimensions2, packaging2.dimensions());
        assertEquals(new BigDecimal(netPricePerQuantity2), packaging2.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity2), packaging2.quantity());
    }

    @ParameterizedTest
    @MethodSource("invalidPackagingAccessoryDataProvider")
    @DisplayName("Should throw DomainException for invalid packaging accessory data")
    void shouldThrowDomainExceptionForInvalidPackagingAccessoryData(String name, String packagingSize,
                                                                    String dimensions, String netPricePerQuantity,
                                                                    String quantity, String expectedField, String expectedValue) {
        // given
        // invalid data passed from MethodSource

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity, quantity)
        );
        assertTrue(exception.getMessage().contains(expectedField));
        assertTrue(exception.getMessage().contains(expectedValue));
    }

    @Test
    @DisplayName("Should save fasteners accessory successfully for valid data")
    void shouldSaveFastenersAccessorySuccessfully() {
        // given
        String name = "Screw Set";
        String netPricePerQuantity = "15.99";
        String quantity = "100.0";

        // when
        accessoryService.saveFastenersAccessory(name, netPricePerQuantity, quantity);

        // then
        List<FastenersAccessory> fastenersAccessories = accessoryRepository.getAllFastenersAccessories();
        assertEquals(1, fastenersAccessories.size());
        FastenersAccessory savedFastenersAccessory = fastenersAccessories.getFirst();

        assertEquals(name, savedFastenersAccessory.name());
        assertEquals(new BigDecimal(netPricePerQuantity), savedFastenersAccessory.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity), savedFastenersAccessory.quantity());
    }

    @Test
    @DisplayName("Should save multiple fasteners accessories and validate repository content")
    void shouldSaveMultipleFastenersAccessories() {
        // given
        String name1 = "Screw Set";
        String netPricePerQuantity1 = "15.99";
        String quantity1 = "100.0";

        String name2 = "Bolt Set";
        String netPricePerQuantity2 = "25.50";
        String quantity2 = "50.0";

        // when
        accessoryService.saveFastenersAccessory(name1, netPricePerQuantity1, quantity1);
        accessoryService.saveFastenersAccessory(name2, netPricePerQuantity2, quantity2);

        // then
        List<FastenersAccessory> fastenersAccessories = accessoryRepository.getAllFastenersAccessories();
        assertEquals(2, fastenersAccessories.size());

        FastenersAccessory fastener1 = fastenersAccessories.getFirst();
        FastenersAccessory fastener2 = fastenersAccessories.get(1);

        assertEquals(name1, fastener1.name());
        assertEquals(new BigDecimal(netPricePerQuantity1), fastener1.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity1), fastener1.quantity());

        assertEquals(name2, fastener2.name());
        assertEquals(new BigDecimal(netPricePerQuantity2), fastener2.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity2), fastener2.quantity());
    }

    @ParameterizedTest
    @MethodSource("invalidFastenersAccessoryDataProvider")
    @DisplayName("Should throw DomainException for invalid fasteners accessory data")
    void shouldThrowDomainExceptionForInvalidFastenersAccessoryData(String name, String netPricePerQuantity,
                                                                    String quantity, String expectedField, String expectedValue) {
        // given
        // invalid data passed from MethodSource

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.saveFastenersAccessory(name, netPricePerQuantity, quantity)
        );
        assertTrue(exception.getMessage().contains(expectedField));
        assertTrue(exception.getMessage().contains(expectedValue));
    }

    @Test
    @DisplayName("Should update packaging accessory successfully with all fields")
    void shouldUpdatePackagingAccessorySuccessfullyWithAllFields() {
        // given
        String name = "Box A";
        String packagingSize = "S";
        String dimensions = "10x20x30";
        String netPricePerQuantity = "5.50";
        String quantity = "50.0";

        accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity, quantity);

        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        assertEquals(1, packagingAccessories.size());
        PackagingAccessory savedPackagingAccessory = packagingAccessories.getFirst();

        // when
        String updatedName = "Box A Updated";
        String updatedPackagingSize = "M";
        String updatedDimensions = "15x25x35";
        String updatedNetPricePerQuantity = "7.75";
        String updatedQuantity = "60.0";

        accessoryService.updatePackagingAccessory(
                savedPackagingAccessory.id(),
                updatedName,
                updatedPackagingSize,
                updatedDimensions,
                updatedNetPricePerQuantity,
                updatedQuantity
        );

        // then
        PackagingAccessory updatedPackagingAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());

        assertEquals(updatedName, updatedPackagingAccessory.name());
        assertEquals(updatedPackagingSize, updatedPackagingAccessory.packagingSize());
        assertEquals(updatedDimensions, updatedPackagingAccessory.dimensions());
        assertEquals(new BigDecimal(updatedNetPricePerQuantity), updatedPackagingAccessory.netPricePerQuantity());
        assertEquals(Double.parseDouble(updatedQuantity), updatedPackagingAccessory.quantity());
    }

    @Test
    @DisplayName("Should update packaging accessory partially with some fields")
    void shouldUpdatePackagingAccessoryPartially() {
        // given
        String name = "Box A";
        String packagingSize = "S";
        String dimensions = "10x20x30";
        String netPricePerQuantity = "5.50";
        String quantity = "50.0";

        accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity, quantity);

        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        assertEquals(1, packagingAccessories.size());
        PackagingAccessory savedPackagingAccessory = packagingAccessories.getFirst();

        // when
        String updatedName = "Box A Updated";
        String updatedDimensions = "15x25x35";
        String updatedNetPricePerQuantity = "7.75";

        accessoryService.updatePackagingAccessory(
                savedPackagingAccessory.id(),
                updatedName,
                null,
                updatedDimensions,
                updatedNetPricePerQuantity,
                null
        );

        // then
        PackagingAccessory updatedPackagingAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());

        assertEquals(updatedName, updatedPackagingAccessory.name());
        assertEquals(packagingSize, updatedPackagingAccessory.packagingSize()); // Bez zmian
        assertEquals(updatedDimensions, updatedPackagingAccessory.dimensions());
        assertEquals(new BigDecimal(updatedNetPricePerQuantity), updatedPackagingAccessory.netPricePerQuantity());
        assertEquals(Double.parseDouble(quantity), updatedPackagingAccessory.quantity()); // Bez zmian
    }

    @ParameterizedTest
    @MethodSource("packagingAccessoryUpdateTestCases")
    @DisplayName("Should not update packaging accessory fields when provided with invalid data")
    void shouldNotUpdatePackagingAccessoryWithInvalidData(
            String initialName,
            String initialPackagingSize,
            String initialDimensions,
            String initialNetPricePerQuantity,
            String initialQuantity,
            String updatedName,
            String updatedPackagingSize,
            String updatedDimensions,
            String updatedNetPricePerQuantity,
            String updatedQuantity,
            String expectedName,
            String expectedPackagingSize,
            String expectedDimensions,
            BigDecimal expectedNetPricePerQuantity,
            double expectedQuantity
    ) {
        // given
        accessoryService.savePackagingAccessory(initialName, initialPackagingSize, initialDimensions, initialNetPricePerQuantity, initialQuantity);
        PackagingAccessory savedAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());

        // when
        accessoryService.updatePackagingAccessory(
                savedAccessory.id(),
                updatedName,
                updatedPackagingSize,
                updatedDimensions,
                updatedNetPricePerQuantity,
                updatedQuantity
        );

        // then
        PackagingAccessory updatedAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());
        assertEquals(expectedName, updatedAccessory.name());
        assertEquals(expectedPackagingSize, updatedAccessory.packagingSize());
        assertEquals(expectedDimensions, updatedAccessory.dimensions());
        assertEquals(expectedNetPricePerQuantity, updatedAccessory.netPricePerQuantity());
        assertEquals(expectedQuantity, updatedAccessory.quantity());
    }

    @Test
    @DisplayName("Should throw DomainException when updating non-existent packaging accessory")
    void shouldThrowDomainExceptionWhenUpdatingNonExistentPackagingAccessory() {
        // given
        String nonExistentId = "non-existent-id";
        String name = "Box X";
        String packagingSize = "L";
        String dimensions = "20x30x40";
        String netPricePerQuantity = "10.00";
        String quantity = "100.0";

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.updatePackagingAccessory(
                        nonExistentId,
                        name,
                        packagingSize,
                        dimensions,
                        netPricePerQuantity,
                        quantity
                )
        );
        assertTrue(exception.getErrorCodes().contains(ErrorCode.NOT_FOUND));
    }

    @ParameterizedTest
    @MethodSource("fastenersAccessoryUpdateTestCases")
    @DisplayName("Should not update fasteners accessory fields when provided with invalid data")
    void shouldNotUpdateFastenersAccessoryWithInvalidData(
            String initialName,
            String initialNetPricePerQuantity,
            String initialQuantity,
            String updatedName,
            String updatedNetPricePerQuantity,
            String updatedQuantity,
            String expectedName,
            BigDecimal expectedNetPricePerQuantity,
            double expectedQuantity
    ) {
        // given
        accessoryService.saveFastenersAccessory(initialName, initialNetPricePerQuantity, initialQuantity);
        FastenersAccessory savedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());

        // when
        accessoryService.updateFastenersAccessory(
                savedAccessory.id(),
                updatedName,
                updatedNetPricePerQuantity,
                updatedQuantity
        );

        // then
        FastenersAccessory updatedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());
        assertEquals(expectedName, updatedAccessory.name());
        assertEquals(expectedNetPricePerQuantity, updatedAccessory.netPricePerQuantity());
        assertEquals(expectedQuantity, updatedAccessory.quantity());
    }

    @Test
    @DisplayName("Should update fasteners accessory successfully with all fields")
    void shouldUpdateFastenersAccessorySuccessfullyWithAllFields() {
        // given
        String initialName = "Screw Set";
        String initialNetPricePerQuantity = "15.99";
        String initialQuantity = "100.0";

        accessoryService.saveFastenersAccessory(initialName, initialNetPricePerQuantity, initialQuantity);
        FastenersAccessory savedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());

        String updatedName = "Screw Set Deluxe";
        String updatedNetPricePerQuantity = "19.99";
        String updatedQuantity = "120.0";

        // when
        accessoryService.updateFastenersAccessory(
                savedAccessory.id(),
                updatedName,
                updatedNetPricePerQuantity,
                updatedQuantity
        );

        // then
        FastenersAccessory updatedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());
        assertEquals(updatedName, updatedAccessory.name());
        assertEquals(new BigDecimal(updatedNetPricePerQuantity), updatedAccessory.netPricePerQuantity());
        assertEquals(Double.parseDouble(updatedQuantity), updatedAccessory.quantity());
    }

    @Test
    @DisplayName("Should update fasteners accessory partially with some fields")
    void shouldUpdateFastenersAccessoryPartially() {
        // given
        String initialName = "Bolt Set";
        String initialNetPricePerQuantity = "25.50";
        String initialQuantity = "50.0";

        accessoryService.saveFastenersAccessory(initialName, initialNetPricePerQuantity, initialQuantity);
        FastenersAccessory savedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());

        String updatedName = "Bolt Set Pro";

        // when
        accessoryService.updateFastenersAccessory(
                savedAccessory.id(),
                updatedName,
                null,
                null
        );

        // then
        FastenersAccessory updatedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());
        assertEquals(updatedName, updatedAccessory.name());
        assertEquals(new BigDecimal(initialNetPricePerQuantity), updatedAccessory.netPricePerQuantity());
        assertEquals(Double.parseDouble(initialQuantity), updatedAccessory.quantity());
    }

    @Test
    @DisplayName("Should throw DomainException when updating non-existent fasteners accessory")
    void shouldThrowDomainExceptionWhenUpdatingNonExistentFastenersAccessory() {
        // given
        String nonExistentId = "non-existent-id";
        String name = "Nut Set";
        String netPricePerQuantity = "10.00";
        String quantity = "200.0";

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.updateFastenersAccessory(
                        nonExistentId,
                        name,
                        netPricePerQuantity,
                        quantity
                )
        );
        assertTrue(exception.getErrorCodes().contains(ErrorCode.NOT_FOUND));
    }

    // Data Providers

    private static Stream<Arguments> invalidFilamentDataProvider() {
        return Stream.of(
                // Niepoprawna ilość (ujemna)
                Arguments.of("PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "Description", "-5.0", "quantity", "-5.0"),
                // Niepoprawna ilość (nie liczba)
                Arguments.of("PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "Description", "abc", "quantity", "abc"),
                // Brak ilości
                Arguments.of("PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "Description", null, "quantity", "null")
        );
    }

    private static Stream<Arguments> filamentUpdateTestCases() {
        return Stream.of(
                // Aktualizacja ilości
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description", 10.0,
                        null, null, null, null, null, null, null, null, "15.0",
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description", 15.0
                ),
                // Aktualizacja z niepoprawną ilością (ujemna) - ilość nie powinna się zmienić
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description", 10.0,
                        null, null, null, null, null, null, null, null, "-5.0",
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description", 10.0
                )
        );
    }

    private static Stream<Arguments> invalidPackagingAccessoryDataProvider() {
        return Stream.of(
                // Niepoprawna ilość (ujemna)
                Arguments.of("Box A", "S", "10x20x30", "5.50", "-10.0", "quantity", "-10.0"),
                // Niepoprawna ilość (nie liczba)
                Arguments.of("Box B", "M", "15x25x35", "7.75", "abc", "quantity", "abc"),
                // Brak ilości
                Arguments.of("Box C", "L", "20x30x40", "10.00", null, "quantity", "null")
        );
    }

    private static Stream<Arguments> packagingAccessoryUpdateTestCases() {
        return Stream.of(
                // Aktualizacja ilości
                Arguments.of(
                        "Box A", "S", "10x20x30", "5.50", "50.0",
                        null, null, null, null, "60.0",
                        "Box A", "S", "10x20x30", new BigDecimal("5.50"), 60.0
                ),
                // Aktualizacja z niepoprawną ilością (ujemna) - ilość nie powinna się zmienić
                Arguments.of(
                        "Box A", "S", "10x20x30", "5.50", "50.0",
                        null, null, null, null, "-10.0",
                        "Box A", "S", "10x20x30", new BigDecimal("5.50"), 50.0
                )
        );
    }

    private static Stream<Arguments> invalidFastenersAccessoryDataProvider() {
        return Stream.of(
                // Niepoprawna ilość (ujemna)
                Arguments.of("Screw Set", "15.99", "-100.0", "quantity", "-100.0"),
                // Niepoprawna ilość (nie liczba)
                Arguments.of("Bolt Set", "25.50", "abc", "quantity", "abc"),
                // Brak ilości
                Arguments.of("Nut Set", "10.00", null, "quantity", "null")
        );
    }

    private static Stream<Arguments> fastenersAccessoryUpdateTestCases() {
        return Stream.of(
                // Aktualizacja ilości
                Arguments.of(
                        "Screw Set", "15.99", "100.0",
                        null, null, "150.0",
                        "Screw Set", new BigDecimal("15.99"), 150.0
                ),
                // Aktualizacja z niepoprawną ilością (ujemna) - ilość nie powinna się zmienić
                Arguments.of(
                        "Bolt Set", "25.50", "50.0",
                        null, null, "-20.0",
                        "Bolt Set", new BigDecimal("25.50"), 50.0
                )
        );
    }
}

package pl.vabanq.erp.domain.accessory;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pl.vabanq.erp.domain.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.accessory.model.PackagingAccessory;
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

        // when
        accessoryService.saveFilament(name, producer, filamentType, printTemperature,
                deskTemperature, pricePerKg, color, description);

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

        String name2 = "ABS 1kg";
        String producer2 = "DEF";
        String filamentType2 = "ABS";
        String printTemperature2 = "230.0";
        String deskTemperature2 = "80.0";
        String pricePerKg2 = "29.99";
        String color2 = "#00FF00";
        String description2 = "Green ABS filament";

        // when
        accessoryService.saveFilament(name1, producer1, filamentType1, printTemperature1,
                deskTemperature1, pricePerKg1, color1, description1);
        accessoryService.saveFilament(name2, producer2, filamentType2, printTemperature2,
                deskTemperature2, pricePerKg2, color2, description2);

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

        assertEquals(name2, filament2.name());
        assertEquals(producer2, filament2.producer());
        assertEquals(filamentType2, filament2.filamentType());
        assertEquals(Double.parseDouble(printTemperature2), filament2.printTemperature());
        assertEquals(Double.parseDouble(deskTemperature2), filament2.deskTemperature());
        assertEquals(new BigDecimal(pricePerKg2), filament2.pricePerKg());
        assertEquals(color2, filament2.color());
        assertEquals(description2, filament2.description());
    }

    @ParameterizedTest
    @MethodSource("invalidFilamentDataProvider")
    @DisplayName("Should throw DomainException for invalid filament data")
    void shouldThrowDomainExceptionForInvalidFilamentData(String name, String producer, String filamentType,
                                                          String printTemperature, String deskTemperature,
                                                          String pricePerKg, String color, String description,
                                                          String expectedField, String expectedValue) {
        // given
        // invalid data passed from MethodSource

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.saveFilament(name, producer, filamentType, printTemperature,
                        deskTemperature, pricePerKg, color, description)
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
            String updatedName,
            String updatedProducer,
            String updatedFilamentType,
            String updatedPrintTemperature,
            String updatedDeskTemperature,
            String updatedPricePerKg,
            String updatedColor,
            String updatedDescription,
            String expectedName,
            String expectedProducer,
            String expectedFilamentType,
            double expectedPrintTemperature,
            double expectedDeskTemperature,
            BigDecimal expectedPricePerKg,
            String expectedColor,
            String expectedDescription
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
                initialDescription
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
                updatedDescription
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
    }


    @Test
    @DisplayName("Should save packaging accessory successfully for valid data")
    void shouldSavePackagingAccessorySuccessfully() {
        // given
        String name = "Box A";
        String packagingSize = "S"; // zakładam, że rozmiar opakowania to pojedynczy znak
        String dimensions = "10x20x30"; // długość x szerokość x wysokość
        String netPricePerQuantity = "5.50";

        // when
        accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity);

        // then
        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        assertEquals(1, packagingAccessories.size());
        PackagingAccessory savedPackagingAccessory = packagingAccessories.getFirst();

        assertEquals(name, savedPackagingAccessory.name());
        assertEquals(packagingSize, savedPackagingAccessory.packagingSize());

        // Sprawdzenie wymiarów
        assertNotNull(savedPackagingAccessory.dimensions());
        assertEquals(dimensions, savedPackagingAccessory.dimensions());

        assertEquals(new BigDecimal(netPricePerQuantity), savedPackagingAccessory.netPricePerQuantity());
    }

    @Test
    @DisplayName("Should save multiple packaging accessories and validate repository content")
    void shouldSaveMultiplePackagingAccessories() {
        // given
        String name1 = "Box A";
        String packagingSize1 = "S";
        String dimensions1 = "10x20x30";
        String netPricePerQuantity1 = "5.50";

        String name2 = "Box B";
        String packagingSize2 = "M";
        String dimensions2 = "15x25x35";
        String netPricePerQuantity2 = "7.75";

        // when
        accessoryService.savePackagingAccessory(name1, packagingSize1, dimensions1, netPricePerQuantity1);
        accessoryService.savePackagingAccessory(name2, packagingSize2, dimensions2, netPricePerQuantity2);

        // then
        List<PackagingAccessory> packagingAccessories = accessoryRepository.getAllPackagingAccessories();
        assertEquals(2, packagingAccessories.size());

        PackagingAccessory packaging1 = packagingAccessories.getFirst();
        PackagingAccessory packaging2 = packagingAccessories.get(1);

        // Sprawdzenie pierwszego opakowania
        assertEquals(name1, packaging1.name());
        assertEquals(packagingSize1, packaging1.packagingSize());
        assertNotNull(packaging1.dimensions());
        assertEquals(dimensions1, packaging1.dimensions());
        assertEquals(new BigDecimal(netPricePerQuantity1), packaging1.netPricePerQuantity());

        // Sprawdzenie drugiego opakowania
        assertEquals(name2, packaging2.name());
        assertEquals(packagingSize2, packaging2.packagingSize());
        assertNotNull(packaging2.dimensions());
        assertEquals(dimensions2, packaging2.dimensions());
        assertEquals(new BigDecimal(netPricePerQuantity2), packaging2.netPricePerQuantity());
    }

    @ParameterizedTest
    @MethodSource("invalidPackagingAccessoryDataProvider")
    @DisplayName("Should throw DomainException for invalid packaging accessory data")
    void shouldThrowDomainExceptionForInvalidPackagingAccessoryData(String name, String packagingSize,
                                                                    String dimensions, String netPricePerQuantity,
                                                                    String expectedField, String expectedValue) {
        // given
        // invalid data passed from MethodSource

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity)
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

        // when
        accessoryService.saveFastenersAccessory(name, netPricePerQuantity);

        // then
        List<FastenersAccessory> fastenersAccessories = accessoryRepository.getAllFastenersAccessories();
        assertEquals(1, fastenersAccessories.size());
        FastenersAccessory savedFastenersAccessory = fastenersAccessories.getFirst();

        assertEquals(name, savedFastenersAccessory.name());
        assertEquals(new BigDecimal(netPricePerQuantity), savedFastenersAccessory.netPricePerQuantity());
    }

    @Test
    @DisplayName("Should save multiple fasteners accessories and validate repository content")
    void shouldSaveMultipleFastenersAccessories() {
        // given
        String name1 = "Screw Set";
        String netPricePerQuantity1 = "15.99";

        String name2 = "Bolt Set";
        String netPricePerQuantity2 = "25.50";

        // when
        accessoryService.saveFastenersAccessory(name1, netPricePerQuantity1);
        accessoryService.saveFastenersAccessory(name2, netPricePerQuantity2);

        // then
        List<FastenersAccessory> fastenersAccessories = accessoryRepository.getAllFastenersAccessories();
        assertEquals(2, fastenersAccessories.size());

        FastenersAccessory fastener1 = fastenersAccessories.getFirst();
        FastenersAccessory fastener2 = fastenersAccessories.get(1);

        // Sprawdzenie pierwszego fastenera
        assertEquals(name1, fastener1.name());
        assertEquals(new BigDecimal(netPricePerQuantity1), fastener1.netPricePerQuantity());

        // Sprawdzenie drugiego fastenera
        assertEquals(name2, fastener2.name());
        assertEquals(new BigDecimal(netPricePerQuantity2), fastener2.netPricePerQuantity());
    }

    @ParameterizedTest
    @MethodSource("invalidFastenersAccessoryDataProvider")
    @DisplayName("Should throw DomainException for invalid fasteners accessory data")
    void shouldThrowDomainExceptionForInvalidFastenersAccessoryData(String name, String netPricePerQuantity,
                                                                    String expectedField, String expectedValue) {
        // given
        // invalid data passed from MethodSource

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.saveFastenersAccessory(name, netPricePerQuantity)
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

        accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity);

        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        assertEquals(1, packagingAccessories.size());
        PackagingAccessory savedPackagingAccessory = packagingAccessories.getFirst();

        // when
        String updatedName = "Box A Updated";
        String updatedPackagingSize = "M";
        String updatedDimensions = "15x25x35";
        String updatedNetPricePerQuantity = "7.75";

        accessoryService.updatePackagingAccessory(
                savedPackagingAccessory.id(),
                updatedName,
                updatedPackagingSize,
                updatedDimensions,
                updatedNetPricePerQuantity
        );

        // then
        PackagingAccessory updatedPackagingAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());

        assertEquals(updatedName, updatedPackagingAccessory.name());
        assertEquals(updatedPackagingSize, updatedPackagingAccessory.packagingSize());
        assertEquals(updatedDimensions, updatedPackagingAccessory.dimensions());
        assertEquals(new BigDecimal(updatedNetPricePerQuantity), updatedPackagingAccessory.netPricePerQuantity());
    }

    @Test
    @DisplayName("Should update packaging accessory partially with some fields")
    void shouldUpdatePackagingAccessoryPartially() {
        // given
        String name = "Box A";
        String packagingSize = "S";
        String dimensions = "10x20x30";
        String netPricePerQuantity = "5.50";

        accessoryService.savePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity);

        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        assertEquals(1, packagingAccessories.size());
        PackagingAccessory savedPackagingAccessory = packagingAccessories.getFirst();

        // when
        String updatedName = "Box A Updated";
        // Aktualizacja tylko dimensions
        String updatedDimensions = "15x25x35";
        // Aktualizacja tylko netPricePerQuantity
        String updatedNetPricePerQuantity = "7.75";

        accessoryService.updatePackagingAccessory(
                savedPackagingAccessory.id(),
                updatedName,
                null,
                updatedDimensions,
                updatedNetPricePerQuantity
        );

        // then
        PackagingAccessory updatedPackagingAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());

        assertEquals(updatedName, updatedPackagingAccessory.name());
        assertEquals(packagingSize, updatedPackagingAccessory.packagingSize()); // Bez zmian
        assertEquals(updatedDimensions, updatedPackagingAccessory.dimensions());
        assertEquals(new BigDecimal(updatedNetPricePerQuantity), updatedPackagingAccessory.netPricePerQuantity());
    }

    @ParameterizedTest
    @MethodSource("packagingAccessoryUpdateTestCases")
    @DisplayName("Should not update packaging accessory fields when provided with invalid data")
    void shouldNotUpdatePackagingAccessoryWithInvalidData(
            String initialName,
            String initialPackagingSize,
            String initialDimensions,
            String initialNetPricePerQuantity,
            String updatedName,
            String updatedPackagingSize,
            String updatedDimensions,
            String updatedNetPricePerQuantity,
            String expectedName,
            String expectedPackagingSize,
            String expectedDimensions,
            BigDecimal expectedNetPricePerQuantity
    ) {
        // given
        accessoryService.savePackagingAccessory(initialName, initialPackagingSize, initialDimensions, initialNetPricePerQuantity);
        PackagingAccessory savedAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());

        // when
        accessoryService.updatePackagingAccessory(
                savedAccessory.id(),
                updatedName,
                updatedPackagingSize,
                updatedDimensions,
                updatedNetPricePerQuantity
        );

        // then
        PackagingAccessory updatedAccessory = accessoryService.getAllPackagingAccessories().getFirst();
        assertEquals(1, accessoryService.getAllPackagingAccessories().size());
        assertEquals(expectedName, updatedAccessory.name());
        assertEquals(expectedPackagingSize, updatedAccessory.packagingSize());
        assertEquals(expectedDimensions, updatedAccessory.dimensions());
        assertEquals(expectedNetPricePerQuantity, updatedAccessory.netPricePerQuantity());
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

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.updatePackagingAccessory(
                        nonExistentId,
                        name,
                        packagingSize,
                        dimensions,
                        netPricePerQuantity
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
            String updatedName,
            String updatedNetPricePerQuantity,
            String expectedName,
            BigDecimal expectedNetPricePerQuantity
    ) {
        // given
        accessoryService.saveFastenersAccessory(initialName, initialNetPricePerQuantity);
        FastenersAccessory savedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());

        // when
        accessoryService.updateFastenersAccessory(
                savedAccessory.id(),
                updatedName,
                updatedNetPricePerQuantity
        );

        // then
        FastenersAccessory updatedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());
        assertEquals(expectedName, updatedAccessory.name());
        assertEquals(expectedNetPricePerQuantity, updatedAccessory.netPricePerQuantity());
    }

    @Test
    @DisplayName("Should update fasteners accessory successfully with all fields")
    void shouldUpdateFastenersAccessorySuccessfullyWithAllFields() {
        // given
        String initialName = "Screw Set";
        String initialNetPricePerQuantity = "15.99";

        accessoryService.saveFastenersAccessory(initialName, initialNetPricePerQuantity);
        FastenersAccessory savedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());

        String updatedName = "Screw Set Deluxe";
        String updatedNetPricePerQuantity = "19.99";

        // when
        accessoryService.updateFastenersAccessory(
                savedAccessory.id(),
                updatedName,
                updatedNetPricePerQuantity
        );

        // then
        FastenersAccessory updatedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());
        assertEquals(updatedName, updatedAccessory.name());
        assertEquals(new BigDecimal(updatedNetPricePerQuantity), updatedAccessory.netPricePerQuantity());
    }

    @Test
    @DisplayName("Should update fasteners accessory partially with some fields")
    void shouldUpdateFastenersAccessoryPartially() {
        // given
        String initialName = "Bolt Set";
        String initialNetPricePerQuantity = "25.50";

        accessoryService.saveFastenersAccessory(initialName, initialNetPricePerQuantity);
        FastenersAccessory savedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());

        String updatedName = "Bolt Set Pro";

        // when
        accessoryService.updateFastenersAccessory(
                savedAccessory.id(),
                updatedName,
                null
        );

        // then
        FastenersAccessory updatedAccessory = accessoryRepository.getAllFastenersAccessories().getFirst();
        assertEquals(1, accessoryRepository.getAllFastenersAccessories().size());
        assertEquals(updatedName, updatedAccessory.name());
        assertEquals(new BigDecimal(initialNetPricePerQuantity), updatedAccessory.netPricePerQuantity());
    }

    @Test
    @DisplayName("Should throw DomainException when updating non-existent fasteners accessory")
    void shouldThrowDomainExceptionWhenUpdatingNonExistentFastenersAccessory() {
        // given
        String nonExistentId = "non-existent-id";
        String name = "Nut Set";
        String netPricePerQuantity = "10.00";

        // when & then
        DomainException exception = assertThrows(DomainException.class, () ->
                accessoryService.updateFastenersAccessory(
                        nonExistentId,
                        name,
                        netPricePerQuantity
                )
        );
        assertTrue(exception.getErrorCodes().contains(ErrorCode.NOT_FOUND));
    }

    private static Stream<Arguments> fastenersAccessoryUpdateTestCases() {
        return Stream.of(
                // Aktualizacja tylko nazwy (poprawna)
                Arguments.of(
                        "Screw Set", "15.99",
                        "Screw Set Pro", null,
                        "Screw Set Pro", new BigDecimal("15.99")
                ),
                // Aktualizacja z niepoprawną nazwą (zbyt krótka) - nazwa nie powinna się zmienić
                Arguments.of(
                        "Bolt Set", "25.50",
                        "Bt", null,
                        "Bolt Set", new BigDecimal("25.50")
                ),
                // Aktualizacja z mieszanką poprawnych i niepoprawnych danych
                Arguments.of(
                        "Nut Set", "10.00",
                        null, "-5.00",
                        "Nut Set", new BigDecimal("10.00")
                ),
                // Aktualizacja z pustymi wartościami (pola nie powinny się zmienić)
                Arguments.of(
                        "Washer Set", "5.00",
                        null, null,
                        "Washer Set", new BigDecimal("5.00")
                )
        );
    }

    private static Stream<Object[]> invalidFilamentDataProvider() {
        return Stream.of(
                // Niepoprawna nazwa (zbyt krótka)
                new Object[]{"PL", "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "Description", "name", "PL"},
                // Niepoprawny producent (zbyt krótki)
                new Object[]{"PLA 1kg", "XY", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "Description", "producer", "XY"},
                // Niepoprawny filamentType (zbyt krótki)
                new Object[]{"PLA 1kg", "XYZ", "PL", "200.0", "60.0", "19.99", "#FFFFFF", "Description", "filamentType", "PL"},
                // Niepoprawna temperatura druku (nie liczba)
                new Object[]{"PLA 1kg", "XYZ", "PLA", "abc", "60.0", "19.99", "#FFFFFF", "Description", "printTemperature", "abc"},
                // Niepoprawna temperatura stołu (ujemna)
                new Object[]{"PLA 1kg", "XYZ", "PLA", "200.0", "-10.0", "19.99", "#FFFFFF", "Description", "deskTemperature", "-10.0"},
                // Niepoprawna cena za kg (zły format)
                new Object[]{"PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.999", "#FFFFFF", "Description", "pricePerKg", "19.999"},
                // Niepoprawny kolor (zły format hex)
                new Object[]{"PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.99", "FFFFFF", "Description", "color", "FFFFFF"},
                // Brak nazwy
                new Object[]{null, "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "Description", "name", "null"},
                // Brak ceny za kg
                new Object[]{"PLA 1kg", "XYZ", "PLA", "200.0", "60.0", null, "#FFFFFF", "Description", "pricePerKg", "null"}
        );
    }

    private static Stream<Arguments> filamentUpdateTestCases() {
        return Stream.of(
                // Aktualizacja tylko nazwy
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description",
                        "New PLA", null, null, null, null, null, null, null,
                        "New PLA", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description"
                ),
                // Aktualizacja tylko producenta
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description",
                        null, "ABC", null, null, null, null, null, null,
                        "PLA 1kg", "ABC", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description"
                ),
                // Aktualizacja wszystkich pól
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description",
                        "ABS 1kg", "DEF", "ABS", "230.0", "80.0", "29.99", "#0000FF", "New Description",
                        "ABS 1kg", "DEF", "ABS", 230.0, 80.0, new BigDecimal("29.99"), "#0000FF", "New Description"
                ),
                // Niepoprawna aktualizacja (błędny kolor) - pole powinno zostać bez zmian
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description",
                        null, null, null, null, null, null, "ZZZZZZ", null,
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description"
                ),
                // Aktualizacja z usunięciem opisu (pole opcjonalne)
                Arguments.of(
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description",
                        null, null, null, null, null, null, null, "",
                        "PLA 1kg", "XYZ", "PLA", 200.0, 60.0, new BigDecimal("19.99"), "#FFFFFF", "Description"
                )
        );
    }


    private static Stream<Arguments> invalidPackagingAccessoryDataProvider() {
        return Stream.of(
                // Niepoprawna nazwa (zbyt krótka)
                Arguments.of("Bx", "S", "10x20x30", "5.50", "name", "Bx"),
                // Niepoprawny rozmiar opakowania (pusty)
                Arguments.of("Box A", "", "10x20x30", "5.50", "packagingSize", ""),
                // Niepoprawne wymiary (brak części)
                Arguments.of("Box B", "M", "15x25", "7.75", "dimensions", "15x25"),
                // Niepoprawne wymiary (nie liczba)
                Arguments.of("Box C", "L", "15x25xabc", "7.75", "dimensions", "15x25xabc"),
                // Niepoprawna cena za ilość (ujemna)
                Arguments.of("Box D", "XL", "20x30x40", "-10.00", "netPricePerQuantity", "-10.00"),
                // Niepoprawna cena za ilość (więcej niż dwa miejsca po przecinku)
                Arguments.of("Box E", "S", "25x35x45", "10.999", "netPricePerQuantity", "10.999"),
                // Brak nazwy
                Arguments.of(null, "S", "10x20x30", "5.50", "name", "null"),
                // Brak ceny za ilość
                Arguments.of("Box F", "M", "10x20x30", null, "netPricePerQuantity", "null")
        );
    }

    private static Stream<Arguments> packagingAccessoryUpdateTestCases() {
        return Stream.of(
                // Aktualizacja tylko nazwy (poprawna)
                Arguments.of(
                        "Box A", "S", "10x20x30", "5.50",
                        "Box B", null, null, null,
                        "Box B", "S", "10x20x30", new BigDecimal("5.50")
                ),
                // Aktualizacja z niepoprawną nazwą (zbyt krótka) - nazwa nie powinna się zmienić
                Arguments.of(
                        "Box A", "S", "10x20x30", "5.50",
                        "Bx", null, null, null,
                        "Box A", "S", "10x20x30", new BigDecimal("5.50")
                ),
                // Aktualizacja wszystkich pól z mieszanką poprawnych i niepoprawnych danych
                Arguments.of(
                        "Box A", "S", "10x20x30", "5.50",
                        "Box C", "L", "15x25xabc", "-10.00",
                        "Box C", "L", "10x20x30", new BigDecimal("5.50")
                ),
                // Aktualizacja z pustymi wartościami (pola nie powinny się zmienić)
                Arguments.of(
                        "Box A", "S", "10x20x30", "5.50",
                        null, null, null, null,
                        "Box A", "S", "10x20x30", new BigDecimal("5.50")
                )
        );
    }

    private static Stream<Arguments> invalidFastenersAccessoryDataProvider() {
        return Stream.of(
                // Niepoprawna nazwa (zbyt krótka)
                Arguments.of("SC", "15.99", "name", "SC"),
                // Niepoprawna cena za ilość (ujemna)
                Arguments.of("Screw Set", "-10.00", "netPricePerQuantity", "-10.00"),
                // Niepoprawna cena za ilość (więcej niż dwa miejsca po przecinku)
                Arguments.of("Bolt Set", "25.999", "netPricePerQuantity", "25.999"),
                // Brak nazwy
                Arguments.of(null, "15.99", "name", "null"),
                // Brak ceny za ilość
                Arguments.of("Nut Set", null, "netPricePerQuantity", "null")
        );
    }
}

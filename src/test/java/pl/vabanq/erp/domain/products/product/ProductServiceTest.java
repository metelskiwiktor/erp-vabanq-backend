package pl.vabanq.erp.domain.products.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.util.Pair;
import pl.vabanq.erp.domain.change.ChangeTrackingService;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.products.accessory.model.*;
import pl.vabanq.erp.domain.products.product.model.Product;
import pl.vabanq.erp.domain.products.product.model.ProductFile;
import pl.vabanq.erp.infrastructure.database.accessory.AccessoryRepositoryJPA;
import pl.vabanq.erp.infrastructure.database.product.ProductRepositoryJPA;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ProductServiceTest {

    private ProductService productService;

    @Autowired
    private ProductRepositoryJPA productRepository;

    @Autowired
    private AccessoryRepositoryJPA accessoryRepository;

    @BeforeEach
    void setUp() {
        this.productService = new ProductService(productRepository, accessoryRepository, new ChangeTrackingService());
        this.productRepository.cleanUp();
        this.accessoryRepository.cleanUp();
    }

    @Test
    @DisplayName("Test saving product with valid inputs")
    void testSaveProductWithValidInputs() {
        // Arrange
        String name = "Test Product";
        String ean = "1234567890123";
        List<Pair<Double, String>> accessoriesQ = List.of(
                Pair.of(2.0, "acc1"),
                Pair.of(1.5, "acc2")
        );
        Integer printHours = 1;
        Integer printMinutes = 30;
        String price = "99.99";
        String allegroTax = "5.00";
        String description = "This is a test product description.";

        // Create accessories and save them in the repository
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1",               // id
                "Accessory 1",        // name
                "Producer A",         // producer
                "PLA",                // filamentType
                200.0,                // printTemperature
                60.0,                 // deskTemperature
                new BigDecimal("20.00"), // pricePerKg
                "Red",                // color
                "Red PLA filament",   // description
                100.0                 // quantity
        );
        FilamentAccessory accessory2 = new FilamentAccessory(
                "acc2",               // id
                "Accessory 2",        // name
                "Producer B",         // producer
                "ABS",                // filamentType
                230.0,                // printTemperature
                80.0,                 // deskTemperature
                new BigDecimal("25.00"), // pricePerKg
                "Blue",               // color
                "Blue ABS filament",  // description
                50.0                  // quantity
        );

        accessoryRepository.saveFilament(accessory1);
        accessoryRepository.saveFilament(accessory2);

        // Act
        productService.saveProduct(name, ean, accessoriesQ, printHours, printMinutes, price, allegroTax, description);

        // Assert
        // Retrieve all products and find the one with matching name and ean
        List<Product> products = productRepository.getAllProducts();
        assertFalse(products.isEmpty(), "Product repository should not be empty.");
        Product savedProduct = products.stream()
                .filter(p -> p.name().equals(name) && p.ean().equals(ean))
                .findFirst()
                .orElse(null);
        assertNotNull(savedProduct, "Saved product should not be null.");

        // Continue with assertions
        assertEquals(name, savedProduct.name());
        assertEquals(ean, savedProduct.ean());
        assertEquals(2, savedProduct.accessoriesQ().size());
        assertEquals(printHours.intValue(), savedProduct.printTime().hours());
        assertEquals(printMinutes.intValue(), savedProduct.printTime().minutes());
        assertEquals(new BigDecimal(price), savedProduct.price());
        assertEquals(new BigDecimal(allegroTax), savedProduct.allegroTax());
        assertEquals(description, savedProduct.description());
    }

    @ParameterizedTest
    @MethodSource("invalidSaveProductParameters")
    @DisplayName("Test saving product with invalid inputs")
    void testSaveProductWithInvalidInputs(String name, String ean, List<Pair<Double, String>> accessoriesQ,
                                          Integer printHours, Integer printMinutes, String price, String allegroTax,
                                          String description, String expectedInvalidField) {
        // Arrange
        // Create accessories if needed
        if (accessoriesQ != null) {
            for (Pair<Double, String> pair : accessoriesQ) {
                String accessoryId = pair.getSecond();
                if (!accessoryId.isEmpty()) {
                    // Save accessory to repository
                    FilamentAccessory accessory = new FilamentAccessory(
                            accessoryId,
                            "Accessory " + accessoryId,
                            "Producer",
                            "PLA",
                            200.0,
                            60.0,
                            new BigDecimal("20.00"),
                            "Color",
                            "Description",
                            100.0 // quantity
                    );
                    accessoryRepository.saveFilament(accessory);
                }
            }
        }

        // Act & Assert
        DomainException exception = assertThrows(DomainException.class,
                () -> productService.saveProduct(name, ean, accessoriesQ, printHours, printMinutes, price, allegroTax, description));

        // Check that the exception message contains the expected invalid field
        String message = exception.getMessage();
        assertTrue(message.contains(expectedInvalidField), "Expected error message to contain: " + expectedInvalidField);
    }

    static Stream<Arguments> invalidSaveProductParameters() {
        return Stream.of(
                // Invalid name (too short)
                Arguments.of("Pr", "1234567890123", List.of(Pair.of(2.0, "acc1")), 1, 30, "99.99", "5.00", "Valid description", "name"),

                // Invalid EAN (too short)
                Arguments.of("Valid Product", "123456", List.of(Pair.of(2.0, "acc1")), 1, 30, "99.99", "5.00", "Valid description", "ean"),

                // Invalid EAN (non-numeric)
                Arguments.of("Valid Product", "ABCDEF1234567", List.of(Pair.of(2.0, "acc1")), 1, 30, "99.99", "5.00", "Valid description", "ean"),

                // Invalid accessory quantity (negative)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(-1.0, "acc1")), 1, 30, "99.99", "5.00", "Valid description", "accessoriesQ"),

                // Invalid accessory ID (empty string)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(2.0, "")), 1, 30, "99.99", "5.00", "Valid description", "accessoriesQ"),

                // Invalid print time (negative hours)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(2.0, "acc1")), -1, 30, "99.99", "5.00", "Valid description", "printTime"),

                // Invalid print time (minutes > 59)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(2.0, "acc1")), 1, 60, "99.99", "5.00", "Valid description", "printTime"),

                // Invalid price (negative value)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(2.0, "acc1")), 1, 30, "-99.99", "5.00", "Valid description", "price"),

                // Invalid allegro tax (negative value)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(2.0, "acc1")), 1, 30, "99.99", "-5.00", "Valid description", "allegroTax"),

                // Invalid description (too short)
                Arguments.of("Valid Product", "1234567890123", List.of(Pair.of(2.0, "acc1")), 1, 30, "99.99", "5.00", "Sh", "description"),

                // All fields invalid
                Arguments.of(null, "12345", List.of(Pair.of(-2.0, "")), -1, 60, "-99.99", "-5.00", "Short", "name")
        );
    }

    @Test
    @DisplayName("Test updating product with valid inputs")
    void testUpdateProductWithValidInputs() {
        // Arrange
        // Save initial product
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Create accessories
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1",
                "Accessory 1",
                "Producer A",
                "PLA",
                200.0,
                60.0,
                new BigDecimal("20.00"),
                "Red",
                "Red PLA filament",
                100.0 // quantity
        );
        accessoryRepository.saveFilament(accessory1);

        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Prepare update data
        String updatedName = "Updated Product";
        String updatedEan = "1234567890124";
        List<Pair<Double, String>> updatedAccessoriesQ = List.of(Pair.of(3.0, "acc1"), Pair.of(1.0, "acc2"));
        Integer updatedPrintHours = 2;
        Integer updatedPrintMinutes = 45;
        String updatedPrice = "109.99";
        String updatedAllegroTax = "6.50";
        String updatedDescription = "Updated product description.";

        // Create new accessory
        FilamentAccessory accessory2 = new FilamentAccessory(
                "acc2",
                "Accessory 2",
                "Producer B",
                "ABS",
                230.0,
                80.0,
                new BigDecimal("25.00"),
                "Blue",
                "Blue ABS filament",
                50.0 // quantity
        );
        accessoryRepository.saveFilament(accessory2);

        // Act
        Product existingProduct = productRepository.getAllProducts().getFirst();
        productService.updateProduct(existingProduct.id(), updatedName, updatedEan, updatedAccessoriesQ, updatedPrintHours, updatedPrintMinutes, updatedPrice, updatedAllegroTax, updatedDescription);

        // Assert
        Product updatedProduct = productRepository.getProduct(existingProduct.id());
        assertEquals(1, productRepository.getAllProducts().size());
        assertNotNull(updatedProduct, "Updated product should not be null.");
        assertEquals(updatedName, updatedProduct.name());
        assertEquals(updatedEan, updatedProduct.ean());
        assertEquals(2, updatedProduct.accessoriesQ().size());
        assertEquals(updatedPrintHours.intValue(), updatedProduct.printTime().hours());
        assertEquals(updatedPrintMinutes.intValue(), updatedProduct.printTime().minutes());
        assertEquals(new BigDecimal(updatedPrice), updatedProduct.price());
        assertEquals(new BigDecimal(updatedAllegroTax), updatedProduct.allegroTax());
        assertEquals(updatedDescription, updatedProduct.description());
    }

    @Test
    @DisplayName("Test updating product with invalid inputs")
    void testUpdateProductWithInvalidInputs() {
        // Arrange
        // Save initial product
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Create accessories
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1",
                "Accessory 1",
                "Producer A",
                "PLA",
                200.0,
                60.0,
                new BigDecimal("20.00"),
                "Red",
                "Red PLA filament",
                100.0 // quantity
        );
        accessoryRepository.saveFilament(accessory1);

        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Prepare invalid update data
        String invalidName = "Up"; // Invalid name
        String invalidEan = "123"; // Invalid EAN
        List<Pair<Double, String>> invalidAccessoriesQ = List.of(Pair.of(-1.0, "acc1")); // Negative quantity
        Integer invalidPrintHours = -1; // Negative hours
        Integer invalidPrintMinutes = 61; // Invalid minutes (>59)
        String invalidPrice = "-109.99"; // Negative price
        String invalidAllegroTax = "-6.50"; // Negative tax
        String invalidDescription = "Sh"; // Too short description

        // Act
        Product existingProduct = productRepository.getAllProducts().getFirst();
        productService.updateProduct(existingProduct.id(), invalidName, invalidEan, invalidAccessoriesQ, invalidPrintHours, invalidPrintMinutes, invalidPrice, invalidAllegroTax, invalidDescription);

        // Assert
        Product updatedProduct = productRepository.getProduct(existingProduct.id());
        assertNotNull(updatedProduct, "Updated product should not be null.");

        // Since invalid inputs were provided, the original values should remain
        assertEquals(initialName, updatedProduct.name(), "Name should remain unchanged.");
        assertEquals(initialEan, updatedProduct.ean(), "EAN should remain unchanged.");
        assertEquals(1, updatedProduct.accessoriesQ().size(), "Accessories should remain unchanged.");
        assertEquals(initialPrintHours.intValue(), updatedProduct.printTime().hours(), "Print hours should remain unchanged.");
        assertEquals(initialPrintMinutes.intValue(), updatedProduct.printTime().minutes(), "Print minutes should remain unchanged.");
        assertEquals(new BigDecimal(initialPrice), updatedProduct.price(), "Price should remain unchanged.");
        assertEquals(new BigDecimal(initialAllegroTax), updatedProduct.allegroTax(), "Allegro tax should remain unchanged.");
        assertEquals(initialDescription, updatedProduct.description(), "Description should remain unchanged.");
    }
    @Test
    @DisplayName("Test updating preview with valid inputs (valid image formats)")
    void testUpdatePreviewWithValidInputs() {
        // Arrange
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Save initial product
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1", "Accessory 1", "Producer A", "PLA", 200.0, 60.0, new BigDecimal("20.00"),
                "Red", "Red PLA filament", 100.0);
        accessoryRepository.saveFilament(accessory1);
        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Prepare valid preview data
        byte[] previewData = {1, 2, 3};
        String previewFilename = "image.jpg";  // valid format

        // Act
        Product existingProduct = productRepository.getAllProducts().get(0);
        productService.updatePreview(existingProduct.id(), previewData, previewFilename);

        // Assert
        Product updatedProduct = productRepository.getProduct(existingProduct.id());
        assertNotNull(updatedProduct.preview(), "Updated preview should not be null.");
        assertArrayEquals(previewData, updatedProduct.preview().data());
        assertEquals(previewFilename, updatedProduct.preview().filename());
    }

    @Test
    @DisplayName("Test updating preview with invalid inputs (invalid image formats)")
    void testUpdatePreviewWithInvalidFileFormat() {
        // Arrange
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Save initial product
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1", "Accessory 1", "Producer A", "PLA", 200.0, 60.0, new BigDecimal("20.00"),
                "Red", "Red PLA filament", 100.0);
        accessoryRepository.saveFilament(accessory1);
        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Prepare invalid preview data
        byte[] previewData = {1, 2, 3};
        String invalidPreviewFilename = "image.zip";  // invalid format

        // Act & Assert
        Product existingProduct = productRepository.getAllProducts().get(0);
        DomainException exception = assertThrows(DomainException.class,
                () -> productService.updatePreview(existingProduct.id(), previewData, invalidPreviewFilename));

        // Sprawdź czy wyjątek zawiera odpowiedni komunikat o niepoprawnym formacie
        assertTrue(exception.getMessage().contains("Invalid preview file format"));
    }

    @Test
    @DisplayName("Test adding file with valid inputs")
    void testAddFileWithValidInputs() {
        // Arrange
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Save initial product
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1", "Accessory 1", "Producer A", "PLA", 200.0, 60.0, new BigDecimal("20.00"),
                "Red", "Red PLA filament", 100.0);
        accessoryRepository.saveFilament(accessory1);
        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Prepare file data
        byte[] fileData = {4, 5, 6};
        String filename = "document.pdf";

        // Act
        Product existingProduct = productRepository.getAllProducts().get(0);
        productService.addFile(existingProduct.id(), fileData, filename);

        // Assert
        Product updatedProduct = productRepository.getProduct(existingProduct.id());
        assertNotNull(updatedProduct.files(), "Files list should not be null.");
        assertFalse(updatedProduct.files().isEmpty(), "Files list should not be empty.");
        ProductFile addedFile = updatedProduct.files().get(0);
        assertArrayEquals(fileData, addedFile.data());
        assertEquals(filename, addedFile.filename());
    }

    @Test
    @DisplayName("Test adding file with invalid inputs (empty file data)")
    void testAddFileWithInvalidInputs() {
        // Arrange
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Save initial product
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1", "Accessory 1", "Producer A", "PLA", 200.0, 60.0, new BigDecimal("20.00"),
                "Red", "Red PLA filament", 100.0);
        accessoryRepository.saveFilament(accessory1);
        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Prepare invalid file data (empty byte array)
        byte[] invalidFileData = {};
        String filename = "document.pdf";

        // Act & Assert
        Product existingProduct = productRepository.getAllProducts().get(0);
        DomainException exception = assertThrows(DomainException.class,
                () -> productService.addFile(existingProduct.id(), invalidFileData, filename));

        // Sprawdź czy wyjątek zawiera odpowiedni komunikat o niepoprawnych danych pliku
        assertTrue(exception.getMessage().contains("File data cannot be null or empty"));
    }

    @Test
    @DisplayName("Test deleting file by valid file ID")
    void testDeleteFileByIdWithValidId() {
        // Arrange
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Save initial product
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1", "Accessory 1", "Producer A", "PLA", 200.0, 60.0, new BigDecimal("20.00"),
                "Red", "Red PLA filament", 100.0);
        accessoryRepository.saveFilament(accessory1);
        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Add a file to product
        byte[] fileData = {4, 5, 6};
        String filename = "document.pdf";
        Product existingProduct = productRepository.getAllProducts().get(0);
        productService.addFile(existingProduct.id(), fileData, filename);

        // Act: delete file by its ID
        Product updatedProductWithFile = productRepository.getProduct(existingProduct.id());
        String fileIdToDelete = updatedProductWithFile.files().get(0).id();
        productService.deleteFileById(existingProduct.id(), fileIdToDelete);

        // Assert
        Product updatedProduct = productRepository.getProduct(existingProduct.id());
        assertTrue(updatedProduct.files().isEmpty(), "Files list should be empty after deletion.");
    }

    @Test
    @DisplayName("Test deleting file by invalid file ID")
    void testDeleteFileByIdWithInvalidId() {
        // Arrange
        String initialName = "Initial Product";
        String initialEan = "1234567890123";
        List<Pair<Double, String>> initialAccessoriesQ = List.of(Pair.of(2.0, "acc1"));
        Integer initialPrintHours = 1;
        Integer initialPrintMinutes = 30;
        String initialPrice = "99.99";
        String initialAllegroTax = "5.00";
        String initialDescription = "Initial product description.";

        // Save initial product
        FilamentAccessory accessory1 = new FilamentAccessory(
                "acc1", "Accessory 1", "Producer A", "PLA", 200.0, 60.0, new BigDecimal("20.00"),
                "Red", "Red PLA filament", 100.0);
        accessoryRepository.saveFilament(accessory1);
        productService.saveProduct(initialName, initialEan, initialAccessoriesQ, initialPrintHours, initialPrintMinutes, initialPrice, initialAllegroTax, initialDescription);

        // Add a file to product
        byte[] fileData = {4, 5, 6};
        String filename = "document.pdf";
        Product existingProduct = productRepository.getAllProducts().get(0);
        productService.addFile(existingProduct.id(), fileData, filename);

        // Act: try to delete a file by an invalid file ID
        String invalidFileId = "invalid-file-id";
        productService.deleteFileById(existingProduct.id(), invalidFileId);

        // Assert
        Product updatedProduct = productRepository.getProduct(existingProduct.id());
        assertFalse(updatedProduct.files().isEmpty(), "Files list should not be empty since file ID was invalid.");
    }

}

package pl.vabanq.erp.domain.products.product;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.util.Pair;
import pl.vabanq.erp.domain.change.ChangeTrackingService;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;
import pl.vabanq.erp.domain.products.accessory.AccessoryRepository;
import pl.vabanq.erp.domain.products.product.model.AccessoryQuantity;
import pl.vabanq.erp.domain.products.product.model.PrintTime;
import pl.vabanq.erp.domain.products.product.model.Product;
import pl.vabanq.erp.domain.products.product.model.ProductFile;
import pl.vabanq.erp.domain.utility.UUIDGenerator;
import pl.vabanq.erp.domain.utility.ValidationUtils;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProductService.class);
    private static final List<String> VALID_PREVIEW_FORMATS = List.of(
            ".jpg", ".jpeg", ".png", ".webp", ".gif", ".svg", ".bmp", ".tiff", ".tif"
    );
    private final ProductRepository productRepository;
    private final AccessoryRepository accessoryRepository;
    private final ChangeTrackingService changeTrackingService;

    public ProductService(ProductRepository productRepository, AccessoryRepository accessoryRepository, ChangeTrackingService changeTrackingService) {
        this.productRepository = productRepository;
        this.accessoryRepository = accessoryRepository;
        this.changeTrackingService = changeTrackingService;
    }

    public Product saveProduct(String name, String ean, List<Pair<Double, String>> accessoriesQ, Integer printHours,
                               Integer printMinutes, String price, String allegroTax, String description) {
        try {
            LOGGER.info("Attempting to save Product with name: {}", name);
            Validator.validateProduct(name, ean, accessoriesQ, printHours, printMinutes, price, allegroTax);

            BigDecimal formattedPrice = new BigDecimal(price);
            BigDecimal formattedAllegroTax = new BigDecimal(allegroTax);
            PrintTime printTime = new PrintTime(printHours, printMinutes);

            Product product = new Product(
                    UUIDGenerator.generateUUID(),
                    name,
                    ean,
                    map(accessoriesQ),
                    printTime,
                    null,
                    null,
                    formattedPrice,
                    formattedAllegroTax,
                    description
            );

            productRepository.saveProduct(product);
            changeTrackingService.logCreate(product);
            LOGGER.info("Successfully saved Product: {}", product);
            return product;

        } catch (Exception e) {
            LOGGER.error("Error saving Product with name: {}", name, e);
            throw e;
        }
    }

    private List<AccessoryQuantity> map(List<Pair<Double, String>> accessoriesQ) {
        return accessoriesQ.stream()
                .map(pair -> new AccessoryQuantity(accessoryRepository.getAccessory(pair.getSecond()), pair.getFirst()))
                .toList();
    }

    public Product updateProduct(String id, String name, String ean, List<Pair<Double, String>> accessoriesQ, Integer printHours,
                                 Integer printMinutes, String price, String allegroTax, String description) {
        try {
            LOGGER.info("Attempting to update Product with id: {}", id);
            Product oldProduct = productRepository.getProduct(id);

            String updatedName = ValidationUtils.isNameValid(name) ? name : oldProduct.name();
            String updatedEan = Validator.isEanValid(ean) ? ean : oldProduct.ean();
            List<AccessoryQuantity> updatedAccessoriesQ = Validator.isAccessoriesValid(accessoriesQ)
                    ? map(accessoriesQ)
                    : oldProduct.accessoriesQ();
            PrintTime updatedPrintTime = Validator.isPrintTimeValid(printHours, printMinutes)
                    ? new PrintTime(printHours, printMinutes)
                    : oldProduct.printTime();
            BigDecimal updatedPrice = ValidationUtils.isPriceValid(price)
                    ? new BigDecimal(price)
                    : oldProduct.price();
            BigDecimal updatedAllegroTax = ValidationUtils.isPriceValid(allegroTax)
                    ? new BigDecimal(allegroTax)
                    : oldProduct.allegroTax();
            String updatedDescription = ValidationUtils.isDescriptionValid(description)
                    ? description
                    : oldProduct.description();

            Product updatedProduct = new Product(
                    id,
                    updatedName,
                    updatedEan,
                    updatedAccessoriesQ,
                    updatedPrintTime,
                    oldProduct.preview(),
                    oldProduct.files(),
                    updatedPrice,
                    updatedAllegroTax,
                    updatedDescription
            );

            productRepository.saveProduct(updatedProduct);
            changeTrackingService.logUpdate(oldProduct, updatedProduct);
            LOGGER.info("Successfully updated Product: {}", updatedProduct);
            return updatedProduct;
        } catch (Exception e) {
            LOGGER.error("Error updating Product with id: {}", id, e);
            throw e;
        }
    }

    // Metoda updatePreview z dodaną walidacją
    public Product updatePreview(String id, byte[] previewData, String filename) {
        try {
            LOGGER.info("Attempting to update preview for Product with id: {}", id);
            Product oldProduct = productRepository.getProduct(id);

            Validator.validatePreviewFile(previewData, filename);  // Walidacja pliku podglądu

            ProductFile updatedPreview = new ProductFile(UUIDGenerator.generateUUID(), previewData, filename);

            Product updatedProduct = new Product(
                    id,
                    oldProduct.name(),
                    oldProduct.ean(),
                    oldProduct.accessoriesQ(),
                    oldProduct.printTime(),
                    updatedPreview,  // update preview
                    oldProduct.files(),
                    oldProduct.price(),
                    oldProduct.allegroTax(),
                    oldProduct.description()
            );

            productRepository.saveProduct(updatedProduct);
            changeTrackingService.logUpdate(oldProduct, updatedProduct);
            LOGGER.info("Successfully updated preview for Product: {}", updatedProduct);
            return updatedProduct;
        } catch (Exception e) {
            LOGGER.error("Error updating preview for Product with id: {}", id, e);
            throw e;
        }
    }

    // Metoda addFile z dodaną walidacją
    public Product addFile(String id, byte[] fileData, String filename) {
        try {
            LOGGER.info("Attempting to add file to Product with id: {}", id);
            Product oldProduct = productRepository.getProduct(id);

            Validator.validateFile(fileData, filename);  // Walidacja dodawanego pliku

            List<ProductFile> updatedFiles = new ArrayList<>(oldProduct.files());
            ProductFile newFile = new ProductFile(UUIDGenerator.generateUUID(), fileData, filename);
            updatedFiles.add(newFile);

            Product updatedProduct = new Product(
                    id,
                    oldProduct.name(),
                    oldProduct.ean(),
                    oldProduct.accessoriesQ(),
                    oldProduct.printTime(),
                    oldProduct.preview(),  // keep the current preview
                    updatedFiles,  // add new file
                    oldProduct.price(),
                    oldProduct.allegroTax(),
                    oldProduct.description()
            );

            productRepository.saveProduct(updatedProduct);
            changeTrackingService.logUpdate(oldProduct, updatedProduct);
            LOGGER.info("Successfully added file to Product: {}", updatedProduct);
            return updatedProduct;
        } catch (Exception e) {
            LOGGER.error("Error adding file to Product with id: {}", id, e);
            throw e;
        }
    }

    public void deleteFileById(String productId, String fileId) {
        try {
            LOGGER.info("Attempting to delete file with id: {} from Product with id: {}", fileId, productId);
            Product oldProduct = productRepository.getProduct(productId);

            List<ProductFile> updatedFiles = oldProduct.files().stream()
                    .filter(file -> !file.id().equals(fileId)) // remove preview by id
                    .toList();

            Product updatedProduct = new Product(
                    productId,
                    oldProduct.name(),
                    oldProduct.ean(),
                    oldProduct.accessoriesQ(),
                    oldProduct.printTime(),
                    oldProduct.preview(),
                    updatedFiles,
                    oldProduct.price(),
                    oldProduct.allegroTax(),
                    oldProduct.description()
            );

            productRepository.saveProduct(updatedProduct);
            changeTrackingService.logUpdate(oldProduct, updatedProduct);
            LOGGER.info("Successfully deleted file from Product: {}", updatedProduct);
        } catch (Exception e) {
            LOGGER.error("Error deleting file from Product with id: {}", productId, e);
            throw e;
        }
    }

    public List<Product> getAllProducts() {
        return productRepository.getAllProducts();
    }

    private static class Validator {

        static void validateProduct(String name, String ean, List<Pair<Double, String>> accessoriesQ,
                                    Integer printHours, Integer printMinutes, String price, String allegroTax) {
            ValidationUtils.validateName(name);
            if (!isEanValid(ean)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "ean", ean);
            }
            if (!isAccessoriesValid(accessoriesQ)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "accessoriesQ", accessoriesQ.toString());
            }
            if (!isPrintTimeValid(printHours, printMinutes)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "printTime", printHours + ":" + printMinutes);
            }
            ValidationUtils.validatePrice("price", price);
            ValidationUtils.validatePrice("allegroTax", allegroTax);
        }

        static void validatePreviewFile(byte[] data, String filename) {
            validateFile(data, filename);
            String fileExtension = getFileExtension(filename).toLowerCase();
            if (!VALID_PREVIEW_FORMATS.contains(fileExtension)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "preview", "Invalid preview file format: " + fileExtension);
            }
        }

        static void validateFile(byte[] data, String filename) {
            if (data == null || data.length == 0) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "file", "File data cannot be null or empty.");
            }
            if (filename == null || filename.trim().isEmpty()) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "filename", "Filename cannot be null or empty.");
            }
        }

        private static String getFileExtension(String filename) {
            int dotIndex = filename.lastIndexOf(".");
            if (dotIndex >= 0 && dotIndex < filename.length() - 1) {
                return filename.substring(dotIndex);
            }
            throw new DomainException(ErrorCode.INVALID_VALUE, "filename", "Invalid filename, missing file extension: " + filename);
        }

        static boolean isEanValid(String ean) {
            return ean != null && ean.matches("\\d{13}");
        }

        static boolean isAccessoriesValid(List<Pair<Double, String>> accessoriesQ) {
            if (accessoriesQ == null || accessoriesQ.isEmpty()) {
                return false;
            }
            for (Pair<Double, String> pair : accessoriesQ) {
                if (pair.getFirst() <= 0 || pair.getSecond().trim().isEmpty()) {
                    return false;
                }
            }
            return true;
        }

        static boolean isPrintTimeValid(Integer hours, Integer minutes) {
            return hours != null && minutes != null && hours >= 0 && minutes >= 0 && minutes < 60;
        }
    }
}

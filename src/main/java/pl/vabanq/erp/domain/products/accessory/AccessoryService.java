package pl.vabanq.erp.domain.products.accessory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.products.accessory.model.PackagingAccessory;
import pl.vabanq.erp.domain.change.ChangeTrackingService;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;
import pl.vabanq.erp.domain.utility.UUIDGenerator;
import pl.vabanq.erp.domain.utility.ValidationUtils;

import java.math.BigDecimal;
import java.util.List;

public class AccessoryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccessoryService.class);
    private final ChangeTrackingService changeTrackingService;
    private final AccessoryRepository accessoryRepository;

    public AccessoryService(ChangeTrackingService changeTrackingService, AccessoryRepository accessoryRepository) {
        this.changeTrackingService = changeTrackingService;
        this.accessoryRepository = accessoryRepository;
    }

    public FilamentAccessory saveFilament(String name, String producer, String filamentType, String printTemperature,
                                          String deskTemperature, String pricePerKg, String color, String description, String quantity) {
        try {
            LOGGER.info("Attempting to save FilamentAccessory with name: {}", name);
            Validator.validateFilament(name, producer, filamentType, printTemperature, deskTemperature, pricePerKg, color, quantity);

            double formattedPrintTemperature = Double.parseDouble(printTemperature);
            double formattedDeskTemperature = Double.parseDouble(deskTemperature);
            BigDecimal formattedPricePerKg = new BigDecimal(pricePerKg);
            double formattedQuantity = Double.parseDouble(quantity);

            FilamentAccessory filamentAccessory = new FilamentAccessory(
                    UUIDGenerator.generateUUID(),
                    name,
                    producer,
                    filamentType,
                    formattedPrintTemperature,
                    formattedDeskTemperature,
                    formattedPricePerKg,
                    color,
                    description,
                    formattedQuantity
            );

            accessoryRepository.saveFilament(filamentAccessory);
            changeTrackingService.logCreate(filamentAccessory);
            LOGGER.info("Successfully saved FilamentAccessory: {}", filamentAccessory);
            return filamentAccessory;

        } catch (Exception e) {
            LOGGER.error("Error saving FilamentAccessory with name: {}", name, e);
            throw e;
        }
    }

    public FilamentAccessory updateFilament(String id, String name, String producer, String filamentType, String printTemperature,
                                            String deskTemperature, String pricePerKg, String color, String description, String quantity) {
        try {
            LOGGER.info("Attempting to update FilamentAccessory with id: {}", id);
            FilamentAccessory oldFilament = accessoryRepository.getFilamentAccessory(id);

            String updatedProducer = Validator.isProducerValid(producer) ? producer : oldFilament.producer();
            String updatedName = ValidationUtils.isNameValid(name) ? name : oldFilament.name();
            String updatedFilamentType = Validator.isFilamentTypeValid(filamentType) ? filamentType : oldFilament.filamentType();
            double updatedPrintTemperature = Validator.isTemperatureValid(printTemperature)
                    ? Double.parseDouble(printTemperature) : oldFilament.printTemperature();
            double updatedDeskTemperature = Validator.isTemperatureValid(deskTemperature)
                    ? Double.parseDouble(deskTemperature) : oldFilament.deskTemperature();
            BigDecimal updatedPricePerKg = ValidationUtils.isPriceValid(pricePerKg)
                    ? new BigDecimal(pricePerKg) : oldFilament.pricePerKg();
            String updatedColor = Validator.isColorValid(color) ? color : oldFilament.color();
            String updatedDescription = ValidationUtils.isDescriptionValid(description)
                    ? description : oldFilament.description();
            double updatedQuantity = Validator.isQuantityValid(quantity)
                    ? Double.parseDouble(quantity) : oldFilament.quantity();

            FilamentAccessory updatedFilament = new FilamentAccessory(
                    id,
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

            accessoryRepository.saveFilament(updatedFilament);
            changeTrackingService.logUpdate(oldFilament, updatedFilament);
            LOGGER.info("Successfully updated FilamentAccessory: {}", updatedFilament);
            return updatedFilament;

        } catch (Exception e) {
            LOGGER.error("Error updating FilamentAccessory with id: {}", id, e);
            throw e;
        }
    }

    public PackagingAccessory savePackagingAccessory(String name, String packagingSize, String dimensions,
                                                     String netPricePerQuantity, String quantity) {
        try {
            LOGGER.info("Attempting to save PackagingAccessory with name: {}", name);
            Validator.validatePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity, quantity);

            BigDecimal formattedNetPricePerQuantity = new BigDecimal(netPricePerQuantity);
            double formattedQuantity = Double.parseDouble(quantity);

            PackagingAccessory packagingAccessory = new PackagingAccessory(
                    UUIDGenerator.generateUUID(),
                    name,
                    packagingSize,
                    dimensions,
                    formattedNetPricePerQuantity,
                    formattedQuantity
            );

            accessoryRepository.savePackagingAccessory(packagingAccessory);
            changeTrackingService.logCreate(packagingAccessory);
            LOGGER.info("Successfully saved PackagingAccessory: {}", packagingAccessory);
            return packagingAccessory;

        } catch (Exception e) {
            LOGGER.error("Error saving PackagingAccessory with name: {}", name, e);
            throw e;
        }
    }

    public PackagingAccessory updatePackagingAccessory(String id, String name, String packagingSize, String dimensions,
                                                       String netPricePerQuantity, String quantity) {
        try {
            LOGGER.info("Attempting to update PackagingAccessory with id: {}", id);
            PackagingAccessory oldPackagingAccessory = accessoryRepository.getPackagingAccessory(id);

            String updatedName = ValidationUtils.isNameValid(name) ? name : oldPackagingAccessory.name();
            String updatedPackagingSize = Validator.isPackagingSizeValid(packagingSize)
                    ? packagingSize : oldPackagingAccessory.packagingSize();
            String updatedDimensions = Validator.isDimensionsValid(dimensions)
                    ? dimensions : oldPackagingAccessory.dimensions();
            BigDecimal updatedNetPricePerQuantity = ValidationUtils.isPriceValid(netPricePerQuantity)
                    ? new BigDecimal(netPricePerQuantity) : oldPackagingAccessory.netPricePerQuantity();
            double updatedQuantity = Validator.isQuantityValid(quantity)
                    ? Double.parseDouble(quantity) : oldPackagingAccessory.quantity();

            PackagingAccessory updatedPackagingAccessory = new PackagingAccessory(
                    id,
                    updatedName,
                    updatedPackagingSize,
                    updatedDimensions,
                    updatedNetPricePerQuantity,
                    updatedQuantity
            );

            accessoryRepository.savePackagingAccessory(updatedPackagingAccessory);
            changeTrackingService.logUpdate(oldPackagingAccessory, updatedPackagingAccessory);
            LOGGER.info("Successfully updated PackagingAccessory: {}", updatedPackagingAccessory);
            return updatedPackagingAccessory;

        } catch (Exception e) {
            LOGGER.error("Error updating PackagingAccessory with id: {}", id, e);
            throw e;
        }
    }

    public FastenersAccessory saveFastenersAccessory(String name, String netPricePerQuantity, String quantity) {
        try {
            LOGGER.info("Attempting to save FastenersAccessory with name: {}", name);
            Validator.validateFastenersAccessory(name, netPricePerQuantity, quantity);

            BigDecimal formattedNetPricePerQuantity = new BigDecimal(netPricePerQuantity);
            double formattedQuantity = Double.parseDouble(quantity);

            FastenersAccessory fastenersAccessory = new FastenersAccessory(
                    UUIDGenerator.generateUUID(),
                    name,
                    formattedNetPricePerQuantity,
                    formattedQuantity
            );

            accessoryRepository.saveFastenersAccessory(fastenersAccessory);
            changeTrackingService.logCreate(fastenersAccessory);
            LOGGER.info("Successfully saved FastenersAccessory: {}", fastenersAccessory);
            return fastenersAccessory;

        } catch (Exception e) {
            LOGGER.error("Error saving FastenersAccessory with name: {}", name, e);
            throw e;
        }
    }

    public FastenersAccessory updateFastenersAccessory(String id, String name, String netPricePerQuantity, String quantity) {
        try {
            LOGGER.info("Attempting to update FastenersAccessory with id: {}", id);
            FastenersAccessory oldAccessory = accessoryRepository.getFastenersAccessory(id);

            String updatedName = ValidationUtils.isNameValid(name) ? name : oldAccessory.name();
            BigDecimal updatedNetPricePerQuantity = ValidationUtils.isPriceValid(netPricePerQuantity)
                    ? new BigDecimal(netPricePerQuantity) : oldAccessory.netPricePerQuantity();
            double updatedQuantity = Validator.isQuantityValid(quantity)
                    ? Double.parseDouble(quantity) : oldAccessory.quantity();

            FastenersAccessory updatedAccessory = new FastenersAccessory(
                    id,
                    updatedName,
                    updatedNetPricePerQuantity,
                    updatedQuantity
            );

            accessoryRepository.saveFastenersAccessory(updatedAccessory);
            changeTrackingService.logUpdate(oldAccessory, updatedAccessory);
            LOGGER.info("Successfully updated FastenersAccessory: {}", updatedAccessory);
            return updatedAccessory;

        } catch (Exception e) {
            LOGGER.error("Error updating FastenersAccessory with id: {}", id, e);
            throw e;
        }
    }

    public List<FilamentAccessory> getAllFilaments() {
        LOGGER.info("Fetching all FilamentAccessories");
        return accessoryRepository.getAllFilaments();
    }

    public List<PackagingAccessory> getAllPackagingAccessories() {
        LOGGER.info("Fetching all PackagingAccessories");
        return accessoryRepository.getAllPackagingAccessories();
    }

    public List<FastenersAccessory> getAllFasteners() {
        LOGGER.info("Fetching all FastenersAccessories");
        return accessoryRepository.getAllFasteners();
    }

    private static class Validator {

        static void validateFilament(String name, String producer, String filamentType, String printTemperature,
                                     String deskTemperature, String pricePerKg, String color, String quantity) {
            ValidationUtils.validateName(name);
            validateProducer(producer);
            if (!isFilamentTypeValid(filamentType)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "filamentType", filamentType);
            }
            validateTemperature(printTemperature, "printTemperature");
            validateTemperature(deskTemperature, "deskTemperature");
            ValidationUtils.validatePrice("pricePerKg", pricePerKg);
            validateColor(color);
            validateQuantity(quantity);
        }

        static boolean isFilamentTypeValid(String filamentType) {
            return filamentType != null && filamentType.trim().length() >= 3;
        }

        static void validatePackagingAccessory(String name, String packagingSize, String dimensions,
                                               String netPricePerQuantity, String quantity) {
            ValidationUtils.validateName(name);
            if (!isPackagingSizeValid(packagingSize)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "packagingSize", packagingSize);
            }
            validateDimensions(dimensions);
            ValidationUtils.validatePrice("netPricePerQuantity", netPricePerQuantity);
            validateQuantity(quantity);
        }

        static boolean isPackagingSizeValid(String packagingSize) {
            return packagingSize != null && !packagingSize.trim().isEmpty();
        }

        static void validateFastenersAccessory(String name, String netPricePerQuantity, String quantity) {
            ValidationUtils.validateName(name);
            ValidationUtils.validatePrice("netPricePerQuantity", netPricePerQuantity);
            validateQuantity(quantity);
        }

        static void validateTemperature(String temperature, String fieldName) {
            if (!isTemperatureValid(temperature)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, fieldName, temperature);
            }
        }

        static boolean isTemperatureValid(String temperature) {
            if (temperature == null) {
                return false;
            }
            try {
                double temp = Double.parseDouble(temperature);
                return temp > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        static void validateColor(String color) {
            if (!isColorValid(color)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "color", color);
            }
        }

        static boolean isColorValid(String color) {
            return color != null && color.matches("^#([A-Fa-f0-9]{6})$");
        }

        static void validateDimensions(String dimensions) {
            if (!isDimensionsValid(dimensions)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "dimensions", dimensions);
            }
        }

        static boolean isDimensionsValid(String dimensions) {
            if (dimensions == null) return false;
            String[] parts = dimensions.split("x");
            if (parts.length != 3) return false;
            try {
                Double.parseDouble(parts[0].trim());
                Double.parseDouble(parts[1].trim());
                Double.parseDouble(parts[2].trim());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        static void validateProducer(String producer) {
            if (!isProducerValid(producer)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "producer", producer);
            }
        }

        static boolean isProducerValid(String producer) {
            return producer != null && producer.trim().length() >= 3;
        }

        static void validateQuantity(String quantity) {
            if (!isQuantityValid(quantity)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "quantity", quantity);
            }
        }

        static boolean isQuantityValid(String quantity) {
            if (quantity == null) return false;
            try {
                double qty = Double.parseDouble(quantity);
                return qty >= 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
}

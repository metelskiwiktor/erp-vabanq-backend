package pl.vabanq.erp.domain.accessory;

import pl.vabanq.erp.domain.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.accessory.model.PackagingAccessory;
import pl.vabanq.erp.domain.change.ChangeTrackingService;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;
import pl.vabanq.erp.domain.utility.UUIDGenerator;

import java.math.BigDecimal;
import java.util.List;

public class AccessoryService {
    private final ChangeTrackingService changeTrackingService;
    private final AccessoryRepository accessoryRepository;

    public AccessoryService(ChangeTrackingService changeTrackingService, AccessoryRepository accessoryRepository) {
        this.changeTrackingService = changeTrackingService;
        this.accessoryRepository = accessoryRepository;
    }

    public void saveFilament(String name, String producer, String filamentType, String printTemperature,
                             String deskTemperature, String pricePerKg, String color, String description) {
        Validator.validateFilament(name, producer, filamentType, printTemperature, deskTemperature, pricePerKg, color);

        double formattedPrintTemperature = Double.parseDouble(printTemperature);
        double formattedDeskTemperature = Double.parseDouble(deskTemperature);
        BigDecimal formattedPricePerKg = new BigDecimal(pricePerKg);

        FilamentAccessory filamentAccessory = new FilamentAccessory(
                UUIDGenerator.generateUUID(),
                name,
                producer,
                filamentType,
                formattedPrintTemperature,
                formattedDeskTemperature,
                formattedPricePerKg,
                color,
                description
        );

        accessoryRepository.saveFilament(filamentAccessory);
        changeTrackingService.logCreate(filamentAccessory);
    }

    public void updateFilament(String id, String name, String producer, String filamentType, String printTemperature,
                               String deskTemperature, String pricePerKg, String color, String description) {
        FilamentAccessory oldFilament = accessoryRepository.getFilamentAccessory(id);

        String updatedProducer = Validator.isProducerValid(producer) ? producer : oldFilament.producer();
        String updatedName = Validator.isNameValid(name) ? name : oldFilament.name();
        String updatedFilamentType = Validator.isFilamentTypeValid(filamentType) ? filamentType : oldFilament.filamentType();
        double updatedPrintTemperature = Validator.isTemperatureValid(printTemperature)
                ? Double.parseDouble(printTemperature) : oldFilament.printTemperature();
        double updatedDeskTemperature = Validator.isTemperatureValid(deskTemperature)
                ? Double.parseDouble(deskTemperature) : oldFilament.deskTemperature();
        BigDecimal updatedPricePerKg = Validator.isPriceValid(pricePerKg)
                ? new BigDecimal(pricePerKg) : oldFilament.pricePerKg();
        String updatedColor = Validator.isColorValid(color) ? color : oldFilament.color();
        String updatedDescription = description != null && !description.trim().isEmpty()
                ? description : oldFilament.description();

        FilamentAccessory updatedFilament = new FilamentAccessory(
                id,
                updatedName,
                updatedProducer,
                updatedFilamentType,
                updatedPrintTemperature,
                updatedDeskTemperature,
                updatedPricePerKg,
                updatedColor,
                updatedDescription
        );

        accessoryRepository.saveFilament(updatedFilament);
        changeTrackingService.logUpdate(oldFilament, updatedFilament);
    }

    public void savePackagingAccessory(String name, String packagingSize, String dimensions,
                                       String netPricePerQuantity) {
        Validator.validatePackagingAccessory(name, packagingSize, dimensions, netPricePerQuantity);

        BigDecimal formattedNetPricePerQuantity = new BigDecimal(netPricePerQuantity);
        PackagingAccessory packagingAccessory = new PackagingAccessory(
                UUIDGenerator.generateUUID(),
                name,
                packagingSize,
                dimensions,
                formattedNetPricePerQuantity
        );

        accessoryRepository.savePackagingAccessory(packagingAccessory);
        changeTrackingService.logCreate(packagingAccessory);
    }

    public void updatePackagingAccessory(String id, String name, String packagingSize, String dimensions,
                                         String netPricePerQuantity) {
        PackagingAccessory oldPackagingAccessory = accessoryRepository.getPackagingAccessory(id);

        String updatedName = Validator.isNameValid(name) ? name : oldPackagingAccessory.name();
        String updatedPackagingSize = Validator.isPackagingSizeValid(packagingSize)
                ? packagingSize : oldPackagingAccessory.packagingSize();
        String updatedDimensions = Validator.isDimensionsValid(dimensions)
                ? dimensions : oldPackagingAccessory.dimensions();
        BigDecimal updatedNetPricePerQuantity = Validator.isPriceValid(netPricePerQuantity)
                ? new BigDecimal(netPricePerQuantity) : oldPackagingAccessory.netPricePerQuantity();

        PackagingAccessory updatedPackagingAccessory = new PackagingAccessory(
                id,
                updatedName,
                updatedPackagingSize,
                updatedDimensions,
                updatedNetPricePerQuantity
        );

        accessoryRepository.savePackagingAccessory(updatedPackagingAccessory);
        changeTrackingService.logUpdate(oldPackagingAccessory, updatedPackagingAccessory);
    }

    public void saveFastenersAccessory(String name, String netPricePerQuantity) {
        Validator.validateFastenersAccessory(name, netPricePerQuantity);

        BigDecimal formattedNetPricePerQuantity = new BigDecimal(netPricePerQuantity);

        FastenersAccessory fastenersAccessory = new FastenersAccessory(
                UUIDGenerator.generateUUID(),
                name,
                formattedNetPricePerQuantity
        );

        accessoryRepository.saveFastenersAccessory(fastenersAccessory);
        changeTrackingService.logCreate(fastenersAccessory);
    }

    public void updateFastenersAccessory(String id, String name, String netPricePerQuantity) {
        FastenersAccessory oldAccessory = accessoryRepository.getFastenersAccessory(id);

        String updatedName = Validator.isNameValid(name) ? name : oldAccessory.name();
        BigDecimal updatedNetPricePerQuantity = Validator.isPriceValid(netPricePerQuantity)
                ? new BigDecimal(netPricePerQuantity) : oldAccessory.netPricePerQuantity();

        FastenersAccessory updatedAccessory = new FastenersAccessory(
                id,
                updatedName,
                updatedNetPricePerQuantity
        );

        accessoryRepository.saveFastenersAccessory(updatedAccessory);
        changeTrackingService.logUpdate(oldAccessory, updatedAccessory);
    }

    public List<FilamentAccessory> getAllFilaments() {
        return accessoryRepository.getAllFilaments();
    }

    public List<PackagingAccessory> getAllPackagingAccessories() {
        return accessoryRepository.getAllPackagingAccessories();
    }

    private static class Validator {
        static void validateFilament(String name, String producer, String filamentType, String printTemperature,
                                     String deskTemperature, String pricePerKg, String color) {
            if (!isNameValid(name)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "name", name);
            }
            if (!isProducerValid(producer)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "producer", producer);
            }
            if (!isFilamentTypeValid(filamentType)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "filamentType", filamentType);
            }
            if (!isTemperatureValid(printTemperature)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "printTemperature", printTemperature);
            }
            if (!isTemperatureValid(deskTemperature)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "deskTemperature", deskTemperature);
            }
            if (!isPriceValid(pricePerKg)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "pricePerKg", pricePerKg);
            }
            if (!isColorValid(color)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "color", color);
            }
        }

        static void validatePackagingAccessory(String name, String packagingSize, String dimensions,
                                               String netPricePerQuantity) {
            if (!isNameValid(name)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "name", name);
            }
            if (!isPackagingSizeValid(packagingSize)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "packagingSize", packagingSize);
            }
            if (!isDimensionsValid(dimensions)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "dimensions", dimensions);
            }
            if (!isPriceValid(netPricePerQuantity)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "netPricePerQuantity", netPricePerQuantity);
            }
        }

        static void validateFastenersAccessory(String name, String netPricePerQuantity) {
            if (!isNameValid(name)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "name", name);
            }
            if (!isPriceValid(netPricePerQuantity)) {
                throw new DomainException(ErrorCode.INVALID_VALUE, "netPricePerQuantity", netPricePerQuantity);
            }
        }

        static boolean isNameValid(String name) {
            return name != null && name.trim().length() >= 3;
        }

        static boolean isProducerValid(String producer) {
            return producer != null && producer.trim().length() >= 3;
        }

        static boolean isFilamentTypeValid(String filamentType) {
            return filamentType != null && filamentType.trim().length() >= 3;
        }

        static boolean isTemperatureValid(String printTemperature) {
            if (printTemperature == null) {
                return false;
            }
            try {
                double temp = Double.parseDouble(printTemperature);
                return temp > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        static boolean isPriceValid(String pricePerKg) {
            if (pricePerKg == null) {
                return false;
            }
            try {
                BigDecimal price = new BigDecimal(pricePerKg);
                return price.compareTo(BigDecimal.ZERO) > 0 && price.scale() <= 2;
            } catch (NumberFormatException e) {
                return false;
            }
        }

        static boolean isColorValid(String color) {
            return color != null && color.matches("^#([A-Fa-f0-9]{6})$");
        }

        static boolean isPackagingSizeValid(String packagingSize) {
            return packagingSize != null && !packagingSize.trim().isEmpty();
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
    }
}

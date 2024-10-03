package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.api.response.*;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.products.accessory.model.PackagingAccessory;
import pl.vabanq.erp.domain.products.product.model.Product;

import java.util.List;

@Component
public class ProductToProductResponseConverter implements Converter<Product, ProductResponse> {

    @Override
    public ProductResponse convert(Product source) {
        // Konwersja FastenersAccessory
        List<Pair<Double, FastenersAccessoryResponse>> fasteners = source.accessoriesQ().stream()
                .filter(accessoryQuantity -> accessoryQuantity.accessory() instanceof FastenersAccessory)
                .map(accessoryQuantity -> Pair.of(accessoryQuantity.quantity(),
                        toFastenersAccessoryResponse((FastenersAccessory) accessoryQuantity.accessory())))
                .toList();

        // Konwersja FilamentAccessory
        List<Pair<Double, FilamentAccessoryResponse>> filaments = source.accessoriesQ().stream()
                .filter(accessoryQuantity -> accessoryQuantity.accessory() instanceof FilamentAccessory)
                .map(accessoryQuantity -> Pair.of(accessoryQuantity.quantity(),
                        toFilamentAccessoryResponse((FilamentAccessory) accessoryQuantity.accessory())))
                .toList();

        // Konwersja PackagingAccessory
        List<Pair<Double, PackagingAccessoryResponse>> packagings = source.accessoriesQ().stream()
                .filter(accessoryQuantity -> accessoryQuantity.accessory() instanceof PackagingAccessory)
                .map(accessoryQuantity -> Pair.of(accessoryQuantity.quantity(),
                        toPackagingAccessoryResponse((PackagingAccessory) accessoryQuantity.accessory())))
                .toList();

        // Tworzenie ProductAccessoriesResponse na podstawie skonwertowanych akcesoriów
        ProductAccessoriesResponse productAccessories = new ProductAccessoriesResponse(fasteners, filaments, packagings);

        // Tworzenie i zwracanie ProductResponse
        return new ProductResponse(
                source.id(),
                source.name(),
                source.ean(),
                productAccessories,
                source.printTime(),
                source.price(),
                source.allegroTax(),
                source.description(),
                source.preview(),
                source.files()
        );
    }

    // Metoda pomocnicza do konwersji FastenersAccessory do FastenersAccessoryResponse
    private FastenersAccessoryResponse toFastenersAccessoryResponse(FastenersAccessory accessory) {
        return new FastenersAccessoryResponse(
                accessory.id(),
                accessory.name(),
                accessory.netPricePerQuantity(),
                accessory.quantity()
        );
    }

    // Metoda pomocnicza do konwersji FilamentAccessory do FilamentAccessoryResponse
    private FilamentAccessoryResponse toFilamentAccessoryResponse(FilamentAccessory accessory) {
        return new FilamentAccessoryResponse(
                accessory.id(),
                accessory.name(),
                accessory.producer(),
                accessory.filamentType(),
                accessory.printTemperature(),
                accessory.deskTemperature(),
                accessory.pricePerKg(),
                accessory.color(),
                accessory.description(),
                accessory.quantity()
        );
    }

    // Metoda pomocnicza do konwersji PackagingAccessory do PackagingAccessoryResponse
    private PackagingAccessoryResponse toPackagingAccessoryResponse(PackagingAccessory accessory) {
        return new PackagingAccessoryResponse(
                accessory.id(),
                accessory.name(),
                accessory.packagingSize(),
                accessory.dimensions(),
                accessory.netPricePerQuantity(),
                accessory.quantity()
        );
    }
}

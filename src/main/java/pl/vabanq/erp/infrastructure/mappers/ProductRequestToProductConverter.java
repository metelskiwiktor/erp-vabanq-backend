package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.data.util.Pair;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.api.request.ProductRequest;
import pl.vabanq.erp.domain.products.product.model.AccessoryQuantity;
import pl.vabanq.erp.domain.products.product.model.PrintTime;
import pl.vabanq.erp.domain.products.product.model.Product;
import pl.vabanq.erp.infrastructure.database.accessory.AccessoryRepositoryJPA;

import java.math.BigDecimal;
import java.util.List;

@Component
public class ProductRequestToProductConverter implements Converter<ProductRequest, Product> {

    private final AccessoryRepositoryJPA accessoryRepository;

    public ProductRequestToProductConverter(AccessoryRepositoryJPA accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
    }

    @Override
    public Product convert(ProductRequest source) {
        BigDecimal price = new BigDecimal(source.price());
        BigDecimal allegroTax = new BigDecimal(source.allegroTax());
        PrintTime printTime = new PrintTime(source.printHours(), source.printMinutes());

        return new Product(
                null, // UUID will be generated at the service level
                source.name(),
                source.ean(),
                mapAccessories(source.accessoriesQ()),
                printTime,
                null,
                null,
                price,
                allegroTax,
                source.description()
        );
    }

    private List<AccessoryQuantity> mapAccessories(List<Pair<Double, String>> accessoriesQ) {
        return accessoriesQ.stream()
                .map(pair -> new AccessoryQuantity(
                        accessoryRepository.getAccessory(pair.getSecond()), pair.getFirst()))
                .toList();
    }
}

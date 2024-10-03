package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.domain.products.product.model.*;
import pl.vabanq.erp.infrastructure.database.product.entity.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductToProductJPA implements Converter<Product, ProductJPA> {
    @Override
    public ProductJPA convert(Product product) {
        List<AccessoryQuantityEmbeddable> accessoriesQEntities = product.accessoriesQ().stream()
                .map(aq -> new AccessoryQuantityEmbeddable(aq.accessory().id(), aq.quantity()))
                .collect(Collectors.toList());

        List<ProductFileEmbeddable> filesEntities = null;
        if (product.files() != null) {
            filesEntities = product.files().stream()
                    .map(pf -> new ProductFileEmbeddable(pf.id(), pf.data(), pf.filename()))
                    .toList();
        }

        ProductFileEmbeddable productFileEmbeddable = null;
        if (product.preview() != null) {
            productFileEmbeddable = new ProductFileEmbeddable(product.preview().id(), product.preview().data(), product.preview().filename());
        }

        return new ProductJPA(
                product.id(),
                product.name(),
                product.ean(),
                accessoriesQEntities,
                new PrintTimeEmbeddable(product.printTime().hours(), product.printTime().minutes()),
                productFileEmbeddable,
                filesEntities,
                product.price(),
                product.allegroTax(),
                product.description()
        );
    }
}

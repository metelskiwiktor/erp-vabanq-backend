package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.domain.products.product.model.*;
import pl.vabanq.erp.infrastructure.database.accessory.AccessoryRepositoryJPA;
import pl.vabanq.erp.infrastructure.database.product.entity.*;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductJPAToProduct implements Converter<ProductJPA, Product> {

    private final AccessoryRepositoryJPA accessoryRepository;

    public ProductJPAToProduct(AccessoryRepositoryJPA accessoryRepository) {
        this.accessoryRepository = accessoryRepository;
    }

    @Override
    public Product convert(ProductJPA productJPA) {
        List<AccessoryQuantity> accessoriesQDomain = productJPA.getAccessoriesQ().stream()
                .map(aq -> new AccessoryQuantity(
                        accessoryRepository.getAccessory(aq.getAccessoryId()),
                        aq.getQuantity()
                ))
                .collect(Collectors.toList());

        List<ProductFile> filesDomain = productJPA.getFiles().stream()
                .map(pf -> new ProductFile(pf.getFileId(), pf.getData(), pf.getFilename()))
                .collect(Collectors.toList());

        ProductFile productFile = null;
        if (productJPA.getFile() != null) {
            productFile = new ProductFile(
                    productJPA.getFile().getFileId(),
                    productJPA.getFile().getData(),
                    productJPA.getFile().getFilename()
            );
        }

        return new Product(
                productJPA.getId(),
                productJPA.getName(),
                productJPA.getEan(),
                accessoriesQDomain,
                new PrintTime(
                        productJPA.getPrintTime().getHours(),
                        productJPA.getPrintTime().getMinutes()
                ),
                productFile,
                filesDomain,
                productJPA.getPrice(),
                productJPA.getAllegroTax(),
                productJPA.getDescription()
        );
    }
}

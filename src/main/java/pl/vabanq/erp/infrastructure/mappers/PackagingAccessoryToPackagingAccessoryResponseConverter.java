package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.api.response.accessory.PackagingAccessoryResponse;
import pl.vabanq.erp.domain.products.accessory.model.PackagingAccessory;

@Component
public class PackagingAccessoryToPackagingAccessoryResponseConverter implements Converter<PackagingAccessory, PackagingAccessoryResponse> {

    @Override
    public PackagingAccessoryResponse convert(PackagingAccessory source) {
        return new PackagingAccessoryResponse(
                source.id(),
                source.name(),
                source.packagingSize(),
                source.dimensions(),
                source.netPricePerQuantity(),
                source.quantity(),
                source.description()
        );
    }
}

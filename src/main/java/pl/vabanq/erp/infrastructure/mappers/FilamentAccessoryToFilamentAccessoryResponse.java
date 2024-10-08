package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.api.response.accessory.FilamentAccessoryResponse;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;

@Component
public class FilamentAccessoryToFilamentAccessoryResponse implements Converter<FilamentAccessory, FilamentAccessoryResponse> {

    @Override
    public FilamentAccessoryResponse convert(FilamentAccessory source) {
        return new FilamentAccessoryResponse(
                source.id(),
                source.name(),
                source.producer(),
                source.filamentType(),
                source.printTemperature(),
                source.deskTemperature(),
                source.pricePerKg(),
                source.color(),
                source.description(),
                source.quantity()
        );
    }
}

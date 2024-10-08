package pl.vabanq.erp.infrastructure.mappers;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import pl.vabanq.erp.api.response.accessory.FastenersAccessoryResponse;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;

@Component
public class FastenersAccessoryToFastenersAccessoryResponseConverter implements Converter<FastenersAccessory, FastenersAccessoryResponse> {

    @Override
    public FastenersAccessoryResponse convert(FastenersAccessory source) {
        return new FastenersAccessoryResponse(
                source.id(),
                source.name(),
                source.netPricePerQuantity(),
                source.quantity(),
                source.description()
        );
    }
}

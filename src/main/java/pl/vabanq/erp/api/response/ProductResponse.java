package pl.vabanq.erp.api.response;

import pl.vabanq.erp.domain.products.product.model.PrintTime;
import pl.vabanq.erp.domain.products.product.model.ProductFile;

import java.math.BigDecimal;
import java.util.List;

public record ProductResponse(
        String id,
        String name,
        String ean,
        ProductAccessoriesResponse productAccessories,
        PrintTime printTime,
        BigDecimal price,
        BigDecimal allegroTax,
        String description,
        ProductFile preview,
        List<ProductFile> files
) {}

package pl.vabanq.erp.domain.products.product.model;

import pl.vabanq.erp.domain.Identifiable;

import java.math.BigDecimal;
import java.util.List;

public record Product(String id, String name, String ean, List<AccessoryQuantity> accessoriesQ, PrintTime printTime,
                      ProductFile preview, List<ProductFile> files, BigDecimal price, BigDecimal allegroTax,
                      String description) implements Identifiable {
}

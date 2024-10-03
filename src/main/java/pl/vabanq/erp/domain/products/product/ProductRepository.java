package pl.vabanq.erp.domain.products.product;

import pl.vabanq.erp.domain.products.product.model.Product;

import java.util.List;

public interface ProductRepository {
    void saveProduct(Product product);

    Product getProduct(String id);

    List<Product> getAllProducts();
}

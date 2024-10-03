package pl.vabanq.erp.infrastructure.database.product;

import org.springframework.core.convert.ConversionService;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;
import pl.vabanq.erp.domain.products.product.ProductRepository;
import pl.vabanq.erp.domain.products.product.model.Product;
import pl.vabanq.erp.infrastructure.database.product.entity.ProductJPA;
import pl.vabanq.erp.infrastructure.database.product.spring.ProductRepositorySpringJPA;

import java.util.List;

@Repository
public class ProductRepositoryJPA implements ProductRepository {
    private final ProductRepositorySpringJPA productRepository;
    private final ConversionService conversionService;

    public ProductRepositoryJPA(ProductRepositorySpringJPA productRepository, ConversionService conversionService) {
        this.productRepository = productRepository;
        this.conversionService = conversionService;
    }

    @Transactional
    @Override
    public void saveProduct(Product product) {
        ProductJPA existingEntity = productRepository.findById(product.id())
                .orElseGet(() -> conversionService.convert(product, ProductJPA.class));

        if (existingEntity == null) {
            throw new DomainException(ErrorCode.INTERNAL_ERROR);
        }

        productRepository.save(conversionService.convert(product, ProductJPA.class));
    }

    @Transactional
    @Override
    public Product getProduct(String id) {
        ProductJPA productJPA = productRepository.findById(id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, id));
        return conversionService.convert(productJPA, Product.class);
    }

    public void cleanUp() {
        productRepository.deleteAll();
    }

    @Transactional
    public List<Product> getAllProducts() {
        return productRepository.findAll().stream()
                .map(productJPA -> conversionService.convert(productJPA, Product.class))
                .toList();
    }
}

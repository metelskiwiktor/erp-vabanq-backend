package pl.vabanq.erp.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pl.vabanq.erp.domain.change.ChangeTrackingService;
import pl.vabanq.erp.domain.products.accessory.AccessoryRepository;
import pl.vabanq.erp.domain.products.accessory.AccessoryService;
import pl.vabanq.erp.domain.products.product.ProductRepository;
import pl.vabanq.erp.domain.products.product.ProductService;

@Configuration
public class ApplicationConfiguration {
    @Bean
    AccessoryService accessoryService(ChangeTrackingService changeTrackingService, AccessoryRepository accessoryRepository) {
        return new AccessoryService(changeTrackingService, accessoryRepository);
    }

    @Bean
    ChangeTrackingService changeTrackingService() {
        return new ChangeTrackingService();
    }

    @Bean
    ProductService productService(ProductRepository productRepository, AccessoryRepository accessoryRepository,
                                  ChangeTrackingService changeTrackingService) {
        return new ProductService(productRepository, accessoryRepository, changeTrackingService);
    }
}

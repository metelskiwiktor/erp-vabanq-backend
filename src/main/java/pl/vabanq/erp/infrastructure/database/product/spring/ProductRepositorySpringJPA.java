package pl.vabanq.erp.infrastructure.database.product.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.vabanq.erp.infrastructure.database.product.entity.ProductJPA;

public interface ProductRepositorySpringJPA extends JpaRepository<ProductJPA, String> {
}

package pl.vabanq.erp.infrastructure.database.accessory.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.vabanq.erp.infrastructure.database.accessory.entity.FilamentAccessoryJPA;
import pl.vabanq.erp.infrastructure.database.accessory.entity.PackagingAccessoryJPA;

import java.util.Optional;

public interface PackagingRepositorySpringJPA extends JpaRepository<PackagingAccessoryJPA, Integer> {
    Optional<PackagingAccessoryJPA> findByAccessory_Id(String domainId);
}

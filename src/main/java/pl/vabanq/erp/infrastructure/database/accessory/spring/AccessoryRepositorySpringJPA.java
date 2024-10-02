package pl.vabanq.erp.infrastructure.database.accessory.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.vabanq.erp.infrastructure.database.accessory.entity.FilamentAccessoryJPA;

import java.util.Optional;

public interface AccessoryRepositorySpringJPA extends JpaRepository<FilamentAccessoryJPA, Integer> {
    Optional<FilamentAccessoryJPA> findByAccessory_Id(String domainId);
}

package pl.vabanq.erp.infrastructure.database.accessory.spring;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.vabanq.erp.infrastructure.database.accessory.entity.FastenersAccessoryJPA;

import java.util.Optional;

public interface FastenersRepositorySpringJPA extends JpaRepository<FastenersAccessoryJPA, Integer> {
    Optional<FastenersAccessoryJPA> findByAccessory_Id(String domainId);
}

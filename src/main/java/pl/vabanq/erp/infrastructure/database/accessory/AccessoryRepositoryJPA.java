package pl.vabanq.erp.infrastructure.database.accessory;

import org.springframework.stereotype.Repository;
import pl.vabanq.erp.domain.Identifiable;
import pl.vabanq.erp.domain.products.accessory.AccessoryRepository;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.products.accessory.model.PackagingAccessory;
import pl.vabanq.erp.domain.error.DomainException;
import pl.vabanq.erp.domain.error.ErrorCode;
import pl.vabanq.erp.infrastructure.database.accessory.entity.FastenersAccessoryJPA;
import pl.vabanq.erp.infrastructure.database.accessory.entity.FilamentAccessoryJPA;
import pl.vabanq.erp.infrastructure.database.accessory.entity.PackagingAccessoryJPA;
import pl.vabanq.erp.infrastructure.database.accessory.spring.AccessoryRepositorySpringJPA;
import pl.vabanq.erp.infrastructure.database.accessory.spring.FastenersRepositorySpringJPA;
import pl.vabanq.erp.infrastructure.database.accessory.spring.PackagingRepositorySpringJPA;

import java.util.List;
import java.util.Optional;

@Repository
public class AccessoryRepositoryJPA implements AccessoryRepository {
    private final AccessoryRepositorySpringJPA accessoryRepository;
    private final PackagingRepositorySpringJPA packagingRepository;
    private final FastenersRepositorySpringJPA fastenersRepository;

    public AccessoryRepositoryJPA(AccessoryRepositorySpringJPA accessoryRepository,
                                  PackagingRepositorySpringJPA packagingRepository,
                                  FastenersRepositorySpringJPA fastenersRepository) {
        this.accessoryRepository = accessoryRepository;
        this.packagingRepository = packagingRepository;
        this.fastenersRepository = fastenersRepository;
    }

    @Override
    public void saveFilament(FilamentAccessory filamentAccessory) {
        FilamentAccessoryJPA existingEntity = accessoryRepository.findByAccessory_Id(filamentAccessory.id())
                .orElse(new FilamentAccessoryJPA(filamentAccessory));

        existingEntity.setAccessory(filamentAccessory);

        accessoryRepository.save(existingEntity);
    }

    @Override
    public FilamentAccessory getFilamentAccessory(String id) {
        return accessoryRepository.findByAccessory_Id(id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, id))
                .getAccessory();
    }

    @Override
    public List<FilamentAccessory> getAllFilaments() {
        return accessoryRepository.findAll().stream()
                .map(FilamentAccessoryJPA::getAccessory)
                .toList();
    }

    @Override
    public void savePackagingAccessory(PackagingAccessory packagingAccessory) {
        PackagingAccessoryJPA existingEntity = packagingRepository.findByAccessory_Id(packagingAccessory.id())
                .orElse(new PackagingAccessoryJPA(packagingAccessory));

        existingEntity.setAccessory(packagingAccessory);

        packagingRepository.save(existingEntity);
    }

    @Override
    public void saveFastenersAccessory(FastenersAccessory fastenersAccessory) {
        FastenersAccessoryJPA existingEntity = fastenersRepository.findByAccessory_Id(fastenersAccessory.id())
                .orElse(new FastenersAccessoryJPA(fastenersAccessory));

        existingEntity.setAccessory(fastenersAccessory);

        fastenersRepository.save(existingEntity);
    }

    @Override
    public List<PackagingAccessory> getAllPackagingAccessories() {
        return packagingRepository.findAll().stream()
                .map(PackagingAccessoryJPA::getAccessory)
                .toList();
    }

    @Override
    public PackagingAccessory getPackagingAccessory(String id) {
        return packagingRepository.findByAccessory_Id(id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, id))
                .getAccessory();
    }

    @Override
    public FastenersAccessory getFastenersAccessory(String id) {
        return fastenersRepository.findByAccessory_Id(id)
                .orElseThrow(() -> new DomainException(ErrorCode.NOT_FOUND, id))
                .getAccessory();
    }

    @Override
    public Identifiable getAccessory(String id) {
        Optional<FilamentAccessoryJPA> filament = accessoryRepository.findByAccessory_Id(id);
        if (filament.isPresent()) {
            return filament.get().getAccessory();
        }
        Optional<PackagingAccessoryJPA> packaging = packagingRepository.findByAccessory_Id(id);
        if (packaging.isPresent()) {
            return packaging.get().getAccessory();
        }
        Optional<FastenersAccessoryJPA> fastener = fastenersRepository.findByAccessory_Id(id);
        if (fastener.isPresent()) {
            return fastener.get().getAccessory();
        }
        throw new DomainException(ErrorCode.NOT_FOUND, id);
    }

    @Override
    public List<FastenersAccessory> getAllFasteners() {
        return fastenersRepository.findAll().stream()
                .map(FastenersAccessoryJPA::getAccessory)
                .toList();
    }

    public List<FastenersAccessory> getAllFastenersAccessories() {
        return fastenersRepository.findAll().stream()
                .map(FastenersAccessoryJPA::getAccessory)
                .toList();
    }

    public void cleanUp() {
        accessoryRepository.deleteAll();
        packagingRepository.deleteAll();
        fastenersRepository.deleteAll();
    }
}

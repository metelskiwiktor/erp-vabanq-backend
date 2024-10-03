package pl.vabanq.erp.domain.products.accessory;

import pl.vabanq.erp.domain.Identifiable;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.products.accessory.model.PackagingAccessory;

import java.util.List;

public interface AccessoryRepository {
    void saveFilament(FilamentAccessory filamentAccessory);

    FilamentAccessory getFilamentAccessory(String id);

    List<FilamentAccessory> getAllFilaments();

    void savePackagingAccessory(PackagingAccessory packagingAccessory);

    void saveFastenersAccessory(FastenersAccessory fastenersAccessory);

    List<PackagingAccessory> getAllPackagingAccessories();

    PackagingAccessory getPackagingAccessory(String id);

    FastenersAccessory getFastenersAccessory(String id);

    Identifiable getAccessory(String id);

    List<FastenersAccessory> getAllFasteners();
}

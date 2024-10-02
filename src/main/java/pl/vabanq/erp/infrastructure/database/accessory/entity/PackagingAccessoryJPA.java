package pl.vabanq.erp.infrastructure.database.accessory.entity;

import jakarta.persistence.*;
import pl.vabanq.erp.domain.accessory.model.FilamentAccessory;
import pl.vabanq.erp.domain.accessory.model.PackagingAccessory;

@Entity
public class PackagingAccessoryJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int databaseId;

    @Embedded
    private PackagingAccessory accessory;

    public PackagingAccessoryJPA() {
    }

    public PackagingAccessoryJPA(PackagingAccessory accessory) {
        this.accessory = accessory;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

    public PackagingAccessory getAccessory() {
        return accessory;
    }

    public void setAccessory(PackagingAccessory accessory) {
        this.accessory = accessory;
    }
}

package pl.vabanq.erp.infrastructure.database.accessory.entity;

import jakarta.persistence.*;
import pl.vabanq.erp.domain.products.accessory.model.FilamentAccessory;

@Entity
public class FilamentAccessoryJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int databaseId;

    @Embedded
    private FilamentAccessory accessory;

    public FilamentAccessoryJPA() {
    }

    public FilamentAccessoryJPA(FilamentAccessory accessory) {
        this.accessory = accessory;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

    public FilamentAccessory getAccessory() {
        return accessory;
    }

    public void setAccessory(FilamentAccessory accessory) {
        this.accessory = accessory;
    }
}

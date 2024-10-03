package pl.vabanq.erp.infrastructure.database.accessory.entity;

import jakarta.persistence.*;
import pl.vabanq.erp.domain.products.accessory.model.FastenersAccessory;

@Entity
public class FastenersAccessoryJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int databaseId;

    @Embedded
    private FastenersAccessory accessory;

    public FastenersAccessoryJPA() {
    }

    public FastenersAccessoryJPA(FastenersAccessory accessory) {
        this.accessory = accessory;
    }

    public int getDatabaseId() {
        return databaseId;
    }

    public void setDatabaseId(int id) {
        this.databaseId = id;
    }

    public FastenersAccessory getAccessory() {
        return accessory;
    }

    public void setAccessory(FastenersAccessory accessory) {
        this.accessory = accessory;
    }
}

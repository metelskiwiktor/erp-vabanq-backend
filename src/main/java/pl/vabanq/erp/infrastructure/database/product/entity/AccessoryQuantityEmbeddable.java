// AccessoryQuantityEmbeddable.java
package pl.vabanq.erp.infrastructure.database.product.entity;

import jakarta.persistence.*;
import java.io.Serializable;

@Embeddable
public class AccessoryQuantityEmbeddable implements Serializable {
    @Column(name = "accessory_id")
    private String accessoryId;

    @Column(name = "quantity")
    private double quantity;

    public AccessoryQuantityEmbeddable() {}

    public AccessoryQuantityEmbeddable(String accessoryId, double quantity) {
        this.accessoryId = accessoryId;
        this.quantity = quantity;
    }

    // Getters and setters
    public String getAccessoryId() {
        return accessoryId;
    }

    public void setAccessoryId(String accessoryId) {
        this.accessoryId = accessoryId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }
}

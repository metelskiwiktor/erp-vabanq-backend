// PrintTimeEmbeddable.java
package pl.vabanq.erp.infrastructure.database.product.entity;

import jakarta.persistence.Embeddable;

@Embeddable
public class PrintTimeEmbeddable {
    private int hours;
    private int minutes;

    public PrintTimeEmbeddable() {}

    public PrintTimeEmbeddable(int hours, int minutes) {
        this.hours = hours;
        this.minutes = minutes;
    }

    // Getters and setters
    public int getHours() {
        return hours;
    }

    public void setHours(int hours) {
        this.hours = hours;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }
}

// ProductFileEmbeddable.java
package pl.vabanq.erp.infrastructure.database.product.entity;

import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;

@Embeddable
public class ProductFileEmbeddable {
    private String fileId;
    @Lob
    private byte[] data;
    private String filename;

    public ProductFileEmbeddable() {}

    public ProductFileEmbeddable(String fileId, byte[] data, String filename) {
        this.fileId = fileId;
        this.data = data;
        this.filename = filename;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String id) {
        this.fileId = id;
    }

    // Getters and setters
    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }
}

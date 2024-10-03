// ProductJPA.java
package pl.vabanq.erp.infrastructure.database.product.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "products")
public class ProductJPA {
    @Id
    private String id;

    private String name;
    private String ean;

    @ElementCollection
    @CollectionTable(name = "product_accessories", joinColumns = @JoinColumn(name = "product_id"))
    private List<AccessoryQuantityEmbeddable> accessoriesQ;

    @Embedded
    private PrintTimeEmbeddable printTime;

    @Embedded
    @AttributeOverrides({
        @AttributeOverride(name = "data", column = @Column(name = "file_data")),
        @AttributeOverride(name = "filename", column = @Column(name = "file_name"))
    })
    private ProductFileEmbeddable file;

    @ElementCollection
    @CollectionTable(name = "product_files", joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductFileEmbeddable> files;

    private BigDecimal price;
    private BigDecimal allegroTax;
    private String description;

    public ProductJPA() {}

    public ProductJPA(String id, String name, String ean, List<AccessoryQuantityEmbeddable> accessoriesQ,
                      PrintTimeEmbeddable printTime, ProductFileEmbeddable file, List<ProductFileEmbeddable> files,
                      BigDecimal price, BigDecimal allegroTax, String description) {
        this.id = id;
        this.name = name;
        this.ean = ean;
        this.accessoriesQ = accessoriesQ;
        this.printTime = printTime;
        this.file = file;
        this.files = files;
        this.price = price;
        this.allegroTax = allegroTax;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEan() {
        return ean;
    }

    public void setEan(String ean) {
        this.ean = ean;
    }

    public List<AccessoryQuantityEmbeddable> getAccessoriesQ() {
        return accessoriesQ;
    }

    public void setAccessoriesQ(List<AccessoryQuantityEmbeddable> accessoriesQ) {
        this.accessoriesQ = accessoriesQ;
    }

    public PrintTimeEmbeddable getPrintTime() {
        return printTime;
    }

    public void setPrintTime(PrintTimeEmbeddable printTime) {
        this.printTime = printTime;
    }

    public ProductFileEmbeddable getFile() {
        return file;
    }

    public void setFile(ProductFileEmbeddable file) {
        this.file = file;
    }

    public List<ProductFileEmbeddable> getFiles() {
        return files;
    }

    public void setFiles(List<ProductFileEmbeddable> files) {
        this.files = files;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public BigDecimal getAllegroTax() {
        return allegroTax;
    }

    public void setAllegroTax(BigDecimal allegroTax) {
        this.allegroTax = allegroTax;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

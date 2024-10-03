package pl.vabanq.erp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import pl.vabanq.erp.api.request.ProductRequest;
import pl.vabanq.erp.api.response.ProductResponse;
import pl.vabanq.erp.domain.products.product.ProductService;
import pl.vabanq.erp.domain.products.product.model.Product;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ConversionService conversionService;

    @Autowired
    public ProductController(ProductService productService, ConversionService conversionService) {
        this.productService = productService;
        this.conversionService = conversionService;
    }

    @PostMapping
    public ResponseEntity<ProductResponse> saveProduct(@RequestBody ProductRequest request) {
        Product product = productService.saveProduct(
                request.name(), request.ean(), request.accessoriesQ(),
                request.printHours(), request.printMinutes(), request.price(),
                request.allegroTax(), request.description()
        );
        ProductResponse response = conversionService.convert(product, ProductResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponse> updateProduct(@PathVariable String id,
                                                         @RequestBody ProductRequest request) {
        Product product = productService.updateProduct(
                id, request.name(), request.ean(), request.accessoriesQ(),
                request.printHours(), request.printMinutes(), request.price(),
                request.allegroTax(), request.description()
        );
        ProductResponse response = conversionService.convert(product, ProductResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }


    // Endpoint do aktualizacji podglądu (preview)
    @PostMapping("/{id}/preview")
    public ResponseEntity<ProductResponse> updatePreview(@PathVariable String id,
                                                         @RequestParam("file") MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        String filename = file.getOriginalFilename();

        Product updatedProduct = productService.updatePreview(id, fileData, filename);
        ProductResponse response = conversionService.convert(updatedProduct, ProductResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint do dodania pliku do produktu
    @PostMapping("/{id}/file")
    public ResponseEntity<ProductResponse> addFile(@PathVariable String id,
                                                   @RequestParam("file") MultipartFile file) throws IOException {
        byte[] fileData = file.getBytes();
        String filename = file.getOriginalFilename();

        Product updatedProduct = productService.addFile(id, fileData, filename);
        ProductResponse response = conversionService.convert(updatedProduct, ProductResponse.class);

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Endpoint do usunięcia pliku z produktu
    @DeleteMapping("/{id}/file/{fileId}")
    public ResponseEntity<Void> deleteFile(@PathVariable String id, @PathVariable String fileId) {
        productService.deleteFileById(id, fileId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        List<ProductResponse> response = products.stream()
                .map(product -> conversionService.convert(product, ProductResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

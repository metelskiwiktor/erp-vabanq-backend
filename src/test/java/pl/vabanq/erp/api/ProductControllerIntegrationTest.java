package pl.vabanq.erp.api;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pl.vabanq.erp.api.request.product.ProductRequest;
import pl.vabanq.erp.api.response.product.ProductResponse;
import pl.vabanq.erp.domain.products.accessory.AccessoryService;
import pl.vabanq.erp.domain.products.product.ProductService;
import pl.vabanq.erp.infrastructure.database.accessory.AccessoryRepositoryJPA;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.util.Pair;
import pl.vabanq.erp.infrastructure.database.product.ProductRepositoryJPA;

import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_PDF_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.util.MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE;
import static org.springframework.util.MimeTypeUtils.IMAGE_JPEG_VALUE;

@SpringBootTest
public class ProductControllerIntegrationTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @Autowired
    private ProductRepositoryJPA productRepository;

    @Autowired
    private AccessoryService accessoryService;

    @Autowired
    private AccessoryRepositoryJPA accessoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private String screwId;
    private String boltId;
    @Autowired
    private ProductService productService;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Clean up repositories
        productRepository.cleanUp();
        accessoryRepository.cleanUp();

        // Use AccessoryService to create accessories and get their IDs
        screwId = accessoryService.saveFastenersAccessory("Screw", "5.99", "100", "description").id();
        boltId = accessoryService.saveFastenersAccessory("Bolt", "3.50", "50", "description").id();
    }

    @Test
    public void shouldSaveProductSuccessfully() throws Exception {
        // Arrange
        ProductRequest productRequest = new ProductRequest(
                "Product1",
                "1234567890123",
                List.of(
                        Pair.of(2.0, screwId), // Using the accessory ID here
                        Pair.of(1.5, boltId)   // Using the accessory ID here
                ),
                2,
                30,
                "150.00",
                "20.00",
                "Description"
        );

        String requestJson = objectMapper.writeValueAsString(productRequest);

        // Act & Assert
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Product1"))
                .andExpect(jsonPath("$.ean").value("1234567890123"));
    }

    @Test
    public void shouldUpdateProductSuccessfully() throws Exception {
        // Arrange: first save a product
        ProductRequest productRequest = new ProductRequest(
                "Product1",
                "1234567890123",
                List.of(
                        Pair.of(2.0, screwId), // Using the accessory ID here
                        Pair.of(1.5, boltId)   // Using the accessory ID here
                ),
                2,
                30,
                "150.00",
                "20.00",
                "Description"
        );
        String requestJson = objectMapper.writeValueAsString(productRequest);

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated());

        // Update the product
        ProductRequest updatedRequest = new ProductRequest(
                "UpdatedProduct",
                "1234567890124",
                List.of(
                        Pair.of(3.0, screwId), // Using the accessory ID here
                        Pair.of(2.0, boltId)   // Using the accessory ID here
                ),
                3,
                45,
                "200.00",
                "25.00",
                "Updated description"
        );

        String updatedJson = objectMapper.writeValueAsString(updatedRequest);

        String productId = productService.getAllProducts().getFirst().id();

        // Act & Assert
        mockMvc.perform(put("/api/products/{id}", productId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updatedJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedProduct"))
                .andExpect(jsonPath("$.ean").value("1234567890124"));
    }

    @Test
    public void shouldGetAllProductsSuccessfully() throws Exception {
        // Arrange: create some products
        ProductRequest productRequest1 = new ProductRequest(
                "Product1",
                "1234567890123",
                List.of(
                        Pair.of(2.0, screwId), // Using the accessory ID here
                        Pair.of(1.5, boltId)   // Using the accessory ID here
                ),
                2,
                30,
                "150.00",
                "20.00",
                "Description"
        );
        ProductRequest productRequest2 = new ProductRequest(
                "Product2",
                "1234567890124",
                List.of(
                        Pair.of(3.0, screwId), // Using the accessory ID here
                        Pair.of(1.0, boltId)   // Using the accessory ID here
                ),
                1,
                15,
                "100.00",
                "10.00",
                "Another description"
        );

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest1)))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productRequest2)))
                .andExpect(status().isCreated());

        // Act & Assert
        mockMvc.perform(get("/api/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));
    }

    @Test
    public void shouldUpdatePreviewSuccessfully() throws Exception {
        // Prepare MultipartFile for preview
        MockMultipartFile previewFile = new MockMultipartFile(
                "file", "preview.jpg", IMAGE_JPEG_VALUE, "image content".getBytes());

        // First, create a product
        String productId = createTestProduct();

        // Perform update preview
        mockMvc.perform(multipart("/api/products/{id}/preview", productId)
                        .file(previewFile))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.preview.filename").value("preview.jpg"))
                .andExpect(jsonPath("$.preview").exists())
                .andExpect(jsonPath("$.preview.filename").value("preview.jpg"));
    }

    @Test
    public void shouldFailUpdatingPreviewWithInvalidFormat() throws Exception {
        // Prepare MultipartFile with invalid format
        MockMultipartFile invalidFile = new MockMultipartFile(
                "file", "invalid.zip", APPLICATION_OCTET_STREAM_VALUE, "file content".getBytes());

        // First, create a product
        String productId = createTestProduct();

        // Perform update preview with invalid file
        mockMvc.perform(multipart("/api/products/{id}/preview", productId)
                        .file(invalidFile))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void shouldAddFileSuccessfully() throws Exception {
        // Prepare MultipartFile for file
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", APPLICATION_PDF_VALUE, "pdf content".getBytes());

        // First, create a product
        String productId = createTestProduct();

        // Perform add file
        mockMvc.perform(multipart("/api/products/{id}/file", productId)
                        .file(file))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.files[0].filename").value("document.pdf"));
    }

    @Test
    public void shouldDeleteFileSuccessfully() throws Exception {
        // Prepare MultipartFile for file
        MockMultipartFile file = new MockMultipartFile(
                "file", "document.pdf", APPLICATION_PDF_VALUE, "pdf content".getBytes());

        // First, create a product and add a file
        String productId = createTestProduct();
        String fileId = addFileToProduct(productId, file);

        // Perform delete file by ID
        mockMvc.perform(delete("/api/products/{id}/file/{fileId}", productId, fileId))
                .andExpect(status().isNoContent());
    }

    private String createTestProduct() throws Exception {
        // Create a sample ProductRequest
        ProductRequest productRequest = new ProductRequest(
                "Test Product",
                "1234567890123",
                List.of(
                        Pair.of(2.0, screwId),  // Example accessory ID
                        Pair.of(1.5, boltId)    // Example accessory ID
                ),
                2,
                30,
                "150.00",
                "20.00",
                "Test description"
        );

        String requestJson = objectMapper.writeValueAsString(productRequest);

        // Perform the POST request to create a product
        String response = mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the product ID from the response
        ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);
        return productResponse.id();
    }

    private String addFileToProduct(String productId, MockMultipartFile file) throws Exception {
        // Perform the POST request to add a file to the product
        String response = mockMvc.perform(multipart("/api/products/{id}/file", productId)
                        .file(file))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Extract the file ID from the response
        ProductResponse productResponse = objectMapper.readValue(response, ProductResponse.class);
        return productResponse.files().get(0).id();  // Return the ID of the first file
    }

}

package pl.vabanq.erp.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import pl.vabanq.erp.api.request.accessory.FastenersAccessoryRequest;
import pl.vabanq.erp.api.request.accessory.FilamentAccessoryRequest;
import pl.vabanq.erp.api.request.accessory.PackagingAccessoryRequest;
import pl.vabanq.erp.domain.products.accessory.AccessoryService;
import pl.vabanq.erp.infrastructure.database.accessory.AccessoryRepositoryJPA;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class AccessoryControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AccessoryService accessoryService;

    @Autowired
    private AccessoryRepositoryJPA accessoryRepository;

    @BeforeEach
    void setUp() {
        accessoryRepository.cleanUp();
    }

    @Test
    @DisplayName("Save Filament Accessory - Success")
    void shouldSaveFilamentAccessory() throws Exception {
        // Arrange
        FilamentAccessoryRequest request = new FilamentAccessoryRequest(
                "PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "High-quality filament", "100.0"
        );

        // Act & Assert
        mockMvc.perform(post("/api/accessories/filament")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("PLA 1kg"))
                .andExpect(jsonPath("$.producer").value("XYZ"))
                .andExpect(jsonPath("$.filamentType").value("PLA"))
                .andExpect(jsonPath("$.printTemperature").value(200.0))
                .andExpect(jsonPath("$.deskTemperature").value(60.0))
                .andExpect(jsonPath("$.pricePerKg").value(19.99))
                .andExpect(jsonPath("$.color").value("#FFFFFF"))
                .andExpect(jsonPath("$.description").value("High-quality filament"))
                .andExpect(jsonPath("$.quantity").value(100.0));
    }

    @Test
    @DisplayName("Update Filament Accessory - Success")
    void shouldUpdateFilamentAccessory() throws Exception {
        // Arrange
        // Najpierw zapisujemy filament
        accessoryService.saveFilament(
                "PLA 1kg", "XYZ", "PLA", "200.0", "60.0", "19.99", "#FFFFFF", "High-quality filament", "100.0"
        );
        String filamentId = accessoryService.getAllFilaments().getFirst().id(); // Pobieramy ID zapisanego filamentu

        // Przygotowujemy żądanie aktualizacji
        FilamentAccessoryRequest request = new FilamentAccessoryRequest(
                "Updated PLA", "ABC", "ABS", "230.0", "80.0", "29.99", "#0000FF", "Updated description", "150.0"
        );

        // Act & Assert
        mockMvc.perform(put("/api/accessories/filament/{id}", filamentId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated PLA"))
                .andExpect(jsonPath("$.producer").value("ABC"))
                .andExpect(jsonPath("$.filamentType").value("ABS"))
                .andExpect(jsonPath("$.printTemperature").value(230.0))
                .andExpect(jsonPath("$.deskTemperature").value(80.0))
                .andExpect(jsonPath("$.pricePerKg").value(29.99))
                .andExpect(jsonPath("$.color").value("#0000FF"))
                .andExpect(jsonPath("$.description").value("Updated description"))
                .andExpect(jsonPath("$.quantity").value(150.0));
    }
    @Test
    @DisplayName("Save Packaging Accessory - Success")
    void shouldSavePackagingAccessory() throws Exception {
        // Arrange
        PackagingAccessoryRequest request = new PackagingAccessoryRequest(
                "Box A", "S", "10x20x30", "5.50", "100.0", "Small packaging box"  // Added description
        );

        // Act & Assert
        mockMvc.perform(post("/api/accessories/packaging")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Box A"))
                .andExpect(jsonPath("$.packagingSize").value("S"))
                .andExpect(jsonPath("$.dimensions").value("10x20x30"))
                .andExpect(jsonPath("$.netPricePerQuantity").value(5.50))
                .andExpect(jsonPath("$.quantity").value(100.0))
                .andExpect(jsonPath("$.description").value("Small packaging box"));  // Check description
    }

    @Test
    @DisplayName("Update Packaging Accessory - Success")
    void shouldUpdatePackagingAccessory() throws Exception {
        // Arrange
        // Najpierw zapisujemy opakowanie
        accessoryService.savePackagingAccessory(
                "Box A", "S", "10x20x30", "5.50", "100.0", "Small packaging box"
        );
        String packagingId = accessoryService.getAllPackagingAccessories().getFirst().id(); // Pobieramy ID zapisanego opakowania

        // Przygotowujemy żądanie aktualizacji
        PackagingAccessoryRequest request = new PackagingAccessoryRequest(
                "Updated Box", "M", "20x30x40", "7.75", "200.0", "Updated packaging box" // Added description
        );

        // Act & Assert
        mockMvc.perform(put("/api/accessories/packaging/{id}", packagingId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Box"))
                .andExpect(jsonPath("$.packagingSize").value("M"))
                .andExpect(jsonPath("$.dimensions").value("20x30x40"))
                .andExpect(jsonPath("$.netPricePerQuantity").value(7.75))
                .andExpect(jsonPath("$.quantity").value(200.0))
                .andExpect(jsonPath("$.description").value("Updated packaging box"));  // Check updated description
    }

    @Test
    @DisplayName("Save Fasteners Accessory - Success")
    void shouldSaveFastenersAccessory() throws Exception {
        // Arrange
        FastenersAccessoryRequest request = new FastenersAccessoryRequest(
                "Screw Set", "15.99", "500.0", "Set of screws" // Added description
        );

        // Act & Assert
        mockMvc.perform(post("/api/accessories/fasteners")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Screw Set"))
                .andExpect(jsonPath("$.netPricePerQuantity").value(15.99))
                .andExpect(jsonPath("$.quantity").value(500.0))
                .andExpect(jsonPath("$.description").value("Set of screws"));  // Check description
    }

    @Test
    @DisplayName("Update Fasteners Accessory - Success")
    void shouldUpdateFastenersAccessory() throws Exception {
        // Arrange
        // Najpierw zapisujemy element złączny
        accessoryService.saveFastenersAccessory(
                "Screw Set", "15.99", "500.0", "Set of screws"
        );
        String fastenersId = accessoryService.getAllFasteners().getFirst().id(); // Pobieramy ID zapisanego elementu

        // Przygotowujemy żądanie aktualizacji
        FastenersAccessoryRequest request = new FastenersAccessoryRequest(
                "Bolt Set", "25.99", "600.0", "Set of bolts" // Added description
        );

        // Act & Assert
        mockMvc.perform(put("/api/accessories/fasteners/{id}", fastenersId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Bolt Set"))
                .andExpect(jsonPath("$.netPricePerQuantity").value(25.99))
                .andExpect(jsonPath("$.quantity").value(600.0))
                .andExpect(jsonPath("$.description").value("Set of bolts"));  // Check updated description
    }

    @Test
    @DisplayName("Get All Filaments - Success")
    void shouldGetAllFilaments() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accessories/filaments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }

    @Test
    @DisplayName("Get All Packaging Accessories - Success")
    void shouldGetAllPackagingAccessories() throws Exception {
        // Act & Assert
        mockMvc.perform(get("/api/accessories/packaging"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").isNotEmpty());
    }
}

package pl.vabanq.erp.api;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.vabanq.erp.api.request.FastenersAccessoryRequest;
import pl.vabanq.erp.api.request.FilamentAccessoryRequest;
import pl.vabanq.erp.api.request.PackagingAccessoryRequest;
import pl.vabanq.erp.api.response.FastenersAccessoryResponse;
import pl.vabanq.erp.api.response.FilamentAccessoryResponse;
import pl.vabanq.erp.api.response.PackagingAccessoryResponse;
import pl.vabanq.erp.domain.products.accessory.AccessoryService;
import pl.vabanq.erp.domain.products.accessory.model.*;

import java.util.List;

@RestController
@RequestMapping("/api/accessories")
public class AccessoryController {

    private final AccessoryService accessoryService;
    private final ConversionService conversionService;

    @Autowired
    public AccessoryController(AccessoryService accessoryService, ConversionService conversionService) {
        this.accessoryService = accessoryService;
        this.conversionService = conversionService;
    }

    // Save Filament
    @PostMapping("/filament")
    public ResponseEntity<FilamentAccessoryResponse> saveFilament(@RequestBody FilamentAccessoryRequest request) {
        FilamentAccessory savedFilament = accessoryService.saveFilament(
                request.name(), request.producer(), request.filamentType(),
                request.printTemperature(), request.deskTemperature(),
                request.pricePerKg(), request.color(), request.description(), request.quantity()
        );
        FilamentAccessoryResponse response = conversionService.convert(savedFilament, FilamentAccessoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Update Filament
    @PutMapping("/filament/{id}")
    public ResponseEntity<FilamentAccessoryResponse> updateFilament(@PathVariable String id,
                                                                    @RequestBody FilamentAccessoryRequest request) {
        FilamentAccessory updatedFilament = accessoryService.updateFilament(
                id, request.name(), request.producer(), request.filamentType(),
                request.printTemperature(), request.deskTemperature(),
                request.pricePerKg(), request.color(), request.description(), request.quantity()
        );
        FilamentAccessoryResponse response = conversionService.convert(updatedFilament, FilamentAccessoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Save Packaging Accessory
    @PostMapping("/packaging")
    public ResponseEntity<PackagingAccessoryResponse> savePackagingAccessory(@RequestBody PackagingAccessoryRequest request) {
        PackagingAccessory savedPackaging = accessoryService.savePackagingAccessory(
                request.name(), request.packagingSize(), request.dimensions(),
                request.netPricePerQuantity(), request.quantity()
        );
        PackagingAccessoryResponse response = conversionService.convert(savedPackaging, PackagingAccessoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Update Packaging Accessory
    @PutMapping("/packaging/{id}")
    public ResponseEntity<PackagingAccessoryResponse> updatePackagingAccessory(@PathVariable String id,
                                                                               @RequestBody PackagingAccessoryRequest request) {
        PackagingAccessory updatedPackaging = accessoryService.updatePackagingAccessory(
                id, request.name(), request.packagingSize(), request.dimensions(),
                request.netPricePerQuantity(), request.quantity()
        );
        PackagingAccessoryResponse response = conversionService.convert(updatedPackaging, PackagingAccessoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Save Fasteners Accessory
    @PostMapping("/fasteners")
    public ResponseEntity<FastenersAccessoryResponse> saveFastenersAccessory(@RequestBody FastenersAccessoryRequest request) {
        FastenersAccessory savedFasteners = accessoryService.saveFastenersAccessory(
                request.name(), request.netPricePerQuantity(), request.quantity()
        );
        FastenersAccessoryResponse response = conversionService.convert(savedFasteners, FastenersAccessoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // Update Fasteners Accessory
    @PutMapping("/fasteners/{id}")
    public ResponseEntity<FastenersAccessoryResponse> updateFastenersAccessory(@PathVariable String id,
                                                                               @RequestBody FastenersAccessoryRequest request) {
        FastenersAccessory updatedFasteners = accessoryService.updateFastenersAccessory(
                id, request.name(), request.netPricePerQuantity(), request.quantity()
        );
        FastenersAccessoryResponse response = conversionService.convert(updatedFasteners, FastenersAccessoryResponse.class);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get All Filaments
    @GetMapping("/filaments")
    public ResponseEntity<List<FilamentAccessoryResponse>> getAllFilaments() {
        List<FilamentAccessory> filaments = accessoryService.getAllFilaments();
        List<FilamentAccessoryResponse> response = filaments.stream()
                .map(filament -> conversionService.convert(filament, FilamentAccessoryResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    // Get All Packaging Accessories
    @GetMapping("/packaging")
    public ResponseEntity<List<PackagingAccessoryResponse>> getAllPackagingAccessories() {
        List<PackagingAccessory> packagingAccessories = accessoryService.getAllPackagingAccessories();
        List<PackagingAccessoryResponse> response = packagingAccessories.stream()
                .map(packaging -> conversionService.convert(packaging, PackagingAccessoryResponse.class))
                .toList();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}

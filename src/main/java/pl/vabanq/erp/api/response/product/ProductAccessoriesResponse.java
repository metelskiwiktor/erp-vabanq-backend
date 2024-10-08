package pl.vabanq.erp.api.response.product;

import org.springframework.data.util.Pair;
import pl.vabanq.erp.api.response.accessory.FastenersAccessoryResponse;
import pl.vabanq.erp.api.response.accessory.FilamentAccessoryResponse;
import pl.vabanq.erp.api.response.accessory.PackagingAccessoryResponse;

import java.util.List;

public record ProductAccessoriesResponse(List<Pair<Double, FastenersAccessoryResponse>> fasteners,
                                         List<Pair<Double, FilamentAccessoryResponse>> filaments,
                                         List<Pair<Double, PackagingAccessoryResponse>> packagings) {
}

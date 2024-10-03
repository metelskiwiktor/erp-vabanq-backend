package pl.vabanq.erp.api.response;

import org.springframework.data.util.Pair;

import java.util.List;

public record ProductAccessoriesResponse(List<Pair<Double, FastenersAccessoryResponse>> fasteners,
                                         List<Pair<Double, FilamentAccessoryResponse>> filaments,
                                         List<Pair<Double, PackagingAccessoryResponse>> packagings) {
}

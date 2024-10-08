package pl.vabanq.erp.api.response.accessory;

import java.util.List;

public record GroupedAccessoriesResponse(
        List<FastenersAccessoryResponse> fasteners,
        List<FilamentAccessoryResponse> filaments,
        List<PackagingAccessoryResponse> packages
) {
}

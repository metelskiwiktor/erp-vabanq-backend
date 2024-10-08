package pl.vabanq.erp.api.request.product;

import org.springframework.data.util.Pair;

import java.util.List;

public record ProductRequest(
        String name,
        String ean,
        List<Pair<Double, String>> accessoriesQ,
        Integer printHours,
        Integer printMinutes,
        String price,
        String allegroTax,
        String description
) {}

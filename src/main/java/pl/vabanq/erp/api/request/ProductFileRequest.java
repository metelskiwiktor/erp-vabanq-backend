package pl.vabanq.erp.api.request;

public record ProductFileRequest(
        byte[] fileData,
        String filename
) {}

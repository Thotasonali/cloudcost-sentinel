package com.cloudcost.sentinel.billing;

import java.util.List;

public record CsvUploadResponse(
        int totalRows,
        int savedRows,
        int skippedRows,
        List<String> errors
) {
}

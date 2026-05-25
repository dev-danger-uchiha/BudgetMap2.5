package com.budgetmap.dto;

import lombok.Data;

@Data
public class CompraPlanRequest {
    private Integer planId;
    private String metodoPago;     // Ej: "PSE", "CREDITO"
    private String referenciaPago; // Ej: "REF-987654321"
}
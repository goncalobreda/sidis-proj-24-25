package com.example.acquisitionserviceQuery.model;

public enum AcquisitionStatus {
    PENDING_APPROVAL, // Aguardando aprovação do administrador
    APPROVED,         // Aprovado para aquisição
    PENDING, REJECTED          // Rejeitado pelo administrador
}

package com.example.readerservice.model;

import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import java.time.LocalDateTime;

public class AuditListener {

    @PrePersist
    public void setCreatedFields(Object entity) {
        if (entity instanceof Reader reader) {
            // Define `createdAt` e `createdBy` somente na criação
            reader.setCreatedAt(LocalDateTime.now());
            reader.setCreatedBy("system"); // ou utilize o usuário autenticado se aplicável
            // `modifiedAt` e `modifiedBy` também são definidos na criação
            reader.setModifiedAt(LocalDateTime.now());
            reader.setModifiedBy("system");
        }
    }

    @PreUpdate
    public void setModifiedFields(Object entity) {
        if (entity instanceof Reader reader) {
            // Atualiza `modifiedAt` e `modifiedBy` para cada atualização
            reader.setModifiedAt(LocalDateTime.now());
            reader.setModifiedBy("system"); // ou utilize o usuário autenticado se aplicável
        }
    }
}

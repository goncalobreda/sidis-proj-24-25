package com.example.bookservice.messaging;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AuthorEventMessage implements Serializable {
    private String action;      // Tipo de ação: "create", "update", "delete"
    private String authorID;    // ID único do autor
    private String name;        // Nome do autor
    private String biography;   // Biografia do autor

    @Override
    public String toString() {
        return "AuthorEventMessage{" +
                "action='" + action + '\'' +
                ", authorID='" + authorID + '\'' +
                ", name='" + name + '\'' +
                ", biography='" + biography + '\'' +
                '}';
    }
}

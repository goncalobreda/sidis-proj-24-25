package com.example.acquisitionserviceCommand.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import java.time.Year;
import java.util.List;

@Entity
public class Acquisition {

    @Id
    @Column(unique = true, updatable = false, nullable = false)
    private String acquisitionID; // Identificador único da aquisição (ex: 2024/1)

    @Version
    private long version; // Controle de versão para evitar atualizações concorrentes

    @Column(nullable = false)
    @NotNull
    @NotBlank
    private String readerID; // ID do leitor que sugeriu a aquisição

    @Column(unique = true, nullable = false)
    @NotNull
    @NotBlank
    private String isbn; // ISBN do livro sugerido

    @Column(nullable = false)
    @NotNull
    @NotBlank
    private String title; // Título do livro sugerido

    @Column(nullable = false)
    @NotNull
    @NotBlank
    @Size(max = 2048)
    private String description;
    @Column(nullable = false)
    @NotNull
    @NotBlank
    @Size(max = 2048)
    private String reason;

    @ElementCollection
    @NotNull
    private List<String> authorIds; // Lista de IDs dos autores do livro sugerido

    @Column(nullable = false)
    @NotNull
    @NotBlank
    private String genre; // Género do livro sugerido

    @Enumerated(EnumType.STRING)
    private AcquisitionStatus status = AcquisitionStatus.PENDING_APPROVAL; // Estado da aquisição

    private static final long serialVersionUID = 1L;
    private static int currentYear = Year.now().getValue();
    private static int counter = 0;

    public Acquisition() {}

    public Acquisition(String readerID, String isbn, String title, String description,String reason,
                       List<String> authorIds, String genre) {
        this.acquisitionID = generateUniqueAcquisitionID();
        this.readerID = readerID;
        this.isbn = isbn;
        this.title = title;
        this.description = description;
        this.reason = reason;
        this.authorIds = authorIds;
        this.genre = genre;
    }

    public void initCounter(String lastAcquisitionID) {
        if (lastAcquisitionID != null && !lastAcquisitionID.isBlank()) {
            String[] parts = lastAcquisitionID.split("/");
            if (parts.length == 2) {
                currentYear = Integer.parseInt(parts[0]);
                counter = Integer.parseInt(parts[1]);
            }
        }
    }

    private synchronized String generateUniqueAcquisitionID() {
        if (Year.now().getValue() != currentYear) {
            currentYear = Year.now().getValue();
            counter = 0;
        }
        counter++;
        return currentYear + "/" + counter;
    }

    public String getAcquisitionID() {
        return acquisitionID;
    }

    public void setAcquisitionID(String acquisitionID) {
        this.acquisitionID = acquisitionID;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }

    public String getReaderID() {
        return readerID;
    }

    public void setReaderID(String readerID) {
        this.readerID = readerID;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public List<String> getAuthorIds() {
        return authorIds;
    }

    public void setAuthorIds(List<String> authorIds) {
        this.authorIds = authorIds;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public AcquisitionStatus getStatus() {
        return status;
    }

    public void setStatus(AcquisitionStatus status) {
        this.status = status;
    }
}

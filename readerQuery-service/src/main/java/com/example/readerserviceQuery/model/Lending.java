package com.example.readerserviceQuery.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.Year;

@Entity
@Table(name = "LENDING")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Lending {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PK", nullable = false)
    private Long pk;

    @Column(name = "LENDINGID", unique = true, nullable = false)
    private String lendingID;

    @Column(name = "BOOK_ID", nullable = false)
    private Long bookID;

    @Column(name = "READER_ID", nullable = false)
    private String readerID;

    @Column(name = "START_DATE", nullable = false)
    private LocalDate startDate;

    @Column(name = "EXPECTED_RETURN_DATE", nullable = false)
    private LocalDate expectedReturnDate;

    @Column(name = "RETURN_DATE")
    private LocalDate returnDate;

    @Column(name = "OVERDUE", nullable = false)
    private boolean overdue;

    @Column(name = "FINE", nullable = false)
    private int fine;

    @Version
    @Column(name = "VERSION", nullable = false)
    private long version;

    private static int currentYear = Year.now().getValue();
    private static int counter = 0;

    public Lending(final Long bookID, final String readerID, final LocalDate startDate, final LocalDate returnDate,
                   final LocalDate expectedReturnDate, final boolean overdue, final int fine) {
        this.bookID = bookID;
        this.readerID = readerID;
        this.startDate = startDate;
        this.returnDate = returnDate;
        this.expectedReturnDate = expectedReturnDate;
        this.overdue = overdue;
        this.fine = fine;
        updateOverdueStatus();
        this.lendingID = generateUniqueLendingID();
    }

    @PrePersist
    @PreUpdate
    public void updateOverdueStatus() {
        if (this.returnDate != null) {
            this.overdue = this.returnDate.isAfter(this.expectedReturnDate);
        } else {
            this.overdue = LocalDate.now().isAfter(this.expectedReturnDate);
        }
    }

    private String generateUniqueLendingID() {
        if (Year.now().getValue() != currentYear) {
            currentYear = Year.now().getValue();
            counter = 0;
        }

        counter++;
        String idCounter = String.format("%d", counter);
        return currentYear + "/" + idCounter;
    }

    public void initCounter(String lastLendingID) {
        if (lastLendingID != null && !lastLendingID.isBlank()) {
            String[] parts = lastLendingID.split("/");
            if (parts.length == 2) {
                currentYear = Integer.parseInt(parts[0]);
                counter = Integer.parseInt(parts[1]);
            }
        }
    }
}

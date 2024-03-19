package com.pishgaman.phonebook.projections;

import java.time.LocalDate;

public interface LetterProjection {
    Long getLetterId();
    LocalDate getCreationDate();
    String getContent();
    String getLetterNumber();
    String getLetterState();
    String getRecipientName(); // Field from joined entity
    String getSenderName(); // Field from joined entity
}


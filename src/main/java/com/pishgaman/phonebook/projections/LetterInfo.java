package com.pishgaman.phonebook.projections;

import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.enums.LetterState;

import java.time.LocalDate;

/**
 * Projection for {@link com.pishgaman.phonebook.entities.Letter}
 */
public interface LetterInfo {
    Long getId();

    String getContent();

    LocalDate getCreationDate();

    String getLetterNumber();

    LetterState getLetterState();

    RecipientInfo getRecipient();

    SenderInfo getSender();

    /**
     * Projection for {@link Customer}
     */
    interface RecipientInfo {
        Long getId();

        String getName();
    }

    /**
     * Projection for {@link com.pishgaman.phonebook.entities.Sender}
     */
    interface SenderInfo {
        Long getId();

        String getName();
    }
}
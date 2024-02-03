package com.pishgaman.phonebook.dtos;

import com.pishgaman.phonebook.enums.LetterState;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

@Data
public class LetterDetailsDto implements Serializable {
    private Long letterId;
    private LocalDate letterDate;
    private String letterTopic;
    private String letterNumber;
    private LetterState letterState;
    private String recipientName;
    private String senderName;

    public LetterDetailsDto(Long letterId, LocalDate letterDate, String letterTopic, String letterNumber, LetterState letterState, String recipientName, String senderName) {
        this.letterId = letterId;
        this.letterDate = letterDate;
        this.letterTopic = letterTopic;
        this.letterNumber = letterNumber;
        this.letterState = letterState;
        this.recipientName = recipientName;
        this.senderName = senderName;
    }
}

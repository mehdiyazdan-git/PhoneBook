package com.pishgaman.phonebook.dtos;

import com.pishgaman.phonebook.entities.Sender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link Sender}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SenderDto implements Serializable {
    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String letterPrefix;
    private int letterCounter;
}
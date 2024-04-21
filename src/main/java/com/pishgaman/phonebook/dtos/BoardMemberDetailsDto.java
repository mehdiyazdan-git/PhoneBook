package com.pishgaman.phonebook.dtos;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.BoardMember}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class BoardMemberDetailsDto  extends BaseDto  implements Serializable {
    private Long id;
    private Long personId;
    private String personFirstName;
    private String personLastName;
    private Long companyId;
    private String companyCompanyName;
    private Long positionId;
    private String positionName;
    private String fullName;
}
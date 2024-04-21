package com.pishgaman.phonebook.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * DTO for {@link com.pishgaman.phonebook.entities.Position}
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PositionDto  extends BaseDto  implements Serializable {
    private Long id;
    private String name;
}
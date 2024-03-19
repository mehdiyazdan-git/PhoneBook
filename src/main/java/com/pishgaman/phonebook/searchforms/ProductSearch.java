package com.pishgaman.phonebook.searchforms;

import lombok.Data;

import java.math.BigDecimal;
@Data
public class ProductSearch {
    private String name;
    private String description;
    private BigDecimal price;
    private String priceComparison;
}

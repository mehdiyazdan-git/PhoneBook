package com.pishgaman.phonebook.specifications;


import com.pishgaman.phonebook.entities.Letter;
import com.pishgaman.phonebook.entities.Customer;
import com.pishgaman.phonebook.entities.Sender;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;
import java.time.LocalDate;

@StaticMetamodel(Letter.class)
public class Letter_ {
    public static volatile SingularAttribute<Letter, Long> id;
    public static volatile SingularAttribute<Letter, LocalDate> creationDate;
    public static volatile SingularAttribute<Letter, String> content;
    public static volatile SingularAttribute<Letter, String> letterNumber;
    public static volatile SingularAttribute<Letter, String> letterState;
    public static volatile SingularAttribute<Letter, Customer> recipient;
    public static volatile SingularAttribute<Letter, Sender> sender;
}


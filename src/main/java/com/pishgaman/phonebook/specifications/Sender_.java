package com.pishgaman.phonebook.specifications;


import com.pishgaman.phonebook.entities.Sender;
import jakarta.persistence.metamodel.SingularAttribute;
import jakarta.persistence.metamodel.StaticMetamodel;

@StaticMetamodel(Sender.class)
public class Sender_ {
    public static volatile SingularAttribute<Sender, Long> id;
    public static volatile SingularAttribute<Sender, String> name;
    public static volatile SingularAttribute<Sender, String> letterPrefix;
    public static volatile SingularAttribute<Sender, Integer> letterCounter;
    // Add other attributes of the Sender entity as needed
}

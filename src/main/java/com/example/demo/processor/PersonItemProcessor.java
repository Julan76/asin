package com.example.demo.processor;

import com.example.demo.domain.Person;
import org.springframework.batch.item.ItemProcessor;

public class PersonItemProcessor implements ItemProcessor<Person, Person> {
    @Override
    public Person process(Person item) {
        return item;
    }
}
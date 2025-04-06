package com.example.demo.reader;

import com.example.demo.domain.Person;
import com.example.demo.service.PersonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Sheet;
import org.springframework.batch.item.ExecutionContext;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemStream;
import org.springframework.batch.item.ItemStreamException;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
public class PersonItemReader implements ItemReader<Person>, ItemStream {
    private final PersonService personService;
    private final InputStream inputStream;
    private int currentIndex = 0;
    private static final String CURRENT_INDEX_KEY = "current.index";
    private List<Person> personList;

    public PersonItemReader(PersonService personService, InputStream inputStream) {
        this.personService = personService;
        this.inputStream = inputStream;
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        if (executionContext.containsKey(CURRENT_INDEX_KEY)) {
            currentIndex = executionContext.getInt(CURRENT_INDEX_KEY);
        }
        personList = personService.getPersonsFromFile(inputStream);
    }
    @Override
    public Person read() {
        if (currentIndex < personList.size()) {
            return personList.get(currentIndex++);
        }
        return null;

    }

    @Override
    public void close() throws ItemStreamException {
        personList = null;
        if (this.inputStream != null && this.inputStream != System.in) {
            try {
                this.inputStream.close();
            } catch (IOException e) {
                throw new ItemStreamException("Erreur lors de la fermeture du flux", e);
            }
        }
    }
}
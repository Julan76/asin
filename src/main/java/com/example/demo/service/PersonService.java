package com.example.demo.service;

import com.example.demo.domain.Person;
import com.example.demo.domain.Status;
import com.example.demo.exception.FileException;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Service
@Slf4j
public class PersonService {


    public List<Person> getPersonsFromFile(InputStream inputStream) {

        List<Person> persons = new ArrayList<>();
        try (Workbook workbook = new XSSFWorkbook(inputStream)) {
            Sheet aSheet = workbook.getSheetAt(0);

            Iterator<Row> rowIterator = aSheet.iterator();

            if (rowIterator.hasNext()) {
                rowIterator.next();
            }
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();
                String matricule = "";
                Cell matriculeCell = row.getCell(0);
                if (matriculeCell != null) {
                    if (matriculeCell.getCellType() == CellType.NUMERIC) {
                        matricule = String.valueOf(matriculeCell.getNumericCellValue());
                    } else if (matriculeCell.getCellType() == CellType.STRING) {
                        matricule = matriculeCell.getStringCellValue();
                    }
                }
                String nom = row.getCell(1).getStringCellValue();
                String prenom = row.getCell(2).getStringCellValue();
                String theDate = row.getCell(3).getStringCellValue();
                String theStatus = row.getCell(4).getStringCellValue();

                LocalDate date = parseDate(theDate);
                Status status = Status.valueOf(theStatus.toUpperCase().replace(" ", "_"));

                Person person = new Person();
                person.setMatricule(matricule);
                person.setNom(nom);
                person.setPrenom(prenom);
                person.setDatedenaissance(date);
                person.setStatus(status);
                persons.add(person);
            }
        } catch (IOException e) {
            log.warn(e.getMessage());
        }

        return persons;
    }

    private LocalDate parseDate(String input) {
        List<String> patterns = List.of("yyyy-MM-dd", "MM/dd/yyyy", "MM-dd-yyyy", "dd/MM/yyyy", "dd-MM-yyyy", "yyyy/MM/dd");
        for (String pattern : patterns) {
            try {
                return LocalDate.parse(input, DateTimeFormatter.ofPattern(pattern));
            } catch (DateTimeParseException ignored) {
                log.info("the date {} is doesn't match the pattern {}", input,pattern);
            }
        }
        throw new IllegalArgumentException("Unsupported date format: " + input);
    }
}

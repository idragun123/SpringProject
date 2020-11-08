package by.dragun.SpringProject.service;

import by.dragun.SpringProject.entity.Person;
import by.dragun.SpringProject.exceptions.ResourceNotFoundException;

import java.util.List;
import java.util.Optional;


public interface PersonService {
    List<Person> getAllPerson();
    void addNewPerson(Person person);
    void deletePerson(Person person );
    void editPerson(Person person, Long id);
    Person getById(long id) throws ResourceNotFoundException;
}

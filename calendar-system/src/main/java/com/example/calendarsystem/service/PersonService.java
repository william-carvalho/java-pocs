package com.example.calendarsystem.service;

import com.example.calendarsystem.dto.PersonRequest;
import com.example.calendarsystem.dto.PersonResponse;
import com.example.calendarsystem.entity.Person;
import com.example.calendarsystem.exception.ResourceNotFoundException;
import com.example.calendarsystem.repository.PersonRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PersonService {

    private final PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @Transactional
    public PersonResponse create(PersonRequest request) {
        Person person = new Person();
        person.setName(request.getName().trim());
        person.setEmail(request.getEmail());
        return toResponse(personRepository.save(person));
    }

    @Transactional(readOnly = true)
    public List<PersonResponse> listAll() {
        return personRepository.findAll()
                .stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Person findEntityById(Long id) {
        return personRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Person not found with id " + id));
    }

    public PersonResponse toResponse(Person person) {
        PersonResponse response = new PersonResponse();
        response.setId(person.getId());
        response.setName(person.getName());
        response.setEmail(person.getEmail());
        return response;
    }
}


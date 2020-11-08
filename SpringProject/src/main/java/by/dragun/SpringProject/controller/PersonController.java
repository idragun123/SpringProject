package by.dragun.SpringProject.controller;

import by.dragun.SpringProject.aop.LogAnnotation;
import by.dragun.SpringProject.dto.NewPersonDto;
import by.dragun.SpringProject.dto.PersonDto;
import by.dragun.SpringProject.entity.Person;
import by.dragun.SpringProject.exceptions.NoSuchEntityException;
import by.dragun.SpringProject.exceptions.ResourceNotFoundException;
import by.dragun.SpringProject.service.PersonService;
import by.dragun.SpringProject.util.Mapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.Date;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping
class PersonController {
    private final PersonService personService;
    @Value("${welcome.message}")
    private String message;
    @Value("${error.message}")
    private String errorMessage;
    @Autowired
    public PersonController(PersonService personService) {
        this.personService = personService;
        // this.personRepository = personRepository;
    }

    //, produces = { "application/json" , "application/xml"}
    @GetMapping(value = {"/personList"})
    public CollectionModel<EntityModel<PersonDto>> personList() {
        // return Mapper.mapAll(personService.getAllPerson(), PersonDto.class);
        CollectionModel<EntityModel<PersonDto>> resource = CollectionModel.wrap(
                Mapper.mapAll(personService.getAllPerson(), PersonDto.class));
        for (final EntityModel<PersonDto> res : resource) {
            Link selfLink = linkTo(PersonController.class)
                    .slash(res.getContent().getPersonId()).withSelfRel();
            res.add(selfLink);
        }
        resource.add(linkTo(methodOn(PersonController.class)
                .personList()).withRel("all"));
        return resource;
    }

    @Operation(summary = "Find person by ID", description = "Returns a single person", tags = {
            "person" })
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "successful operation",
                    content = @Content(schema = @Schema(implementation = Person.class))),
            @ApiResponse(responseCode = "404", description = "Person not found") })
    @GetMapping(value = {"/personList/{id}"})
    public PersonDto findById(
            @Parameter(description="Id of the person to be obtained. Cannot be empty",
                    required=true)
            @PathVariable("id") Long id) throws ResourceNotFoundException {
        return Mapper.map(personService.getById(id), PersonDto.class);
    }

    @PutMapping(value = "/editPerson/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void editPerson(@PathVariable("id") Long id, @Valid @RequestBody
            PersonDto persondto) throws ResourceNotFoundException {
        personService.getById(id);
        personService.editPerson(Mapper.map(persondto, Person.class),id);
    }

    @PostMapping("/addPerson")
    @ResponseStatus(HttpStatus.CREATED)
    public void savePerson( @Valid @RequestBody NewPersonDto personDto) {
        personService.addNewPerson(Mapper.map(personDto, Person.class));
    }

    @DeleteMapping(value = "/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deletePerson(@PathVariable("id") Long id) throws
            ResourceNotFoundException {
        personService.deletePerson(personService.getById(id));
    }
}



package com.ninos.service;

import com.ninos.business.api.PersonService;
import com.ninos.business.model.Person;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static com.ninos.service.ServiceUtils.createPerson;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Component Integration Test class for the Persons Routes
 */

@DisplayName("Person Route Integration Test")
@WebFluxTest(PersonsRoutes.class)
public class PersonsRoutesCIT {

  @MockBean
  private PersonService personService;

  private final WebTestClient webTestClient;

  @Autowired
  public PersonsRoutesCIT(
      WebTestClient webTestClient
  ) {
    this.webTestClient = webTestClient;
  }

  @Test
  @DisplayName("Saving a person with no issues - should return HTTP 202 Accepted")
  @Tag("CIT")
  public void savePerson202() throws URISyntaxException {

    PersonService.PersonActionResult result = new PersonService.PersonActionResult(false, "OK");
    when(personService.store(any(Person.class))).thenReturn(result);

    webTestClient
        .post()
        .uri(new URI("/v1/persons"))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .syncBody(createPerson())
        .exchange()
        .expectStatus().isAccepted();
  }

  @Test
  @DisplayName("Saving a person but repository is unavailable - should return HTTP 503 Service Unavailable")
  @Tag("CIT")
  public void savePerson503() throws URISyntaxException {

    PersonService.PersonActionResult result = new PersonService.PersonActionResult(true, "Repository unavailable");
    when(personService.store(any(Person.class))).thenReturn(result);

    webTestClient
        .post()
        .uri(new URI("/v1/persons"))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .syncBody(createPerson())
        .exchange()
        .expectStatus().isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
  }

  @Test
  @DisplayName("Saving a person with invalid json - should return HTTP 400 Bad Request")
  @Tag("CIT")
  public void savePerson400() throws URISyntaxException {
    String invalidJson = "<xml>invalid</xml>";

    webTestClient
        .post()
        .uri(new URI("/v1/persons"))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .syncBody(invalidJson)
        .exchange()
        .expectStatus().isBadRequest();
  }
}

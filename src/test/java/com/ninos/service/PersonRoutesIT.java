package com.ninos.service;

import com.ninos.business.model.Person;
import com.ninos.business.spi.PersonRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;


@DisplayName("Person Route Integration Test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PersonRoutesIT {

  @MockBean
  private PersonRepository personRepository;

  private final WebTestClient webTestClient;

  @Autowired
  public PersonRoutesIT(
      WebTestClient webTestClient
  ) {
    this.webTestClient = webTestClient;
  }


  @Test
  @DisplayName("Saving a person - should return HTTP 200 OK")
  public void savePerson(@LocalServerPort int port) throws URISyntaxException {
    LocalDate dob = LocalDate.now().minus(30, ChronoUnit.YEARS);
    Person personToSave = new Person("Spyridon", "Ninos", dob);

    webTestClient
        .post()
        .uri(new URI("http://localhost:" + port + "/v1/persons"))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .body(Mono.just(personToSave), Person.class)
        .exchange()
        .expectStatus().is2xxSuccessful();
  }
}

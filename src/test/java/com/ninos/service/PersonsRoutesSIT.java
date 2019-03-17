package com.ninos.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninos.business.model.Person;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import static com.ninos.service.ServiceUtils.createPerson;

/**
 * System Integration Test class for the persons routes
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@EmbeddedKafka(
    partitions = 1,
    topics = { "first" }
)
@DirtiesContext
@TestPropertySource(properties = "kafka.bootstrap-servers=${spring.embedded.kafka.brokers}")
public class PersonsRoutesSIT {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  private final EmbeddedKafkaBroker broker;
  private final WebTestClient webTestClient;

  @Autowired
  public PersonsRoutesSIT(
      EmbeddedKafkaBroker broker,
      WebTestClient webTestClient
  ) {
    this.broker = broker;
    this.webTestClient = webTestClient;
  }

  @Test
  @DisplayName("Send a valid person")
  public void send() throws URISyntaxException, IOException {

    Person sentPerson = createPerson();

    webTestClient
        .post()
        .uri(new URI("/v1/persons"))
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .syncBody(sentPerson)
        .exchange()
        .expectStatus().isAccepted();
  }

}

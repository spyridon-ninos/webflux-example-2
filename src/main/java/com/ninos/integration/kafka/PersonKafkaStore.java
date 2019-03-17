package com.ninos.integration.kafka;

import com.ninos.business.model.Person;
import com.ninos.business.spi.PersonRepository;
import com.ninos.service.EndpointConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Repository;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Kafka backed person repository
 */
@Repository
public class PersonKafkaStore implements PersonRepository {

  private final KafkaTemplate<String, Person> personKafkaTemplate;
  private final KafkaConfiguration.KafkaConfig kafkaConfig;
  private final EndpointConfiguration endpointConfiguration;

  @Autowired
  public PersonKafkaStore(
      KafkaTemplate<String, Person> personKafkaTemplate,
      KafkaConfiguration.KafkaConfig kafkaConfig,
      EndpointConfiguration endpointConfiguration
  ) {
    this.personKafkaTemplate = personKafkaTemplate;
    this.kafkaConfig = kafkaConfig;
    this.endpointConfiguration = endpointConfiguration;
  }

  /*
   * saves the person object to each of the topics configured
   * for the producer
   */
  @Override
  public void save(Person person) throws RepositoryException {
    String topic = kafkaConfig.getProducer().getTopic();

    try {
      personKafkaTemplate
          .send(topic, person)
          .get(endpointConfiguration.endpoint().getTimeoutMs(), TimeUnit.MILLISECONDS);
    } catch (InterruptedException | ExecutionException | TimeoutException e) {
      throw new RepositoryException("Got exception while waiting for the message to be sent");
    }
  }
}

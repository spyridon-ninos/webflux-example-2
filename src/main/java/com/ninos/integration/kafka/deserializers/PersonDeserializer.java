package com.ninos.integration.kafka.deserializers;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninos.business.model.Person;
import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

/**
 * custom deserializer for the person class
 * should be used by a kafka consumer
 */
public class PersonDeserializer implements Deserializer<Person> {
  private static final Logger logger = LoggerFactory.getLogger(PersonDeserializer.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> map, boolean b) {
    // nothing
  }

  @Override
  public Person deserialize(String s, byte[] bytes) {
    try {
      return objectMapper.readValue(bytes, Person.class);
    } catch (JsonParseException | JsonMappingException e) {
      logger.error("Caught parsing exception: {}", e.getMessage(), e);
    } catch (IOException e) {
      logger.error("Caught IO exception: {}", e.getMessage(), e);
    }

    return new Person("", "", LocalDate.now());
  }

  @Override
  public void close() {
    // nothing
  }
}

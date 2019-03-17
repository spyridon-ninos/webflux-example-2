package com.ninos.integration.kafka.serializers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.ninos.business.model.Person;
import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * custom serializer for the person class
 * this is used by the kafka producer
 */
@Component
public class PersonSerializer implements Serializer<Person> {
  private static final Logger logger = LoggerFactory.getLogger(PersonSerializer.class);
  private static final ObjectMapper objectMapper = new ObjectMapper();

  @Override
  public void configure(Map<String, ?> map, boolean b) {
    // nothing
  }

  @Override
  public byte[] serialize(String s, Person person) {
    try {
      return objectMapper.writeValueAsString(person).getBytes();
    } catch (JsonProcessingException e) {
      logger.error("Caught exception while processing person: {}: {}", person, e.getMessage(), e);
    }

    return new byte[0];
  }

  @Override
  public void close() {
    // nothing
  }
}

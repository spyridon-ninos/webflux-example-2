package com.ninos.business.api;

import com.ninos.business.model.Person;
import com.ninos.business.spi.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Implements the main actions around persons
 */
@Service
public class PersonService {
  private static final Logger logger = LoggerFactory.getLogger(PersonService.class);

  private final PersonRepository personRepository;

  @Autowired
  public PersonService(
      PersonRepository personRepository
  ) {
    this.personRepository = personRepository;
  }

  /**
   * stores a person in the persistence layer
   *
   * @param person the object to store
   *
   * @return the stored person
   */
  public PersonActionResult store(Person person) {
    logger.debug("Received: {}", person);
    try {
      personRepository.save(person);
      return new PersonActionResult(false, "OK");
    } catch (PersonRepository.RepositoryException e) {
      logger.error("Failed to save the person: {}, reason: {}", person, e.getReason(), e);
      return new PersonActionResult(true, e.getReason());
    }
  }

  /**
   * models the result of any action related to a person
   */
  public static class PersonActionResult {
    private final boolean failed;
    private final String reason;

    public PersonActionResult(boolean failed, String reason) {
      this.failed = failed;
      this.reason = reason;
    }

    public boolean isFailed() {
      return failed;
    }

    public String getReason() {
      return reason;
    }
  }
}

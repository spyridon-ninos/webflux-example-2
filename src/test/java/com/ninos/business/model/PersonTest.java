package com.ninos.business.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("Person Unit Tests")
public class PersonTest {

  private Person person;

  @BeforeEach
  public void beforeEach() {
    LocalDate dob = LocalDate.now().minus(30, ChronoUnit.YEARS);
    person = new Person("Spyridon", "Ninos", dob);
  }

  @Test
  @DisplayName("The person's calculated age is correct")
  public void ageIs30() {
    assertEquals(30, person.getAge());
  }
}

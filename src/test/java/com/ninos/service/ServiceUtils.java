package com.ninos.service;

import com.ninos.business.model.Person;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * utility methods for the service layer
 */
public class ServiceUtils {

  /**
   * creates a person with fixed facts
   *
   * @return a person object
   */
  public static Person createPerson() {
    LocalDate dob = LocalDate.now().minus(30, ChronoUnit.YEARS);
    return new Person("Spyridon", "Ninos", dob);
  }
}

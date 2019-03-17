package com.ninos.business.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.Objects;
import java.util.UUID;

/**
 * models a person
 */
public class Person implements Comparable<Person> {
  private final String firstName;
  private final String lastName;
  private final LocalDate dob;
  private final String uuid;

  @JsonCreator
  public Person(
    @JsonProperty("first_name") String firstName,
    @JsonProperty("last_name") String lastName,
    @JsonProperty("dob") LocalDate dob
  ) {
      this.firstName = firstName;
      this.lastName = lastName;
      this.dob = dob;
      uuid = UUID.randomUUID().toString();
  }

  @JsonProperty("first_name")
  public String getFirstName() {
      return firstName;
  }

  @JsonProperty("last_name")
  public String getLastName() {
      return lastName;
  }

  @JsonProperty("dob")
  @JsonFormat(
      shape = JsonFormat.Shape.STRING,
      pattern = "yyyy-MM-dd"
  )
  public LocalDate getDob() {
      return dob;
  }

  @JsonProperty("id")
  public String getUuid() {
    return uuid;
  }

  /**
   * calculates the age of the person
   *
   * @return the age in years
   */
  @JsonIgnore
  public long getAge() {
      return getAge(ChronoUnit.YEARS);
  }

  /**
   * calculates the age of the person
   *
   * @param unit the time unit to use to calculate the age
   * @return the age in the units provided as input
   */
  @JsonIgnore
  public long getAge(ChronoUnit unit) {
      return unit.between(dob, LocalDate.now());
  }

  @Override
  public int compareTo(Person person) {
    return Comparator
            .comparing(Person::getLastName)
            .thenComparing(Person::getFirstName)
            .thenComparing(Person::getDob)
            .compare(this, person);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Person person = (Person) o;
    return firstName.equals(person.firstName) &&
           lastName.equals(person.lastName) &&
           dob.equals(person.dob);
  }

  @Override
  public int hashCode() {
    return Objects.hash(firstName, lastName, dob);
  }

  @Override
  public String toString() {
    return "Person{" +
           "firstName='" + firstName + '\'' +
           ", lastName='" + lastName + '\'' +
           ", dob=" + dob +
           ", uuid='" + uuid + '\'' +
           '}';
  }
}

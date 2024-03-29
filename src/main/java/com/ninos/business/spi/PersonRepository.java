package com.ninos.business.spi;

import com.ninos.business.model.Person;

/**
 * should be implemented by the persistence code
 */
public interface PersonRepository {
    /**
     * should take a person object and store it in a persistence construct
     *
     * @param person the object to persist
     *
     * @throws RepositoryException if something wrong happened while saving the person
     */
    void save(Person person) throws RepositoryException;

    /**
     * a generic exception thrown by any repository related method
     */
    class RepositoryException extends Exception {
        private final String reason;
        private final Throwable cause;

        public RepositoryException(String reason, Throwable cause) {
            this.reason = reason;
            this.cause = cause;
        }

        public String getReason() {
            return reason;
        }

        @Override
        public Throwable getCause() {
            return cause;
        }
    }
}

package com.ninos.service;

import com.ninos.business.api.PersonService;
import com.ninos.business.model.Person;
import io.netty.handler.codec.DecoderException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.methods;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * defines the methods that implement the routes followed by the person related web paths
 */
@Configuration
@ConditionalOnProperty(prefix = "endpoints-enabled", name = "persons", havingValue = "true")
public class PersonsRoutes {

  private static final Logger logger = LoggerFactory.getLogger(PersonsRoutes.class);


  private final String personsPath;
  private final HttpMethod[] unsupportedHttpMethods = {
    HttpMethod.GET,
    HttpMethod.PUT,
    HttpMethod.DELETE,
    HttpMethod.OPTIONS,
    HttpMethod.HEAD,
    HttpMethod.TRACE
  };

  private final PersonService personService;

  @Autowired
  public PersonsRoutes(
      PersonService personService,
      @Value("${api.version}") String apiVersion
  ) {
    this.personService = personService;
    personsPath = "/" + apiVersion + "/persons";

    logger.info("Initialized the persons endpoint");
  }

  /**
   * the route that saves a person to the persistence layer
   *
   * @return a router function
   */
  @Bean
  public RouterFunction<ServerResponse> save() {
    return route(POST(personsPath).and(accept(MediaType.APPLICATION_JSON_UTF8)), this::saveHandler);
  }

  /**
   * return the appropriate http status for all the unsupported methods
   *
   * @return METHOD_NOT_ALLOWED for all unsupported methods
   */
  @Bean
  public RouterFunction<ServerResponse> unsupportedMethods() {
    return route(path(personsPath).and(methods(unsupportedHttpMethods)), request -> ServerResponse.status(HttpStatus.METHOD_NOT_ALLOWED).build());
  }

  /*
   * the handler that builds the response on the save action
   * produces json
   */
  private Mono<ServerResponse> saveHandler(ServerRequest request) {
      return request
          .bodyToMono(Person.class)
          .map(personService::store)
          .flatMap(result -> {
            if (result.isFailed()) {
              return ServerResponse.status(HttpStatus.SERVICE_UNAVAILABLE).build();
            } else {
              return ServerResponse.status(HttpStatus.ACCEPTED).build();
            }
          })
          .onErrorResume(DecoderException.class, e -> ServerResponse.badRequest().build());
  }
}

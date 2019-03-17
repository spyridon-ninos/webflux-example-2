package com.ninos.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import static org.springframework.web.reactive.function.server.RequestPredicates.all;
import static org.springframework.web.reactive.function.server.RequestPredicates.path;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

/**
 * implements the routes handling the root path
 *
 * the root path should not be supported, but it should redirect to the current api version
 * the current api version root should provide a URI Template of all supported endpoints
 */
@Configuration
public class RootRoute {

  private static final Logger logger = LoggerFactory.getLogger(RootRoute.class);
  private final String apiVersion;

  @Autowired
  public RootRoute(
      @Value("${api.version}") String apiVersion
  ) {
    this.apiVersion = apiVersion;
  }

  /**
   * Redirects all requests to the versioned root path
   * all methods are redirected there
   *
   * if the apiVersion is not properly configured, then we may get an URISyntaxException -
   * at this case we just return a MOVED_PERMANENTLY status with no redirection
   */
  @Bean
  public RouterFunction<ServerResponse> getRoot() {
    try {
      URI redirectLocation = new URI("/" + apiVersion + "/");
      return route(all().and(path("/")), request -> ServerResponse.permanentRedirect(redirectLocation).build());
    } catch (URISyntaxException ex) {
      logger.error("Invalid URI format. Is the api version correctly formatted? (api version: {})", apiVersion, ex);
    }

    return route(all().and(path("/")), request -> ServerResponse.status(HttpStatus.MOVED_PERMANENTLY).build());
  }

  /**
   * returns the URI template with all enabled endpoints of this app
   *
   * @param enabledEndpoints enabled endpoints from the configuration file
   * @return the URI template
   */
  @Bean
  public RouterFunction<ServerResponse> getRootWithVersionOne(EnabledEndpoints enabledEndpoints) {
    return route(all().and(path("/v1")), request -> buildUriTemplate(request, buildRoutes(enabledEndpoints)));
  }

  /*
   * we build the URI template here
   *
   * we get the server request and the map with the urls from the configuration file
   */
  private Mono<ServerResponse> buildUriTemplate(ServerRequest request, Map<String, String> urlMap) {
    // we need the bellow in order to build absolute urls
    String scheme = request.uri().getScheme();
    String fqdn = request.uri().getHost();
    int port = request.uri().getPort();

    // transform all enabled endpoints to absolute urls
    Map<String, String> decoratedUrls = urlMap
        .keySet()
        .stream()
        .collect(Collectors.toMap(Function.identity(), key -> scheme + "://" + fqdn + ":" + port + "/" + urlMap.get(key)));

    // return the uri template
    return ServerResponse
        .ok()
        .contentType(MediaType.APPLICATION_JSON_UTF8)
        .syncBody(decoratedUrls);
  }

  /*
   * gets the map with the enabled endpoings and builds the relative url for each endpoint
   */
  private Map<String, String> buildRoutes(EnabledEndpoints enabledEndpoints) {
    Map<String, Boolean> endpoints = enabledEndpoints.getEndpointsEnabled();

    return endpoints
        .keySet()
        .stream()
        .filter(endpoints::get)
        .collect(Collectors.toMap(Function.identity(), this::buildUrlPath));
  }

  /*
   * receives the path fragment and prefixes the api version
   */
  private String buildUrlPath(String key) {
    return apiVersion + "/" + key + "/";
  }

  /**
   * provides the enabled endpoints configuration bean
   *
   * @return an EnabledEndpoints populated class
   */
  @Bean
  @ConfigurationProperties
  public EnabledEndpoints buildRoutes() {
    return new EnabledEndpoints();
  }

  /**
   * models the enabled endpoints map from the configuration file
   */
  public static class EnabledEndpoints {
    Map<String, Boolean> endpointsEnabled;

    public Map<String, Boolean> getEndpointsEnabled() {
      return endpointsEnabled;
    }

    public void setEndpointsEnabled(Map<String, Boolean> endpointsEnabled) {
      this.endpointsEnabled = endpointsEnabled;
    }
  }
}

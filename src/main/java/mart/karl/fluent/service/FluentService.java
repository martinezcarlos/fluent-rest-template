/*
 * Copyright (c) 2020 Karl Mart
 * Carlos Martinez, ingcarlosmartinez@icloud.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package mart.karl.fluent.service;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import mart.karl.fluent.uri.FluentUriBuilder;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * FluentService is a handy abstraction of a service's API collection of resources, identified by
 * Uniform Resource Identifiers ({@linkplain URI}). <br>
 * FluentService provides the generic syntax of an URI required to reach a service plus optional API
 * version, a {@linkplain Map} of endpoints paths exposed by that service, <i>common</i> service
 * query params and<i>common</i> fragment. These optional parameters can be used to request the
 * creation of complete URI at runtime.<br>
 * <br>
 *
 * <p>The API <b>version</b>, if given, is treated as a <i>pathSegment</i> during URI construction
 * and will be placed after the URI's <i>path</i>.
 *
 * <p>An <b>endpoint's value</b>, if requested, is treated as a <i>pathSegment</i> during URI
 * construction and will be placed after the <i>path</i> and <i>version</i>, if present.
 *
 * <p>Any <i>common</i> query params and fragment will be applied to all of the URIs created with
 * this Service. Providing extra query params or overriding the fragment is possible when building a
 * specific URI, but this doesn't affect the original <i>common</i> query params and fragment for
 * this Service.<br>
 * <br>
 * The endpoints map is a {@code "Map<String, String>"} containing endpoints paths values mapped by
 * arbitrary keys. Endpoints values can contain <i>URI template variables</i> that will be replaced
 * during the building process.<br>
 *
 * <pre class="code">
 * fetchProviderFoo: provider/{providerId}/foo
 * createPersonBar: person/bar
 * </pre>
 *
 * <p>FluenService will provide the means to build an URI of the like:
 *
 * <pre class="code">
 *  scheme://host:port/path/version/endpoint?query#fragment
 * </pre>
 *
 * <p>Building an URI through FluentService follows these internal phases:
 *
 * <ol>
 *   <li><b>FluentService starter:</b> an instance of FluentService is created in any of the
 *       initialization ways.
 *   <li><b>URI builder:</b> allows to provide specific {@linkplain URI} parts to be used for
 *       buiding the final URI. These parts will apply to the specified endpoint used, if any.
 *       Specific URI parts override some <i>common</i> URI parts (fragment) or enhance others
 *       (query params, uri variables), if any were given during FluentService initialization.
 *   <li><b>URI creation:</b> This phase uses all the provided <i>common</i> and specific URI parts
 *       to {@linkplain UriComponentsBuilder#buildAndExpand(Map) build and expand} the uri
 *       components which eventually conforms to a URI.
 * </ol>
 *
 * <h1>FluentBuilder's initialization ways</h1>
 *
 * <p>There are several ways to instantiate a FluentService. This list describes the initialization
 * ways ordered from stronly recommended to not recommended.
 *
 * <ol>
 *   <li>Spring Boot's ConfigurationProperties.
 *   <li>FluentService's own Builder pattern.
 *   <li>FluentService's {@linkplain FluentService#from(URI)} method to obtain a builder.
 *   <li>FluentService's {@linkplain FluentService#from(String)} method to obtain a builder.
 *   <li>FluentService's NoArgsConstructor (not recommended).
 * </ol>
 *
 * <h2>1. Spring Boot's ConfigurationProperties</h2>
 *
 * <p>This is the main idea behind FluentService and the preferred initialization way. All you need
 * to do is to provide a Spring's application file (YAML preferably) and declare the FluentService
 * parameters you need.
 *
 * <pre class="code">
 * services:
 *   my-cool-service:
 *     scheme: https
 *     host: cool-service.com
 *     endpoints:
 *       getCoolStuff: get/stuff/{stuffId}
 *       postReminder: reminder/set
 * </pre>
 *
 * <p>And declare a empty ConfigurationProperties Spring component extending FluentService.
 *
 * <pre class="code">
 * {@code @Component}
 * {@code @ConfigurationProperties(value = "services.my-cool-service", ignoreUnknownFields = false)}
 * public class PostmanService extends FluentService {
 * }</pre>
 *
 * <p>With that alone you have an injectable FluentService Spring component.<br>
 * <br>
 *
 * <p>The complete set of Service parameters you can define in an application file (YAML preferably)
 * are these:
 *
 * <pre class="code">
 * services: # nice to group your many services. Not required but suggested.
 *   service-name:
 *     scheme:
 *     host:
 *     port:
 *     context-path:
 *     version:
 *     endpoints:
 *       keyOne: valueOne
 *       keyTwo: valueTwo
 *       ...
 *       keyN: valueN
 *     common-query-params:
 *       nameOne: foo1
 *       nameTwo: bar1,bar2,...,barN # multiple values separated by comma.
 *       ...
 *     fragment:
 * </pre>
 *
 * <h2>2. FluentService's own Builder pattern</h2>
 *
 * <p>Useful for when you can't use ConfigurationProperties or want to instantiate FluentServices
 * dynamically.
 *
 * <pre class="code">
 * FluentService service =
 *   FluentService.builder()
 *     .scheme(SCHEME)
 *     .host(HOST)
 *     .port(PORT)
 *     .contextPath(PATH)
 *     .version(VERSION)
 *     .endpoints(Collections.singletonMap(FOO, BAR))
 *     .commonQueryParams(multiValueMap)
 *     .commonFragment(FRAGMENT)
 *     .build();
 * </pre>
 *
 * <h2>3, 4. FluentService's from(URI) and from(String) methods</h2>
 *
 * <p>These abstract methods let you request a FluentService builder provided you have a URI
 * instance or a String representation of a URI. You can use this builder to further enhance your
 * Service if you need parameters other that those provided by the URI, e.g. endpoints:
 *
 * <pre class="code">
 * final String uriString = "https://foo.bar:80/context-path?baz=boom#fragment";
 * final URI uri = URI.create(uriString);
 *
 * final FluentService service1 = FluentService.from(uri).build(); // No extra params required.
 * final FluentService service2 =
 *     FluentService.from(uriString).version(myVersion).endpoints(myEndpointsMap).build();
 * </pre>
 *
 * <h2>5. FluentService's NoArgsConstructor</h2>
 *
 * <p>Although strongly discouraged, you can use plain old NoArgsConstructor to construct
 * FluentServices, but let's face it, this is not January 23, 1996 anymore. NoArgsConstructor is
 * there due to restrictions with ConfigurationProperties.
 *
 * <pre class="code">
 * FluentService emptyService = new FluentService(); // WARNING
 * </pre>
 *
 * <h1>Usage</h1>
 *
 * Independently of the initialization way you used, a FluentService can be used as easy as this:
 * <br>
 * <br>
 *
 * <p>Given a service built by using "http://dummy.host:8080", context path "cool/path", version
 * "v1" and one endpoint entry with key "endpointKey" and value "/foo/{foo}/baz/{baz}".
 *
 * <pre class="code">
 * final FluentService service = ... // FluentService starter phase
 *
 * // Creates a basic uri without using an endpoint or any other parameters.
 * final URI uri1 =
 *    service
 *    .uriBuilder() // URI builder phase
 *    .build();     // URI creation phase
 *
 * final URI uri2 =
 *    service
 *    .uriBuilder(endpointKey)         // URI builder phase
 *    .uriVariable(FOO, BAR)
 *    .uriVariable(BAZ, BUM)
 *    .queryParam(IS_COOL, OBVIOUSLY)
 *    .build();                        // URI creation phase
 * </pre>
 *
 * <p>uri1 will be created as "http://dummy.host:8080/cool/path/v1/".
 *
 * <p>uri2 will be created as
 * "http://dummy.host:8080/cool/path/v1/foo/bar/baz/bum?isCool=obviously".
 *
 * @author Carlos Martinez - Karl Mart
 */
@Setter
@ToString
@SuperBuilder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FluentService {
  private String scheme;
  private String host;
  private String port;
  private String contextPath;
  private String version;
  @NonNull @Builder.Default private Map<String, String> endpoints = new HashMap<>();

  @NonNull @Builder.Default
  private MultiValueMap<String, String> commonQueryParams = new LinkedMultiValueMap<>();

  private String commonFragment;

  /**
   * Convenient method to create a FluentServiceBuilder from a {@linkplain URI}. <br>
   * This will populate the builder with all the basic URI parts of {@linkplain FluentService}
   * except for the version and endpoints map.<br>
   * <br>
   * If desired, this FluentService builder can be provided with a <i>version</i> and/or
   * <i>endpoints</i> before building the FluentService instance.<br>
   * <br>
   * <b>Note:</b> call builder's {@code build()} method to build the FluentService instance after
   * you have configured the builder as required.
   *
   * @param uri Standard {@linkplain URI}.
   * @return A FluentServiceBuilder instance with the necessary parameters to build a FluentService.
   */
  public static FluentServiceBuilder from(final URI uri) {
    Assert.notNull(uri, "uri must not be null");
    return from(UriComponentsBuilder.fromUri(uri).build());
  }

  private static FluentServiceBuilder from(final UriComponents uriComponents) {
    Assert.notNull(uriComponents, "uriComponents must not be null");
    return FluentService.builder()
        .scheme(uriComponents.getScheme())
        .host(uriComponents.getHost())
        .port(uriComponents.getPort() == -1 ? null : String.valueOf(uriComponents.getPort()))
        .contextPath(uriComponents.getPath())
        .commonQueryParams(new LinkedMultiValueMap<>(uriComponents.getQueryParams()))
        .commonFragment(uriComponents.getFragment());
  }

  /**
   * Convenient method to create a FluentServiceBuilder from a String representation of a
   * {@linkplain URI URI}. <br>
   * This will populate the builder with all the basic URI parts of {@linkplain FluentService}
   * except for the version and endpoints map.<br>
   * <br>
   * If desired, this FluentService builder can be provided with a <i>version</i> and/or
   * <i>endpoints</i> before building the FluentService instance.<br>
   * <br>
   * <b>Note:</b> call builder's {@code build()} method to build the FluentService instance after
   * you have configured the builder as required. <br>
   * <br>
   *
   * <p><b>Note:</b> Any sequence of characters between URI's <i>authority</i> and <i>query</i>
   * sections (if any) will be considered URI's <b><i>path</i></b>.
   *
   * <pre class="code">
   * https://foo.bar/<b>this-is-path</b>
   * https://foo.bar:80/<b>this/is/path/too</b>?baz=bam
   * </pre>
   *
   * @param uriString Standard String representation of {@linkplain URI}.
   * @return A FluentServiceBuilder instance with the necessary parameters to build a FluentService.
   */
  public static FluentServiceBuilder from(final String uriString) {
    Assert.hasText(uriString, "UriString must not be null or empty");
    return from(UriComponentsBuilder.fromUriString(uriString).build());
  }

  /**
   * After a {@linkplain FluentService} has been initialized by any of the provided mechanisms, use
   * this method to create a {@linkplain ServiceUriBuilder} backed by the FluentService and start
   * the <b>URI Builder phase</b>.<br>
   * <br>
   * This method will <b>NOT</b> provide an endpoint to the ServiceUriBuilder.<br>
   * <br>
   *
   * <p><b>Note:</b> if FluentService was initialized with <i>common</i> query params and/or
   * <i>common</i> fragment, these properties will be passed down to the ServiceUriBuilder and used
   * to build your URI.
   *
   * @return A {@linkplain ServiceUriBuilder} backed by the current FluentService.
   */
  public final ServiceUriBuilder uriBuilder() {
    return uriBuilder(null);
  }

  /**
   * After a {@linkplain FluentService} has been initialized by any of the provided mechanisms, use
   * this method to create a {@linkplain ServiceUriBuilder} backed by the FluentService and start
   * the <b>URI Builder phase</b>.<br>
   * <br>
   * This method provides and endpoint value, represented by the key, to the ServiceUriBuilder which
   * will use it as part of the URI path.<br>
   * <br>
   *
   * <p><b>Note:</b> if FluentService was initialized with <i>common</i> query params and/or
   * <i>common</i> fragment, these properties will be passed down to the ServiceUriBuilder and used
   * to build your URI.
   *
   * @param endpointKey The key representing an endpoint value in the endpoints map.
   * @return A {@linkplain ServiceUriBuilder} backed by the current FluentService.
   */
  public final ServiceUriBuilder uriBuilder(final String endpointKey) {
    final UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)
            .port(StringUtils.isEmpty(port) ? null : port)
            .path(contextPath)
            .pathSegment(version)
            .pathSegment(Optional.ofNullable(endpoints).map(m -> m.get(endpointKey)).orElse(null))
            .queryParams(commonQueryParams)
            .fragment(commonFragment);
    return new DefaultUriBuilder(uriComponentsBuilder);
  }

  /**
   * Declares useful methods to provide specific {@linkplain URI} parts to be used during URI
   * construction and the {@linkplain ServiceUriBuilder#build() build()} method itself. This
   * interface represents the <b>URI builder phase</b> of {@linkplain FluentService}.
   */
  public interface ServiceUriBuilder extends FluentUriBuilder<ServiceUriBuilder> {

    /**
     * Once the desired {@linkplain URI} parts have been provided, this method uses those parts to
     * {@linkplain UriComponentsBuilder#buildAndExpand(Map) build and expand} the uri components
     * which eventually conforms to a URI.<br>
     *
     * <p>The execution of this method ends the URI builder phase of {@linkplain FluentService
     * FluentService} and comforms to the brief URI creation phase.
     *
     * @return A fully formed URI.
     */
    URI build();
  }

  @RequiredArgsConstructor
  private static class DefaultUriBuilder implements ServiceUriBuilder {

    private final Map<String, Object> uriVariables = new HashMap<>();
    private final UriComponentsBuilder uriComponentsBuilder;

    @Override
    public URI build() {
      return uriComponentsBuilder.buildAndExpand(uriVariables).toUri();
    }

    @Override
    public DefaultUriBuilder queryParam(final String key, final Object... values) {
      uriComponentsBuilder.queryParam(key, values);
      return this;
    }

    @Override
    public DefaultUriBuilder queryParam(final String key, final Collection<?> values) {
      Optional.ofNullable(values).ifPresent(l -> uriComponentsBuilder.queryParam(key, l.toArray()));
      return this;
    }

    @Override
    public DefaultUriBuilder queryParams(final MultiValueMap<String, String> params) {
      uriComponentsBuilder.queryParams(params);
      return this;
    }

    @Override
    public ServiceUriBuilder fragment(final String fragment) {
      uriComponentsBuilder.fragment(fragment);
      return this;
    }

    @Override
    public DefaultUriBuilder uriVariable(final String key, final Object value) {
      uriVariables.put(key, value);
      return this;
    }

    @Override
    public DefaultUriBuilder uriVariables(final Map<String, ?> variables) {
      Optional.ofNullable(variables).ifPresent(uriVariables::putAll);
      return this;
    }
  }
}

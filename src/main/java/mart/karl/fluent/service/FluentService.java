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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import mart.karl.fluent.uri.FluentUriBuilder;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * FluentService is a handy abstraction of a service's API collection of resources, identified by
 * Uniform Resource Identifiers ({@linkplain URI}). <br>
 * FluentService provides the generic syntax of an URI required to reach a service plus an API
 * version and a {@linkplain Map} of endpoints paths exposed by that service. This map of endpoints
 * can be used to request the creation of complete URI at runtime.<br>
 * <br>
 *
 * <p>The API <b>version</b>, if given, is treated as a <i>pathSegment</i> during URI construction
 * and will be placed after the URI's <i>path</i>.
 *
 * <p>An <b>endpoint's value</b>, if requested, is treated as a <i>pathSegment</i> during URI
 * construction and will be placed after the <i>path</i> and <i>version</i>, if present. <br>
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
 *   <li><b>Starter:</b> an instance of FluentService is created in any of the initialisation ways.
 *       After a FluentService is initialised, and before transitioning to the next phase, you can
 *       provide <i>common</i> URI parts, like query params and fragment. These <i>common</i> will
 *       apply to any created URI, with some exceptions for fragment. See {@linkplain
 *       ServiceUriBuilder#fragment(String) fragment} method. While in this phase you can also
 *       provide and API version and Service endpoints, if you haven't provided during
 *       initialisation or if you just wish.
 *   <li><b>Builder:</b> allows to provide specific {@linkplain URI} parts to be used for buiding
 *       the final URI. These parts will apply to the specified endpoint, if any. Specific URI parts
 *       override any <i>common</i> URI parts (fragment) or enhance others (query params, uri
 *       variables), if any were given during FluentService initialisation.
 *   <li><b>Creation:</b> This phase uses all the provided <i>common</i> and specific URI parts to
 *       {@linkplain UriComponentsBuilder#buildAndExpand(Map) build and expand} the uri components
 *       which eventually conforms to a URI.
 * </ol>
 *
 * <p>There are several ways to instantiate a FluentService. This list describes the initialisation
 * ways ordered from stronly recommended to not recommended.
 *
 * <ol>
 *   <li>Spring Boot's ConfigurationProperties.
 *   <li>FluentService's own Builder pattern.
 *   <li>FluentService's {@linkplain FluentService#from(URI)} method.
 *   <li>FluentService's {@linkplain FluentService#from(String)} method.
 *   <li>FluentService's NoArgsConstructor (not recommended).
 * </ol>
 *
 * <h2>Spring Boot's ConfigurationProperties</h2>
 *
 * <p>This is the source idea behind FluentService and the preferred initialisation way. All you
 * need to do is to provide a Spring's application file (YAML preferably) and declare the
 * FluentService you need.
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
 * <h2>FluentService's own Builder pattern</h2>
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
 *     .build();
 * </pre>
 *
 * <h2>FluentService's from(URI) and from(String) methods</h2>
 *
 * <p>These abstract methods let you build a FluentService provided you have a URI instance or a
 * String representation of a URI:
 *
 * <pre class="code">
 * final String uriString = "https://foo.bar:80/context-path?baz=boom#fragment";
 * final URI uri = URI.create(uriString);
 *
 * final FluentService service1 = FluentService.from(uri);
 * final FluentService service2 = FluentService.from(uriString);
 * </pre>
 *
 * <p>As expected, these methods won't provide API version or endpoints as these are not part of a
 * URI's generic syntax. You can, however, provide those parameters after initialisation. <br>
 * <br>
 *
 * <h2>FluentService's NoArgsConstructor</h2>
 *
 * <p>Although strongly discouraged, you can use plain old NoArgsConstructor to construct
 * FluentServices, but let's face it, this is not January 23, 1996 anymore. NoArgsConstructor is
 * there due to restrictions with ConfigurationProperties.
 *
 * <pre class="code">
 * FluentService emptyService = new FluentService(); // WARNING
 * </pre>
 *
 * @author Carlos Martinez - Karl Mart
 */
@Setter
@ToString
@SuperBuilder
@EqualsAndHashCode
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class FluentService {
  private String scheme;
  private String host;
  private String port;
  private String contextPath;
  private String version;
  private Map<String, String> endpoints;
  private MultiValueMap<String, String> commonQueryParams;
  private String commonFragment;

  public FluentService() {
    endpoints = new HashMap<>();
    commonQueryParams = new LinkedMultiValueMap<>();
  }

  /**
   * Convenient method to create a FluentService from a {@linkplain URI}. <br>
   * This will populate all the basic URI parts of {@linkplain FluentService} except for the version
   * and endpoints map.<br>
   * <br>
   * If desired, an instance of FluentService built in any way can be provided with a <i>version</i>
   * and/or <i>endpoints</i>. See {@link FluentService#version(String)}, {@link
   * FluentService#endpoint(String, String)} and {@link FluentService#endpoints(Map)}.
   *
   * @param uri Standard {@linkplain URI}.
   * @return A FluentService instance.
   */
  public static FluentService from(final URI uri) {
    Assert.notNull(uri, "uri must not be null");
    return from(UriComponentsBuilder.fromUri(uri).build());
  }

  private static FluentService from(final UriComponents uriComponents) {
    Assert.notNull(uriComponents, "uriComponents must not be null");
    return FluentService.builder()
        .scheme(uriComponents.getScheme())
        .host(uriComponents.getHost())
        .port(uriComponents.getPort() == -1 ? null : String.valueOf(uriComponents.getPort()))
        .contextPath(uriComponents.getPath())
        .endpoints(new HashMap<>())
        .commonQueryParams(new LinkedMultiValueMap<>(uriComponents.getQueryParams()))
        .commonFragment(uriComponents.getFragment())
        .build();
  }

  /**
   * Convenient method to create a FluentService from a String representation of a {@linkplain URI
   * URI}. <br>
   * This will populate all the basic URI parts of {@linkplain FluentService} except for the version
   * and endpoints map.<br>
   * <br>
   * If desired, an instance of FluentService built in any way can be provided with a <i>version</i>
   * and/or <i>endpoints</i>. See {@link FluentService#version(String)}, {@link
   * FluentService#endpoint(String, String)} and {@link FluentService#endpoints(Map)}.<br>
   * <br>
   *
   * <p><strong>Note:</strong> Any sequence of characters between URI's <i>authority</i> and
   * <i>query</i> sections (if any) will be considered URI's <b><i>path</i></b>.
   *
   * <pre class="code">
   * https://foo.bar/<b>this-is-path</b>
   * https://foo.bar:80/<b>this/is/path/too</b>?baz=bam
   * </pre>
   *
   * @param uriString Standard String representation of {@linkplain URI}.
   * @return A FluentService instance.
   */
  public static FluentService from(@NonNull final String uriString) {
    Assert.hasText(uriString, "UriString must not be null or empty");
    return from(UriComponentsBuilder.fromUriString(uriString).build());
  }

  public void setEndpoints(final Map<String, String> endpoints) {
    this.endpoints = Optional.ofNullable(endpoints).orElseGet(HashMap::new);
  }

  public void setCommonQueryParams(final MultiValueMap<String, String> commonQueryParams) {
    this.commonQueryParams =
        Optional.ofNullable(commonQueryParams).orElseGet(LinkedMultiValueMap::new);
  }

  /**
   * Sets (or overrides) the <i>version</i> value of this FluentService.
   *
   * @param version Representation of a API version.
   * @return The FluentService instance invoking this method.
   */
  public final FluentService version(@Nullable final String version) {
    this.version = version;
    return this;
  }

  /**
   * Registers a service's endpoint. If a service's endpoint <i>value</i> previously exists under
   * the same <i>key</i>, it's value is replaced.
   *
   * @param key Key of the service's endpoint.
   * @param value Value of the service's endpoint
   * @return The FluentService instance invoking this method.
   */
  public final FluentService endpoint(final String key, final String value) {
    Assert.hasText(key, "key must not be null or empty");
    Assert.hasText(value, "value must not be null or empty");
    endpoints.put(key, value);
    return this;
  }

  /**
   * Registers several service's endpoints provided by the given {@linkplain Map}. If a service's
   * endpoint <i>value</i> exists under any of the <i>keys</i> provided by the Map, it's value is
   * replaced.
   *
   * @param endpoints Key-value map representing service's endpoints.
   * @return The FluentService instance invoking this method.
   */
  public final FluentService endpoints(final Map<String, String> endpoints) {
    Assert.notNull(endpoints, "endpoints must not be null");
    this.endpoints.putAll(endpoints);
    return this;
  }

  /**
   * Registers common query params to the service. If service's common query param <i>values</i>
   * previously exist under the same <i>key</i>, these new values will be included in the list of
   * that key. <br>
   * <br>
   * Registered common query parameters are <i>common</i> to all of the {@linkplain URI}s created
   * with this FluentService independently of the endpoint used, if any, and will be used in the
   * construction process. If additional query params are provided during the uri builder phase,
   * these parameters will be included as well.
   *
   * @param key Key of the service's common query params.
   * @param values Values to include in the service's common query params list.
   * @return The FluentService instance invoking this method.
   */
  public final FluentService commonQueryParams(final String key, final String... values) {
    Assert.hasText(key, "key must not be null or empty");
    Assert.notNull(values, "values must not be null");
    return commonQueryParams(key, Arrays.asList(values));
  }

  /**
   * Registers a list of common query params to the service. If service's common query param
   * <i>values</i> previously exist under the same <i>key</i>, this new list of values will be
   * included in the list of that key.<br>
   * <br>
   * <br>
   * Registered common query parameters are <i>common</i> to all of the {@linkplain URI}s created
   * with this FluentService independently of the endpoint used, if any, and will be used in the
   * construction process. If additional query params are provided during the uri builder phase,
   * these parameters will be included as well.
   *
   * @param key Key of the service's common query params.
   * @param values List of String values to include in the service's common query params list.
   * @return The FluentService instance invoking this method.
   */
  public final FluentService commonQueryParams(final String key, final List<String> values) {
    Assert.hasText(key, "key must not be null or empty");
    Assert.notNull(values, "values must not be null");
    commonQueryParams.addAll(key, values);
    return this;
  }

  /**
   * Registers a map of common query params to the service. If service's common query param
   * <i>values</i> previously exist under some of the same <i>keys</i> provided in the map, these
   * new values will be included in the lists of those keys. <br>
   * <br>
   * Registered common query parameters are <i>common</i> to all of the {@linkplain URI}s created
   * with this FluentService independently of the endpoint used, if any, and will be used in the
   * construction process. If additional query params are provided during the uri builder phase,
   * these parameters will be included as well.
   *
   * @param valueMap of String key-values to include in the service's common query params list.
   * @return The FluentService instance invoking this method.
   */
  public final FluentService commonQueryParams(final MultiValueMap<String, String> valueMap) {
    Assert.notNull(valueMap, "valueMap must not be null");
    commonQueryParams.addAll(valueMap);
    return this;
  }

  /**
   * Registers a common fragment to the service. <br>
   * <br>
   * A registered common fragment is <i>common</i> to all of the {@linkplain URI}s created with this
   * FluentService independently of the endpoint used, if any, and will be used in the construction
   * process. If a specific fragment is provided during the uri builder phase, the <i>common</i>
   * fragment is overriden by the specific fragment. If fragment is set to {@code null} during any
   * of the building phases, no fragment will be used in the resulting URI.
   *
   * @param commonFragment URI fragment to include in any URI created by this FluentService, if not
   *     null.
   * @return The FluentService instance invoking this method.
   */
  public final FluentService commonFragment(@Nullable final String commonFragment) {
    this.commonFragment = commonFragment;
    return this;
  }

  /**
   * After a {@linkplain FluentService} has been initialised by any of the provided mechanisms, use
   * this method to create a {@linkplain ServiceUriBuilder} backed by the FluentService and start
   * the <b>URI Builder phase</b>.<br>
   * <br>
   * This method will <b>NOT</b> provide an endpoint to the ServiceUriBuilder.
   *
   * @return A {@linkplain ServiceUriBuilder} backed by the current FluentService.
   */
  public final ServiceUriBuilder uriBuilder() {
    return uriBuilder(null);
  }

  /**
   * After a {@linkplain FluentService} has been initialised by any of the provided mechanisms, use
   * this method to create a {@linkplain ServiceUriBuilder} backed by the FluentService and start
   * the <b>URI Builder phase</b>.<br>
   * <br>
   * This method provides and endpoint value, representeed by the key, to the ServiceUriBuilder
   * which will use it as part of the URI path.
   *
   * @param endpointKey The key representing an endpoint value in the endpoints map.
   * @return A {@linkplain ServiceUriBuilder} backed by the current FluentService.
   */
  public final ServiceUriBuilder uriBuilder(@Nullable final String endpointKey) {
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
   * Declares useful methods to provide {@linkplain URI} parts to be used during URI construction
   * and the {@linkplain ServiceUriBuilder#build() build()} method itself. This class represents the
   * <b>URI builder phase</b> of {@linkplain FluentService}.
   */
  public interface ServiceUriBuilder extends FluentUriBuilder<ServiceUriBuilder> {

    /**
     * Once the desired {@linkplain URI} parts have been provided, this method uses those parts to
     * {@linkplain UriComponentsBuilder#buildAndExpand(Map) build and expand} the uri components
     * which eventually conforms to a URI.<br>
     *
     * <p>The execution of this method ends the URI builder phase of {@linkplain FluentService
     * FluentService}.
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
      uriComponentsBuilder.queryParam(key, values);
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

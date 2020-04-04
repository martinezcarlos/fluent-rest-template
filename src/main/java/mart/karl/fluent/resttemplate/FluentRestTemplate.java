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

package mart.karl.fluent.resttemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import mart.karl.fluent.service.FluentService;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * A handy wrapper to construct and invoke remote REST services with a single functional expression.
 * <br>
 * <br>
 *
 * <p>In the background, FluentRestTemplate evolves in different phases of a workflow. These phases
 * are
 *
 * <ol>
 *   <li><b>Starter:</b> indicates which {@code HTTP} verb will be used for the REST invokation.
 *   <li><b>Builder:</b> indicates which means will be used to build FluentRestTemplate's
 *       {@linkplain URI} parts in its basic canonical form. This phase uses a handy {@linkplain
 *       FluentService} that can be provided or automatically built.
 *       <ol>
 *         <li><b>Service builder:</b> if FluentService is used as source to build the
 *             FluentRestTemplate, an intermediate phase is presented. This phase will request the
 *             user to specify a service endpoint to use, if desired. See {@linkplain
 *             UriServiceBuilder}.
 *       </ol>
 *   <li><b>Uri builder:</b> indicates additional URI parts to be used in the Request like query
 *       params, uri variables and fragment.
 *   <li><b>Executor builder:</b> applied to the Request itself, executor phase provides request
 *       related data like headers, media types and charsets.
 *   <li><b>REST Service invokation:</b> this final phase used the URI and Request entity built
 *       during the workflow and executes a REST call.
 * </ol>
 *
 * <p>FluentRestTemplate is backed by {@linkplain RestTemplate}, for REST invokation, and in all the
 * cases by {@linkplain FluentService} for URI construction, which in turn is backed by {@linkplain
 * RequestEntity}, {@linkplain ResponseEntity} and {@linkplain UriComponentsBuilder}. <br>
 * <br>
 *
 * <p>FluentRestTemplate follows the Fluent Builder pattern to guide the user through convenient
 * methods according to the followed build path.<br>
 * <br>
 *
 * <h2>FluentRestTemplate initialisation</h2>
 *
 * <p>There are several ways on how you can initialise a FluentRestTemplate. This is a simple, yet
 * recommended one:
 *
 * <pre class="code">
 * {@code @Configuration}
 * public class BeanConfig {
 *
 *   // Build a basic RestTemplate bean.
 *   {@code @Bean}
 *   public RestTemplate initRestTemplate(final RestTemplateBuilder builder) {
 *     return builder.build();
 *   }
 *
 *   // Use RestTemplate to build FluentRestTemplate.
 *   {@code @Bean}
 *   public FluentRestTemplate initFluentRestTemplate(final RestTemplate restTemplate) {
 *     return new FluentRestTemplate(restTemplate);
 *   }
 * }
 * </pre>
 *
 * <h2>FluentRestTemplate usage</h2>
 *
 * <p>Using FluentRestTemplate is easy. These are basic examples.<br>
 * <br>
 *
 * <p>Example 1: invokes a GET method in a service at http://dummy.uri/foo/bar.
 *
 * <pre class="code">
 * String uriString = "http://dummy.uri/foo/{foo}";
 * ResponseEntity&lt;String;&gt; response =
 *   fluentRestTemplate
 *   .get()                     // starter phase
 *   .from(uriString)           // builder phase
 *   .uriVariable("foo", "bar") // uri builder phase
 *   .executor()                // executor phase
 *   .execute(String.class);    // REST service invokation
 * </pre>
 *
 * <p>Example 2: given a FluentService was created using the following YAML file:
 *
 * <pre class="code">
 * services:
 *   my-cool-service:
 *     scheme: https
 *     host: cool-service.com
 *     endpoints:
 *       updateCoolStuff: update/stuff/{stuffId}
 *       postReminder: reminder/set
 * </pre>
 *
 * <p>invokes a PUT method in "https://cool-service.com/update/stuff/123" to update cool stuff,
 * providing a request body, accepting a JSON media type, with a locale de_DE and expects an updated
 * CoolSuff object as a result:
 *
 * <pre class="code">
 * FluentService myCoolService = ...
 * CoolStuff coolStuff = ...
 * CoolStuff updatedCoolStuff =
 *   fluentRestTemplate
 *   .put(coolStuff)                     // starter phase
 *   .into(myCoolService)                // builder phase
 *   .withEndpoint("updateCoolStuff")    // service builder phase
 *   .uriVariable("stuffId", "123")      // uri builder phase
 *   .executor()                         // executor phase
 *   .accept(MediaType.APPLICATION_JSON)
 *   .header("locale", "de_DE")
 *   .executeForObject(CoolStuff.class); // REST service invokation
 * </pre>
 *
 * @author Carlos Martinez - Karl Mart
 */
@RequiredArgsConstructor
public final class FluentRestTemplate {

  @NonNull private final RestTemplate restTemplate;

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#GET}.
   *
   * @return A UriStarter used transition to the builder phase.
   */
  public UriStarter get() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.GET, null);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#DELETE}.
   *
   * @return A UriStarter used transition to the builder phase.
   */
  public UriStarter delete() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.DELETE, null);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#POST}.<br>
   * This method also indicates that the REST call will lack of a request body.
   *
   * @return A UriStarter used transition to the builder phase.
   */
  public UriBodyStarter post() {
    return post(null);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#POST}. <br>
   * This method also indicates that the REST call will be provided with a request body.
   *
   * @param body The request body to provide to the REST call.
   * @param <T> Request body's class type.
   * @return A UriStarter used transition to the builder phase.
   */
  public <T> UriBodyStarter post(@Nullable final T body) {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.POST, body);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#PUT}.<br>
   * This method also indicates that the REST call will lack of a request body.
   *
   * @return A UriStarter used transition to the builder phase.
   */
  public UriBodyStarter put() {
    return put(null);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#PUT}. <br>
   * This method also indicates that the REST call will be provided with a request body.
   *
   * @param body The request body to provide to the REST call.
   * @param <T> Request body's class type.
   * @return A UriStarter used transition to the builder phase.
   */
  public <T> UriBodyStarter put(@Nullable final T body) {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.PUT, body);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#PATCH}.<br>
   * This method also indicates that the REST call will lack of a request body. <br>
   * <br>
   *
   * <p><b>NOTE:</b> Due to a Spring's RestTemplate known limitation, PATCH verb cannot be used when
   * RestTemplate was built by using a {@linkplain SimpleClientHttpRequestFactory}, which is the
   * default factory when using the default constructor or RestTemplateBuilder with default
   * parameters.<br>
   * See https://github.com/spring-projects/spring-framework/issues/19618#issuecomment-453449390.
   * <br>
   * <br>
   *
   * <p>To allow FluentRestTemplate to use PATCH verb, consider building the provided RestTemplate
   * with a RestTemplateBuilder, providing one of the documented factories:
   *
   * <pre class="code">
   * {@code @Bean}
   * public RestTemplate initRestTemplate(final RestTemplateBuilder builder) {
   *   return builder
   *       //.requestFactory(SimpleClientHttpRequestFactory.class) // Default - NO
   *       .requestFactory(HttpComponentsClientHttpRequestFactory.class) // YES
   *       //.requestFactory(Netty4ClientHttpRequestFactory.class) // YES
   *       //.requestFactory(OkHttp3ClientHttpRequestFactory.class) // YES
   *       .build();
   * }
   * </pre>
   *
   * <p><b>NOTE:</b> keep in mind that in order to use a ClientHttpRequestFactory factory other than
   * SimpleClientHttpRequestFactory you might need to explicitly declare the dependency in your
   * preferrd dpendency management system, if any.
   *
   * @return A UriStarter used transition to the builder phase.
   */
  public UriBodyStarter patch() {
    return patch(null);
  }

  /**
   * Starts a FluentRestTemplate flow indicating that the REST verb to use in the invokation is
   * {@linkplain HttpMethod#PATCH}.<br>
   * This method also indicates that the REST call will be provided with a request body.<br>
   * <br>
   *
   * <p><b>NOTE:</b> Due to a Spring's RestTemplate known limitation, PATCH verb cannot be used when
   * RestTemplate was built by using a {@linkplain SimpleClientHttpRequestFactory}, which is the
   * default factory when using the default constructor or RestTemplateBuilder with default
   * parameters.<br>
   * See https://github.com/spring-projects/spring-framework/issues/19618#issuecomment-453449390.
   * <br>
   * <br>
   *
   * <p>To allow FluentRestTemplate to use PATCH verb, consider building the provided RestTemplate
   * with a RestTemplateBuilder, providing one of the documented factories:
   *
   * <pre class="code">
   * {@code @Bean}
   * public RestTemplate initRestTemplate(final RestTemplateBuilder builder) {
   *   return builder
   *       //.requestFactory(SimpleClientHttpRequestFactory.class) // Default - NO
   *       .requestFactory(HttpComponentsClientHttpRequestFactory.class) // YES
   *       //.requestFactory(Netty4ClientHttpRequestFactory.class) // YES
   *       //.requestFactory(OkHttp3ClientHttpRequestFactory.class) // YES
   *       .build();
   * }
   * </pre>
   *
   * <p><b>NOTE:</b> keep in mind that in order to use a ClientHttpRequestFactory factory other than
   * SimpleClientHttpRequestFactory you might need to explicitly declare the dependency in your
   * preferrd dpendency management system, if any.
   *
   * @param body The request body to provide to the REST call.
   * @param <T> Request body's class type.
   * @return A UriStarter used transition to the builder phase.
   */
  public <T> UriBodyStarter patch(@Nullable final T body) {
    if (restTemplate.getRequestFactory() instanceof SimpleClientHttpRequestFactory) {
      // https://github.com/spring-projects/spring-framework/issues/19618
      throw new UnsupportedOperationException(
          "PATCH method not supported in RestTemplate created using SimpleClientHttpRequestFactory");
    }
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.PATCH, body);
  }

  @RequiredArgsConstructor
  private static class FluentRestTemplateManager<T> implements UriStarter, UriBodyStarter {

    private final RestTemplate restTemplate;
    private final HttpMethod httpMethod;
    private final T body;

    @Override
    public ExecutorUriBuilder from(@NonNull final String uriString) {
      Assert.hasText(uriString, "uriString must not be null or empty");
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uriString).uriBuilder());
    }

    @Override
    public ExecutorUriBuilder from(@NonNull final URI uri) {
      Assert.notNull(uri, "uri must not be null");
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uri).uriBuilder());
    }

    @Override
    public UriServiceBuilder from(@NonNull final FluentService service) {
      Assert.notNull(service, "service must not be null");
      return new DefaultUriServiceBuilder<>(restTemplate, httpMethod, body, service);
    }

    @Override
    public ExecutorUriBuilder into(@NonNull final String uriString) {
      Assert.hasText(uriString, "uriString must not be null or empty");
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uriString).uriBuilder());
    }

    @Override
    public ExecutorUriBuilder into(@NonNull final URI uri) {
      Assert.notNull(uri, "uri must not be null");
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uri).uriBuilder());
    }

    @Override
    public UriServiceBuilder into(@NonNull final FluentService service) {
      Assert.notNull(service, "service must not be null");
      return new DefaultUriServiceBuilder<>(restTemplate, httpMethod, body, service);
    }

    @RequiredArgsConstructor
    private static class DefaultUriServiceBuilder<T> implements UriServiceBuilder {
      private final RestTemplate restTemplate;
      private final HttpMethod httpMethod;
      private final T body;
      private final FluentService fluentService;

      @Override
      public ExecutorUriBuilder withEndpoint(final String key) {
        return new DefaultExecutorUriBuilder<>(
            restTemplate, httpMethod, body, fluentService.uriBuilder(key));
      }

      @Override
      public ExecutorUriBuilder withoutEndpoint() {
        return withEndpoint(null);
      }
    }

    @RequiredArgsConstructor
    private static class DefaultExecutorUriBuilder<T> implements ExecutorUriBuilder {
      private final Map<String, Object> uriVariables = new HashMap<>();
      private final RestTemplate restTemplate;
      private final HttpMethod httpMethod;
      private final T body;
      private final FluentService.ServiceUriBuilder serviceUriBuilder;

      @Override
      public ExecutorUriBuilder queryParam(final String key, final Object... values) {
        serviceUriBuilder.queryParam(key, values);
        return this;
      }

      @Override
      public ExecutorUriBuilder queryParam(final String key, final Collection<?> values) {
        serviceUriBuilder.queryParam(key, values);
        return this;
      }

      @Override
      public ExecutorUriBuilder queryParams(final MultiValueMap<String, String> params) {
        serviceUriBuilder.queryParams(params);
        return this;
      }

      @Override
      public ExecutorUriBuilder fragment(final String fragment) {
        serviceUriBuilder.fragment(fragment);
        return this;
      }

      @Override
      public ExecutorUriBuilder uriVariable(final String key, final Object value) {
        serviceUriBuilder.uriVariable(key, value);
        return this;
      }

      @Override
      public ExecutorUriBuilder uriVariables(final Map<String, ?> variables) {
        serviceUriBuilder.uriVariables(variables);
        return this;
      }

      @Override
      public Executor executor() {
        final URI uri = serviceUriBuilder.build();
        return new DefaultExecutor<>(restTemplate, body, RequestEntity.method(httpMethod, uri));
      }
    }

    @RequiredArgsConstructor
    private static class DefaultExecutor<T> implements Executor {
      private final RestTemplate restTemplate;
      private final T body;
      private final RequestEntity.BodyBuilder requestEntityBuilder;

      @Override
      public Executor header(final String name, final String... values) {
        requestEntityBuilder.header(name, values);
        return this;
      }

      @Override
      public Executor headers(final HttpHeaders headers) {
        requestEntityBuilder.headers(headers);
        return this;
      }

      @Override
      public Executor headers(final Consumer<HttpHeaders> consumer) {
        requestEntityBuilder.headers(consumer);
        return this;
      }

      @Override
      public Executor accept(final MediaType... types) {
        requestEntityBuilder.accept(types);
        return this;
      }

      @Override
      public Executor acceptCharset(final Charset... charsets) {
        requestEntityBuilder.acceptCharset(charsets);
        return this;
      }

      @Override
      public ResponseEntity<Void> execute() {
        return processExecution(new ParameterizedTypeReference<Void>() {});
      }

      @Override
      public <O> ResponseEntity<O> execute(final Class<O> responseClass) {
        return restTemplate.exchange(requestEntityBuilder.body(body), responseClass);
      }

      @Override
      public <O> ResponseEntity<O> execute(final ParameterizedTypeReference<O> typeReference) {
        return processExecution(typeReference);
      }

      @Override
      public void executeForObject() {
        execute();
      }

      private <O> ResponseEntity<O> processExecution(
          final ParameterizedTypeReference<O> typeReference) {
        return restTemplate.exchange(requestEntityBuilder.body(body), typeReference);
      }

      @Override
      public <O> O executeForObject(final Class<O> responseClass) {
        return Optional.ofNullable(execute(responseClass)).map(HttpEntity::getBody).orElse(null);
      }

      @Override
      public <O> O executeForObject(final ParameterizedTypeReference<O> typeReference) {
        return Optional.ofNullable(execute(typeReference)).map(HttpEntity::getBody).orElse(null);
      }
    }
  }
}

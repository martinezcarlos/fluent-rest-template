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

package mart.karl.fluentresttemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import lombok.RequiredArgsConstructor;
import mart.karl.fluentresttemplate.executor.Executor;
import mart.karl.fluentresttemplate.executor.ExecutorUriBuilder;
import mart.karl.fluentresttemplate.service.FluentService;
import mart.karl.fluentresttemplate.uri.UriBodyStarter;
import mart.karl.fluentresttemplate.uri.UriStarter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RequiredArgsConstructor
public final class FluentRestTemplate {

  @NonNull private final RestTemplate restTemplate;

  public UriStarter get() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.GET, null);
  }

  public UriStarter delete() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.DELETE, null);
  }

  public UriBodyStarter post() {
    return post(null);
  }

  public <T> UriBodyStarter post(final T body) {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.POST, body);
  }

  public UriBodyStarter put() {
    return put(null);
  }

  public <T> UriBodyStarter put(final T body) {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.PUT, body);
  }

  public UriBodyStarter patch() {
    return patch(null);
  }

  public <T> UriBodyStarter patch(final T body) {
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
    public ExecutorUriBuilder from(final String uriString) {
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uriString).uriBuilder());
    }

    @Override
    public ExecutorUriBuilder from(final URI uri) {
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uri).uriBuilder());
    }

    @Override
    public ExecutorUriBuilder from(final FluentService service) {
      return from(service, null);
    }

    @Override
    public ExecutorUriBuilder from(
        @NonNull final FluentService service, final String serviceEndpointName) {
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, service.uriBuilder(serviceEndpointName));
    }

    @Override
    public ExecutorUriBuilder into(final String uriString) {
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uriString).uriBuilder());
    }

    @Override
    public ExecutorUriBuilder into(final URI uri) {
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, FluentService.from(uri).uriBuilder());
    }

    @Override
    public ExecutorUriBuilder into(final FluentService service) {
      return into(service, null);
    }

    @Override
    public ExecutorUriBuilder into(
        @NonNull final FluentService service, final String serviceEndpointName) {
      return new DefaultExecutorUriBuilder<>(
          restTemplate, httpMethod, body, service.uriBuilder(serviceEndpointName));
    }

    @RequiredArgsConstructor
    private static class DefaultExecutorUriBuilder<T> implements ExecutorUriBuilder {

      private final Map<String, Object> uriVariables = new HashMap<>();
      @NonNull private final RestTemplate restTemplate;
      @NonNull private final HttpMethod httpMethod;
      private final T body;
      @NonNull private final FluentService.ServiceUriBuilder serviceUriBuilder;

      @Override
      public ExecutorUriBuilder queryParam(final String name, final Object... values) {
        serviceUriBuilder.queryParam(name, values);
        return this;
      }

      @Override
      public ExecutorUriBuilder queryParam(final String name, final Collection<?> values) {
        serviceUriBuilder.queryParam(name, values);
        return this;
      }

      @Override
      public ExecutorUriBuilder queryParams(final MultiValueMap<String, String> params) {
        serviceUriBuilder.queryParams(params);
        return this;
      }

      @Override
      public ExecutorUriBuilder uriVariable(final String name, final Object value) {
        serviceUriBuilder.uriVariable(name, value);
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

      @Override
      public <O> O executeForObject(final Class<O> responseClass) {
        return Optional.ofNullable(execute(responseClass)).map(HttpEntity::getBody).orElse(null);
      }

      @Override
      public <O> O executeForObject(final ParameterizedTypeReference<O> typeReference) {
        return Optional.ofNullable(execute(typeReference)).map(HttpEntity::getBody).orElse(null);
      }

      private <O> ResponseEntity<O> processExecution(
          final ParameterizedTypeReference<O> typeReference) {
        return restTemplate.exchange(requestEntityBuilder.body(body), typeReference);
      }
    }
  }
}

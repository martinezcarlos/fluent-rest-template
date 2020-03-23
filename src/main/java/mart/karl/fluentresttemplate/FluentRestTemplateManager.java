/*
 *
 *  * Copyright (c) 2020 Karl Mart
 *  * Carlos Martinez, ingcarlosmartinez@icloud.com
 *  *
 *  * Licensed under the Apache License, Version 2.0 (the "License");
 *  * you may not use this file except in compliance with the License.
 *  * You may obtain a copy of the License at
 *  *
 *  *    http://www.apache.org/licenses/LICENSE-2.0
 *  *
 *  * Unless required by applicable law or agreed to in writing, software
 *  * distributed under the License is distributed on an "AS IS" BASIS,
 *  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  * See the License for the specific language governing permissions and
 *  * limitations under the License.
 *
 */

package mart.karl.fluentresttemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import mart.karl.fluentresttemplate.executor.Executor;
import mart.karl.fluentresttemplate.uri.FluentUriBuilder;
import mart.karl.fluentresttemplate.uri.UriBodyStarter;
import mart.karl.fluentresttemplate.uri.UriStarter;
import mart.karl.fluentresttemplate.uri.service.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

final class FluentRestTemplateManager<T>
    implements UriStarter, UriBodyStarter, FluentUriBuilder, Executor {

  private final RestTemplate restTemplate;
  private final HttpMethod httpMethod;
  private final T body;

  private final Map<String, Object> uriVariables = new HashMap<>();
  private UriComponentsBuilder uriComponentsBuilder;
  private RequestEntity.BodyBuilder requestEntityBuilder;

  FluentRestTemplateManager(
      final RestTemplate restTemplate, final HttpMethod httpMethod, final T body) {
    this.restTemplate = restTemplate;
    this.httpMethod = httpMethod;
    this.body = body;
  }

  @Override
  public FluentUriBuilder from(final String uriString) {
    uriComponentsBuilder = UriComponentsBuilder.fromUriString(uriString);
    return this;
  }

  @Override
  public FluentUriBuilder from(final URI uri) {
    uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
    return this;
  }

  @Override
  public FluentUriBuilder from(final Service service) {
    return from(service, null);
  }

  @Override
  public FluentUriBuilder from(final Service service, final String serviceEndpointName) {
    Assert.notNull(service, "Service must not be null");
    uriComponentsBuilder = service.getUriComponentsBuilder(serviceEndpointName, null);
    return this;
  }

  @Override
  public FluentUriBuilder into(final String uriString) {
    uriComponentsBuilder = UriComponentsBuilder.fromUriString(uriString);
    return this;
  }

  @Override
  public FluentUriBuilder into(final URI uri) {
    uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
    return this;
  }

  @Override
  public FluentUriBuilder into(final Service service) {
    return into(service, null);
  }

  @Override
  public FluentUriBuilder into(final Service service, final String serviceEndpointName) {
    Assert.notNull(service, "Service must not be null");
    uriComponentsBuilder = service.getUriComponentsBuilder(serviceEndpointName, null);
    return this;
  }

  @Override
  public FluentUriBuilder queryParam(final String name, final Object... values) {
    uriComponentsBuilder.queryParam(name, values);
    return this;
  }

  @Override
  public FluentUriBuilder queryParam(final String name, final Collection<?> values) {
    Optional.ofNullable(values).ifPresent(v -> uriComponentsBuilder.queryParam(name, v.toArray()));
    // Activate when in Spring version 5.2.0.RELEASE or higher.
    // uriComponentsBuilder.queryParam(name, values);
    return this;
  }

  @Override
  public FluentUriBuilder queryParams(final MultiValueMap<String, String> params) {
    uriComponentsBuilder.queryParams(params);
    return this;
  }

  @Override
  public FluentUriBuilder uriVariable(final String name, final Object value) {
    uriVariables.put(name, value);
    return this;
  }

  @Override
  public FluentUriBuilder uriVariables(final Map<String, ?> variables) {
    Optional.ofNullable(variables).ifPresent(uriVariables::putAll);
    return this;
  }

  @Override
  public Executor executor() {
    final URI uri = uriComponentsBuilder.buildAndExpand(uriVariables).toUri();
    requestEntityBuilder = RequestEntity.method(httpMethod, uri);
    return this;
  }

  @Override
  public Executor header(final String name, final String... values) {
    requestEntityBuilder.header(name, values);
    return this;
  }

  @Override
  public Executor headers(final HttpHeaders headers) {
    Optional.ofNullable(headers)
        .ifPresent(
            hs -> hs.forEach((k, v) -> requestEntityBuilder.header(k, v.toArray(new String[] {}))));
    // Activate this block when in Spring version 5.2.0.RELEASE or higher.
    // requestEntityBuilder.headers(headers);
    return this;
  }

  //// Activate this block when in Spring version 5.2.0.RELEASE or higher.
  // @Override
  // public Executor headers(final Consumer<HttpHeaders> consumer) {
  //  requestEntityBuilder.headers(consumer);
  //  return this;
  // }

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
    Optional.ofNullable(execute()).orElse(null);
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

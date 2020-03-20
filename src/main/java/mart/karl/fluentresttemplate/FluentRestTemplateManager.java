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
import mart.karl.fluentresttemplate.executor.Executor;
import mart.karl.fluentresttemplate.uri.FluentUriBuilder;
import mart.karl.fluentresttemplate.uri.UriBodyStarter;
import mart.karl.fluentresttemplate.uri.UriStarter;
import mart.karl.fluentresttemplate.uri.service.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
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
  public FluentUriBuilder from(final Service service, final String serviceEndpointName) {
    //    this.service = service;
    //    this.serviceEndpointName = serviceEndpointName;
    if (service == null || StringUtils.isEmpty(serviceEndpointName)) {
      throw new IllegalArgumentException("service and/or serviceEndpointName are invalid");
    }
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
  public FluentUriBuilder into(final Service service, final String serviceEndpointName) {
    //    this.service = service;
    //    this.serviceEndpointName = serviceEndpointName;
    if (service == null || StringUtils.isEmpty(serviceEndpointName)) {
      throw new IllegalArgumentException("service and/or serviceEndpointName are invalid");
    }
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
    if (!CollectionUtils.isEmpty(values)) {
      uriComponentsBuilder.queryParam(name, values.toArray());
    }
    // Activate when in Spring Boot version 2.2.5.RELEASE or higher.
    // uriComponentsBuilder.queryParam(name, values);
    return this;
  }

  @Override
  public FluentUriBuilder queryParams(final Map<String, ?> params) {
    if (!CollectionUtils.isEmpty(params)) {
      params.forEach(uriComponentsBuilder::queryParam);
    }
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
    if (!CollectionUtils.isEmpty(variables)) {
      uriVariables.putAll(variables);
    }
    return this;
  }

  @Override
  public Executor executor() {
    requestEntityBuilder = RequestEntity.method(httpMethod, buildUri());
    return this;
  }

  private URI buildUri() {
    return uriComponentsBuilder.buildAndExpand(uriVariables).toUri();
  }

  @Override
  public Executor header(final String name, final String... values) {
    requestEntityBuilder.header(name, values);
    return this;
  }

  // Activate this block when in Spring Boot version 2.2.5.RELEASE or older
  //  @Override
  //  public Executor headers(final Consumer<HttpHeaders> consumer) {
  //    requestEntityBuilder.headers(consumer);
  //    return this;
  //  }

  @Override
  public Executor headers(final HttpHeaders headers) {
    if (headers != null) {
      headers.forEach((k, v) -> requestEntityBuilder.header(k, v.toArray(new String[] {})));
    }
    // Activate this block when in Spring Boot version 2.2.5.RELEASE or older
    // requestEntityBuilder.headers(headers);
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

  private <O> ResponseEntity<O> processExecution(
      final ParameterizedTypeReference<O> typeReference) {
    return restTemplate.exchange(requestEntityBuilder.body(body), typeReference);
  }
}

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

package mart.karl.fluentresttemplate.executor;

import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import mart.karl.fluentresttemplate.service.Service;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RequiredArgsConstructor
final class ExecutorManagerBuilder<T> implements UriStarter, UriBodyStarter, UriBuilder, Executor {

  private final RestTemplate restTemplate;
  private final HttpMethod httpMethod;
  private final T body;

  private Service service;
  private String serviceEndpointName;
  private UriComponentsBuilder uriComponentsBuilder;
  private Map<String, ?> uriVariables;
  private Map<String, ?> queryParams;
  private HttpHeaders headers;

  @Override
  public UriBuilder from(final String uriString) {
    uriComponentsBuilder = UriComponentsBuilder.fromUriString(uriString);
    return this;
  }

  @Override
  public UriBuilder from(final URI uri) {
    uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
    return this;
  }

  @Override
  public UriBuilder from(final Service service, final String serviceEndpointName) {
    this.service = service;
    this.serviceEndpointName = serviceEndpointName;
    return this;
  }

  @Override
  public UriBuilder into(final String uriString) {
    uriComponentsBuilder = UriComponentsBuilder.fromUriString(uriString);
    return this;
  }

  @Override
  public UriBuilder into(final URI uri) {
    uriComponentsBuilder = UriComponentsBuilder.fromUri(uri);
    return this;
  }

  @Override
  public UriBuilder into(final Service service, final String serviceEndpointName) {
    this.service = service;
    this.serviceEndpointName = serviceEndpointName;
    return this;
  }

  @Override
  public UriBuilder withUriVariables(final Map<String, Object> uriVariables) {
    this.uriVariables = uriVariables;
    return this;
  }

  @Override
  public UriBuilder withQueryParams(final Map<String, Object> queryParams) {
    this.queryParams = queryParams;
    return this;
  }

  @Override
  public UriBuilder withHeaders(final HttpHeaders headers) {
    this.headers = headers;
    return this;
  }

  @Override
  public Executor executor() {
    return this;
  }

  @Override
  public ResponseEntity<Void> execute() {
    return processExecution(new ParameterizedTypeReference<Void>() {});
  }

  @Override
  public <O> ResponseEntity<O> execute(final ParameterizedTypeReference<O> typeReference) {
    return processExecution(typeReference);
  }

  private <O> ResponseEntity<O> processExecution(
      final ParameterizedTypeReference<O> typeReference) {
    final HttpEntity<?> entity = new HttpEntity<>(body, headers);
    return restTemplate.exchange(buildUri(), httpMethod, entity, typeReference);
  }

  private URI buildUri() {
    return Optional.ofNullable(service)
        .map(this::buildUriFromService)
        .orElseGet(this::buildUriFromComponentsBuilder);
  }

  private URI buildUriFromService(final Service service) {
    return service.getUri(serviceEndpointName, uriVariables, queryParams);
  }

  private URI buildUriFromComponentsBuilder() {
    if (!CollectionUtils.isEmpty(queryParams)) {
      queryParams.forEach(
          (k, v) -> {
            if (v instanceof Collection) {
              uriComponentsBuilder.queryParam(k, ((Collection) v).toArray());
            } else {
              uriComponentsBuilder.queryParam(k, v);
            }
          });
    }
    return uriComponentsBuilder
        .buildAndExpand(Optional.ofNullable(uriVariables).orElse(Collections.emptyMap()))
        .toUri();
  }
}

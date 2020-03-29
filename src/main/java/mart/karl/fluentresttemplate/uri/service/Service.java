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

package mart.karl.fluentresttemplate.uri.service;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import lombok.EqualsAndHashCode;
import lombok.Setter;
import lombok.ToString;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Setter
@ToString
@EqualsAndHashCode
public abstract class Service {
  private String scheme;
  private String host;
  private String port;
  private String contextPath;
  private String version;
  private Map<String, String> endpoints;

  public final URI getUri() {
    return getUri(null);
  }

  public final URI getUri(final String endpointName) {
    return getUri(endpointName, null);
  }

  public final URI getUri(final String endpointName, final Map<String, ?> uriVariables) {
    return getUri(endpointName, uriVariables, null);
  }

  public final URI getUri(
      final String endpointName,
      final Map<String, ?> uriVariables,
      final Map<String, ?> queryParams) {
    return getUriComponents(endpointName, uriVariables, queryParams).toUri();
  }

  private UriComponents getUriComponents(
      final String endpointName,
      final Map<String, ?> uriVariables,
      final Map<String, ?> queryParams) {
    final UriComponentsBuilder uriComponentsBuilder =
        getUriComponentsBuilder(endpointName, queryParams);
    return CollectionUtils.isEmpty(uriVariables)
        ? uriComponentsBuilder.build()
        : uriComponentsBuilder.buildAndExpand(uriVariables);
  }

  // TODO: Think about this method's visibility
  public UriComponentsBuilder getUriComponentsBuilder(
      final String endpointName, final Map<String, ?> queryParams) {
    final UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)
            .port(StringUtils.isEmpty(port) ? null : port)
            .pathSegment(
                contextPath,
                version,
                Objects.isNull(endpoints) ? null : endpoints.get(endpointName));
    if (!CollectionUtils.isEmpty(queryParams)) {
      queryParams.forEach(
          (k, v) -> {
            // Remove this if block when in Spring version 5.2.0.RELEASE or higher as
            // uriComponentsBuilder.queryParam will receive a Collection.
            if (v instanceof Collection) {
              uriComponentsBuilder.queryParam(k, ((Collection<?>) v).toArray());
            } else {
              uriComponentsBuilder.queryParam(k, v);
            }
          });
    }
    return uriComponentsBuilder;
  }

  public final String getUriString() {
    return getUriString(null);
  }

  public final String getUriString(final String endpointName) {
    return getUriString(endpointName, null);
  }

  public final String getUriString(final String endpointName, final Map<String, ?> uriVariables) {
    return getUriString(endpointName, uriVariables, null);
  }

  public final String getUriString(
      final String endpointName,
      final Map<String, ?> uriVariables,
      final Map<String, ?> queryParams) {
    return getUriComponents(endpointName, uriVariables, queryParams).toUriString();
  }
}

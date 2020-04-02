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

package mart.karl.fluentresttemplate.service;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import mart.karl.fluentresttemplate.uri.FluentUriBuilder;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Setter
@Builder
@ToString
@EqualsAndHashCode
public class FluentService {
  private String scheme;
  private String host;
  private String port;
  private String contextPath;
  private String version;
  private Map<String, String> endpoints;

  public static FluentService from(final URI uri) {
    return from(UriComponentsBuilder.fromUri(uri).build());
  }

  private static FluentService from(final UriComponents components) {
    return FluentService.builder()
        .scheme(components.getScheme())
        .host(components.getHost())
        .port(components.getPort() == -1 ? null : String.valueOf(components.getPort()))
        .contextPath(components.getPath())
        .build();
  }

  public static FluentService from(final String uriString) {
    return from(UriComponentsBuilder.fromUriString(uriString).build());
  }

  public final ServiceUriBuilder uriBuilder() {
    return uriBuilder(null);
  }

  public final ServiceUriBuilder uriBuilder(final String endpointName) {
    final UriComponentsBuilder uriComponentsBuilder =
        UriComponentsBuilder.newInstance()
            .scheme(scheme)
            .host(host)
            .port(StringUtils.isEmpty(port) ? null : port)
            .pathSegment(
                contextPath,
                version,
                Objects.isNull(endpoints) ? null : endpoints.get(endpointName));
    return new DefaultUriBuilder(uriComponentsBuilder);
  }

  public interface ServiceUriBuilder extends FluentUriBuilder<ServiceUriBuilder> {
    URI build();
  }

  @RequiredArgsConstructor
  private static class DefaultUriBuilder implements ServiceUriBuilder {

    private final Map<String, Object> uriVariables = new HashMap<>();
    @NonNull private final UriComponentsBuilder uriComponentsBuilder;

    @Override
    public URI build() {
      return uriComponentsBuilder.buildAndExpand(uriVariables).toUri();
    }

    @Override
    public DefaultUriBuilder queryParam(final String name, final Object... values) {
      uriComponentsBuilder.queryParam(name, values);
      return this;
    }

    @Override
    public DefaultUriBuilder queryParam(final String name, final Collection<?> values) {
      uriComponentsBuilder.queryParam(name, values);
      return this;
    }

    @Override
    public DefaultUriBuilder queryParams(final MultiValueMap<String, String> params) {
      uriComponentsBuilder.queryParams(params);
      return this;
    }

    @Override
    public DefaultUriBuilder uriVariable(final String name, final Object value) {
      uriVariables.put(name, value);
      return this;
    }

    @Override
    public DefaultUriBuilder uriVariables(final Map<String, ?> variables) {
      Optional.ofNullable(variables).ifPresent(uriVariables::putAll);
      return this;
    }
  }
}

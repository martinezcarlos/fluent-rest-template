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

import lombok.RequiredArgsConstructor;
import mart.karl.fluentresttemplate.uri.UriBodyStarter;
import mart.karl.fluentresttemplate.uri.UriStarter;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
@RequiredArgsConstructor
public final class FluentRestTemplate {

  private final RestTemplate restTemplate;

  public UriStarter get() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.GET, null);
  }

  public UriStarter delete() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.DELETE, null);
  }

  public UriBodyStarter post() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.POST, null);
  }

  public <T> UriBodyStarter post(final T body) {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.POST, body);
  }

  public UriBodyStarter put() {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.PUT, null);
  }

  public <T> UriBodyStarter put(final T body) {
    return new FluentRestTemplateManager<>(restTemplate, HttpMethod.PUT, body);
  }

  public UriBodyStarter patch() {
    throw new UnsupportedOperationException("Patch operation not supported in this version");
    // https://github.com/spring-projects/spring-framework/issues/19618
  }

  public <T> UriBodyStarter patch(final T body) {
    throw new UnsupportedOperationException("Patch operation not supported in this version");
    // https://github.com/spring-projects/spring-framework/issues/19618
  }
}

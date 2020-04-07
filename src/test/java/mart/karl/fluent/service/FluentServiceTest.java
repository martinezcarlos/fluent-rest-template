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
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import static org.assertj.core.api.Assertions.assertThat;

class FluentServiceTest {

  private static final String SCHEME = "http";
  private static final String HOST = "localhost";
  private static final String PORT = "8080";
  private static final String CONTEXT_PATH = "context-path";
  private static final String V_1 = "v1";
  private static final String FOO = "foo";
  private static final String BAR = "bar";
  private static final String PATH = "/baz/bam";
  private static final String DUMMY_URI = "https://foo.bar:80" + PATH;
  private static final String VERSION = "v1";
  private static final String MY_KEY = "MyKey";
  private static final String BUY_BEER = "buyBeer";
  private static final String QUERY = "baz=boom&la=le";
  private static final String DUMMY_URI2 = "https://foo.bar:80/any-path?" + QUERY;
  private static final String FRAGMENT = "fragment";

  @Test
  void givenServiceHasEndpoints_whenUriIsBuilt_thenEndpointValueIsPresent() {
    // Given
    final MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
    final FluentService service =
        FluentService.builder()
            .scheme(SCHEME)
            .host(HOST)
            .port(PORT)
            .contextPath(CONTEXT_PATH)
            .version(V_1)
            .endpoints(Collections.singletonMap(FOO, BAR))
            .commonQueryParams(multiValueMap)
            .commonFragment(FRAGMENT)
            .build();
    final String path = String.format("/%s/%s/%s", CONTEXT_PATH, V_1, BAR);
    // When
    final URI uri = service.uriBuilder(FOO).build();
    // Then
    assertThat(uri)
        .hasScheme(SCHEME)
        .hasHost(HOST)
        .hasPort(Integer.parseInt(PORT))
        .hasPath(path)
        .hasNoQuery()
        .hasFragment(FRAGMENT);
  }

  @Test
  void givenAUri_whenServiceIsCreatedAndLaterProvidedVersion_thenItHasVersionInPath() {
    // Given
    final URI uri = URI.create(DUMMY_URI);
    // When
    final FluentService service =
        FluentService.from(uri)
            .endpoints(Collections.singletonMap(FOO, BAR))
            .version(VERSION)
            .build();
    // Then
    assertThat(service)
        .extracting(FluentService::uriBuilder)
        .extracting(FluentService.ServiceUriBuilder::build)
        .extracting(URI::getPath)
        .isEqualTo(PATH + "/" + VERSION);
  }

  @Test
  void givenAUri_whenServiceIsCreatedAndLaterProvidedEndpoints_thenItHasEndpointValueInPath() {
    // Given
    final URI uri = URI.create(DUMMY_URI);
    // When
    final FluentService service =
        FluentService.from(uri).endpoints(Collections.singletonMap(FOO, BAR)).build();
    // Then
    assertThat(service)
        .extracting(s -> s.uriBuilder(MY_KEY))
        .extracting(FluentService.ServiceUriBuilder::build)
        .extracting(URI::getPath)
        .isEqualTo(PATH);
  }

  @Test
  void givenAUri_whenServiceIsCreated_thenItHasQuery() {
    // Given
    final URI uri = URI.create(DUMMY_URI2);
    // When
    final FluentService service = FluentService.from(uri).build();
    // Then
    assertThat(service)
        .extracting(FluentService::uriBuilder)
        .extracting(FluentService.ServiceUriBuilder::build)
        .extracting(URI::getQuery)
        .isEqualTo(QUERY);
  }

  @Test
  void givenCommonParamsAreSet_whenServiceIsCreated_commonParamsAreOverridden() {
    // Given
    final HttpHeaders map1 = new HttpHeaders();
    map1.add("baz", "firstCommon");
    final HttpHeaders map2 = new HttpHeaders();
    map2.add("baz", "thisWillRemain");
    final FluentService service =
        FluentService.from(DUMMY_URI).commonQueryParams(map1).commonFragment("oldFragment").build();
    // When
    final URI uri =
        service
            .uriBuilder()
            .queryParam("baz", "lost1")
            .queryParam("baz", Collections.singletonList("lost2"))
            .queryParams(map2)
            .fragment("newFragment")
            .build();
    // Then
    assertThat(uri).hasQuery("baz=thisWillRemain").hasFragment("newFragment");
  }
}

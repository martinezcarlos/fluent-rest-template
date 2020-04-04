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

  @Test
  void givenServiceHasEndpoints_whenUriIsBuilt_thenEndpointValueIsPresent() {
    // Given
    final FluentService service =
        FluentService.builder()
            .scheme(SCHEME)
            .host(HOST)
            .port(PORT)
            .contextPath(CONTEXT_PATH)
            .version(V_1)
            .endpoints(Collections.singletonMap(FOO, BAR))
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
        .hasNoFragment();
  }

  @Test
  void givenAUri_whenServiceIsCreatedAndLaterProvidedVersion_thenItHasVersionInPath() {
    // Given
    final URI uri = URI.create(DUMMY_URI);
    // When
    final FluentService service =
        FluentService.from(uri)
            .commonQueryParams("lala", "lele", "lolo")
            .endpoint("foo", "bar")
            .endpoints(Collections.singletonMap(FOO, BAR))
            .version(VERSION);
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
        FluentService.from(uri)
            .endpoints(Collections.singletonMap(FOO, BAR))
            .endpoint(MY_KEY, BUY_BEER);
    // Then
    assertThat(service)
        .extracting(s -> s.uriBuilder(MY_KEY))
        .extracting(FluentService.ServiceUriBuilder::build)
        .extracting(URI::getPath)
        .isEqualTo(PATH + "/" + BUY_BEER);
  }

  @Test
  void givenAUri_whenServiceIsCreated_thenItHasQuery() {
    // Given
    final URI uri = URI.create(DUMMY_URI2);
    // When
    final FluentService service = FluentService.from(uri);
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
    map1.add("baz", "test3");
    final HttpHeaders map2 = new HttpHeaders();
    map2.add("baz", "test6");
    final FluentService service =
        FluentService.from(DUMMY_URI)
            .commonQueryParams("baz", "test1")
            .commonQueryParams("baz", Collections.singletonList("test2"))
            .commonQueryParams(map1)
            .commonFragment("oldFragment");
    // When
    final URI uri =
        service
            .uriBuilder()
            .queryParam("baz", "test4")
            .queryParam("baz", Collections.singletonList("test5"))
            .queryParams(map2)
            .fragment("newFragment")
            .build();
    // Then
    assertThat(uri)
        .hasQuery("baz=test1&baz=test2&baz=test3&baz=test4&baz=test5&baz=test6")
        .hasFragment("newFragment");
  }

  @Test
  void givenService_whenSettingNullEndpointsAndParams_thenTheyAreNotNull() {
    // Given
    final FluentService service = new FluentService();
    // When
    service.setEndpoints(null);
    service.setCommonQueryParams(null);
    final URI uri =
        service.endpoint("foo", "bar").commonQueryParams("foo", "bar").uriBuilder("foo").build();
    // Then
    assertThat(uri).hasPath("/bar").hasQuery("foo=bar");
  }
}

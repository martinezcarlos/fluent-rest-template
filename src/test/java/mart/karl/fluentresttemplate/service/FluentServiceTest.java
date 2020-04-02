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
import java.util.Collections;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class FluentServiceTest {

  private static final String HTTP = "http";
  private static final String LOCALHOST = "localhost";
  private static final String PORT = "8080";
  private static final String CONTEXT_PATH = "context-path";
  private static final String V_1 = "v1";
  private static final String FOO = "foo";
  private static final String BAR = "bar";

  @Test
  void givenServiceHasEndpoints_whenUriIsBuild_thenEndpointValueIsPresent() {
    // Given
    final FluentService service =
        FluentService.builder()
            .scheme(HTTP)
            .host(LOCALHOST)
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
        .hasScheme(HTTP)
        .hasHost(LOCALHOST)
        .hasPort(Integer.parseInt(PORT))
        .hasPath(path)
        .hasNoFragment();
  }
}

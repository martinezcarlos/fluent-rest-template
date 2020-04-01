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

package mart.karl.fluentresttemplate.uri.service;

import java.net.URI;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponents;

import static org.assertj.core.api.BDDAssertions.then;

class ServiceFactoryTest {

  private static final String DUMMY_URI = "http://dummy.uri";
  private static final String DUMMY_URI_WITH_FOO = "http://dummy.uri/foo/{foo}";
  private static final String DUMMY_URI_WITH_PORT = "http://dummy.uri:1234";
  private static final String FOO = "foo";
  private static final String BAR = "bar";
  private static final String BAZ = "baz";

  @Test
  void fromUri() {
    // Given
    final URI inputUri = URI.create(DUMMY_URI);
    // When
    final Service service = ServiceFactory.from(inputUri);
    // Then
    then(service).extracting(Service::getUri).isEqualTo(inputUri);
  }

  @Test
  void fromUriString() {
    // Given
    // When
    final Service service = ServiceFactory.from(DUMMY_URI);
    // Then
    then(service).extracting(Service::getUriString).isEqualTo(DUMMY_URI);
  }

  @Test
  void testEquals() {
    // Given
    final Service service1 = ServiceFactory.from(DUMMY_URI);
    final Service service2 = ServiceFactory.from(DUMMY_URI);
    // When
    // Then
    then(service1).isEqualTo(service2);
  }

  @Test
  void testToString() {
    // Given
    final Service service1 = ServiceFactory.from(DUMMY_URI);
    final Service service2 = ServiceFactory.from(DUMMY_URI);
    // When
    final String s1 = service1.toString();
    final String s2 = service2.toString();
    // Then
    then(s1).isEqualTo(s2);
  }

  @Test
  void canEqual() {
    // Given
    final Service service1 = ServiceFactory.from(DUMMY_URI);
    final Service service2 = ServiceFactory.from(DUMMY_URI);
    // When
    // Then
    then(service1.canEqual(service2)).isTrue();
  }

  @Test
  void testHashCode() {
    // Given
    final Service service1 = ServiceFactory.from(DUMMY_URI);
    final Service service2 = ServiceFactory.from(DUMMY_URI);
    // When
    final int hashCode1 = service1.hashCode();
    final int hashCode2 = service2.hashCode();
    // Then
    then(hashCode1).isEqualTo(hashCode2);
  }

  @Test
  void testUriComponentsBuilderSingleValue() {
    // Given
    final Condition<MultiValueMap> condition = new Condition<>();
    final Service service = ServiceFactory.from(DUMMY_URI);
    // When
    final UriComponents components =
        service.getUriComponentsBuilder(null, Collections.singletonMap(FOO, BAR)).build();
    final List<String> fooValues = components.getQueryParams().get(FOO);
    // Then
    then(fooValues).contains(BAR);
  }

  @Test
  void testUriComponentsBuilderMultiValue() {
    // Given
    final Service service = ServiceFactory.from(DUMMY_URI_WITH_PORT);
    // When
    final UriComponents components =
        service
            .getUriComponentsBuilder(null, Collections.singletonMap(FOO, Arrays.asList(BAR, BAZ)))
            .build();
    final List<String> fooValues = components.getQueryParams().get(FOO);
    // Then
    then(fooValues).contains(BAR, BAZ);
  }

  @Test
  void buildUriWithVariables() {
    // Given
    final Map<String, String> uriVariables = Collections.singletonMap(FOO, BAR);
    final Service service = ServiceFactory.from(DUMMY_URI_WITH_FOO);
    // When
    final String uriString = service.getUriString(null, uriVariables, null);
    // Then
    then(uriString).contains(BAR);
  }

  @Test
  void buildUriWithEndpoint() {
    // Given
    final Map<String, String> uriVariables = Collections.singletonMap(FOO, BAR);
    final Service service = ServiceFactory.from(DUMMY_URI);
    service.setEndpoints(Collections.singletonMap(FOO, BAR));
    // When
    final String uriString = service.getUriString(FOO, uriVariables, null);
    // Then
    then(uriString).contains(BAR);
  }
}

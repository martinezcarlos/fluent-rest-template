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

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import mart.karl.fluentresttemplate.uri.service.BaseService;
import mart.karl.fluentresttemplate.uri.service.Service;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class FluentRestTemplateTest {

  private static final String DUMMY_URI = "http://dummy.url";
  private static final String DUMMY_MESSAGE = "DummyMessage";
  private static final String TEST_STRING = "Test String";
  private static final String DUMMY_RESPONSE = "DummyResponse";
  private static final String FOO = "Foo";
  private static final String BAR = "Bar";
  private static final String BAZ = "Baz";
  private static final ParameterizedTypeReference<String> STRING_TYPE_REFERENCE =
      new ParameterizedTypeReference<String>() {};

  @Mock private RestTemplate restTemplate;
  @InjectMocks private FluentRestTemplate fluent;

  @Test
  void getVoid() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok().build());
    // When
    final ResponseEntity<Void> execute = fluent.get().from(DUMMY_URI).executor().execute();
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute)
        .extracting(ResponseEntity::getStatusCode, HttpEntity::getBody)
        .containsExactly(HttpStatus.OK, null);
  }

  @Test
  void getNonVoid() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    // When
    final ResponseEntity<String> execute =
        fluent
            .get()
            .from(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri())
            .executor()
            .execute(STRING_TYPE_REFERENCE);
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void invalidType() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willThrow(new RestClientException(DUMMY_MESSAGE));
    final Service service =
        BaseService.from(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri());
    // When
    // Then
    assertThrows(
        RestClientException.class,
        () ->
            fluent
                .get()
                .from(service, BaseService.BASE_PATH)
                .executor()
                .execute(new ParameterizedTypeReference<Integer>() {}));
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }

  @Test
  void postNoBody() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    final Map<String, Object> queryParams = Collections.singletonMap(FOO, BAR);
    final HttpHeaders headers = new HttpHeaders();
    headers.set(FOO, BAR);
    // When
    final ResponseEntity<String> execute =
        fluent
            .post()
            .into(DUMMY_URI)
            .queryParams(queryParams)
            .executor()
            .headers(headers)
            .execute(STRING_TYPE_REFERENCE);
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void postWithBody() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    final Map<String, Object> queryParams = Collections.singletonMap(FOO, BAR);
    final HttpHeaders headers = new HttpHeaders();
    headers.set(FOO, BAR);
    // When
    final ResponseEntity<String> execute =
        fluent
            .post(TEST_STRING)
            .into(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri())
            .queryParams(queryParams)
            .executor()
            .headers(headers)
            .execute(STRING_TYPE_REFERENCE);
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void delete() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok().build());
    // When
    final ResponseEntity<Void> execute = fluent.delete().from(DUMMY_URI).executor().execute();
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute)
        .extracting(ResponseEntity::getStatusCode, HttpEntity::getBody)
        .containsExactly(HttpStatus.OK, null);
  }

  @Disabled // TODO: enable this test
  @Test
  void putNoBody() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    final Service service = BaseService.from(DUMMY_URI);
    final Map<String, Object> queryParams = Collections.singletonMap(FOO, Arrays.asList(BAR, BAZ));
    // When
    final ResponseEntity<String> execute =
        fluent
            .put()
            .into(service, BaseService.BASE_PATH)
            .queryParams(queryParams)
            .executor()
            .execute(STRING_TYPE_REFERENCE);
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void putWithBody() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    // When
    final Map<String, Object> queryParams = Collections.singletonMap(FOO, Arrays.asList(BAR, BAZ));
    final ResponseEntity<String> execute =
        fluent
            .put(TEST_STRING)
            .into(DUMMY_URI)
            .queryParams(queryParams)
            .executor()
            .execute(STRING_TYPE_REFERENCE);
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void patchNoBody() {
    // Given
    final Service service = BaseService.from(DUMMY_URI);
    // When
    // Then
    assertThrows(
        UnsupportedOperationException.class,
        () -> fluent.patch().into(service, BaseService.BASE_PATH).executor().execute());
    then(restTemplate)
        .should(never())
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }

  @Test
  void patchWithBody() {
    // Given
    // When
    // Then
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            fluent
                .patch(TEST_STRING)
                .into(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri())
                .executor()
                .execute());
    then(restTemplate)
        .should(never())
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }
}

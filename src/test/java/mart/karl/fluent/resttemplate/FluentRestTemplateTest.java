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

package mart.karl.fluent.resttemplate;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.Collections;
import mart.karl.fluent.service.FluentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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

  private static final String DUMMY_URI = "http://dummy.uri:8080";
  private static final String DUMMY_URI_WITH_FRAGMENT = "http://dummy.uri:8080#fragment";
  private static final String DUMMY_URI_WITH_FOO = "http://dummy.uri/foo/{foo}";
  private static final String DUMMY_MESSAGE = "DummyMessage";
  private static final String TEST_STRING = "Test String";
  private static final String DUMMY_RESPONSE = "DummyResponse";
  private static final String FOO = "foo";
  private static final String BAR = "bar";
  private static final String BAZ = "baz";
  private static final ParameterizedTypeReference<String> TYPE_REFERENCE =
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
            .execute(TYPE_REFERENCE);
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
    final URI uri = UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri();
    final FluentService service = FluentService.from(uri).build();
    // When
    // Then
    assertThrows(
        RestClientException.class,
        () ->
            fluent
                .get()
                .from(service)
                .withoutEndpoint()
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
    final HttpHeaders headers = new HttpHeaders();
    headers.set(FOO, BAR);
    // When
    final ResponseEntity<String> execute =
        fluent
            .post()
            .into(DUMMY_URI)
            .executor()
            .contenType(MediaType.APPLICATION_JSON)
            .execute(TYPE_REFERENCE);
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
    // When
    final ResponseEntity<String> execute =
        fluent
            .post(TEST_STRING)
            .into(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri())
            .queryParam(FOO, BAR)
            .executor()
            .header(FOO, BAR)
            .execute(TYPE_REFERENCE);
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
    final ResponseEntity<Void> execute =
        fluent.delete().from(DUMMY_URI_WITH_FOO).uriVariable(FOO, BAR).executor().execute();
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute)
        .extracting(ResponseEntity::getStatusCode, HttpEntity::getBody)
        .containsExactly(HttpStatus.OK, null);
  }

  @Test
  void putNoBody() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    final FluentService service = FluentService.from(DUMMY_URI_WITH_FOO).build();
    final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<>();
    queryParams.put(FOO, Arrays.asList(BAR, BAZ));
    // When
    final ResponseEntity<String> execute =
        fluent
            .put()
            .into(service)
            .withoutEndpoint()
            .uriVariables(Collections.singletonMap(FOO, BAR))
            .queryParams(queryParams)
            .executor()
            .execute(TYPE_REFERENCE);
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
    given(restTemplate.exchange(any(RequestEntity.class), any(Class.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    // When
    final ResponseEntity<String> execute =
        fluent
            .put(TEST_STRING)
            .into(DUMMY_URI)
            .queryParam(FOO, Arrays.asList(BAR, BAZ))
            .executor()
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.defaultCharset())
            .execute(String.class);
    // Then
    then(restTemplate).should().exchange(any(RequestEntity.class), any(Class.class));
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void patchNoBodySimpleClientHttpRequestFactory() {
    // Given
    final SimpleClientHttpRequestFactory factory =
        Mockito.mock(SimpleClientHttpRequestFactory.class);
    given(restTemplate.getRequestFactory()).willReturn(factory);
    final FluentService service = FluentService.from(DUMMY_URI).build();
    // When
    assertThrows(
        UnsupportedOperationException.class,
        () -> fluent.patch().into(service).withoutEndpoint().executor().execute());
    // Then
    then(restTemplate).should().getRequestFactory();
    then(restTemplate)
        .should(never())
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }

  @Test
  void patchNoBodyAnyClientHttpRequestFactory() {
    // Given
    final ClientHttpRequestFactory factory = Mockito.mock(ClientHttpRequestFactory.class);
    given(restTemplate.getRequestFactory()).willReturn(factory);
    final FluentService service = FluentService.from(DUMMY_URI).build();
    // When
    final ResponseEntity<String> execute =
        fluent
            .patch()
            .into(service)
            .withoutEndpoint()
            .queryParam(FOO, Arrays.asList(BAR, BAZ))
            .executor()
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.defaultCharset())
            .execute(String.class);
    // Then
    then(restTemplate).should().getRequestFactory();
    then(restTemplate).should().exchange(any(RequestEntity.class), any(Class.class));
  }

  @Test
  void patchWithBodySimpleClientHttpRequestFactory() {
    // Given
    final SimpleClientHttpRequestFactory factory =
        Mockito.mock(SimpleClientHttpRequestFactory.class);
    given(restTemplate.getRequestFactory()).willReturn(factory);
    // When
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            fluent
                .patch(TEST_STRING)
                .into(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri())
                .executor()
                .execute());
    // Then
    then(restTemplate).should().getRequestFactory();
    then(restTemplate)
        .should(never())
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }

  @Test
  void putWithBodyForObject() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(Class.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    // When
    final String execute =
        fluent
            .put(TEST_STRING)
            .into(DUMMY_URI)
            .queryParam(FOO, Arrays.asList(BAR, BAZ))
            .executor()
            .accept(MediaType.APPLICATION_JSON)
            .acceptCharset(Charset.defaultCharset())
            .executeForObject(String.class);
    // Then
    then(restTemplate).should().exchange(any(RequestEntity.class), any(Class.class));
    assertThat(execute).isNotNull().isNotEmpty();
  }

  @Test
  void getForObject() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok(DUMMY_RESPONSE));
    // When
    final String execute =
        fluent
            .get()
            .from(UriComponentsBuilder.fromUriString(DUMMY_URI).build().toUri())
            .executor()
            .executeForObject(TYPE_REFERENCE);
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
    assertThat(execute).isNotNull().isNotEmpty();
  }

  @Test
  void getVoidForObject() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok().build());
    // When
    fluent.get().from(DUMMY_URI).executor().executeForObject();
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }

  @Test
  void givenAFragmentIsGiven_whenExecution_thenExecutionSuccess() {
    // Given
    given(restTemplate.exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class)))
        .willReturn(ResponseEntity.ok().build());
    // When
    final ResponseEntity<Void> out =
        fluent.get().from(DUMMY_URI_WITH_FRAGMENT).fragment("newFragment").executor().execute();
    // Then
    then(restTemplate)
        .should()
        .exchange(any(RequestEntity.class), any(ParameterizedTypeReference.class));
  }
}

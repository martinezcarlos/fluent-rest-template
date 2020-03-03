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

import mart.karl.fluentresttemplate.uri.service.BaseService;
import mart.karl.fluentresttemplate.uri.service.Service;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

// TODO: improve existing tests and add new ones
@SpringBootTest
class FluentRestTemplateTest {

  private static final String POSTMAN_ECHO_GET = "https://postman-echo.com/get?foo1=bar1&foo2=bar2";
  private static final String POSTMAN_ECHO_POST = "https://postman-echo.com/post";
  private static final String POSTMAN_ECHO_PUT = "https://postman-echo.com/put";
  private static final String POSTMAN_ECHO_PATCH = "https://postman-echo.com/patch";
  private static final String POSTMAN_ECHO_DELETE = "https://postman-echo.com/delete";
  private static final ParameterizedTypeReference<String> STRING_TYPE_REFERENCE =
      new ParameterizedTypeReference<String>() {
      };

  @Autowired
  private FluentRestTemplate manager;

  @BeforeEach
  void setUp() {
  }

  @Test
  void getVoid() {
    final ResponseEntity<Void> execute = manager.get().from(POSTMAN_ECHO_GET).executor().execute();
    assertThat(execute)
        .extracting(ResponseEntity::getStatusCode, HttpEntity::getBody)
        .containsExactly(HttpStatus.OK, null);
  }

  @Test
  void getNonVoid() {
    final ResponseEntity<String> execute =
        manager
            .get()
            .from(UriComponentsBuilder.fromUriString(POSTMAN_ECHO_GET).build().toUri())
            .executor()
            .execute(STRING_TYPE_REFERENCE);
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void invalidType() {
    final Service service =
        BaseService.from(UriComponentsBuilder.fromUriString(POSTMAN_ECHO_GET).build().toUri());
    assertThrows(
        RestClientException.class,
        () ->
            manager
                .get()
                .from(service, BaseService.UNIQUE_ENDPOINT_NAME)
                .executor()
                .execute(new ParameterizedTypeReference<Integer>() {}));
  }

  @Test
  void postNoBody() {
    final Map<String, Object> queryParams = Collections.singletonMap("Foo", "bar");
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Foo", "bar");
    final ResponseEntity<String> execute =
        manager
            .post()
            .into(POSTMAN_ECHO_POST)
            .queryParams(queryParams)
            .executor()
            .headers(headers)
            .execute(STRING_TYPE_REFERENCE);
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void postWithBody() {
    final Map<String, Object> queryParams = Collections.singletonMap("Foo", "bar");
    final HttpHeaders headers = new HttpHeaders();
    headers.set("Foo", "bar");
    final ResponseEntity<String> execute =
        manager
            .post("Test String")
            .into(UriComponentsBuilder.fromUriString(POSTMAN_ECHO_POST).build().toUri())
            .queryParams(queryParams)
            .executor()
            .headers(headers)
            .execute(STRING_TYPE_REFERENCE);
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void delete() {
    final ResponseEntity<Void> execute =
        manager.delete().from(POSTMAN_ECHO_DELETE).executor().execute();
    assertThat(execute)
        .extracting(ResponseEntity::getStatusCode, HttpEntity::getBody)
        .containsExactly(HttpStatus.OK, null);
  }

  @Test
  void putNoBody() {
    final Service service = BaseService.from(POSTMAN_ECHO_PUT);
    final Map<String, Object> queryParams =
        Collections.singletonMap("Foo", Arrays.asList("bar", "baz"));
    final ResponseEntity<String> execute =
        manager
            .put()
            .into(service, BaseService.UNIQUE_ENDPOINT_NAME)
            .queryParams(queryParams)
            .executor()
            .execute(STRING_TYPE_REFERENCE);
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void putWithBody() {
    final Map<String, Object> queryParams =
        Collections.singletonMap("Foo", Arrays.asList("bar", "baz"));
    final ResponseEntity<String> execute =
        manager
            .put("Test String")
            .into(POSTMAN_ECHO_PUT)
            .queryParams(queryParams)
            .executor()
            .execute(STRING_TYPE_REFERENCE);
    assertThat(execute).extracting(ResponseEntity::getStatusCode).isEqualTo(HttpStatus.OK);
    assertThat(execute).extracting(HttpEntity::getBody).isNotNull();
  }

  @Test
  void patchNoBody() {
    final Service service = BaseService.from(POSTMAN_ECHO_PATCH);
    assertThrows(
        UnsupportedOperationException.class,
        () -> manager.patch().into(service, BaseService.UNIQUE_ENDPOINT_NAME).executor().execute());
  }

  @Test
  void patchWithBody() {
    assertThrows(
        UnsupportedOperationException.class,
        () ->
            manager
                .patch("Test String")
                .into(UriComponentsBuilder.fromUriString(POSTMAN_ECHO_PATCH).build().toUri())
                .executor()
                .execute());
  }
}

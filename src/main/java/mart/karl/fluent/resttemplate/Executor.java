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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

/**
 * Provides handy methods to prepare the {@linkplain RestTemplate RestTemplate's} request parts like
 * headers and uri variables. These method are backed by {@linkplain RequestEntity RequestEntity}.
 * <br>
 * <br>
 *
 * <p>Additionally, provides ways to perfom the REST request to the URI built during the process.
 * These ways internally use {@linkplain RestTemplate#exchange(RequestEntity, Class)} method. This
 * REST request represents the conclusion of all the fluent phases.<br>
 * <br>
 *
 * <p>Executor represents the <i>FluentService executor phase</i>.
 */
public interface Executor {

  /**
   * Registers a header for the request. If headers <i>values</i> previously exist under the same
   * <i>name</i>, these new values will be included in the list of that name. <br>
   * <br>
   *
   * @param name Name of the header.
   * @param values Values to include in the header list.
   * @return The Executor instance invoking this method.
   */
  Executor header(String name, String... values);

  /**
   * Registers a group of headers for the request. If headers <i>values</i> previously exist under
   * some of the <i>names</i> provided in the input, these new values will be included in the list
   * of those names. <br>
   * <br>
   *
   * @param headers A {@linkplain HttpHeaders} data structure.
   * @return The Executor instance invoking this method.
   */
  Executor headers(HttpHeaders headers);

  /**
   * Set the list of acceptable {@linkplain MediaType media types}, as specified by the {@code
   * Accept} header.
   *
   * @param types the acceptable media types.
   * @return The Executor instance invoking this method.
   */
  Executor accept(MediaType... types);

  /**
   * Set the list of acceptable {@linkplain Charset charsets}, as specified by the {@code
   * Accept-Charset} header.
   *
   * @param charsets the acceptable charsets.
   * @return The Executor instance invoking this method.
   */
  Executor acceptCharset(Charset... charsets);

  /**
   * Executes the defined {@linkplain RequestEntity RequestEntity}, pointing to the defined
   * {@linkplain URI URI} and expects a {@linkplain ResponseEntity ResponseEntity} with empy body.
   *
   * @return A ResponseEntity with no body.
   */
  ResponseEntity<Void> execute();

  /**
   * Executes the defined {@linkplain RequestEntity RequestEntity}, pointing to the defined
   * {@linkplain URI URI} and expects a {@linkplain ResponseEntity ResponseEntity} with a body of
   * the type defined by {@code responseClass}.
   *
   * @param <O> Response type.
   * @param responseClass The class type that will be used to parse the REST response.
   * @return A ResponseEntity with a body of the defined type.
   */
  <O> ResponseEntity<O> execute(final Class<O> responseClass);

  /**
   * Executes the defined {@linkplain RequestEntity RequestEntity}, pointing to the defined
   * {@linkplain URI URI} and expects a {@linkplain ResponseEntity ResponseEntity} containing a body
   * parametrized by the given {@linkplain ParameterizedTypeReference typeReference}.<br>
   * <br>
   *
   * <p>This executor method gains relevance in cases where the expected response is parametrized,
   * avoiding extra casting or transformation phases. <br>
   * <br>
   *
   * <pre class="code">
   *   public ResponseEntity&lt;List&lt;Person&gt;&gt; fetchPersons(...)  {
   *     ...
   *     ParameterizedTypeReference&lt;List&lt;String&lt;&lt; ptr =
   *       new ParameterizedTypeReference&lt;ResponseEntity&lt;List&lt;Person&gt;&gt;&gt;() {};
   *     return fluentRestTemplate...executor().execute(ptr)
   *   }
   * </pre>
   *
   * @param typeReference A parametrized type reference to parse the remote service response.
   * @param <O> Response type.
   * @return A ResponseEntity with a body of the defined type reference..
   */
  <O> ResponseEntity<O> execute(final ParameterizedTypeReference<O> typeReference);

  /**
   * Executes the defined {@linkplain RequestEntity RequestEntity}, pointing to the defined
   * {@linkplain URI URI} and expects a plain {@code void} (or no response), extracted from
   * {@linkplain ResponseEntity#getBody()}.
   */
  void executeForObject();

  /**
   * Executes the defined {@linkplain RequestEntity RequestEntity}, pointing to the defined
   * {@linkplain URI URI} and expects a plain object of the type defined by {@code responseClass}.
   * This element is extracted from the obtained {@linkplain ResponseEntity#getBody()}.
   *
   * @param <O> Response type.
   * @param responseClass The class type that will be used to parse the REST response.
   * @return A plain object of the defined type.
   */
  <O> O executeForObject(final Class<O> responseClass);

  /**
   * Executes the defined {@linkplain RequestEntity RequestEntity}, pointing to the defined
   * {@linkplain URI URI} and expects a plain parametrized object defined by the given {@linkplain
   * ParameterizedTypeReference typeReference}.<br>
   * <br>
   *
   * <p>This executor method gains relevance in cases where the expected response is parametrized,
   * avoiding extra casting or transformation phases. <br>
   * <br>
   *
   * <pre class="code">
   *   public List&lt;Person&gt; fetchPersons(...)  {
   *     ...
   *     ParameterizedTypeReference&lt;List&lt;String&lt;&lt; ptr =
   *       new ParameterizedTypeReference&lt;List&lt;Person&gt;&gt;() {};
   *     return fluentRestTemplate...executor().execute(ptr)
   *   }
   * </pre>
   *
   * @param typeReference A parametrized type reference to parse the remote service response.
   * @param <O> Response type.
   * @return A plain parametrized object of the defined type reference..
   */
  <O> O executeForObject(final ParameterizedTypeReference<O> typeReference);
}

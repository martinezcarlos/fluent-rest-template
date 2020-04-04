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
import mart.karl.fluent.service.FluentService;
import org.springframework.lang.Nullable;

/**
 * Intermediate starter phase to explicity request the user to provide (or not) a Service's endpoint
 * key. The resolved endpoint's value will be used during {@linkplain URI URI} construction.
 */
public interface UriServiceBuilder {
  /**
   * Transitions to the {@linkplain FluentRestTemplate}'s URI builder phase providing an endpoint
   * key. The resolved value for this key will be treated as a URI's path segment.<br>
   * <br>
   *
   * <p>If the provided key is {@code null} or is not present in the endpoint's map used to build
   * the {@linkplain FluentService}, this method behaves exactly as {@linkplain
   * UriServiceBuilder#withoutEndpoint()} method.
   *
   * @param key Endpoint key used to fetch the defined value, provided the endpoints map contains
   *     the given key.
   * @return A ExecutorUriBuilder in charge of the URI builder phase.
   */
  ExecutorUriBuilder withEndpoint(@Nullable String key);

  /**
   * Using a given {@linkplain FluentService}, transitions to the {@linkplain FluentRestTemplate}'s
   * URI builder phase without providing an endpoint key. Only URI-specific parts and service's
   * version, if present, will be used to build the URI.
   *
   * @return A ExecutorUriBuilder in charge of the URI builder phase.
   */
  ExecutorUriBuilder withoutEndpoint();
}

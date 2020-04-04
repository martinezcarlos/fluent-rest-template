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

package mart.karl.fluent.uri;

import java.net.URI;
import java.util.Collection;
import java.util.Map;
import mart.karl.fluent.service.FluentService;
import org.springframework.lang.NonNull;
import org.springframework.util.MultiValueMap;

/**
 * Declares useful methods to provide {@linkplain URI URI} parts to be used during URI construction.
 * This class represents the <b>URI builder phase</b> of {@linkplain FluentService}.
 *
 * @param <T> Type of an interface or concrete class inheriting from this interface. It'll be used
 *     as the return parameter for the declared methods.
 */
public interface FluentUriBuilder<T extends FluentUriBuilder<T>> {

  /**
   * Registers query params to the {@linkplain FluentUriBuilder}. If FluentUriBuilder's query param
   * <i>values</i> previously exist under the same <i>key</i>, this new value will be included in
   * the list of that key. <br>
   * <br>
   *
   * <p>If there are registered <i>common</i> query parameters during the fluent builder phase those
   * will be used in the URI construction process as well.
   *
   * @param key Key of the service's query params.
   * @param values Values to include in the service's query params list.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T queryParam(@NonNull String key, Object... values);

  /**
   * Registers query params to the {@linkplain FluentUriBuilder}. If FluentUriBuilder's query param
   * <i>values</i> previously exist under the same <i>key</i>, this new value will be included in
   * the list of that key. <br>
   * <br>
   *
   * <p>If there are registered <i>common</i> query parameters during the fluent builder phase those
   * will be used in the URI construction process as well.
   *
   * @param key Key of the service's query params.
   * @param values Values to include in the service's query params list.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T queryParam(@NonNull String key, @NonNull Collection<?> values);

  /**
   * Registers a map of query params to the {@linkplain FluentUriBuilder}. If FluentUriBuilder's
   * query param <i>values</i> previously exist under some of the same <i>keys</i> provided in the
   * map, these new values will be included in the lists of those keys. <br>
   * <br>
   *
   * <p>If there are registered <i>common</i> query parameters during the fluent builder phase those
   * will be used in the URI construction process as well.
   *
   * @param params Map of String key-values to include in the FluentUriBuilder's query params list.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T queryParams(@NonNull MultiValueMap<String, String> params);

  /**
   * Registers a fragment to the FluentUriBuilder. <br>
   * <br>
   *
   * <p>If a specific fragment is provided during the uri builder phase, the <i>common</i> fragment
   * is overriden by this specific fragment. If fragment is set to {@code null} during this phase,
   * no fragment will be used in the resulting URI.<br>
   * <br>
   *
   * <p>If this method is not invoked during the URI builder phase and given that a <i>common</i>
   * fragment was supplied during the {@linkplain FluentService} starter phase, that <i>common</i>
   * fragment will be used as part of the generated URI.
   *
   * @param fragment URI fragment to include in any URI created by this FluentService, if not null.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T fragment(String fragment);

  /**
   * Registers a service's uri variable. If a service's uri variable <i>value</i> previously exists
   * under the same <i>key</i>, it's value is replaced.
   *
   * @param key Key of the service's uri variable.
   * @param value Value of the service's uri variable
   * @return The FluentUriBuilder instance invoking this method.
   */
  T uriVariable(@NonNull String key, @NonNull Object value);

  /**
   * Registers several service's uri variables provided by the given {@linkplain Map}. If a
   * service's uri variable <i>value</i> exists under any of the <i>keys</i> provided by the Map,
   * it's value is replaced.
   *
   * @param variables Key-value map representing service's uri variables.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T uriVariables(@NonNull Map<String, ?> variables);
}

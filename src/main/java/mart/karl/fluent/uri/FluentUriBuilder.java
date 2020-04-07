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
import org.springframework.util.MultiValueMap;

/**
 * Declares useful methods to provide specific {@linkplain URI URI} parts to be used during URI
 * construction. This interface represents the <b>URI builder phase</b> of {@linkplain
 * FluentService}.
 *
 * @param <T> Type of an interface or concrete class inheriting from this interface. It'll be used
 *     as the return parameter for the declared methods.
 */
public interface FluentUriBuilder<T extends FluentUriBuilder<T>> {

  /**
   * Registers query params to the {@linkplain FluentUriBuilder}. If FluentUriBuilder's query param
   * <i>values</i> previously existed under the same <i>key</i>, these new value will be included in
   * the list of that key. <br>
   * <br>
   *
   * <p>If there are registered <i>common</i> query parameters during the fluent builder phase,
   * those will be used in the URI construction process in conjunction with the ones provided here.
   *
   * @param key Key of the service's query params.
   * @param values Values to include in the service's query params list for the given key.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T queryParam(String key, Object... values);

  /**
   * Registers query params to the {@linkplain FluentUriBuilder}. If FluentUriBuilder's query param
   * <i>values</i> previously existed under the same <i>key</i>, these new value will be included in
   * the list of that key. <br>
   * <br>
   *
   * <p>If there are registered <i>common</i> query parameters during the fluent builder phase,
   * those will be used in the URI construction process in conjunction with the ones provided here.
   *
   * @param key Key of the service's query params.
   * @param values Values to include in the service's query params list for the given key.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T queryParam(String key, Collection<?> values);

  /**
   * Registers a map of query params to the {@linkplain FluentUriBuilder}, removing any existing
   * query params used to build this instance. Only the query params provided in the new map, if
   * any, will remain.<br>
   * <br>
   *
   * @param params Map of String key-values to with query params.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T queryParams(MultiValueMap<String, String> params);

  /**
   * Registers a fragment to the FluentUriBuilder. <br>
   * <br>
   *
   * <p>If a <i>common</i> fragment was provided during FluentService instantiation, it will be
   * overriden. If fragment is set to {@code null} in this method, no fragment will be used in the
   * resulting URI.<br>
   * * <br>
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
  T uriVariable(String key, Object value);

  /**
   * Registers several service's uri variables provided by the given {@linkplain Map}. If service's
   * uri variables <i>values</i> exist under the <i>keys</i> provided by the Map, those values are
   * overriden.
   *
   * @param variables Key-value map representing service's uri variables.
   * @return The FluentUriBuilder instance invoking this method.
   */
  T uriVariables(Map<String, ?> variables);
}

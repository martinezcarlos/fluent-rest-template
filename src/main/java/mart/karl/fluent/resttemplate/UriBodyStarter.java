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
import org.springframework.http.HttpMethod;

/**
 * Provides simple methods to initialize a {@linkplain FluentRestTemplate}. <br>
 * <br>
 *
 * <p>These methods gramatically match {@linkplain HttpMethod}'s verbs POST, PUT and PATCH. <br>
 * <br>
 *
 * <p>Using any of the provided methods starts the <i>FluentRestTemplate builder phase</i>.
 */
public interface UriBodyStarter {
  /**
   * Starts the <i>FluentRestTemplate builder phase</i> by internally using {@linkplain
   * FluentService#from(String)} to initialize a FluentService.
   *
   * @param uriString Standard String representation of {@linkplain URI}.
   * @return The ExecutorUriBuilder that will handle the <i>FluentRestTemplate builder phase</i>.
   */
  ExecutorUriBuilder into(final String uriString);

  /**
   * Starts the <i>FluentRestTemplate builder phase</i> by internally using {@linkplain
   * FluentService#from(URI)} to initialize a FluentService.
   *
   * @param uri Standard {@linkplain URI}.
   * @return The ExecutorUriBuilder that will handle the <i>FluentRestTemplate builder phase</i>.
   */
  ExecutorUriBuilder into(final URI uri);

  /**
   * Starts the <i>FluentRestTemplate builder phase</i> by receving an instance of FluentService.
   *
   * @param service A non null {@linkplain FluentService}.
   * @return An intermediate starter phase to request (or not) a Service's endpoint key.
   */
  UriServiceBuilder into(final FluentService service);
}

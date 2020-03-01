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
import lombok.experimental.SuperBuilder;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@SuperBuilder
public class BaseService extends Service {

  public static final String UNIQUE_ENDPOINT_NAME = "uniqueEndpoint";

  public static Service from(final URI uri) {
    return buildFromComponents(UriComponentsBuilder.fromUri(uri).build());
  }

  private static BaseService buildFromComponents(final UriComponents components) {
    return BaseService.builder()
        .scheme(components.getScheme())
        .host(components.getHost())
        .port(components.getPort() == -1 ? null : String.valueOf(components.getPort()))
        .endpoints(Collections.singletonMap(UNIQUE_ENDPOINT_NAME, components.getPath()))
        .build();
  }

  public static Service from(final String uriString) {
    return buildFromComponents(UriComponentsBuilder.fromUriString(uriString).build());
  }
}
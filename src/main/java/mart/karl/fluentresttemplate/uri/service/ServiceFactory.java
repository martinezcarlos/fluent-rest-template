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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public final class ServiceFactory extends Service {

  public static Service from(final URI uri) {
    return from(UriComponentsBuilder.fromUri(uri).build());
  }

  private static Service from(final UriComponents components) {
    final Service baseService = new ServiceFactory();
    baseService.setScheme(components.getScheme());
    baseService.setHost(components.getHost());
    baseService.setPort(components.getPort() == -1 ? null : String.valueOf(components.getPort()));
    baseService.setContextPath(components.getPath());
    return baseService;
  }

  public static Service from(final String uriString) {
    return from(UriComponentsBuilder.fromUriString(uriString).build());
  }
}

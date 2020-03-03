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

package mart.karl.fluentresttemplate.uri;

import mart.karl.fluentresttemplate.executor.Executor;
import org.springframework.util.MultiValueMap;

import java.util.Collection;
import java.util.Map;

public interface FluentUriBuilder {

  FluentUriBuilder queryParam(String name, Object... values);

  FluentUriBuilder queryParam(String name, Collection<?> values);

  FluentUriBuilder queryParams(Map<String, ?> params);

  FluentUriBuilder queryParams(MultiValueMap<String, String> params);

  FluentUriBuilder uriVariable(String name, Object value);

  FluentUriBuilder uriVariables(Map<String, ?> variables);

  Executor executor();
}

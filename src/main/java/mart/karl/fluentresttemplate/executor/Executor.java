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

package mart.karl.fluentresttemplate.executor;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;

import java.nio.charset.Charset;
import java.util.function.Consumer;

public interface Executor {
  Executor header(String name, String... values);

  Executor headers(@Nullable HttpHeaders headers);

  Executor headers(Consumer<HttpHeaders> consumer);

  Executor accept(MediaType... types);

  Executor acceptCharset(Charset... charsets);

  ResponseEntity<Void> execute();

  <O> ResponseEntity<O> execute(final Class<O> responseClass);

  <O> ResponseEntity<O> execute(final ParameterizedTypeReference<O> typeReference);
}

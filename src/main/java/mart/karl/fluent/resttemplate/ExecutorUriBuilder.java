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

import mart.karl.fluent.uri.FluentUriBuilder;

/**
 * Provides handy methods to prepare the URI parts to be used in the creation of the URI.
 * ExecutorUriBuilder represents the <i>FluentService URI builder phase</i>. <br>
 * <br>
 *
 * <p>Additionally, provides a way to transition to the <i>FluentRestTemplate executor phase</i> by
 * means of {@linkplain ExecutorUriBuilder#executor()} method.
 */
public interface ExecutorUriBuilder extends FluentUriBuilder<ExecutorUriBuilder> {

  /**
   * Finishes {@linkplain FluentRestTemplate FluentRestTemplate's} builder phase and starts the
   * executor phase.
   *
   * @return Executor to handle executor phase.
   */
  Executor executor();
}

/*
 * Copyright (C) 2020 Johannes Donath <johannesd@torchmind.com>
 * and other copyright owners as documented in the project's IP log.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tv.dotstart.overlord.shared

/**
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 25/04/2020
 */
enum class MachineVersion(
    private val specificationVersion: String,
    open val specificationNumber: Int) {

  JAVA_8("1.8", 8),
  JAVA_9("9", 9),
  JAVA_10("10", 10),
  JAVA_11("11", 11),
  JAVA_12("12", 12),
  JAVA_13("13", 13),
  JAVA_14("14", 14),
  UNKNOWN("", 0) {
    override val detected = false

    override val specificationNumber: Int by lazy {
      try {
        System.getProperty("java.specification.version", "0").toInt(10)
      } catch (ex: NumberFormatException) {
        0
      }
    }
  };

  open val detected: Boolean by lazy {
    val currentSpec = System.getProperty("java.specification.version", "")
    this.specificationVersion == currentSpec
  }

  fun require() {
    check(
        current.specificationNumber >= this.specificationNumber) { "Expected JVM with $this support or newer" }
  }

  companion object {

    /**
     * Retrieves the detected machine version implementation for the executing JVM.
     *
     * When the given VM version is unknown, [UNKNOWN] will be returned instead.
     */
    val current: MachineVersion by lazy {
      values().firstOrNull { it.detected } ?: UNKNOWN
    }
  }
}

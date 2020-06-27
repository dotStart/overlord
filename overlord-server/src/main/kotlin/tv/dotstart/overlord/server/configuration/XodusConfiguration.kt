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
package tv.dotstart.overlord.server.configuration

import jetbrains.exodus.database.TransientEntityStore
import kotlinx.dnq.XdModel
import kotlinx.dnq.store.container.StaticStoreContainer
import kotlinx.dnq.util.initMetaData
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.annotation.EnableTransactionManagement
import tv.dotstart.overlord.server.configuration.properties.XodusConfigurationProperties
import tv.dotstart.overlord.server.database.XodusTransactionManager

/**
 * Configures Xodus and Spring's transaction management APIs for use within the application.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
@Configuration
@EnableTransactionManagement
@EnableConfigurationProperties(XodusConfigurationProperties::class)
class XodusConfiguration(private val properties: XodusConfigurationProperties) {

  @Bean
  fun xodusEntityStore(): TransientEntityStore {
    XdModel.scanJavaClasspath()

    val store = StaticStoreContainer.init(this.properties.location, "overlord")
    initMetaData(XdModel.hierarchy, store)

    return store
  }

  @Bean
  fun xodusTransactionManager(store: TransientEntityStore) = XodusTransactionManager(store)
}

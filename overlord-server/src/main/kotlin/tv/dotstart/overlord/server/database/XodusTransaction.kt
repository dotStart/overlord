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
package tv.dotstart.overlord.server.database

import jetbrains.exodus.database.TransientEntityStore
import jetbrains.exodus.entitystore.StoreTransaction
import org.springframework.transaction.TransactionDefinition

/**
 * Encapsulates a Xodus transaction for use with the Spring transaction API.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
internal class XodusTransaction(private val store: TransientEntityStore) {

  private lateinit var transaction: StoreTransaction

  fun begin(definition: TransactionDefinition) {
    this.transaction = when {
      definition.isReadOnly -> this.store.beginReadonlyTransaction()
      else -> this.store.beginSession()
    }

    this.store.transactional(readonly = true) {}
  }

  fun abort() {
    this.transaction.abort()
  }

  fun commit() {
    this.transaction.commit()
  }
}

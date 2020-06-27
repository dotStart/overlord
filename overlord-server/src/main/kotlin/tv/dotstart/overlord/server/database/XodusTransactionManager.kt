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
import org.springframework.transaction.TransactionDefinition
import org.springframework.transaction.support.AbstractPlatformTransactionManager
import org.springframework.transaction.support.DefaultTransactionStatus

/**
 * Provides Spring transaction support for our embedded Xodus store.
 *
 * @author [Johannes Donath](mailto:johannesd@torchmind.com)
 * @date 27/06/2020
 */
class XodusTransactionManager(
    private val store: TransientEntityStore) : AbstractPlatformTransactionManager() {

  override fun doGetTransaction(): Any = XodusTransaction(this.store)

  override fun doCommit(status: DefaultTransactionStatus) {
    val transaction = status.transaction as XodusTransaction
    transaction.commit()
  }

  override fun doBegin(transaction: Any, definition: TransactionDefinition) {
    (transaction as XodusTransaction).begin(definition)
  }

  override fun doRollback(status: DefaultTransactionStatus) {
    val transaction = status.transaction as XodusTransaction
    transaction.abort()
  }
}

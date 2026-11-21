/*
 * This source is part of the
 *      _____  ___   ____
 *  __ / / _ \/ _ | / __/___  _______ _
 * / // / , _/ __ |/ _/_/ _ \/ __/ _ `/
 * \___/_/|_/_/ |_/_/ (_)___/_/  \_, /
 *                              /___/
 * repository.
 *
 * Copyright (C) 2026 Benoit 'BoD' Lubek (BoD@JRAF.org)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package org.jraf.intellijplugin.nyantray.plugin

import com.apollographql.apollo.ApolloClient
import com.apollographql.apollo.api.Optional
import com.intellij.openapi.application.ApplicationInfo
import org.jraf.intellijplugin.nyantray.graphql.SetBusyMutation
import org.jraf.intellijplugin.nyantray.graphql.SetIdleMutation
import org.jraf.intellijplugin.nyantray.graphql.type.scalar.ProcessId

private const val PROCESS_ID: String = "org.jraf.intellijplugin.nyantray"
private val DESCRIPTION: String = ApplicationInfo.getInstance().versionName

class NyanClient {
  private val apolloClient = ApolloClient.Builder().serverUrl("http://localhost:2026/graphql").build()

  suspend fun setBusy(): Boolean {
    return apolloClient.mutation(SetBusyMutation(processId = ProcessId(PROCESS_ID), Optional.present(DESCRIPTION)))
      .execute()
      .data != null
  }

  suspend fun setIdle(): Boolean {
    return apolloClient.mutation(SetIdleMutation(processId = ProcessId(PROCESS_ID)))
      .execute()
      .data != null
  }
}

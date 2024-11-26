/*
 * Copyright 2022 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.common.viewmodel

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

interface StateViewModel<State> {
    val state: StateFlow<State>
}

interface EventViewModel<ViewEvent> {
    val viewEvent: Flow<ViewEvent>
}

class StateDelegate<State> : StateViewModel<State> {
    private lateinit var _state: MutableStateFlow<State>
    override val state: StateFlow<State>
        get() {
            return _state.asStateFlow()
        }

    fun setDefaultState(state: State) {
        _state = MutableStateFlow(state)
    }

    fun updateState(block: (State) -> State) {
        _state.update {
            block(it)
        }
    }

    inline fun <reified SubState : State> onState(block: (SubState) -> Unit) {
        val currentState = state.value
        if (currentState is SubState) {
            block(currentState)
        }
    }
}

class EventDelegate<ViewEvent> : EventViewModel<ViewEvent> {
    private val _viewEvent = MutableSharedFlow<ViewEvent>()
    override val viewEvent: Flow<ViewEvent> = _viewEvent.asSharedFlow()

    fun sendEvent(scope: CoroutineScope, newEvent: ViewEvent) {
        scope.launch {
            sendEvent(newEvent)
        }
    }

    suspend fun sendEvent(newEvent: ViewEvent) {
        _viewEvent.emit(newEvent)
    }
}

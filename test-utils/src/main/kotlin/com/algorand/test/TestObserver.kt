/*
 * Copyright 2022-2025 Pera Wallet, LDA
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */

package com.algorand.test

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestObserver<T>(flow: Flow<T>, coroutineScope: CoroutineScope) {
    private val emittedValues = mutableListOf<T>()
    private var flowError: Throwable? = null
    private val job: Job = flow.onEach {
        emittedValues.add(it)
    }.catch {
        flowError = it
    }.launchIn(coroutineScope)

    fun assertNoValue() {
        assertTrue(emittedValues.isEmpty())
    }

    fun assertValue(value: T) {
        assertTrue(emittedValues.isNotEmpty())
        assertEquals(value, emittedValues.last())
    }

    fun assertValueHistory(vararg values: T) {
        assertSize(values.size)
        assertSequence(values.toList())
    }

    private fun assertSize(size: Int) {
        assertEquals(size, emittedValues.size)
    }

    private fun getValues(): MutableList<T> = emittedValues

    fun stopObserving() {
        job.cancel()
    }

    fun value(): T = getValues().last()

    private fun assertSequence(values: List<T>) {
        for ((index, v) in values.withIndex()) {
            assertEquals(v, emittedValues[index])
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
fun <T> Flow<T>.test(coroutineScope: CoroutineScope = CoroutineScope(UnconfinedTestDispatcher())): TestObserver<T> {
    return TestObserver(this, coroutineScope)
}

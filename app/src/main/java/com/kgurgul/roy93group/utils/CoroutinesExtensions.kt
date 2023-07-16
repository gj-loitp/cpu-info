package com.kgurgul.roy93group.utils

import com.kgurgul.roy93group.utils.wrappers.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import timber.log.Timber

/**
 * Wrap passed [block] with [Result]. Flow will always start with [Result.Loading] and finish
 * with [Result.Success] in case of success otherwise it will emit [Result.Error].
 */
fun <T> wrapToResultFlow(block: suspend () -> T): Flow<Result<T>> = flow {
    emit(Result.Loading)
    try {
        val result = block()
        emit(Result.Success(result))
    } catch (throwable: Throwable) {
        Timber.e(throwable)
        emit(Result.Error(throwable))
    }
}

/**
 * Create flow from suspended function wrapped with [Result]. Flow will always start with
 * [Result.Loading] and finish with [Result.Success] in case of success otherwise it will emit
 * [Result.Error].
 */
fun <T> (suspend () -> T).asResultFlow(): Flow<Result<T>> = flow {
    emit(Result.Loading)
    try {
        val result = invoke()
        emit(Result.Success(result))
    } catch (throwable: Throwable) {
        Timber.e(throwable)
        emit(Result.Error(throwable))
    }
}
package com.alfabank.qapp.base

/**
 * Input-Output UseCase variant
 * Can receive parameters
 */
interface IOUseCase<Input, Output> : UseCase {
    suspend operator fun invoke(input: Input): Output
}

/**
 * Common interface for all UseCases
 */
interface UseCase

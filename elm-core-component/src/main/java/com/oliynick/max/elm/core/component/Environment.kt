@file:Suppress("FunctionName")

package com.oliynick.max.elm.core.component

/**
 * Dependencies holder
 */
data class Env<M, C, S>(
    inline val initializer: Initializer<S, C>,
    inline val resolver: Resolver<C, M>,
    inline val update: Update<M, S, C>
)

class EnvBuilder<M, C, S>(
    var initializer: Initializer<S, C>,
    var resolver: Resolver<C, M>,
    var update: Update<M, S, C>
) {
    constructor(env: Env<M, C, S>) : this(
        env.initializer,
        env.resolver,
        env.update
    )
}

fun <M, C, S> Env(
    initializer: Initializer<S, C>,
    resolver: Resolver<C, M>,
    update: Update<M, S, C>,
    config: EnvBuilder<M, C, S>.() -> Unit = {}
) = EnvBuilder(initializer, resolver, update)
    .apply(config)
    .toEnv()

fun <M, C, S> Env(
    initialState: S,
    resolver: Resolver<C, M>,
    update: Update<M, S, C>,
    vararg initialCommands: C,
    config: EnvBuilder<M, C, S>.() -> Unit = {}
) = Env(
    Initializer(
        initialState,
        setOf(*initialCommands)
    ), resolver, update, config
)

fun <M, C, S> Env(
    initialState: S,
    resolver: Resolver<C, M>,
    update: Update<M, S, C>,
    initialCommands: Set<C>,
    config: EnvBuilder<M, C, S>.() -> Unit = {}
) = Env(
    Initializer(
        initialState,
        initialCommands
    ), resolver, update, config
)

fun <M, C, S> EnvBuilder<M, C, S>.toEnv(): Env<M, C, S> =
    Env(initializer, resolver, update)
package com.jnape.palatable.lambda.functor.builtin;

import com.jnape.palatable.lambda.functor.Contravariant;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.lens.Iso;

import java.util.function.Function;

/**
 * A profunctor used to extract the isomorphic functions an {@link Iso} is composed of.
 *
 * @param <A> the smaller viewed value of an {@link Iso}
 * @param <B> the smaller viewing value of an {@link Iso}
 * @param <S> the larger viewing value of an {@link Iso}
 * @param <T> the larger viewed value of an {@link Iso}
 */
public final class Exchange<A, B, S, T> implements
        Functor<T, Exchange<A, B, S, ?>>,
        Contravariant<S, Exchange<A, B, ?, T>> {
    private final Function<? super S, ? extends A> sa;
    private final Function<? super B, ? extends T> bt;

    public Exchange(Function<? super S, ? extends A> sa, Function<? super B, ? extends T> bt) {
        this.sa = sa;
        this.bt = bt;
    }

    public Function<? super S, ? extends A> sa() {
        return sa;
    }

    public Function<? super B, ? extends T> bt() {
        return bt;
    }

    @Override
    public <U> Exchange<A, B, S, U> fmap(Function<? super T, ? extends U> fn) {
        return new Exchange<>(sa, bt.andThen(fn));
    }

    @Override
    public <R> Exchange<A, B, R, T> contraMap(Function<? super R, ? extends S> fn) {
        return new Exchange<>(sa.compose(fn), bt);
    }
}

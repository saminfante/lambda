package com.jnape.palatable.lambda.functions.specialized;

import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;

/**
 * Generalized, portable {@link Applicative#pure(Object)}, with a loosened functor constraint.
 *
 * @param <F> the {@link Functor} to lift into
 */
public interface Pure<F extends Functor> {

    <A> Functor<A, F> apply(A a);
}

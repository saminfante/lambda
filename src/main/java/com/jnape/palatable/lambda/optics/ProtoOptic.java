package com.jnape.palatable.lambda.optics;

import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.Profunctor;

public interface ProtoOptic<P extends Profunctor, F extends Functor, S, T, A, B> {

    <CoF extends F> Optic<P, CoF, S, T, A, B> toOptic(Pure<CoF> pure);

    interface Simple<P extends Profunctor, F extends Functor, S, A> extends ProtoOptic<P, F, S, S, A, A> {

    }
}

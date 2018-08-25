package com.jnape.palatable.lambda.functions.builtin.fn3;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functions.Fn2;
import com.jnape.palatable.lambda.functions.Fn3;
import com.jnape.palatable.lambda.functor.Contravariant;
import com.jnape.palatable.lambda.functor.Functor;

import java.util.function.Function;

public final class DiMap<Z, A, B, C, Profunctor extends Contravariant<?, ?> & Functor<?, ?>,
        PAB extends Contravariant<A, ? extends Profunctor> & Functor<B, ? extends Profunctor>,
        PZC extends Contravariant<Z, ? extends Profunctor> & Functor<C, ? extends Profunctor>>
        implements Fn3<Function<? super Z, ? extends A>, Function<? super B, ? extends C>, PAB, PZC> {

    private static final DiMap INSTANCE = new DiMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public PZC apply(Function<? super Z, ? extends A> f, Function<? super B, ? extends C> g, PAB PAB) {
        return (PZC) ((Contravariant<Z, ? extends Profunctor> & Functor<B, ? extends Profunctor>) PAB.contraMap(f)).fmap(g);
    }

    @SuppressWarnings("unchecked")
    public static <Z, A, B, C, F extends Contravariant<?, ?> & Functor<?, ?>,
            In extends Contravariant<A, ? extends F> & Functor<B, ? extends F>,
            Out extends Contravariant<Z, ? extends F> & Functor<C, ? extends F>> DiMap<Z, A, B, C, F, In, Out> dimap() {
        return INSTANCE;
    }

    public static <Z, A, B, C, F extends Contravariant<?, ?> & Functor<?, ?>,
            In extends Contravariant<A, ? extends F> & Functor<B, ? extends F>,
            Out extends Contravariant<Z, ? extends F> & Functor<C, ? extends F>> Fn2<Function<? super B, ? extends C>, In, Out> dimap(
            Function<? super Z, ? extends A> f) {
        return new DiMap<Z, A, B, C, F, In, Out>().apply(f);
    }

    public static <Z, A, B, C, F extends Contravariant<?, ?> & Functor<?, ?>,
            In extends Contravariant<A, ? extends F> & Functor<B, ? extends F>,
            Out extends Contravariant<Z, ? extends F> & Functor<C, ? extends F>> Fn1<In, Out> dimap(
            Function<? super Z, ? extends A> f, Function<? super B, ? extends C> g) {
        return new DiMap<Z, A, B, C, F, In, Out>().apply(f).apply(g);
    }

    public static <Z, A, B, C, F extends Contravariant<?, ?> & Functor<?, ?>,
            In extends Contravariant<A, ? extends F> & Functor<B, ? extends F>,
            Out extends Contravariant<Z, ? extends F> & Functor<C, ? extends F>> Out dimap(
            Function<? super Z, ? extends A> f, Function<? super B, ? extends C> g, In in) {
        return new DiMap<Z, A, B, C, F, In, Out>().apply(f).apply(g).apply(in);
    }
}

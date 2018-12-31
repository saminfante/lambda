package com.jnape.palatable.lambda.optics;

import com.jnape.palatable.lambda.adt.Maybe;
import com.jnape.palatable.lambda.adt.choice.Choice2;
import com.jnape.palatable.lambda.adt.coproduct.CoProduct2;
import com.jnape.palatable.lambda.functions.specialized.Pure;
import com.jnape.palatable.lambda.functor.Cocartesian;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.builtin.Const;

import java.util.Map;
import java.util.function.Function;

import static com.jnape.palatable.lambda.adt.Maybe.maybe;
import static com.jnape.palatable.lambda.optics.functions.View.view;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonMap;

public interface Prism<S, T, A, B> extends ProtoOptic<Cocartesian, Functor, S, T, A, B> {

    static <S, T, A, B> Prism<S, T, A, B> prism(Function<? super S, ? extends CoProduct2<T, A, ?>> sta,
                                                Function<? super B, ? extends T> bt) {
        return new Prism<S, T, A, B>() {
            @Override
            public <CoF extends Functor> Optic<Cocartesian, CoF, S, T, A, B> toOptic(Pure<CoF> pure) {
                return Optic.<Cocartesian, CoF, S, T, A, B, Cocartesian<A, ? extends Functor<B, CoF>, Cocartesian>, Cocartesian<S, ? extends Functor<T, CoF>, Cocartesian>>optic(
                        pafb -> pafb.<T>costrengthen().diMap(s -> sta.apply(s).match(Choice2::a, Choice2::b),
                                                             tOrFb -> tOrFb.match(pure::apply, fb -> fb.fmap(bt))));
            }
        };
    }

    static <S, A> Prism.Simple<S, A> simplePrism(Function<? super S, ? extends Maybe<A>> sMaybeA,
                                                 Function<? super A, ? extends S> as) {
        return Prism.<S, S, A, A>prism(s -> sMaybeA.apply(s).toEither(() -> s), as)::toOptic;
    }

    interface Simple<S, A> extends Prism<S, S, A, A> {

    }
}

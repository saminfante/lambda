package com.jnape.palatable.lambda.plumbing;

import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;

public class Example {

    interface Functor<A, F extends App<?, ?>> {
        <B> Functor<B, F> fmap(Function<? super A, ? extends B> fn);
    }

    interface Bifunctor<A, B, BF extends App<? extends App<?, ?>, ?>> extends Functor<B, App<BF, A>> {
        @Override
        default <C> Bifunctor<A, C, BF> fmap(Function<? super B, ? extends C> fn) {
            return biMapR(fn);
        }

        default <C> Bifunctor<C, B, BF> biMapL(Function<? super A, ? extends C> fn) {
            return biMap(fn, id());
        }

        default <C> Bifunctor<A, C, BF> biMapR(Function<? super B, ? extends C> fn) {
            return biMap(id(), fn);
        }

        <C, D> Bifunctor<C, D, BF> biMap(Function<? super A, ? extends C> lFn, Function<? super B, ? extends D> rFn);
    }

    interface Contravariant<A, Contra extends App<?, ?>> {
        <Z> Contravariant<Z, Contra> contraMap(Function<? super Z, ? extends A> fn);
    }

    interface Profunctor<A, B, PF extends App<? extends App<?, ?>, ?>> extends Functor<B, App<PF, A>>, Contravariant<A, App<? extends App<PF, ?>, B>> {
        @Override
        default <C> Profunctor<A, C, PF> fmap(Function<? super B, ? extends C> fn) {
            throw new UnsupportedOperationException();
        }

        @Override
        default <Z> Profunctor<Z, B, PF> contraMap(Function<? super Z, ? extends A> fn) {
            throw new UnsupportedOperationException();
        }
    }

    interface Either<L, R> extends Bifunctor<L, R, App<App<Either<?, ?>, ?>, ?>> {
        @Override
        default <R2> Either<L, R2> fmap(Function<? super R, ? extends R2> fn) {
            return null;
        }

        @Override
        default <L2> Either<L2, R> biMapL(Function<? super L, ? extends L2> fn) {
            throw new UnsupportedOperationException();
        }

        @Override
        default <C> Either<L, C> biMapR(Function<? super R, ? extends C> fn) {
            return null;
        }

        @Override
        default <C, D> Either<C, D> biMap(Function<? super L, ? extends C> lFn,
                                          Function<? super R, ? extends D> rFn) {
            return null;
        }
    }


    interface Tuple2<A, B> extends Bifunctor<A, B, App<App<Tuple2<?, ?>, ?>, ?>> {
        @Override
        default <C> Tuple2<A, C> fmap(Function<? super B, ? extends C> fn) {
            throw new UnsupportedOperationException();
        }

        @Override
        default <C> Tuple2<C, B> biMapL(Function<? super A, ? extends C> fn) {
            throw new UnsupportedOperationException();
        }

        @Override
        default <C> Tuple2<A, C> biMapR(Function<? super B, ? extends C> fn) {
            throw new UnsupportedOperationException();
        }

        @Override
        default <C, D> Tuple2<C, D> biMap(Function<? super A, ? extends C> lFn,
                                          Function<? super B, ? extends D> rFn) {
            throw new UnsupportedOperationException();
        }

        public static <A, B> Tuple2<A, B> tuple() {
            throw new UnsupportedOperationException();
        }
    }

    public static interface App<Type, A> {
    }


}

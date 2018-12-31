package com.jnape.palatable.lambda.functor;

import com.jnape.palatable.lambda.adt.hlist.Tuple2;

import java.util.function.Function;

/**
 * "Strong" {@link Profunctor profunctors} are profunctors that can be "strengthened" to preserve the pairing of an
 * arbitrary type under <code>dimap</code> (<code>p a b -&gt; p (c, a) (c, b)</code> for any type <code>c</code>).
 *
 * @param <A>    the type of the left parameter
 * @param <B>    the type of the left parameter
 * @param <Cart> the unification parameter
 * @see com.jnape.palatable.lambda.functions.Fn1
 */
public interface Cartesian<A, B, Cart extends Cartesian> extends Profunctor<A, B, Cart> {

    /**
     * Pair some type <code>C</code> to this profunctor's carrier types.
     *
     * @param <C> the paired type
     * @return the strengthened profunctor
     */
    <C> Cartesian<Tuple2<C, A>, Tuple2<C, B>, Cart> strengthen();

    /**
     * Pair the covariantly-positioned carrier type with the contravariantly-positioned carrier type. This can be
     * thought of as "carrying" or "inspecting" the left parameter.
     *
     * @return the profunctor with the first parameter carried
     */
    default Cartesian<A, Tuple2<A, B>, Cart> carry() {
        return this.<A>strengthen().contraMap(Tuple2::fill);
    }

    @Override
    <Z, C> Cartesian<Z, C, Cart> diMap(Function<? super Z, ? extends A> lFn, Function<? super B, ? extends C> rFn);

    @Override
    default <Z> Cartesian<Z, B, Cart> diMapL(Function<? super Z, ? extends A> fn) {
        return (Cartesian<Z, B, Cart>) Profunctor.super.<Z>diMapL(fn);
    }

    @Override
    default <C> Cartesian<A, C, Cart> diMapR(Function<? super B, ? extends C> fn) {
        return (Cartesian<A, C, Cart>) Profunctor.super.<C>diMapR(fn);
    }

    @Override
    default <Z> Cartesian<Z, B, Cart> contraMap(Function<? super Z, ? extends A> fn) {
        return (Cartesian<Z, B, Cart>) Profunctor.super.<Z>contraMap(fn);
    }
}

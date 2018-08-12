package com.jnape.palatable.lambda.functor;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.lens.Lens;

import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.builtin.fn1.Id.id;

/**
 * A dually-parametric functor that maps contravariantly over the left parameter and covariantly over the right.
 * <p>
 * For more information, read about <a href="https://en.wikipedia.org/wiki/Profunctor" target="_top">Profunctors</a>.
 *
 * @param <A>  The type of the left parameter
 * @param <B>  The type of the right parameter
 * @param <PF> The unification parameter
 * @see Functor
 * @see Bifunctor
 * @see Contravariant
 * @see Fn1
 * @see Lens
 */
@FunctionalInterface
public interface Profunctor<A, B, F extends PF, CF extends PF, PF extends Profunctor> extends
        Contravariant<A, CF>,
        Functor<B, F> {

    /**
     * Dually map contravariantly over the left parameter and covariantly over the right parameter. This is isomorphic
     * to <code>diMapL(lFn).diMapR(rFn)</code>.
     *
     * @param <Z> the new left parameter type
     * @param <C> the new right parameter type
     * @param lFn the left parameter mapping function
     * @param rFn the right parameter mapping function
     * @return a profunctor over Z (the new left parameter type) and C (the new right parameter type)
     */
    <Z, C> Profunctor<Z, C, ? extends PF, ? extends PF, PF> diMap(Function<? super Z, ? extends A> lFn,
                                                                  Function<? super B, ? extends C> rFn);

    /**
     * Contravariantly map over the left parameter.
     *
     * @param <Z> the new left parameter type
     * @param fn  the mapping function
     * @return a profunctor over Z (the new left parameter type) and C (the same right parameter type)
     */
    default <Z> Profunctor<Z, B, ? extends PF, CF, PF> diMapL(Function<? super Z, ? extends A> fn) {
        return diMap(fn, id()).downcast();
    }

    /**
     * Covariantly map over the right parameter. For all profunctors that are also functors, it should hold that
     * <code>diMapR(f) == fmap(f)</code>.
     *
     * @param <C> the new right parameter type
     * @param fn  the mapping function
     * @return a profunctor over A (the same left parameter type) and C (the new right parameter type)
     */
    @SuppressWarnings("unchecked")
    default <C> Profunctor<A, C, F, ? extends PF, PF> diMapR(Function<? super B, ? extends C> fn) {
        return (Profunctor<A, C, F, ? extends PF, PF>) diMap(id(), fn);
    }

    @Override
    default <Z> Profunctor<Z, B, ?, CF, PF> contraMap(Function<? super Z, ? extends A> fn) {
        return diMapL(fn);
    }

    @Override
    default <C> Profunctor<A, C, F, ?, PF> fmap(Function<? super B, ? extends C> fn) {
        return diMapR(fn);
    }
}

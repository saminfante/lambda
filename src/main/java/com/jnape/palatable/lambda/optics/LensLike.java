package com.jnape.palatable.lambda.optics;

import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.Profunctor;
import com.jnape.palatable.lambda.monad.Monad;

import java.util.function.Function;

/**
 * The generic supertype of all types that can be treated as lenses but should preserve type-specific return types in
 * overrides. This type only exists to appease Java's unfortunate parametric type hierarchy constraints. If you're here,
 * you're probably looking for {@link Lens} or {@link Iso}.
 *
 * @param <S>  the type of the "larger" value for reading
 * @param <T>  the type of the "larger" value for putting
 * @param <A>  the type of the "smaller" value that is read
 * @param <B>  the type of the "smaller" update value
 * @param <LL> the concrete lens subtype
 * @see Lens
 * @see Iso
 */
public interface LensLike<S, T, A, B, LL extends LensLike> extends Monad<T, LensLike<S, ?, A, B, LL>>, Profunctor<S, T, LensLike<?, ?, A, B, LL>> {

    <F extends Functor, FT extends Functor<T, F>, FB extends Functor<B, F>> FT apply(
            Function<? super A, ? extends FB> fn, S s);

    @Override
    <U> LensLike<S, U, A, B, LL> flatMap(Function<? super T, ? extends Monad<U, LensLike<S, ?, A, B, LL>>> f);

    @Override
    <U> LensLike<S, U, A, B, LL> pure(U u);

    @Override
    default <U> LensLike<S, U, A, B, LL> fmap(Function<? super T, ? extends U> fn) {
        return Monad.super.<U>fmap(fn).coerce();
    }

    @Override
    default <U> LensLike<S, U, A, B, LL> zip(
            Applicative<Function<? super T, ? extends U>, LensLike<S, ?, A, B, LL>> appFn) {
        return Monad.super.zip(appFn).coerce();
    }

    @Override
    default <U> LensLike<S, U, A, B, LL> discardL(Applicative<U, LensLike<S, ?, A, B, LL>> appB) {
        return Monad.super.discardL(appB).coerce();
    }

    @Override
    default <U> LensLike<S, T, A, B, LL> discardR(Applicative<U, LensLike<S, ?, A, B, LL>> appB) {
        return Monad.super.discardR(appB).coerce();
    }

    @Override
    default <R> LensLike<R, T, A, B, LL> diMapL(Function<? super R, ? extends S> fn) {
        return (LensLike<R, T, A, B, LL>) Profunctor.super.<R>diMapL(fn);
    }

    @Override
    default <U> LensLike<S, U, A, B, LL> diMapR(Function<? super T, ? extends U> fn) {
        return (LensLike<S, U, A, B, LL>) Profunctor.super.<U>diMapR(fn);
    }

    @Override
    <R, U> LensLike<R, U, A, B, LL> diMap(Function<? super R, ? extends S> lFn,
                                          Function<? super T, ? extends U> rFn);

    @Override
    default <R> LensLike<R, T, A, B, LL> contraMap(Function<? super R, ? extends S> fn) {
        return (LensLike<R, T, A, B, LL>) Profunctor.super.<R>contraMap(fn);
    }

    /**
     * A simpler type signature for lenses where <code>S/T</code> and <code>A/B</code> are equivalent.
     *
     * @param <S>  the "larger" type
     * @param <A>  the "smaller" type
     * @param <LL> the concrete lens subtype
     */
    interface Simple<S, A, LL extends LensLike> extends LensLike<S, S, A, A, LL> {
    }
}

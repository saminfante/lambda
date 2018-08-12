package com.jnape.palatable.lambda.functions;

import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Contravariant;
import com.jnape.palatable.lambda.monad.Monad;

import java.util.function.BiFunction;
import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.Fn2.fn2;
import static com.jnape.palatable.lambda.functions.builtin.fn1.Constantly.constantly;

/**
 * A function taking a single argument. This is the core function type that all other function types extend and
 * auto-curry with.
 *
 * @param <A> The argument type
 * @param <B> The result type
 */
@FunctionalInterface
public interface Fn1<A, B> extends Monad<B, Fn1<A, ?>>, Contravariant<A, Fn1<?, B>>, Function<A, B> {

    /**
     * Invoke this function with the given argument.
     *
     * @param a the argument
     * @return the result of the function application
     */
    B apply(A a);

    /**
     * Convert this {@link Fn1} to an {@link Fn0} by supplying an argument to this function. Useful for fixing an
     * argument now, but deferring application until a later time.
     *
     * @param a the argument
     * @return an {@link Fn0}
     */
    default Fn0<B> thunk(A a) {
        return () -> apply(a);
    }

    /**
     * Widen this function's argument list by prepending an ignored argument of any type to the front.
     *
     * @param <Z> the new first argument type
     * @return the widened function
     */
    default <Z> Fn2<Z, A, B> widen() {
        return fn2(constantly(this));
    }

    @Override
    default <C> Fn1<A, C> flatMap(Function<? super B, ? extends Monad<C, Fn1<A, ?>>> f) {
        return a -> f.apply(apply(a)).<Fn1<A, C>>downcast().apply(a);
    }

    /**
     * Also left-to-right composition (<a href="http://jnape.com/the-perils-of-implementing-functor-in-java/">sadly</a>).
     *
     * @param <C> the return type of the next function to invoke
     * @param f   the function to invoke with this function's return value
     * @return a function representing the composition of this function and f
     */
    @Override
    default <C> Fn1<A, C> fmap(Function<? super B, ? extends C> f) {
        return Monad.super.<C>fmap(f).downcast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <C> Fn1<A, C> pure(C c) {
        return __ -> c;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <C> Fn1<A, C> zip(Applicative<Function<? super B, ? extends C>, Fn1<A, ?>> appFn) {
        return a -> appFn.<Fn1<A, Function<? super B, ? extends C>>>downcast().apply(a).apply(apply(a));
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    default <C> Fn1<A, C> zip(Fn2<A, B, C> appFn) {
        return zip((Fn1<A, Function<? super B, ? extends C>>) (Object) appFn);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <C> Fn1<A, C> discardL(Applicative<C, Fn1<A, ?>> appB) {
        return Monad.super.discardL(appB).downcast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <C> Fn1<A, B> discardR(Applicative<C, Fn1<A, ?>> appB) {
        return Monad.super.discardR(appB).downcast();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default <Z> Fn1<Z, B> contraMap(Function<? super Z, ? extends A> fn) {
        return fn.andThen(this)::apply;
    }

    /**
     * Override of {@link Function#compose(Function)}, returning an instance of {@link Fn1} for compatibility.
     * Right-to-left composition.
     *
     * @param before the function who's return value is this function's argument
     * @param <Z>    the new argument type
     * @return an {@link Fn1}&lt;Z, B&gt;
     */
    @Override
    default <Z> Fn1<Z, B> compose(Function<? super Z, ? extends A> before) {
        return z -> apply(before.apply(z));
    }

    /**
     * Right-to-left composition between different arity functions. Preserves highest arity in the return type,
     * specialized to lambda types (in this case, {@link BiFunction} -&gt; {@link Fn2}).
     *
     * @param before the function to pass its return value to this function's input
     * @param <Y>    the resulting function's first argument type
     * @param <Z>    the resulting function's second argument type
     * @return an {@link Fn2}&lt;Y, Z, B&gt;
     */
    default <Y, Z> Fn2<Y, Z, B> compose(BiFunction<? super Y, ? super Z, ? extends A> before) {
        return compose(fn2(before));
    }

    /**
     * Right-to-left composition between different arity functions. Preserves highest arity in the return type.
     *
     * @param before the function to pass its return value to this function's input
     * @param <Y>    the resulting function's first argument type
     * @param <Z>    the resulting function's second argument type
     * @return an {@link Fn2}&lt;Y, Z, B&gt;
     */
    default <Y, Z> Fn2<Y, Z, B> compose(Fn2<? super Y, ? super Z, ? extends A> before) {
        return fn2(before.fmap(this::compose))::apply;
    }

    /**
     * Left-to-right composition between different arity functions. Preserves highest arity in the return type,
     * specialized to lambda types (in this case, {@link BiFunction} -&gt; {@link Fn2}).
     *
     * @param after the function to invoke on this function's return value
     * @param <C>   the resulting function's second argument type
     * @param <D>   the resulting function's return type
     * @return an {@link Fn2}&lt;A, C, D&gt;
     */
    default <C, D> Fn2<A, C, D> andThen(BiFunction<? super B, ? super C, ? extends D> after) {
        return (a, c) -> after.apply(apply(a), c);
    }

    /**
     * Override of {@link Function#andThen(Function)}, returning an instance of {@link Fn1} for compatibility.
     * Left-to-right composition.
     *
     * @param after the function to invoke on this function's return value
     * @param <C>   the new result type
     * @return an <code>{@link Fn1}&lt;A, C&gt;</code>
     */
    @Override
    default <C> Fn1<A, C> andThen(Function<? super B, ? extends C> after) {
        return a -> after.apply(apply(a));
    }

    /**
     * Static factory method for wrapping a {@link Function} in an {@link Fn1}. Useful for avoid explicit casting when
     * using method references as {@link Fn1}s.
     *
     * @param function the function to adapt
     * @param <A>      the input argument type
     * @param <B>      the output type
     * @return the {@link Fn1}
     */
    static <A, B> Fn1<A, B> fn1(Function<? super A, ? extends B> function) {
        return function::apply;
    }
}

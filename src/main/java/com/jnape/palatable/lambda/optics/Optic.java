package com.jnape.palatable.lambda.optics;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.Profunctor;

import java.util.function.Function;

/**
 * A generic supertype representation for profunctor optics.
 * <p>
 * Precisely stated, for some {@link Profunctor} <code>P</code> and some {@link Functor} <code>F</code>, and for the
 * types <code>S</code> <code>T</code> <code>A</code> <code>B</code>, an
 * <code>{@link Optic}&lt;P, F, S, T, A, B&gt;</code> is a polymorphic function
 * <code>(P&lt;A, F&lt;B&gt;&gt; -&gt; P&lt;S, F&lt;T&gt;&gt;)</code> (existentially-quantified allowing for
 * covariance).
 *
 * @param <P> the {@link Profunctor} type
 * @param <F> the {@link Functor} type
 * @param <S> the left side of the output profunctor
 * @param <T> the right side's functor embedding of the output profunctor
 * @param <A> the left side of the input profunctor
 * @param <B> the right side's functor embedding of the input profunctor
 */
@FunctionalInterface
public interface Optic<P extends Profunctor, F extends Functor, S, T, A, B> {

    <CoP extends P, CoF extends F,
            PAFB extends Profunctor<A, ? extends Functor<B, CoF>, CoP>,
            PSFT extends Profunctor<S, ? extends Functor<T, CoF>, CoP>> PSFT apply(PAFB pafb);

    /**
     * Produce a monomorphic {@link Fn1} backed by this {@link Optic}.
     *
     * @param <CoP>  the covariant bound on P
     * @param <CoF>  the covariant bound on F
     * @param <PAFB> the fixed input profunctor type
     * @param <PSFT> the fixed output profunctor type
     * @return the monomorphic {@link Fn1} backed by this {@link Optic}
     */
    default <CoP extends P, CoF extends F,
            PAFB extends Profunctor<A, ? extends Functor<B, CoF>, CoP>,
            PSFT extends Profunctor<S, ? extends Functor<T, CoF>, CoP>> Fn1<PAFB, PSFT> monomorphize() {
        return this::apply;
    }

    /**
     * Left-to-right composition of optics. Requires compatibility between <code>S</code>> and <code>T</code>.
     *
     * @param f   the other optic
     * @param <Z> the new left side of the input profunctor
     * @param <C> the new right side's functor embedding of the input profunctor
     * @return the composed optic
     */
    default <Z, C> Optic<P, F, S, T, Z, C> andThen(Optic<? super P, ? super F, A, B, Z, C> f) {
        return new Optic<P, F, S, T, Z, C>() {
            @Override
            public <CoP extends P, CoF extends F, PZFC extends Profunctor<Z, ? extends Functor<C, CoF>, CoP>, PSFT extends Profunctor<S, ? extends Functor<T, CoF>, CoP>> PSFT apply(
                    PZFC pzfc) {
                return Optic.this.apply((Profunctor<A, ? extends Functor<B, CoF>, CoP>) f.apply(pzfc));
            }
        };
    }

    /**
     * Right-to-Left composition of optics. Requires compatibility between <code>A</code> and <code>B</code>.
     *
     * @param g   the other optic
     * @param <R> the new left side of the output profunctor
     * @param <U> the new right side's functor embedding of the output profunctor
     * @return the composed optic
     */
    default <R, U> Optic<P, F, R, U, A, B> compose(Optic<? super P, ? super F, R, U, S, T> g) {
        return new Optic<P, F, R, U, A, B>() {
            @Override
            public <CoP extends P, CoF extends F, PAFB extends Profunctor<A, ? extends Functor<B, CoF>, CoP>, PRFU extends Profunctor<R, ? extends Functor<U, CoF>, CoP>> PRFU apply(
                    PAFB pafb) {
                return g.apply((Profunctor<S, ? extends Functor<T, CoF>, CoP>) Optic.this.apply(pafb));
            }
        };
    }

    static <P extends Profunctor, F extends Functor, S, T, A, B,
            PAFB extends Profunctor<A, ? extends Functor<B, F>, P>,
            PSFT extends Profunctor<S, ? extends Functor<T, F>, P>> Optic<P, F, S, T, A, B> optic(
            Function<PAFB, PSFT> fn) {
        return new Optic<P, F, S, T, A, B>() {
            @Override
            @SuppressWarnings("unchecked")
            public <CoP extends P, CoF extends F, CoPAFB extends Profunctor<A, ? extends Functor<B, CoF>, CoP>, CoPSFT extends Profunctor<S, ? extends Functor<T, CoF>, CoP>> CoPSFT apply(
                    CoPAFB coPafb) {
                return (CoPSFT) fn.apply((PAFB) coPafb);
            }
        };
    }

    interface Simple<P extends Profunctor, F extends Functor, S, A> extends Optic<P, F, S, S, A, A> {

        /**
         * Compose two simple optics from left to right.
         *
         * @param f   the other simple optic
         * @param <B> the new left side and right side's functor embedding of the input profunctor
         * @return the composed simple optic
         */
        default <B> Optic.Simple<P, F, S, B> andThen(Optic.Simple<? super P, ? super F, A, B> f) {
            Optic<P, F, S, S, B, B> composed = Optic.super.andThen(f);
            return new Simple<P, F, S, B>() {
                @Override
                public <CoP extends P, CoF extends F, PAFB extends Profunctor<B, ? extends Functor<B, CoF>, CoP>, PSFT extends Profunctor<S, ? extends Functor<S, CoF>, CoP>> PSFT apply(
                        PAFB pafb) {
                    return composed.apply(pafb);
                }
            };
        }

        /**
         * Compose two simple optics from right to left.
         *
         * @param g   the other simple optic
         * @param <R> the new left side and right side's functor embedding of the output profunctor
         * @return the composed simple optic
         */
        default <R> Optic.Simple<P, F, R, A> compose(Optic.Simple<? super P, ? super F, R, S> g) {
            Optic<P, F, R, R, A, A> composed = Optic.super.compose(g);
            return new Simple<P, F, R, A>() {
                @Override
                public <CoP extends P, CoF extends F, PAFB extends Profunctor<A, ? extends Functor<A, CoF>, CoP>, PSFT extends Profunctor<R, ? extends Functor<R, CoF>, CoP>> PSFT apply(
                        PAFB pafb) {
                    return composed.apply(pafb);
                }
            };
        }
    }
}

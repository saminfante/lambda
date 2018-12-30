package testsupport;

import com.jnape.palatable.lambda.functions.Fn1;
import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.Profunctor;
import com.jnape.palatable.lambda.functor.builtin.Const;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.optics.Lens;
import com.jnape.palatable.lambda.optics.LensLike;

import java.util.Objects;
import java.util.function.Function;

public final class EqualityAwareLens<S, T, A, B> implements Lens<S, T, A, B> {
    private final S                s;
    private final Lens<S, T, A, B> lens;

    public EqualityAwareLens(S s, Lens<S, T, A, B> lens) {
        this.s = s;
        this.lens = lens;
    }

    @Override
    public <CoP extends Fn1, CoF extends Functor, PAFB extends Profunctor<A, ? extends Functor<B, CoF>, CoP>, PSFT extends Profunctor<S, ? extends Functor<T, CoF>, CoP>> PSFT apply(
            PAFB pafb) {
        return lens.apply(pafb);
    }

    @Override
    public <U> EqualityAwareLens<S, U, A, B> flatMap(
            Function<? super T, ? extends Monad<U, LensLike<S, ?, A, B, Lens>>> f) {
        return new EqualityAwareLens<>(s, lens.flatMap(f));
    }

    @Override
    public <U> EqualityAwareLens<S, U, A, B> fmap(Function<? super T, ? extends U> fn) {
        return new EqualityAwareLens<>(s, lens.fmap(fn));
    }

    @Override
    public <U> EqualityAwareLens<S, U, A, B> pure(U u) {
        return new EqualityAwareLens<>(s, lens.pure(u));
    }

    @Override
    public <U> EqualityAwareLens<S, U, A, B> zip(
            Applicative<Function<? super T, ? extends U>, LensLike<S, ?, A, B, Lens>> appFn) {
        return new EqualityAwareLens<>(s, lens.zip(appFn));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        return other instanceof Lens
                && Objects.equals(((Lens<S, T, A, B>) other).<Const<A, ?>, Const<A, T>, Const<A, B>>apply(Const::new, s).runConst(),
                                  this.<Const<A, ?>, Const<A, T>, Const<A, B>>apply(Const::new, s).runConst());
    }
}

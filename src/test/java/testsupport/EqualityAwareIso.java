package testsupport;

import com.jnape.palatable.lambda.functor.Applicative;
import com.jnape.palatable.lambda.functor.Functor;
import com.jnape.palatable.lambda.functor.Profunctor;
import com.jnape.palatable.lambda.monad.Monad;
import com.jnape.palatable.lambda.optics.Iso;

import java.util.Objects;
import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.builtin.fn2.Both.both;
import static com.jnape.palatable.lambda.optics.functions.View.view;

public final class EqualityAwareIso<S, T, A, B> implements Iso<S, T, A, B> {
    private final S               s;
    private final B               b;
    private final Iso<S, T, A, B> iso;

    public EqualityAwareIso(S s, B b, Iso<S, T, A, B> iso) {
        this.s = s;
        this.b = b;
        this.iso = iso;
    }

    @Override
    public <CoP extends Profunctor, CoF extends Functor, PAFB extends Profunctor<A, ? extends Functor<B, CoF>, CoP>, PSFT extends Profunctor<S, ? extends Functor<T, CoF>, CoP>> PSFT apply(
            PAFB pafb) {
        return iso.apply(pafb);
    }

    @Override
    public <U> EqualityAwareIso<S, U, A, B> fmap(Function<? super T, ? extends U> fn) {
        return new EqualityAwareIso<>(s, b, iso.fmap(fn));
    }

    @Override
    public <U> EqualityAwareIso<S, U, A, B> pure(U u) {
        return new EqualityAwareIso<>(s, b, iso.pure(u));
    }

    @Override
    public <U> EqualityAwareIso<S, U, A, B> zip(Applicative<Function<? super T, ? extends U>, Iso<S, ?, A, B>> appFn) {
        return new EqualityAwareIso<>(s, b, iso.zip(appFn));
    }

    @Override
    public <U> EqualityAwareIso<S, U, A, B> flatMap(Function<? super T, ? extends Monad<U, Iso<S, ?, A, B>>> fn) {
        return new EqualityAwareIso<>(s, b, iso.flatMap(fn));
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean equals(Object other) {
        if (other instanceof EqualityAwareIso) {
            Iso<S, T, A, B> that = (EqualityAwareIso<S, T, A, B>) other;
            Boolean sameForward = both(view(this), view(that)).apply(s).into(Objects::equals);
            Boolean sameReverse = both(view(this.mirror()), view(that.mirror())).apply(b).into(Objects::equals);
            return sameForward && sameReverse;
        }
        return false;
    }
}

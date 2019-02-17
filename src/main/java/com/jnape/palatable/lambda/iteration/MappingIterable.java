package com.jnape.palatable.lambda.iteration;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

import static com.jnape.palatable.lambda.functions.builtin.fn3.FoldLeft.foldLeft;
import static java.util.Collections.singletonList;

public final class MappingIterable<A, B> implements Iterable<B> {
    private final Iterable<A>          as;
    private final List<Function<?, ?>> mappers;

    public MappingIterable(Function<? super A, ? extends B> fn, Iterable<A> as) {
        List<Function<?, ?>> mappers = new ArrayList<>(singletonList(fn));
        while (as instanceof MappingIterable) {
            MappingIterable<A, ?> nested = (MappingIterable<A, ?>) as;
            as = nested.as;
            mappers.addAll(0, nested.mappers);
        }
        this.as = as;
        this.mappers = mappers;
    }

    @Override
    @SuppressWarnings("unchecked")
    public Iterator<B> iterator() {
        Function<A, B> fnComposedOnTheHeap = o -> (B) foldLeft((x, fn) -> ((Function<Object, Object>) fn).apply(x),
                                                               (Object) o,
                                                               mappers);
        return new MappingIterator<>(fnComposedOnTheHeap, as.iterator());
    }
}

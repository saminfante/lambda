package com.jnape.palatable.lambda.functor;

import com.jnape.palatable.lambda.adt.choice.Choice2;

import java.util.function.Function;

public interface Cocartesian<A, B, Cocart extends Cocartesian> extends Profunctor<A, B, Cocart> {

    //todo: rename, as this isn't costrength at all
    <Z> Cocartesian<Choice2<Z, A>, Choice2<Z, B>, Cocart> costrengthen();

    @Override
    <Z, C> Cocartesian<Z, C, Cocart> diMap(Function<? super Z, ? extends A> lFn, Function<? super B, ? extends C> rFn);

    @Override
    default <Z> Cocartesian<Z, B, Cocart> diMapL(Function<? super Z, ? extends A> fn) {
        return (Cocartesian<Z, B, Cocart>) Profunctor.super.<Z>diMapL(fn);
    }

    @Override
    default <C> Cocartesian<A, C, Cocart> diMapR(Function<? super B, ? extends C> fn) {
        return (Cocartesian<A, C, Cocart>) Profunctor.super.<C>diMapR(fn);
    }

    @Override
    default <Z> Cocartesian<Z, B, Cocart> contraMap(Function<? super Z, ? extends A> fn) {
        return (Cocartesian<Z, B, Cocart>) Profunctor.super.<Z>contraMap(fn);
    }
}

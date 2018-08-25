package com.jnape.palatable.lambda.functor;

public interface HigherKindedType<HKT extends HigherKindedType> {

    /**
     * Convenience method for coercing this hk instance into another concrete type. Unsafe.
     *
     * @param <Concrete> the concrete instance to coerce this hk instance to
     * @return the coerced hk type
     */
    @SuppressWarnings("unchecked")
    default <Concrete extends HigherKindedType<? extends HKT>> Concrete downcast() {
        return (Concrete) this;
    }
}

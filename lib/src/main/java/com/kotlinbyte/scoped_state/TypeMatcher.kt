package com.kotlinbyte.scoped_state

class TypeMatcher<P, out D : P> private constructor(private val clazz: Class<D>) {
    private val predicates: (P) -> Boolean = { clazz.isInstance(it) }


    fun matches(value: P) = predicates(value)


    override fun hashCode(): Int {
        return clazz.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        return (hashCode() == other.hashCode())
    }

    companion object {
        fun <P, D : P> create(clazz: Class<D>): TypeMatcher<P, D> = TypeMatcher(clazz)
        inline fun <P, reified D : P> create(): TypeMatcher<P, D> = create(D::class.java)
    }
}
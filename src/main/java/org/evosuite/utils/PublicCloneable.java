package org.evosuite.utils;

public interface PublicCloneable<T> extends Cloneable {
    /**
     * <p>clone</p>
     *
     * @return a T object.
     */
    T clone();
}
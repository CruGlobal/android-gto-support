package org.ccci.gto.android.common.db;

@FunctionalInterface
public interface Closure<T, X extends Throwable> {
    T run() throws X;
}

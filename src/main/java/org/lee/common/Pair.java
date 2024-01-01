package org.lee.common;

public class Pair<F, S> {

    final public  F first;
    final public  S second;

    Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    public F getFirst() {
        return first;
    }

    public S getSecond() {
        return second;
    }

    public static<F,S>  Pair<F,S> of(F f, S s){
        return new Pair<>(f,s);
    }
}

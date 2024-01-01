package org.lee.lab1;

import java.io.Serializable;

public class MinResult<T> implements Serializable {
    public static final long serialVersionUID = 1L;
    private T mid;

    public T getMid() {
        return mid;
    }

    public void setMid(T mid) {
        this.mid = mid;
    }
}

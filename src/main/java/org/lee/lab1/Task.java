package org.lee.lab1;

import java.io.Serializable;
import java.util.List;
import java.util.function.Function;

public class Task<RESULT,ARG> implements Serializable {

    static final long serialVersionUID = 1L;

    List<ARG> args;
    Function1<List<ARG>,RESULT> map;

    public Task(List<ARG> args, Function1<List<ARG>, RESULT> map) {
        this.args = args;
        this.map = map;
    }

    public RESULT run(){
        return map.apply(args);
    }

    @Override
    public String toString() {
        return "Task{" +
                "args=" + args +
                ", map=" + map +
                '}';
    }
}

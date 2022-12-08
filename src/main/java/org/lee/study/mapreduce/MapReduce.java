package org.lee.study.mapreduce;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public abstract class MapReduce<IN,OUT> {

    protected MapReduce downStream;
    private MapReduce upStream;
    private Iterator<?> iterator;

    public MapReduce(MapReduce upStream) {
        this.upStream = upStream;
        this.iterator    = upStream.iterator;
    }
    public MapReduce(Iterator<?> iterator) {
        this.iterator = iterator;
    }




    public <O> MapReduce<OUT, O> map(Function<OUT, O> map) {
        return new MapReduce<OUT, O>(this) {
            @Override
            public void consume(OUT seed) {
                O apply = map.apply(seed);
                downStream.consume(apply);
            }
        };
    }

    public MapReduce<IN,IN> filter(Predicate<IN> predicate) {
        return new MapReduce<>(this) {
            @Override
            public void consume(IN seed) {
                if (predicate.test(seed)){
                    downStream.consume(seed);
                }
            }
        };
    }
    public abstract void consume(IN seed);


    public List<IN> toList() {
        List<IN> list = new ArrayList<>();
        MapReduce<IN, List<IN>> mapReduce = new MapReduce<IN, List<IN>>(this) {
            @Override
            public void consume(IN seed) {
                list.add(seed);
            }
        };
        configDownStream(mapReduce);
        return list;
    }

    public void foreach(Consumer<IN> consumer) {
        MapReduce<IN, Void> mapReduce = new MapReduce<IN, Void>(this) {
            @Override
            public void consume(IN seed) {
                consumer.accept(seed);
            }
        };
        configDownStream(mapReduce);
    }

    public <O> void configDownStream(MapReduce<IN, O> mapReduce ){
        MapReduce pre = mapReduce.upStream;
        MapReduce cur = mapReduce;
        while (pre.upStream != null) {
            pre.downStream = cur;
            cur = pre;
            pre = pre.upStream;
        }
        while (iterator.hasNext()){
            cur.consume(iterator.next());
        }
    }

    public static <T> MapReduce<Iterable<T>, T> from(Iterable<T> iterable) {
        return new MapReduce<>(iterable.iterator()) {
            @Override
            public void consume(Iterable<T> seed) {
                downStream.consume(seed);
            }
        };
    }
}

package org.lee.store.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class   PutRequest<T> {

    private String key;
    private T value;


    public String key(){
        return key;
    }
    public T value(){
        return value;
    }
}

package org.lee.client.core;

import org.lee.store.domain.GetResult;
import org.lee.store.domain.PutResult;

public interface Operation {
    PutResult put(String key,Object data);
    String get(String key);
}

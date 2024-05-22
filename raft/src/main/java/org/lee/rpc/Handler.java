package org.lee.rpc;

public interface Handler {
    Object handle(String requestJson);
    default Object handle(Object requestJson){
        if (requestJson instanceof String ){
            return handle(requestJson.toString());
        }
        return handle(requestJson);
    }
   default Object handleObj(Object requestJson){
        return null;
   }
}

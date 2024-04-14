package org.lee.store.handler;

import org.lee.common.Context;
import org.lee.common.utils.JsonUtil;
import org.lee.election.domain.CurrentActor;
import org.lee.rpc.Handler;
import org.lee.store.core.DBServiceUseCase;
import org.lee.store.domain.PutRequest;
import org.lee.store.domain.PutResult;
import org.lee.store.service.DBServiceInConcurrentHashMap;

public class DbPutDataHandler implements Handler {
    private final DBServiceUseCase dbService = DBServiceInConcurrentHashMap.getInstance();
    private final Context context;

    public DbPutDataHandler(Context context) {
        this.context = context;
    }

    @Override
    public Object handle(String requestJson) {
        if (CurrentActor.FOLLOWER.equals(context.getCurrentActor())){
            return PutResult.redirect(context.getMaster());
        }
        PutRequest putRequest = JsonUtil.fromJson(requestJson, PutRequest.class);
        context.getLogSyncer().sync(requestJson);
        boolean set = dbService.set(putRequest.key(), putRequest.value());
        return PutResult.success(set);
    }
}

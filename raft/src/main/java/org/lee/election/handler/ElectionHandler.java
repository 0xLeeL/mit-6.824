package org.lee.election.handler;

import org.lee.common.Context;
import org.lee.common.utils.JsonUtil;
import org.lee.election.domain.Propose;
import org.lee.election.domain.ProposeResult;
import org.lee.rpc.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElectionHandler implements Handler {
    private final Logger log = LoggerFactory.getLogger(ElectionHandler.class);
    private final Context context;

    public ElectionHandler(Context context) {
        this.context = context;
    }

    @Override
    public ProposeResult handle(String requestJson) {
        Propose propose = JsonUtil.fromJson(requestJson, Propose.class);
        log.info("current epoch is {}, receive:{}", context.getEpoch(), propose);
        if (accept(propose)) {
            context.setAcceptedEpoch(propose.epoch());
            return ProposeResult.acceptPropose();
        }
        return ProposeResult.refusePropose();
    }

    public boolean accept(Propose propose){
        return propose.epoch() > context.getAcceptedEpoch() && propose.epoch() > context.getEpoch();
    }
}

package org.lee.election;

import org.lee.common.Global;
import org.lee.common.utils.JsonUtil;
import org.lee.election.domain.Propose;
import org.lee.election.domain.ProposeResult;
import org.lee.rpc.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ElectionHandler implements Handler {
    private final Logger log = LoggerFactory.getLogger(ElectionHandler.class);
    private final Global global;

    public ElectionHandler(Global global) {
        this.global = global;
    }

    @Override
    public ProposeResult handle(String requestJson) {
        Propose propose = JsonUtil.fromJson(requestJson, Propose.class);
        log.info("current epoch is {}, receive:{}", global.getEpoch(), propose);
        if (accept(propose)) {
            global.setAcceptedEpoch(propose.epoch());
            return ProposeResult.acceptPropose();
        }
        return ProposeResult.refusePropose();
    }

    public boolean accept(Propose propose){
        return propose.epoch() > global.getAcceptedEpoch() && propose.epoch() > global.getEpoch();
    }
}

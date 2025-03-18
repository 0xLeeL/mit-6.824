package org.lee.study.server.node;

import org.lee.study.common.Constant;
import org.lee.study.common.JsonUtil;
import org.lee.study.server.node.domain.Request;
import org.lee.study.server.node.log.LogEntry;
import org.lee.study.server.node.log.LogHandler;

public class NodeDispatcher {
    private final LogHandler logHandler = new LogHandler();

    public void dispatch(Request request) {

        String method = request.getPath();
        switch (method) {
            case Constant.APPEND_LOG:
                LogEntry logEntry = JsonUtil.fromJson(request.getRequest(), LogEntry.class);
                logHandler.handleLog(logEntry);
                break;
            default:
                break;
        }
        request.getResponse().back("response");
    }
}

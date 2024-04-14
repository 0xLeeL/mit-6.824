package org.lee.store.domain;

import org.lee.common.utils.JsonUtil;
import org.lee.election.Endpoint;

import static org.lee.common.Constant.REDIRECT;
import static org.lee.common.Constant.SUCCESS;

public record PutResult(
        String putResult,
        Object data,
        String message
) {

    public static PutResult success(Object data) {
        return new PutResult(SUCCESS, data, SUCCESS);
    }

    public static PutResult redirect(Endpoint data) {
        return new PutResult(REDIRECT, data, REDIRECT);
    }

    public boolean success() {
        return "success".equalsIgnoreCase(putResult);
    }

    public boolean isRedirect() {
        return REDIRECT.equals(putResult);
    }



    public Endpoint redirect() {
        if (data instanceof Endpoint e) {
            return e;
        }
        return JsonUtil.fromJson(JsonUtil.toJson(data),Endpoint.class);
    }
}

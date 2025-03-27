package org.lee.common.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Resp {
    private String msg;
    private Object data;
    private Integer status;

    public boolean ok(){
        return 200 == getStatus();
    }
}

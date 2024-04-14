package org.lee.client.domain;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Config {

    private String host;
    private Integer port;
}

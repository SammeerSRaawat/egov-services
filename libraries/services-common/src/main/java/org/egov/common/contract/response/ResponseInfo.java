package org.egov.common.contract.response;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResponseInfo {

    private String apiId;

    private String ver;

    private String ts;

    private String resMsgId;

    private String msgId;

    private String status;
}
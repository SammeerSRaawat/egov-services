package org.egov.tradelicense.domain.model;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class NoticeDocument {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("licenseId")
    private Long licenseId;

    @JsonProperty("tenantId")
    private String tenantId;

    @NotNull
    @JsonProperty("documentName")
    private String documentName;

    @NotNull
    @JsonProperty("fileStoreId")
    private String fileStoreId;

    @JsonProperty("auditDetails")
    private AuditDetails auditDetails;

}
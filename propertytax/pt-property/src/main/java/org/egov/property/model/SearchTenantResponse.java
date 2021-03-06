package org.egov.property.model;

import java.util.List;

import org.egov.models.ResponseInfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class SearchTenantResponse {

    private ResponseInfo responseInfo;
    private List<Tenant> tenant;
}

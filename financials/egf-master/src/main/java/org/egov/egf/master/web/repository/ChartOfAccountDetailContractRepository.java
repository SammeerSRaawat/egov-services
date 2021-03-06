package org.egov.egf.master.web.repository;

import org.egov.egf.master.web.contract.ChartOfAccountDetailContract;
import org.egov.egf.master.web.requests.ChartOfAccountDetailResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ChartOfAccountDetailContractRepository {
	
	private RestTemplate restTemplate;
	private String hostUrl;
	public static final String SEARCH_URL = "/egf-masters/chartofaccountdetails/_search?";

	public ChartOfAccountDetailContractRepository(@Value("${egf.master.host.url}") String hostUrl,
			RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
		this.hostUrl = hostUrl;
	}

	public ChartOfAccountDetailContract findById(ChartOfAccountDetailContract chartOfAccountDetailContract) {

		String url = String.format("%s%s", hostUrl, SEARCH_URL);
		StringBuffer content = new StringBuffer();
		if (chartOfAccountDetailContract.getId() != null) {
			content.append("id=" + chartOfAccountDetailContract.getId());
		}

		if (chartOfAccountDetailContract.getTenantId() != null) {
			content.append("&tenantId=" + chartOfAccountDetailContract.getTenantId());
		}
		url = url + content.toString();
		ChartOfAccountDetailResponse result = restTemplate.postForObject(url, null, ChartOfAccountDetailResponse.class);

		if (result.getChartOfAccountDetails() != null && result.getChartOfAccountDetails().size() == 1) {
			return result.getChartOfAccountDetails().get(0);
		} else {
			return null;
		}

	}
}
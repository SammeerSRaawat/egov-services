package org.egov.tradelicense.notification.web.repository;

import java.util.Date;

import org.egov.tl.commons.web.requests.RequestInfoWrapper;
import org.egov.tl.commons.web.response.LicenseStatusResponse;
import org.egov.tradelicense.notification.config.PropertiesManager;
import org.egov.tradelicense.notification.web.requests.TlMasterRequestInfo;
import org.egov.tradelicense.notification.web.requests.TlMasterRequestInfoWrapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class StatusRepository {

	private RestTemplate restTemplate;

	@Autowired
	PropertiesManager propertiesManager;

	public StatusRepository(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;

	}

	public LicenseStatusResponse findByIds(String tenantId, String ids, RequestInfoWrapper requestInfoWrapper) {

		String hostUrl = propertiesManager.getTradeLicenseMasterServiceHostName()
				+ propertiesManager.getTradeLicenseMasterServiceBasePath();
		String searchUrl = propertiesManager.getStatusServiceSearchPath();
		String url = String.format("%s%s", hostUrl, searchUrl);
		StringBuffer content = new StringBuffer();
		if (ids != null) {
			content.append("ids=" + ids);
		}

		if (tenantId != null) {
			content.append("&tenantId=" + tenantId);
		}
		url = url + content.toString();
		TlMasterRequestInfoWrapper tlMasterRequestInfoWrapper = getRequestInfoWrapper(requestInfoWrapper);
		LicenseStatusResponse licenseStatusResponse = null;

		try {

			licenseStatusResponse = restTemplate.postForObject(url, tlMasterRequestInfoWrapper,
					LicenseStatusResponse.class);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		if (licenseStatusResponse != null && licenseStatusResponse.getLicenseStatuses() != null
				&& licenseStatusResponse.getLicenseStatuses().size() > 0) {
			return licenseStatusResponse;
		} else {
			return null;
		}

	}

	public TlMasterRequestInfoWrapper getRequestInfoWrapper(RequestInfoWrapper requestInfoWrapper) {

		TlMasterRequestInfoWrapper tlMasterRequestInfoWrapper = new TlMasterRequestInfoWrapper();
		TlMasterRequestInfo tlMasterRequestInfo = new TlMasterRequestInfo();
		ModelMapper mapper = new ModelMapper();
		mapper.map(requestInfoWrapper.getRequestInfo(), tlMasterRequestInfo);
		tlMasterRequestInfo.setTs(new Date().getTime());
		tlMasterRequestInfoWrapper.setRequestInfo(tlMasterRequestInfo);

		return tlMasterRequestInfoWrapper;
	}

}
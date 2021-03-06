package org.egov.property.repository;

import java.net.URI;

import org.egov.models.DemandResponse;
import org.egov.models.DemandUpdateMisRequest;
import org.egov.models.RequestInfoWrapper;
import org.egov.property.config.PropertiesManager;
import org.egov.property.exception.ValidationUrlNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

/**
 * Description: This class will call demand service api's
 *
 * @author WTC
 *
 */

@Slf4j
@Repository
public class DemandRepository {

    @Autowired
    PropertiesManager propertiesManager;

    /**
     * Description :This method will get all demands based on upic number and tenantId
     *
     * @param upicNo
     * @param tenantId
     * @param requestInfo
     * @return demandResponse
     * @throws Exception
     */

    public DemandResponse getDemands(final String upicNo, final String tenantId, final RequestInfoWrapper requestInfo)
            throws Exception {
        final RestTemplate restTemplate = new RestTemplate();
        DemandResponse resonse = null;
        final StringBuffer demandUrl = new StringBuffer();
        demandUrl.append(propertiesManager.getBillingServiceHostname());
        demandUrl.append(propertiesManager.getBillingServiceSearchdemand());
        final MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<String, String>();
        requestMap.add("tenantId", tenantId);
        requestMap.add("consumerCode", upicNo);
        requestMap.add("businessService", propertiesManager.getBusinessService());

        final URI uri = UriComponentsBuilder.fromHttpUrl(demandUrl.toString()).queryParams(requestMap).build().encode()
                .toUri();
        log.info("Get demand url is " + uri + " demand request is : " + requestInfo);
        try {
            final String demandResponse = restTemplate.postForObject(uri, requestInfo, String.class);
            log.info("Get demand response is :" + demandResponse);
            if (demandResponse != null && demandResponse.contains("Demands")) {
                final ObjectMapper objectMapper = new ObjectMapper();
                resonse = objectMapper.readValue(demandResponse, DemandResponse.class);
            }
            return resonse;
        } catch (final HttpClientErrorException exception) {
            throw new ValidationUrlNotFoundException(propertiesManager.getInvalidDemandValidation(),
                    exception.getMessage(), requestInfo.getRequestInfo());
        }
    }

    public DemandResponse updateMisDemands(final DemandUpdateMisRequest demandRequest) throws Exception {
        final RestTemplate restTemplate = new RestTemplate();
        final ObjectMapper objectMapper = new ObjectMapper();
        DemandResponse resonse = null;
        final StringBuffer demandUrl = new StringBuffer();
        demandUrl.append(propertiesManager.getBillingServiceHostname());
        demandUrl.append(propertiesManager.getBillingServiceUpdateMisPath());
        final MultiValueMap<String, String> requestMap = new LinkedMultiValueMap<String, String>();
        requestMap.add("tenantId", demandRequest.getTenantId());
        requestMap.add("consumerCode", demandRequest.getConsumerCode());
        final String demandId = demandRequest.getId().toString().substring(1, demandRequest.getId().toString().length() - 1);
        requestMap.add("id", demandId);
        requestMap.add("demandRequest", objectMapper.writeValueAsString(demandRequest));

        final URI uri = UriComponentsBuilder.fromHttpUrl(demandUrl.toString()).queryParams(requestMap).build().encode()
                .toUri();
        final RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(demandRequest.getRequestInfo());
        try {
            final String demandResponse = restTemplate.postForObject(uri, requestInfoWrapper, String.class);
            log.info("Update mis demand response is :" + demandResponse);
            if (demandResponse != null && demandResponse.contains("Demands"))
                resonse = objectMapper.readValue(demandResponse, DemandResponse.class);
            return resonse;
        } catch (final HttpClientErrorException exception) {
            throw new ValidationUrlNotFoundException(propertiesManager.getInvalidDemandValidation(),
                    exception.getMessage(), demandRequest.getRequestInfo());
        }
    }
}

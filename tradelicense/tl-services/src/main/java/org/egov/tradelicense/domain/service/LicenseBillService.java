/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 * accountability and the service delivery of the government  organizations.
 *
 *  Copyright (C) 2016  eGovernments Foundation
 *
 *  The updated version of eGov suite of products as by eGovernments Foundation
 *  is available at http://www.egovernments.org
 *
 *  This program is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation, either version 3 of the License, or
 *  any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program. If not, see http://www.gnu.org/licenses/ or
 *  http://www.gnu.org/licenses/gpl.html .
 *
 *  In addition to the terms of the GPL license to be adhered to in using this
 *  program, the following additional terms are to be complied with:
 *
 *      1) All versions of this program, verbatim or modified must carry this
 *         Legal Notice.
 *
 *      2) Any misrepresentation of the origin of the material is prohibited. It
 *         is required that all modified versions of this material be marked in
 *         reasonable ways as different from the original version.
 *
 *      3) This license does not grant any rights to any user of the program
 *         with regards to rights under trademark law for use of the trade names
 *         or trademarks of eGovernments Foundation.
 *
 *  In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */

package org.egov.tradelicense.domain.service;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import org.egov.tl.commons.web.contract.RequestInfo;
import org.egov.tl.commons.web.requests.RequestInfoWrapper;
import org.egov.tradelicense.common.config.PropertiesManager;
import org.egov.tradelicense.domain.model.LicenseFeeDetail;
import org.egov.tradelicense.domain.model.TradeLicense;
import org.egov.tradelicense.web.contract.Demand;
import org.egov.tradelicense.web.contract.DemandDetail;
import org.egov.tradelicense.web.contract.DemandRequest;
import org.egov.tradelicense.web.contract.DemandResponse;
import org.egov.tradelicense.web.contract.FinancialYearContract;
import org.egov.tradelicense.web.contract.Owner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;;
@Service
public class LicenseBillService {

    @Autowired
    private PropertiesManager propertiesManager;
    
    @Autowired
    private FinancialYearService financialYearService;
    
    @Autowired
    private RestTemplate restTemplate;
    
    public DemandResponse createBill(final TradeLicense tradeLicense, final RequestInfo requestInfo) throws ParseException {

        List<Demand> demands = prepareBill(tradeLicense, requestInfo);
        DemandResponse demandRes = createBill(demands, requestInfo);
        if (demandRes != null && demandRes.getDemands() != null && !demandRes.getDemands().isEmpty())
            tradeLicense.setBillId(demandRes.getDemands().get(0).getId());
        return demandRes;
    }

    private List<Demand> prepareBill(final TradeLicense tradeLicense, final RequestInfo requestInfo) throws ParseException {
        List<Demand> demandList = new ArrayList<>();
        Date fromDate;
        Date toDate;
        Calendar date = Calendar.getInstance();
        Demand demand;
        DemandDetail demandDetail;
        String tenantId = tradeLicense.getTenantId();
        String tradeType = tradeLicense.getTradeType().toString();
        List<DemandDetail> demandDetailsList;
        RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
        requestInfoWrapper.setRequestInfo(requestInfo);
        demand = new Demand();
        demand.setTenantId(tenantId);
        demand.setBusinessService(propertiesManager.getBillBusinessService());
        demand.setConsumerType(tradeType);
        demand.setConsumerCode(tradeLicense.getApplication().getApplicationNumber());
        demand.setMinimumAmountPayable(BigDecimal.ONE);
        demandDetailsList = new ArrayList<>();
        demandDetail = new DemandDetail();
        demandDetail.setTaxHeadMasterCode(propertiesManager.getTaxHeadMasterCode());
        demandDetail.setTaxAmount(BigDecimal.valueOf(tradeLicense.getApplication().getLicenseFee()));
        demandDetail.setTenantId(tenantId);
        demandDetailsList.add(demandDetail);
        demand.setDemandDetails(demandDetailsList);
        FinancialYearContract currentFYResponse = financialYearService
                .findFinancialYearIdByDate(tenantId, tradeLicense.getTradeCommencementDate(), requestInfoWrapper);
        
        // :TODO setting UTC is not advised, since Billing service is tweaking we are forced to do this
        SimpleDateFormat dbDateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        dbDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        fromDate = dbDateFormat.parse(sdf.format(currentFYResponse.getStartingDate()));
        
        date.setTimeInMillis(dbDateFormat.parse(sdf.format(currentFYResponse.getEndingDate())).getTime());
        date.add(Calendar.YEAR, tradeLicense.getValidityYears().intValue() - 1);
        toDate = date.getTime();
        demand.setTaxPeriodFrom(fromDate.getTime());
        demand.setTaxPeriodTo(toDate.getTime());

        Owner owner = new Owner();
        owner.setId(1L);
        demand.setOwner(owner);
        demandList.add(demand);
        return demandList;
    }
    
    private DemandResponse createBill(final List<Demand> demands, final RequestInfo requestInfo) {
        final DemandRequest demandRequest = new DemandRequest();
        demandRequest.setRequestInfo(requestInfo);
        demandRequest.setDemands(demands);

        final String url = propertiesManager.getBillingServiceHostName() +
                propertiesManager.getBillingServiceCreatedBill();

        return restTemplate.postForObject(url, demandRequest, DemandResponse.class);
    }
}

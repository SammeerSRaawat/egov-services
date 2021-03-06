
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
package org.egov.wcms.service;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.wcms.model.MeterCost;
import org.egov.wcms.repository.MeterCostRepository;
import org.egov.wcms.web.contract.MeterCostGetRequest;
import org.egov.wcms.web.contract.MeterCostReq;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class MeterCostServiceTest {

    @Mock
    MeterCostRepository meterCostRepository;

    @InjectMocks
    MeterCostService meterCostService;

    @Test
    public void test_should_push_create_meterCostRequest_to_Queue() {
        when(meterCostRepository.pushCreateMeterCostReqToQueue(getMeterCostRequest()))
                .thenReturn(getListOfMeterCosts());
        List<MeterCost> actualMeterCosts = meterCostService.createMeterCostPushToQueue(getMeterCostRequest());
        assertTrue(actualMeterCosts.equals(getListOfMeterCosts()));
    }

    @Test
    public void test_should_push_update_meterCostRequest_to_Queue() {
        when(meterCostRepository.pushUpdateMeterCostReqToQueue(getMeterCostRequestForUpdate()))
                .thenReturn(getListOfUpdatedMeterCosts());
        List<MeterCost> actualMeterCosts = meterCostService.updateMeterCostPushToQueue(getMeterCostRequestForUpdate());
        assertTrue(actualMeterCosts.equals(getListOfUpdatedMeterCosts()));
    }

    @Test
    public void test_should_persist_meterCost_to_DB() {
        when(meterCostRepository.persistCreateMeterCost(getMeterCostRequest())).thenReturn(getMeterCostRequest());
        MeterCostReq actualMeterCostRequest = meterCostService.createMeterCost(getMeterCostRequest());
        assertTrue(actualMeterCostRequest.equals(getMeterCostRequest()));
    }

    @Test
    public void test_should_update_meterCost_in_DB() {
        when(meterCostRepository.persistUpdateMeterCost(getMeterCostRequestForUpdate()))
                .thenReturn(getMeterCostRequestForUpdate());
        MeterCostReq actualMeterCostRequest = meterCostService.updateMeterCost(getMeterCostRequestForUpdate());
        assertTrue(actualMeterCostRequest.equals(getMeterCostRequestForUpdate()));
    }

    @Test
    public void test_should_search_meterCost_as_per_Criteria() {
        when(meterCostRepository.searchMeterCostByCriteria(getMeterCostGetRequest())).thenReturn(getListOfMeterCosts());
        List<MeterCost> actualListOfMeterCosts = meterCostService.getMeterCostByCriteria(getMeterCostGetRequest());
        assertTrue(actualListOfMeterCosts.get(0).getCode().equals(getListOfMeterCosts().get(0).getCode()));
        assertTrue(actualListOfMeterCosts.get(0).getMeterMake().equals(getListOfMeterCosts().get(0).getMeterMake()));
        assertTrue(actualListOfMeterCosts.get(1).getCode().equals(getListOfMeterCosts().get(1).getCode()));
        assertTrue(actualListOfMeterCosts.get(1).getMeterMake().equals(getListOfMeterCosts().get(1).getMeterMake()));
    }

    @Test
    public void test_should_check_meterCost_exists_and_return_true_if_it_doesnot_exists_in_DB() {
        when(meterCostRepository.checkMeterMakeAlreadyExistsInDB(getMeterCost())).thenReturn(true);
        Boolean value = meterCostService.checkMeterMakeAlreadyExists(getMeterCost());
        assertTrue(value.equals(true));
    }

    @Test
    public void test_should_check_meterCost_exists_and_return_false_if_it_exists_in_DB() {
        when(meterCostRepository.checkMeterMakeAlreadyExistsInDB(getMeterCost())).thenReturn(false);
        Boolean value = meterCostService.checkMeterMakeAlreadyExists(getMeterCost());
        assertTrue(value.equals(false));
    }

    private MeterCost getMeterCost() {
        return MeterCost.builder().id(1L).code("MeterCost123").pipeSizeId(1L).meterMake("meterMakeUpdated1")
                .amount(3000.0).active(true).createdBy(1L).lastModifiedBy(1L).tenantId("default").build();
    }

    private MeterCostGetRequest getMeterCostGetRequest() {
        return MeterCostGetRequest.builder().active(true).ids(Arrays.asList(1L, 2L)).tenantId("default").sortBy("code")
                .sortOrder("desc").build();
    }

    private MeterCostReq getMeterCostRequestForUpdate() {
        User userInfo = User.builder().id(1L).build();
        RequestInfo requestInfo = RequestInfo.builder().apiId("org.egov.wcms").ver("1.0").action("POST")
                .did("4354648646").key("xyz").msgId("654654").authToken("345678f").userInfo(userInfo).build();
        MeterCost meterCost1 = MeterCost.builder().id(1L).code("MeterCost123").pipeSizeId(1L)
                .meterMake("meterMakeUpdated1").amount(3000.0).active(true).createdBy(1L).lastModifiedBy(1L)
                .tenantId("default").build();
        MeterCost meterCost2 = MeterCost.builder().id(2L).code("MeterCost234").pipeSizeId(2L)
                .meterMake("meterMakeUpdated2").amount(4000.0).active(true).createdBy(1L).lastModifiedBy(1L)
                .tenantId("default").build();
        return MeterCostReq.builder().requestInfo(requestInfo).meterCost(Arrays.asList(meterCost1, meterCost2)).build();

    }

    private List<MeterCost> getListOfUpdatedMeterCosts() {
        MeterCost meterCost1 = MeterCost.builder().id(1L).code("MeterCost123").pipeSizeId(1L)
                .meterMake("meterMakeUpdated1").amount(3000.0).active(true).createdBy(1L).lastModifiedBy(1L)
                .tenantId("default").build();
        MeterCost meterCost2 = MeterCost.builder().id(2L).code("MeterCost234").pipeSizeId(2L)
                .meterMake("meterMakeUpdated2").amount(4000.0).active(true).createdBy(1L).lastModifiedBy(1L)
                .tenantId("default").build();
        return Arrays.asList(meterCost1, meterCost2);
    }

    private List<MeterCost> getListOfMeterCosts() {
        MeterCost meterCost1 = MeterCost.builder().id(1L).code("MeterCost123").pipeSizeId(1L).meterMake("meterMake123")
                .amount(4000.0).active(true).createdBy(1L).lastModifiedBy(1L).tenantId("default").build();
        MeterCost meterCost2 = MeterCost.builder().id(2L).code("MeterCost234").pipeSizeId(2L).meterMake("meterMake234")
                .amount(5000.0).active(true).createdBy(1L).lastModifiedBy(1L).tenantId("default").build();
        return Arrays.asList(meterCost1, meterCost2);
    }

    private MeterCostReq getMeterCostRequest() {
        User userInfo = User.builder().id(1L).build();
        RequestInfo requestInfo = RequestInfo.builder().apiId("org.egov.wcms").ver("1.0").action("POST")
                .did("4354648646").key("xyz").msgId("654654").authToken("345678f").userInfo(userInfo).build();
        MeterCost meterCost1 = MeterCost.builder().id(1L).code("MeterCost123").pipeSizeId(1L).meterMake("meterMake123")
                .amount(4000.0).active(true).createdBy(1L).lastModifiedBy(1L).tenantId("default").build();
        MeterCost meterCost2 = MeterCost.builder().id(2L).code("MeterCost234").pipeSizeId(2L).meterMake("meterMake234")
                .amount(5000.0).active(true).createdBy(1L).lastModifiedBy(1L).tenantId("default").build();
        return MeterCostReq.builder().requestInfo(requestInfo).meterCost(Arrays.asList(meterCost1, meterCost2)).build();

    }

}

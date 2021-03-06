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
package org.egov.wcms.repository;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import org.egov.common.contract.request.RequestInfo;
import org.egov.common.contract.request.User;
import org.egov.wcms.model.PropertyTypePipeSize;
import org.egov.wcms.repository.builder.PropertyPipeSizeQueryBuilder;
import org.egov.wcms.repository.rowmapper.PropertyPipeSizeRowMapper;
import org.egov.wcms.service.PropertyTypePipeSizeService;
import org.egov.wcms.service.RestWaterExternalMasterService;
import org.egov.wcms.web.contract.PropertyTypePipeSizeGetRequest;
import org.egov.wcms.web.contract.PropertyTypePipeSizeRequest;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@WebAppConfiguration
public class PropertyPipeSizeRepositoryTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @Mock
    private PropertyPipeSizeQueryBuilder propertyPipeSizeQueryBuilder;

    @InjectMocks
    private PropertyPipeSizeRepository propertyPipeSizeRepository;

    @Mock
    private PropertyPipeSizeRowMapper propertyPipeSizeRowMapper;

    @MockBean
    private PropertyTypePipeSizeService propertyTypePipeSizeService;

    @Mock
    private RestWaterExternalMasterService restExternalMasterService;
    
    @Mock
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Test
    public void test_Should_Search_PropertyPipeSize() throws Exception {
        final List<Object> preparedStatementValues = new ArrayList<>();
        final List<PropertyTypePipeSize> propertyPipeSizes = new ArrayList<>();
        final PropertyTypePipeSize propertyPipeSize = getPropertyPipeSize();
        propertyPipeSizes.add(propertyPipeSize);

        // when(propertyPipeSizeQueryBuilder.getQuery(any(PropertyTypePipeSizeGetRequest.class), any(List.class)))
        // .thenReturn("");
        final PropertyTypePipeSizeGetRequest propertyPipeSizeRequest = Mockito.mock(PropertyTypePipeSizeGetRequest.class);
        when(jdbcTemplate.query("query", preparedStatementValues.toArray(), propertyPipeSizeRowMapper))
                .thenReturn(propertyPipeSizes);

        assertNotNull(propertyPipeSizeRepository.findForCriteria(propertyPipeSizeRequest));

    }

    @Test
    public void test_Inavalid_Find_PropertyPipeSize() throws Exception {
        final List<Object> preparedStatementValues = new ArrayList<>();
        final List<PropertyTypePipeSize> propertyPipeSizes = new ArrayList<>();
        final PropertyTypePipeSize propertyPipeSize = getPropertyPipeSize();
        propertyPipeSizes.add(propertyPipeSize);
        final PropertyTypePipeSizeGetRequest propertyPipeSizeRequest = Mockito.mock(PropertyTypePipeSizeGetRequest.class);
        when(propertyPipeSizeQueryBuilder.getQuery(propertyPipeSizeRequest, preparedStatementValues)).thenReturn(null);
        when(jdbcTemplate.query("query", preparedStatementValues.toArray(), propertyPipeSizeRowMapper))
                .thenReturn(propertyPipeSizes);

        assertTrue(!propertyPipeSizes.equals(propertyPipeSizeRepository.findForCriteria(propertyPipeSizeRequest)));
    }

    @Test
    public void test_Should_Create_PropertyPipeSize() {

        final PropertyTypePipeSizeRequest propertyPipeSizeRequest = new PropertyTypePipeSizeRequest();
        final RequestInfo requestInfo = new RequestInfo();
        final User user = new User();
        user.setId(1l);
        requestInfo.setUserInfo(user);
        propertyPipeSizeRequest.setRequestInfo(requestInfo);
        final List<PropertyTypePipeSize> propertyPipeSizeList = new ArrayList<>();
        propertyPipeSizeList.add(getPropertyPipeSize());
        propertyPipeSizeRequest.setPropertyTypePipeSize(propertyPipeSizeList);

        when(jdbcTemplate.update(any(String.class), any(Object[].class))).thenReturn(1);
        assertTrue(propertyPipeSizeRequest
                .equals(propertyPipeSizeRepository.persistCreatePropertyPipeSize(propertyPipeSizeRequest)));
    }

    @Test
    public void test_Should_Update_PropertyPipeSize() {

        final PropertyTypePipeSizeRequest propertyPipeSizeRequest = new PropertyTypePipeSizeRequest();
        final RequestInfo requestInfo = new RequestInfo();
        final User user = new User();
        user.setId(1l);
        requestInfo.setUserInfo(user);
        propertyPipeSizeRequest.setRequestInfo(requestInfo);
        final List<PropertyTypePipeSize> propertyPipeSizeList = new ArrayList<>();
        propertyPipeSizeList.add(getPropertyPipeSize());
        propertyPipeSizeRequest.setPropertyTypePipeSize(propertyPipeSizeList);

        when(jdbcTemplate.update(any(String.class), any(Object[].class))).thenReturn(1);
        assertTrue(propertyPipeSizeRequest
                .equals(propertyPipeSizeRepository.persistUpdatePropertyPipeSize(propertyPipeSizeRequest)));
    }

    private PropertyTypePipeSize getPropertyPipeSize() {
        final PropertyTypePipeSize propertyPipeSize = new PropertyTypePipeSize();
        propertyPipeSize.setTenantId("default");
        propertyPipeSize.setId(2l);
        propertyPipeSize.setCode("2");
        propertyPipeSize.setPipeSize(2d);
        propertyPipeSize.setPropertyTypeName("property type");
        propertyPipeSize.setActive(true);
        return propertyPipeSize;
    }

}

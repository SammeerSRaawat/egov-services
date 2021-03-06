/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.workflow.persistence.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.workflow.domain.model.Attribute;
import org.egov.workflow.domain.model.ServiceType;
import org.egov.workflow.domain.model.Value;
import org.egov.workflow.web.contract.BoundaryIdType;
import org.egov.workflow.web.contract.RouterType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

@Component
public class RouterRowMapper implements RowMapper<RouterType> {
	private static final Logger logger = LoggerFactory.getLogger(RouterRowMapper.class);
	public Map<String, List<Value>> attribValue = new HashMap<>();
	public Map<String, Map<String, Attribute>> serviceAttrib = new HashMap<>();
	public Map<Long, Map< String, List<ServiceType>>> serviceMap = new HashMap<>();
	public Map<Long, RouterType> routerMap = new HashMap<>();
	
	@Override
	public RouterType mapRow(final ResultSet rs, final int rowNum) throws SQLException {
		
		if(!routerMap.containsKey(rs.getLong("id"))){
			routerMap.put(rs.getLong("id"), prepareRouterType(rs));
		}
		
		if(serviceMap.containsKey(rs.getLong("id"))){
			Map<String, List<ServiceType>> innerServiceMap = serviceMap.get(rs.getLong("id"));
			if(!innerServiceMap.containsKey(rs.getString("servicecode"))){
				List<ServiceType> serviceTypeList = new ArrayList<>();
				serviceTypeList.add(prepareServiceType(rs));
				innerServiceMap.put(rs.getString("servicecode"), serviceTypeList); 
			}
		} else {
			List<ServiceType> serviceTypeList = new ArrayList<>();
			serviceTypeList.add(prepareServiceType(rs));
			Map<String, List<ServiceType>> innerServiceMap = new HashMap<>();
			innerServiceMap.put(rs.getString("servicecode"), serviceTypeList);
			serviceMap.put(rs.getLong("id"), innerServiceMap);
		}
		
		if(serviceAttrib.containsKey(rs.getString("servicecode"))){
			Map<String, Attribute> innerAttrMap = serviceAttrib.get(rs.getString("servicecode"));
			if(!innerAttrMap.containsKey(rs.getString("attributecode"))){
				innerAttrMap.put(rs.getString("attributecode"), prepareAttribute(rs));
			}
		} else {
			Map<String, Attribute> innerAttrMap = new HashMap<>();
			innerAttrMap.put(rs.getString("attributecode"), prepareAttribute(rs));
			serviceAttrib.put(rs.getString("servicecode"), innerAttrMap);
		}
		
		if(attribValue.containsKey(rs.getString("attributecode"))){
			List<Value> valueList = attribValue.get(rs.getString("attributecode"));
			valueList.add(prepareValue(rs));
		} else {
			List<Value> valueList = new ArrayList<>();
			valueList.add(prepareValue(rs));
			attribValue.put(rs.getString("attributecode"), valueList);
		}
		return null;
	}
	
	private ServiceType prepareServiceType(ResultSet rs) {
		ServiceType serviceType = new ServiceType();
		try {
			serviceType.setId(rs.getLong("complaintid"));
			serviceType.setServiceCode(rs.getString("servicecode"));
			serviceType.setServiceName(rs.getString("servicename"));
			serviceType.setDescription(rs.getString("servicedescription"));
			serviceType.setCategory(rs.getInt("category"));
		} catch (Exception e) {
			logger.error("Exception Encountered : " + e);
		}
		return serviceType;
	}
	
	private Attribute prepareAttribute(ResultSet rs) {
		Attribute attrib = new Attribute();
		try {
			attrib.setCode(rs.getString("attributecode"));
			attrib.setDescription(rs.getString("attributedescription"));
			attrib.setDatatype(rs.getString("datatype"));
			attrib.setDatatypeDescription(rs.getString("datatypedescription"));
			attrib.setRequired(rs.getBoolean("required"));
			attrib.setVariable(rs.getBoolean("variable"));
		} catch (Exception e) {
			logger.error("Exception Encountered : " + e);
		}
		return attrib;
	}
	
	private Value prepareValue(ResultSet rs) {
		Value value = new Value();
		try {
			value.setKey(rs.getString("key"));
			value.setName(rs.getString("name"));
		} catch (Exception e) {
			logger.error("Exception Encountered : " + e);
		}
		return value;
	}
	
	private RouterType prepareRouterType(ResultSet rs) {
		RouterType router = new RouterType();
		try {
			router.setId(rs.getLong("id"));
			router.setPosition(rs.getInt("position"));
			router.setTenantId(rs.getString("tenantid"));
			BoundaryIdType boundary = new BoundaryIdType();
			boundary.setBoundaryType(rs.getInt("bndryid"));
			router.setBoundary(boundary);
		} catch (Exception e) {
			logger.error("Exception Encountered : " + e);
		}
		return router;
	}

}

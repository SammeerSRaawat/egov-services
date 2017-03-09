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

package org.egov.eis.repository.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.eis.model.Assignment;
import org.egov.eis.model.HODDepartment;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.stereotype.Component;

@Component
public class AssignmentRowMapper implements ResultSetExtractor<List<Assignment>> {

	@Override
	public List<Assignment> extractData(ResultSet rs) throws SQLException, DataAccessException {
		Map<String, Assignment> assignmentMap = new HashMap<String, Assignment>();

		while (rs.next()) {
			String assignmentId = rs.getString("a_id");

			Assignment assignment = assignmentMap.get(assignmentId);

			if (assignment == null) {
				assignment = new Assignment();
				assignment.setId(rs.getLong("a_id"));
				assignment.setPosition(rs.getLong("a_positionId"));
				assignment.setFund(rs.getLong("a_fundId"));
				assignment.setFunctionary(rs.getLong("a_functionaryId"));
				assignment.setFunction(rs.getLong("a_functionId"));
				assignment.setDesignation(rs.getLong("a_designationId"));
				assignment.setDepartment(rs.getLong("a_departmentId"));
				assignment.setIsPrimary(rs.getBoolean("a_isPrimary"));
				assignment.setFromDate(rs.getDate("a_fromDate"));
				assignment.setToDate(rs.getDate("a_toDate"));
				assignment.setGrade(rs.getLong("a_gradeId"));
				assignment.setGovtOrderNumber(rs.getString("a_govtOrderNumber"));
				assignment.setDocuments(rs.getString("a_documents"));
				assignment.setCreatedBy(rs.getLong("a_createdBy"));
				assignment.setCreatedDate(rs.getDate("a_createdDate"));
				assignment.setLastModifiedBy(rs.getLong("a_lastModifiedBy"));
				assignment.setLastModifiedDate(rs.getDate("a_lastModifiedDate"));
				assignment.setTenantId(rs.getString("a_tenantId"));

				assignmentMap.put(assignmentId, assignment);
			}

			List<HODDepartment> hodDepartmentsList = assignment.getHod();
			if (hodDepartmentsList == null) {
				hodDepartmentsList = new ArrayList<HODDepartment>();
				assignment.setHod(hodDepartmentsList);
			}
			if (rs.getLong("hod_id") != 0) {
				HODDepartment hodDepartment = new HODDepartment();
				hodDepartment.setId(rs.getLong("hod_id"));
				hodDepartment.setDepartment(rs.getLong("hod_departmentId"));
				hodDepartment.setTenantId(rs.getString("a_tenantId"));
				hodDepartmentsList.add(hodDepartment);
			}
		}
		return new ArrayList<Assignment>(assignmentMap.values());
	}
}
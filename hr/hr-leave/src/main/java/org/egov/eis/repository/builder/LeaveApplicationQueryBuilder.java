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

package org.egov.eis.repository.builder;

import org.egov.common.contract.request.RequestInfo;
import org.egov.eis.config.ApplicationProperties;
import org.egov.eis.model.LeaveApplication;
import org.egov.eis.service.HRStatusService;
import org.egov.eis.web.contract.LeaveApplicationGetRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.*;

@Component
public class LeaveApplicationQueryBuilder {

    private static final Logger logger = LoggerFactory.getLogger(LeaveApplicationQueryBuilder.class);
    private static final String BASE_QUERY = "SELECT la.id AS la_id, la.applicationNumber AS la_applicationNumber,"
            + " la.employeeId AS la_employeeId, la.fromDate AS la_fromDate, la.toDate AS la_toDate,"
            + " la.compensatoryForDate AS la_compensatoryForDate, la.leaveDays AS la_leaveDays,"
            + " la.availableDays AS la_availableDays, la.halfdays AS la_halfdays, la.firstHalfleave AS la_firstHalfleave,"
            + " la.reason AS la_reason, la.status AS la_status, la.stateId AS la_stateId, la.createdBy AS la_createdBy,"
            + " la.createdDate AS la_createdDate, la.lastModifiedBy AS la_lastModifiedBy,"
            + " la.lastModifiedDate AS la_lastModifiedDate, la.tenantId AS la_tenantId,"
            + " lt.id AS lt_id, lt.name AS lt_name, lt.description AS lt_description, lt.halfdayAllowed AS lt_halfdayAllowed,"
            + " lt.payEligible AS lt_payEligible, lt.accumulative AS lt_accumulative, lt.encashable AS lt_encashable,"
            + " lt.active AS lt_active, lt.createdBy AS lt_createdBy, lt.createdDate AS lt_createdDate,"
            + " lt.lastModifiedBy AS lt_lastModifiedBy, lt.lastModifiedDate AS lt_lastModifiedDate"
            + " FROM egeis_leaveApplication la" + " JOIN egeis_leaveType lt ON la.leaveTypeId = lt.id";
    @Autowired
    public NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private HRStatusService hrStatusService;


    public LeaveApplicationQueryBuilder(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    private static String getIdQuery(final List<Long> idList) {
        final StringBuilder query = new StringBuilder("(");
        if (idList.size() >= 1) {
            query.append(idList.get(0).toString());
            for (int i = 1; i < idList.size(); i++)
                query.append(", " + idList.get(i));
        }
        return query.append(")").toString();
    }

    public static String insertLeaveApplicationQuery() {
        return "INSERT INTO egeis_leaveapplication(id, applicationnumber, employeeid, "
                + "leavetypeid, fromdate, todate, compensatoryfordate, leavedays, availabledays,"
                + " halfdays, firsthalfleave, reason, status, stateid, createdby, createddate, "
                + "lastmodifiedby, lastmodifieddate, tenantid) VALUES "
                + "(nextval('seq_egeis_leaveapplication'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
    }

    public static String updateLeaveApplicationQuery() {
        return "UPDATE egeis_leaveapplication SET applicationnumber=?, employeeid=?,"
                + " leavetypeid=?, fromdate=?, todate=?, compensatoryfordate=?, leavedays=?,"
                + " availabledays=?, halfdays=?, firsthalfleave=?, reason=?, status=?, stateid=?,"
                + " lastmodifiedby=?, lastmodifieddate=? WHERE id=? and tenantid=?";
    }

    public static String getLeaveApplicationForDateRangeQuery() {
        return "SELECT la.id AS la_id, la.applicationNumber AS la_applicationNumber,"
                + " la.employeeId AS la_employeeId, la.fromDate AS la_fromDate, la.toDate AS la_toDate,"
                + " la.compensatoryForDate AS la_compensatoryForDate, la.leaveDays AS la_leaveDays,"
                + " la.availableDays AS la_availableDays, la.halfdays AS la_halfdays, la.firstHalfleave AS la_firstHalfleave,"
                + " la.reason AS la_reason, la.status AS la_status, la.stateId AS la_stateId, la.createdBy AS la_createdBy,"
                + " la.createdDate AS la_createdDate, la.lastModifiedBy AS la_lastModifiedBy,"
                + " la.lastModifiedDate AS la_lastModifiedDate, la.tenantId AS la_tenantId,"
                + " lt.id AS lt_id, lt.name AS lt_name, lt.description AS lt_description, lt.halfdayAllowed AS lt_halfdayAllowed,"
                + " lt.payEligible AS lt_payEligible, lt.accumulative AS lt_accumulative, lt.encashable AS lt_encashable,"
                + " lt.active AS lt_active, lt.createdBy AS lt_createdBy, lt.createdDate AS lt_createdDate,"
                + " lt.lastModifiedBy AS lt_lastModifiedBy, lt.lastModifiedDate AS lt_lastModifiedDate"
                + " FROM egeis_leaveApplication la JOIN egeis_leaveType lt ON la.leaveTypeId = lt.id"
                + " where ((fromdate between ? and ?) or (todate between ? and ?)) and la.employeeid = ?"
                + " and la.status != ? and la.id != ? and la.tenantid = ?";
    }

    public static String getLeaveApplicationForCompensatoryDate() {
        return "select * from egeis_leaveapplication where employeeid=? and DATE(compensatoryfordate)=? and tenantid=? ";
    }

    @SuppressWarnings("rawtypes")
    public String getQuery(final LeaveApplicationGetRequest leaveApplicationGetRequest,
                           final List preparedStatementValues, final RequestInfo requestInfo) {
        final StringBuilder selectQuery = new StringBuilder(BASE_QUERY);

        addWhereClause(selectQuery, preparedStatementValues, leaveApplicationGetRequest, requestInfo);
        addOrderByClause(selectQuery, leaveApplicationGetRequest);
        addPagingClause(selectQuery, preparedStatementValues, leaveApplicationGetRequest);

        logger.debug("Query : " + selectQuery);
        return selectQuery.toString();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addWhereClause(final StringBuilder selectQuery, final List preparedStatementValues,
                                final LeaveApplicationGetRequest leaveApplicationGetRequest, final RequestInfo requestInfo) {

        if (leaveApplicationGetRequest.getId() == null && leaveApplicationGetRequest.getApplicationNumber() == null
                && leaveApplicationGetRequest.getEmployee() == null && leaveApplicationGetRequest.getLeaveType() == null
                && leaveApplicationGetRequest.getFromDate() == null && leaveApplicationGetRequest.getToDate() == null
                && leaveApplicationGetRequest.getTenantId() == null)
            return;

        selectQuery.append(" WHERE");
        boolean isAppendAndClause = false;

        if (leaveApplicationGetRequest.getTenantId() != null) {
            isAppendAndClause = true;
            selectQuery.append(" la.tenantId = ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getTenantId());
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" lt.tenantId = ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getTenantId());
        }

        if (leaveApplicationGetRequest.getId() != null && !leaveApplicationGetRequest.getId().isEmpty()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.id IN " + getIdQuery(leaveApplicationGetRequest.getId()));
        }

        if (leaveApplicationGetRequest.getApplicationNumber() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.applicationNumber = ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getApplicationNumber());
        }

        if (leaveApplicationGetRequest.getStatus() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.status = ?");
            preparedStatementValues.add(hrStatusService.getHRStatuses(leaveApplicationGetRequest.getStatus(), leaveApplicationGetRequest.getTenantId(),
                    requestInfo).get(0).getId());
        }

        if (leaveApplicationGetRequest.getEmployee() != null && !leaveApplicationGetRequest.getEmployee().isEmpty()) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.employeeId IN " + getIdQuery(leaveApplicationGetRequest.getEmployee()));
        }

        if (leaveApplicationGetRequest.getLeaveType() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.leaveTypeId = ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getLeaveType());
        }

        if (leaveApplicationGetRequest.getFromDate() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.fromDate > ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getFromDate());
        }

        if (leaveApplicationGetRequest.getToDate() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.toDate < ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getToDate());
        }
        if (leaveApplicationGetRequest.getStateId() != null) {
            isAppendAndClause = addAndClauseIfRequired(isAppendAndClause, selectQuery);
            selectQuery.append(" la.stateId = ?");
            preparedStatementValues.add(leaveApplicationGetRequest.getStateId());
        }
    }

    private void addOrderByClause(final StringBuilder selectQuery,
                                  final LeaveApplicationGetRequest leaveApplicationGetRequest) {
        final String sortBy = leaveApplicationGetRequest.getSortBy() == null ? "lt.name"
                : leaveApplicationGetRequest.getSortBy();
        final String sortOrder = leaveApplicationGetRequest.getSortOrder() == null ? "ASC"
                : leaveApplicationGetRequest.getSortOrder();
        selectQuery.append(" ORDER BY " + sortBy + " " + sortOrder);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void addPagingClause(final StringBuilder selectQuery, final List preparedStatementValues,
                                 final LeaveApplicationGetRequest leaveApplicationGetRequest) {
        // handle limit(also called pageSize) here
        selectQuery.append(" LIMIT ?");
        long pageSize = Integer.parseInt(applicationProperties.hrLeaveSearchPageSizeDefault());
        if (leaveApplicationGetRequest.getPageSize() != null)
            pageSize = leaveApplicationGetRequest.getPageSize();
        preparedStatementValues.add(pageSize); // Set limit to pageSize

        // handle offset here
        selectQuery.append(" OFFSET ?");
        int pageNumber = 0; // Default pageNo is zero meaning first page
        if (leaveApplicationGetRequest.getPageNumber() != null)
            pageNumber = leaveApplicationGetRequest.getPageNumber() - 1;
        preparedStatementValues.add(pageNumber * pageSize); // Set offset to
        // pageNo * pageSize
    }

    /**
     * This method is always called at the beginning of the method so that and
     * is prepended before the field's predicate is handled.
     *
     * @param appendAndClauseFlag
     * @param queryString
     * @return boolean indicates if the next predicate should append an "AND"
     */
    private boolean addAndClauseIfRequired(final boolean appendAndClauseFlag, final StringBuilder queryString) {
        if (appendAndClauseFlag)
            queryString.append(" AND");

        return true;
    }

    public List<LeaveApplication> getLeaveApplicationForCompensatoryDate(final Long employeeId, final Date compensatoryDate, final String tenantId) {
        String searchQuery = "select :selectfields from :tablename :condition  ";
        Map<String, Object> paramValues = new HashMap<>();
        StringBuffer params = new StringBuffer();

        searchQuery = searchQuery.replace(":tablename", "egeis_leaveapplication ");


        searchQuery = searchQuery.replace(":selectfields", " * ");

        if (employeeId != null && employeeId.equals("")) {
            if (params.length() > 0) {
                params.append(" and ");
            }

            params.append("employeeid =:employeeid");
            paramValues.put("employeeid", employeeId);
        }
        if (compensatoryDate != null && !compensatoryDate.equals("")) {

            if (params.length() > 0) {
                params.append(" and ");
            }
            params.append("compensatoryfordate = :compensatorydate");
            paramValues.put("compensatorydate", compensatoryDate);
        }

        if (!StringUtils.isEmpty(tenantId)) {

            if (params.length() > 0) {
                params.append(" and ");
            }
            params.append("tenantid=:tenantid");
            paramValues.put("tenantid", tenantId);
        }

        if (params.length() > 0) {

            searchQuery = searchQuery.replace(":condition", " where  " +
                    params.toString());

        } else {
            return new ArrayList<LeaveApplication>();
        }

        BeanPropertyRowMapper row = new BeanPropertyRowMapper(LeaveApplication.class);

        return namedParameterJdbcTemplate.query(searchQuery.toString(), paramValues, row);

    }

}

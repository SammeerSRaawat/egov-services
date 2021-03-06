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
package org.egov.wcms.repository;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.egov.wcms.model.Donation;
import org.egov.wcms.repository.builder.DonationQueryBuilder;
import org.egov.wcms.repository.rowmapper.DonationRowMapper;
import org.egov.wcms.service.RestWaterExternalMasterService;
import org.egov.wcms.util.WcmsConstants;
import org.egov.wcms.web.contract.DonationGetRequest;
import org.egov.wcms.web.contract.DonationRequest;
import org.egov.wcms.web.contract.PropertyTaxResponseInfo;
import org.egov.wcms.web.contract.PropertyTypeResponse;
import org.egov.wcms.web.contract.UsageTypeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.extern.slf4j.Slf4j;

@Repository
@Slf4j
public class DonationRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private DonationRowMapper donationRowMapper;

    @Autowired
    private DonationQueryBuilder donationQueryBuilder;

    @Autowired
    private RestWaterExternalMasterService restExternalMasterService;

    @Autowired
    private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public DonationRequest persistDonationDetails(final DonationRequest donationRequest) {
        log.info("Donation Request::" + donationRequest);

        final String donationInsert = DonationQueryBuilder.donationInsertQuery();
        final List<Donation> donationList = donationRequest.getDonation();
        final List<Map<String, Object>> batchValues = new ArrayList<>(donationList.size());
        for (final Donation donation : donationList) {

            final String categoryQuery = DonationQueryBuilder.getCategoryId();
            Long categoryId = 0L;
            try {
                categoryId = jdbcTemplate.queryForObject(categoryQuery,
                        new Object[] { donation.getCategory(), donation.getTenantId() }, Long.class);
                log.info("Category Id: " + categoryId);
            } catch (final EmptyResultDataAccessException e) {
                log.info("EmptyResultDataAccessException: Query returned empty result set");
            }
            if (categoryId == null)
                log.info("Invalid input.");

            final String pipesizeQuery = DonationQueryBuilder.getPipeSizeIdQuery();
            Long maxPipeSizeId = 0L;
            try {
                maxPipeSizeId = jdbcTemplate.queryForObject(pipesizeQuery,
                        new Object[] { donation.getMaxPipeSize(), donation.getTenantId() }, Long.class);
            } catch (final EmptyResultDataAccessException e) {
                log.info("EmptyResultDataAccessException: Query returned empty result set");
            }
            if (maxPipeSizeId == null)
                log.info("Invalid input for MaxPipeSize.");

            Long minPipeSizeId = 0L;
            try {
                minPipeSizeId = jdbcTemplate.queryForObject(pipesizeQuery,
                        new Object[] { donation.getMinPipeSize(), donation.getTenantId() }, Long.class);
            } catch (final EmptyResultDataAccessException e) {
                log.info("EmptyResultDataAccessException: Query returned empty result set");
            }
            if (minPipeSizeId == null)
                log.info("Invalid input for MinPipeSize");

            batchValues.add(
                    new MapSqlParameterSource("id", Long.valueOf(donation.getCode())).addValue("code", donation.getCode())
                            .addValue("propertytypeid", donation.getPropertyTypeId())
                            .addValue("usagetypeid", donation.getUsageTypeId())
                            .addValue("subusagetypeid", donation.getSubUsageTypeId())
                            .addValue("outsideulb", donation.getOutsideUlb())
                            .addValue("categorytypeid", categoryId)
                            .addValue("maxpipesizeid", maxPipeSizeId).addValue("minpipesizeid", minPipeSizeId)
                            .addValue("fromdate", donation.getFromDate()).addValue("todate", donation.getToDate())
                            .addValue("donationamount", donation.getDonationAmount()).addValue("active", donation.getActive())
                            .addValue("tenantid", donation.getTenantId())
                            .addValue("createdby", Long.valueOf(donationRequest.getRequestInfo().getUserInfo().getId()))
                            .addValue("lastmodifiedby", Long.valueOf(donationRequest.getRequestInfo().getUserInfo().getId()))
                            .addValue("createddate", new Date(new java.util.Date().getTime()))
                            .addValue("lastmodifieddate", new Date(new java.util.Date().getTime()))
                            .getValues());
        }
        namedParameterJdbcTemplate.batchUpdate(donationInsert, batchValues.toArray(new Map[donationList.size()]));

        return donationRequest;
    }

    public DonationRequest persistModifyDonationDetails(final DonationRequest donationRequest) {
        log.info("Donation update Request::" + donationRequest);
        final String donationUpdate = DonationQueryBuilder.donationUpdateQuery();
        final List<Donation> donationList = donationRequest.getDonation();
        final List<Map<String, Object>> batchValues = new ArrayList<>(donationList.size());
        for (final Donation donation : donationList) {
            final String categoryQuery = DonationQueryBuilder.getCategoryId();
            Long categoryId = 0L;
            try {
                categoryId = jdbcTemplate.queryForObject(categoryQuery,
                        new Object[] { donation.getCategory(), donation.getTenantId() }, Long.class);
                log.info("Category Id: " + categoryId);
            } catch (final EmptyResultDataAccessException e) {
                log.info("EmptyResultDataAccessException: Query returned empty result set");
            }
            if (categoryId == null)
                log.info("Invalid input.");

            final String pipesizeQuery = DonationQueryBuilder.getPipeSizeIdQuery();
            Long maxPipeSizeId = 0L;
            try {
                maxPipeSizeId = jdbcTemplate.queryForObject(pipesizeQuery,
                        new Object[] { donation.getMaxPipeSize(), donation.getTenantId() }, Long.class);
            } catch (final EmptyResultDataAccessException e) {
                log.info("EmptyResultDataAccessException: Query returned empty result set for max pipesize");
            }
            if (maxPipeSizeId == null)
                log.info("Invalid input for MaxPipeSize.");

            Long minPipeSizeId = 0L;
            try {
                minPipeSizeId = jdbcTemplate.queryForObject(pipesizeQuery,
                        new Object[] { donation.getMinPipeSize(), donation.getTenantId() }, Long.class);
            } catch (final EmptyResultDataAccessException e) {
                log.info("EmptyResultDataAccessException: Query returned empty result set for min pipesize");
            }
            if (minPipeSizeId == null)
                log.info("Invalid input for MinPipeSize");

            batchValues.add(
                    new MapSqlParameterSource("propertytypeid", donation.getPropertyTypeId())
                            .addValue("usagetypeid", donation.getUsageTypeId())
                            .addValue("subusagetypeid", donation.getSubUsageTypeId())
                            .addValue("outsideulb", donation.getOutsideUlb())
                            .addValue("categorytypeid", categoryId)
                            .addValue("maxpipesizeid", maxPipeSizeId).addValue("minpipesizeid", minPipeSizeId)
                            .addValue("fromdate", donation.getFromDate()).addValue("todate", donation.getToDate())
                            .addValue("donationamount", donation.getDonationAmount()).addValue("active", donation.getActive())
                            .addValue("tenantid", donation.getTenantId())
                            .addValue("lastmodifiedby", Long.valueOf(donationRequest.getRequestInfo().getUserInfo().getId()))
                            .addValue("lastmodifieddate", new Date(new java.util.Date().getTime()))
                            .addValue("code", donation.getCode())
                            .getValues());
        }
        namedParameterJdbcTemplate.batchUpdate(donationUpdate, batchValues.toArray(new Map[donationList.size()]));

        return donationRequest;
    }

    public List<Donation> findForCriteria(final DonationGetRequest donationRequest) {

        final List<Object> preparedStatementValues = new ArrayList<>();
        final List<Integer> propertyTypeIdsList = new ArrayList<>();
        final List<Integer> usageTypeIdsList = new ArrayList<>();
        final List<Integer> subUsageTypeIdsList = new ArrayList<>();
        try {
            if (donationRequest.getCategoryType() != null)
                donationRequest.setCategoryTypeId(jdbcTemplate.queryForObject(DonationQueryBuilder.getCategoryId(),
                        new Object[] { donationRequest.getCategoryType(), donationRequest.getTenantId() }, Long.class));
        } catch (final EmptyResultDataAccessException e) {
            log.info("EmptyResultDataAccessException: Query returned empty set for category type.");
        }

        try {
            if (donationRequest.getMaxPipeSize() != null)
                donationRequest.setMaxPipeSizeId(jdbcTemplate.queryForObject(DonationQueryBuilder.getPipeSizeIdQuery(),
                        new Object[] { donationRequest.getMaxPipeSize(),
                                donationRequest.getTenantId() },
                        Long.class));
        } catch (final EmptyResultDataAccessException e) {
            log.error("EmptyResultDataAccessException: Query returned empty RS.");

        }
        try {
            if (donationRequest.getMinPipeSize() != null)
                donationRequest.setMinPipeSizeId(jdbcTemplate.queryForObject(DonationQueryBuilder.getPipeSizeIdQuery(),
                        new Object[] { donationRequest.getMinPipeSize(),
                                donationRequest.getTenantId() },
                        Long.class));
        } catch (final EmptyResultDataAccessException e) {
            log.error("EmptyResultDataAccessException: Query returned empty RS.");

        }

        final String queryStr = donationQueryBuilder.getQuery(donationRequest, preparedStatementValues);
        final String categoryNameQuery = DonationQueryBuilder.getCategoryTypeName();
        final String pipeSizeInmmQuery = DonationQueryBuilder.getPipeSizeInmm();
        final List<Donation> donationList = jdbcTemplate.query(queryStr, preparedStatementValues.toArray(),
                donationRowMapper);
        for (final Donation donations : donationList) {
            donations.setCategory(jdbcTemplate.queryForObject(categoryNameQuery,
                    new Object[] { donations.getCategoryTypeId(), donations.getTenantId() }, String.class));
            donations.setMaxPipeSize(jdbcTemplate.queryForObject(pipeSizeInmmQuery,
                    new Object[] { donations.getMaxPipeSizeId(), donations.getTenantId() }, Double.class));
            donations.setMinPipeSize(jdbcTemplate.queryForObject(pipeSizeInmmQuery,
                    new Object[] { donations.getMinPipeSizeId(), donations.getTenantId() }, Double.class));
        }

        // fetch property type Id and set the property type name here
        for (final Donation donationObj : donationList)
            propertyTypeIdsList.add(Integer.valueOf(donationObj.getPropertyTypeId()));
        final Integer[] propertypeIds = propertyTypeIdsList.toArray(new Integer[propertyTypeIdsList.size()]);
        final PropertyTypeResponse propertyTypes = restExternalMasterService.getPropertyNameFromPTModule(
                propertypeIds, donationRequest.getTenantId());
        for (final Donation donation : donationList)
            for (final PropertyTaxResponseInfo propertyResponse : propertyTypes.getPropertyTypes())
                if (propertyResponse.getId().equals(donation.getPropertyTypeId()))
                    donation.setPropertyType(propertyResponse.getName());

        // fetch usage type Id and set the usage type name here
        for (final Donation donation : donationList)
            usageTypeIdsList.add(Integer.valueOf(donation.getUsageTypeId()));
        final Integer[] usageTypeIds = usageTypeIdsList.toArray(new Integer[usageTypeIdsList.size()]);
        final UsageTypeResponse usageResponse = restExternalMasterService.getUsageNameFromPTModule(
                usageTypeIds, WcmsConstants.WC, donationRequest.getTenantId());
        for (final Donation donation : donationList)
            for (final PropertyTaxResponseInfo propertyResponse : usageResponse.getUsageMasters())
                if (propertyResponse.getId().equals(donation.getUsageTypeId()))
                    donation.setUsageType(propertyResponse.getName());

        // fetch sub usage type Id and set the usage type name here
        for (final Donation donation : donationList)
            subUsageTypeIdsList.add(Integer.valueOf(donation.getSubUsageTypeId()));
        final Integer[] subUsageTypeIds = subUsageTypeIdsList.toArray(new Integer[subUsageTypeIdsList.size()]);
        final UsageTypeResponse subUsageResponse = restExternalMasterService.getSubUsageNameFromPTModule(
                subUsageTypeIds, WcmsConstants.WC, donationRequest.getTenantId());
        for (final Donation donationObj : donationList)
            for (final PropertyTaxResponseInfo propertyResponse : subUsageResponse.getUsageMasters())
                if (propertyResponse.getId().equals(donationObj.getSubUsageTypeId()))
                    donationObj.setSubUsageType(propertyResponse.getName());
        return donationList;

    }

    public boolean checkDonationsExist(final String code, final String propertTypeId, final String usageTypeId,
            final String subUsageTypeId, final String categoryName, final Double maxPipeSize, final Double minPipeSize,
            final String tenantId) {
        final List<Object> preparedStatementValues = new ArrayList<>();
        final String pipesizeQuery = DonationQueryBuilder.getPipeSizeIdQuery();
        final String categoryQuery = DonationQueryBuilder.getCategoryId();
        Long categoryId = 0L;
        try {
            categoryId = jdbcTemplate.queryForObject(categoryQuery,
                    new Object[] { categoryName, tenantId }, Long.class);
            log.info("Category Id: " + categoryId);
        } catch (final EmptyResultDataAccessException e) {
            log.info("EmptyResultDataAccessException: Query returned empty result set");
        }
        if (categoryId == null)
            log.info("Invalid input.");

        Long maxPipeSizeId = 0L;
        try {
            maxPipeSizeId = jdbcTemplate.queryForObject(pipesizeQuery,
                    new Object[] { maxPipeSize, tenantId }, Long.class);
        } catch (final EmptyResultDataAccessException e) {
            log.info("EmptyResultDataAccessException: Query returned empty result set for max pipesize");
        }
        if (maxPipeSizeId == null)
            log.info("Invalid input for MaxPipeSize.");

        Long minPipeSizeId = 0L;
        try {
            minPipeSizeId = jdbcTemplate.queryForObject(pipesizeQuery,
                    new Object[] { minPipeSize, tenantId }, Long.class);
        } catch (final EmptyResultDataAccessException e) {
            log.info("EmptyResultDataAccessException: Query returned empty result set for min pipesize");
        }
        preparedStatementValues.add(propertTypeId);
        preparedStatementValues.add(usageTypeId);
        preparedStatementValues.add(subUsageTypeId);
        preparedStatementValues.add(categoryId);
        preparedStatementValues.add(maxPipeSizeId);
        preparedStatementValues.add(minPipeSizeId);
        preparedStatementValues.add(tenantId);
        final String query;
        if (code == null)
            query = DonationQueryBuilder.selectDonationByCodeQuery();
        else {
            preparedStatementValues.add(code);
            query = DonationQueryBuilder.selectDonationByCodeNotInQuery();
        }
        final List<Map<String, Object>> donations = jdbcTemplate.queryForList(query,
                preparedStatementValues.toArray());
        if (!donations.isEmpty())
            return false;

        return true;
    }

}

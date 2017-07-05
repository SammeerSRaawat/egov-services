package org.egov.egf.budget.persistence.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.egov.common.domain.model.Pagination;
import org.egov.common.persistence.repository.JdbcRepository;
import org.egov.egf.budget.domain.model.BudgetReAppropriation;
import org.egov.egf.budget.domain.model.BudgetReAppropriationSearch;
import org.egov.egf.budget.persistence.entity.BudgetReAppropriationEntity;
import org.egov.egf.budget.persistence.entity.BudgetReAppropriationSearchEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Service;

@Service
public class BudgetReAppropriationJdbcRepository extends JdbcRepository {
	private static final Logger LOG = LoggerFactory.getLogger(BudgetReAppropriationJdbcRepository.class);

	static {
		LOG.debug("init budgetReAppropriation");
		init(BudgetReAppropriationEntity.class);
		LOG.debug("end init budgetReAppropriation");
	}

	public BudgetReAppropriationEntity create(BudgetReAppropriationEntity entity) {

		entity.setId(UUID.randomUUID().toString().replace("-", ""));
		super.create(entity);
		return entity;
	}

	public BudgetReAppropriationEntity update(BudgetReAppropriationEntity entity) {
		super.update(entity);
		return entity;

	}

	public Pagination<BudgetReAppropriation> search(BudgetReAppropriationSearch domain) {
		BudgetReAppropriationSearchEntity budgetReAppropriationSearchEntity = new BudgetReAppropriationSearchEntity();
		budgetReAppropriationSearchEntity.toEntity(domain);

		String searchQuery = "select :selectfields from :tablename :condition  :orderby   ";

		Map<String, Object> paramValues = new HashMap<>();
		StringBuffer params = new StringBuffer();
		String orderBy = "";

		searchQuery = searchQuery.replace(":tablename", BudgetReAppropriationEntity.TABLE_NAME);

		searchQuery = searchQuery.replace(":selectfields", " * ");

		// implement jdbc specfic search
		if (budgetReAppropriationSearchEntity.getId() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("id =:id");
			paramValues.put("id", budgetReAppropriationSearchEntity.getId());
		}
		if (budgetReAppropriationSearchEntity.getBudgetDetailId() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("budgetDetail =:budgetDetail");
			paramValues.put("budgetDetail", budgetReAppropriationSearchEntity.getBudgetDetailId());
		}
		if (budgetReAppropriationSearchEntity.getAdditionAmount() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("additionAmount =:additionAmount");
			paramValues.put("additionAmount", budgetReAppropriationSearchEntity.getAdditionAmount());
		}
		if (budgetReAppropriationSearchEntity.getDeductionAmount() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("deductionAmount =:deductionAmount");
			paramValues.put("deductionAmount", budgetReAppropriationSearchEntity.getDeductionAmount());
		}
		if (budgetReAppropriationSearchEntity.getOriginalAdditionAmount() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("originalAdditionAmount =:originalAdditionAmount");
			paramValues.put("originalAdditionAmount", budgetReAppropriationSearchEntity.getOriginalAdditionAmount());
		}
		if (budgetReAppropriationSearchEntity.getOriginalDeductionAmount() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("originalDeductionAmount =:originalDeductionAmount");
			paramValues.put("originalDeductionAmount", budgetReAppropriationSearchEntity.getOriginalDeductionAmount());
		}
		if (budgetReAppropriationSearchEntity.getAnticipatoryAmount() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("anticipatoryAmount =:anticipatoryAmount");
			paramValues.put("anticipatoryAmount", budgetReAppropriationSearchEntity.getAnticipatoryAmount());
		}
		if (budgetReAppropriationSearchEntity.getStatusId() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("status =:status");
			paramValues.put("status", budgetReAppropriationSearchEntity.getStatusId());
		}
		if (budgetReAppropriationSearchEntity.getAsOnDate() != null) {
			if (params.length() > 0)
				params.append(" and ");
			params.append("asOnDate =:asOnDate");
			paramValues.put("asOnDate", budgetReAppropriationSearchEntity.getAsOnDate());
		}

		Pagination<BudgetReAppropriation> page = new Pagination<>();
		page.setOffSet(budgetReAppropriationSearchEntity.getOffset());
		page.setPageSize(budgetReAppropriationSearchEntity.getPageSize());

		if (params.length() > 0) {

			searchQuery = searchQuery.replace(":condition", " where " + params.toString());

		} else {
			searchQuery = searchQuery.replace(":condition", "");
		}

		searchQuery = searchQuery.replace(":orderby", "order by id ");

		page = getPagination(searchQuery, page,paramValues);
		searchQuery = searchQuery + " :pagination";

		searchQuery = searchQuery.replace(":pagination", "limit " + budgetReAppropriationSearchEntity.getPageSize()
				+ " offset "
				+ budgetReAppropriationSearchEntity.getOffset() * budgetReAppropriationSearchEntity.getPageSize());

		BeanPropertyRowMapper row = new BeanPropertyRowMapper(BudgetReAppropriationEntity.class);

		List<BudgetReAppropriationEntity> budgetReAppropriationEntities = namedParameterJdbcTemplate
				.query(searchQuery.toString(), paramValues, row);

		page.setTotalResults(budgetReAppropriationEntities.size());

		List<BudgetReAppropriation> budgetreappropriations = new ArrayList<BudgetReAppropriation>();
		for (BudgetReAppropriationEntity budgetReAppropriationEntity : budgetReAppropriationEntities) {

			budgetreappropriations.add(budgetReAppropriationEntity.toDomain());
		}
		page.setPagedData(budgetreappropriations);

		return page;
	}

	public BudgetReAppropriationEntity findById(BudgetReAppropriationEntity entity) {
		List<String> list = allUniqueFields.get(entity.getClass().getSimpleName());

		final List<Object> preparedStatementValues = new ArrayList<>();

		for (String s : list) {
			preparedStatementValues.add(getValue(getField(entity, s), entity));
		}

		List<BudgetReAppropriationEntity> budgetreappropriations = jdbcTemplate.query(
				getByIdQuery.get(entity.getClass().getSimpleName()), preparedStatementValues.toArray(),
				new BeanPropertyRowMapper<BudgetReAppropriationEntity>());
		if (budgetreappropriations.isEmpty()) {
			return null;
		} else {
			return budgetreappropriations.get(0);
		}

	}

}
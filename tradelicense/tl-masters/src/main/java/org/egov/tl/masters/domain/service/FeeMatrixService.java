package org.egov.tl.masters.domain.service;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.egov.tl.commons.web.contract.FeeMatrixContract;
import org.egov.tl.commons.web.contract.RequestInfo;
import org.egov.tl.commons.web.requests.FeeMatrixRequest;
import org.egov.tl.commons.web.requests.RequestInfoWrapper;
import org.egov.tl.masters.contract.repository.FinancialRepository;
import org.egov.tl.masters.domain.enums.ApplicationTypeEnum;
import org.egov.tl.masters.domain.enums.BusinessNatureEnum;
import org.egov.tl.masters.domain.enums.FeeTypeEnum;
import org.egov.tl.masters.domain.model.FeeMatrix;
import org.egov.tl.masters.domain.model.FeeMatrixDetail;
import org.egov.tl.masters.domain.model.FeeMatrixSearch;
import org.egov.tl.masters.domain.model.FeeMatrixSearchCriteria;
import org.egov.tl.masters.domain.repository.FeeMatrixDetailDomainRepository;
import org.egov.tl.masters.domain.repository.FeeMatrixDomainRepository;
import org.egov.tl.masters.persistence.entity.FeeMatrixEntity;
import org.egov.tl.masters.persistence.entity.FeeMatrixSearchEntity;
import org.egov.tl.masters.persistence.queue.FeeMatrixQueueRepository;
import org.egov.tradelicense.config.PropertiesManager;
import org.egov.tradelicense.domain.exception.InvalidInputException;
import org.egov.tradelicense.domain.exception.InvalidRangeException;
import org.egov.tradelicense.domain.model.FinancialYearContract;
import org.egov.tradelicense.persistence.repository.helper.UtilityHelper;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * FeeMatrixService implementation class
 * 
 * @author Pavan Kumar Kamma
 *
 */
@Service
public class FeeMatrixService {

	@Autowired
	FeeMatrixDomainRepository feeMatrixDomainRepository;

	@Autowired
	FeeMatrixDetailDomainRepository feeMatrixDetailDomainRepository;

	@Autowired
	UtilityHelper utilityHelper;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	FinancialRepository financialRepository;

	@Autowired
	FeeMatrixQueueRepository feeMatrixQueueRepository;

	@Transactional
	public List<FeeMatrix> createFeeMatrixMaster(List<FeeMatrix> feeMatrices, RequestInfo requestInfo) {

		validateFeeMatrixRequest(feeMatrices, requestInfo, Boolean.TRUE);
		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);
		for (FeeMatrix feeMatrix : feeMatrices) {

			Long id = feeMatrixDomainRepository.getFeeMatrixNextSequence();
			feeMatrix.setId(id);
			feeMatrix = populateEffectiveFromAndToDates(feeMatrix, requestInfoWrapper);
			for (FeeMatrixDetail feeMatrixDetail : feeMatrix.getFeeMatrixDetails()) {
				Long feematrixDetalsId = feeMatrixDetailDomainRepository.getFeeDetailMatrixNextSequence();
				feeMatrixDetail.setId(feematrixDetalsId);
				feeMatrixDetail.setFeeMatrixId(id);
			}
		}
		FeeMatrixRequest feeMatrixRequest = buidFeeMatixRequest(feeMatrices, requestInfo);
		addToQue(feeMatrixRequest, propertiesManager.getFeeMatrixCreateValidated());
		return feeMatrices;
	}

	public void validateFeeMatrixRequest(List<FeeMatrix> feeMatrixes, RequestInfo requestInfo, Boolean isNew) {

		for (FeeMatrix feeMatrix : feeMatrixes) {
			// validating the categoryId
			validateCategoryId(feeMatrix.getCategoryId(), null, feeMatrix.getTenantId(), requestInfo);
			// validating the subCategoryId
			validateCategoryId(feeMatrix.getSubCategoryId(), feeMatrix.getCategoryId(), feeMatrix.getTenantId(),
					requestInfo);
			if (!isNew) { // false
				if (feeMatrix.getId() != null) {
					validateIdOfFeeMatrices(feeMatrix.getId(), feeMatrix.getTenantId(), requestInfo);
				} else {
					throw new InvalidInputException(propertiesManager.getInvalidIdMsg(), requestInfo);
				}
			} else { // create
				boolean isExists = validateUniqueness(feeMatrix.getTenantId(), feeMatrix.getApplicationType(),
						feeMatrix.getFeeType(), feeMatrix.getBusinessNature(), feeMatrix.getCategoryId(),
						feeMatrix.getSubCategoryId(), feeMatrix.getFinancialYear(), requestInfo);

				if (isExists) {
					throw new InvalidInputException(propertiesManager.getUniquenessErrorMsg(), requestInfo);
				}

				validateFeeMatrixDetails(feeMatrix, requestInfo, isNew);
			}

		}

	}

	private void validateFeeDetailsUsingUom(FeeMatrix feeMatrix, RequestInfo requestInfo) {
		for (FeeMatrixDetail feeMatrixDetails : feeMatrix.getFeeMatrixDetails()) {
			boolean isExists = feeMatrixDomainRepository.validateFeeMatrixDetails(feeMatrixDetails.getUomFrom(),
					feeMatrixDetails.getUomTo());

			if (isExists) {
				throw new InvalidInputException(propertiesManager.getUniquenessErrorMsg(), requestInfo);
			}
		}
	}

	private boolean validateUniqueness(String tenantId, ApplicationTypeEnum applicationTypeEnum,
			FeeTypeEnum feeTypeEnum, BusinessNatureEnum businessNatureEnum, Long categoryId, Long subCategoryId,
			String financialYear, RequestInfo requestInfo) {
		return feeMatrixDomainRepository.checkUniquenessOfFeeMatrix(tenantId, applicationTypeEnum, feeTypeEnum,
				businessNatureEnum, categoryId, subCategoryId, financialYear);
	}

	public void validateFeeMatrixDetails(FeeMatrix feeMatrix, RequestInfo requestInfo, boolean validateNew) {

		List<FeeMatrixDetail> feeMatrixDetails = new ArrayList<>();

		// FIXME: Use of else condition
		if (validateNew) {

			feeMatrixDetails = feeMatrix.getFeeMatrixDetails();
		} else {
			Long feeMatrixId = feeMatrix.getId();
			try {

				feeMatrixDetails = feeMatrixDetailDomainRepository.getFeeMatrixDetailsByFeeMatrixId(feeMatrixId);
			} catch (Exception e) {

				throw new InvalidInputException(e.getLocalizedMessage(), requestInfo);
			}

			for (FeeMatrixDetail feeMatrixDetail : feeMatrixDetails) {

				for (int i = 0; i < feeMatrixDetails.size(); i++) {
					Long id = feeMatrixDetails.get(i).getId();
					if (feeMatrixDetail.getId() != null && id == feeMatrixDetail.getId()) {
						feeMatrixDetails.set(i, feeMatrixDetail);
					}
				}
			}
		}

		if (feeMatrixDetails.size() > 1) {
			feeMatrixDetails.sort((r1, r2) -> r1.getUomFrom().compareTo(r2.getUomFrom()));
		}

		Long uomFrom = null;
		Long oldUomTo = null;
		Long uomTo = null;
		int count = 0;

		for (FeeMatrixDetail feeMatrixDetail : feeMatrixDetails) {

			uomFrom = feeMatrixDetail.getUomFrom();
			uomTo = feeMatrixDetail.getUomTo();
			if (count == 0) {
				if (uomFrom != 0) {
					throw new InvalidRangeException(propertiesManager.getInvalidSequenceRangeMsg(), requestInfo);
				}
			}
			if (uomTo != null && uomFrom >= uomTo) {
				throw new InvalidRangeException(propertiesManager.getInvalidSequenceRangeMsg(), requestInfo);
			} else if (uomTo == null && count != (feeMatrixDetails.size() - 1)) {
				throw new InvalidRangeException(propertiesManager.getInvalidSequenceRangeMsg(), requestInfo);
			}
			feeMatrixDetail.setAuditDetails(feeMatrix.getAuditDetails());
			if (count > 0) {
				if (!uomFrom.equals(oldUomTo)) {
					throw new InvalidRangeException(propertiesManager.getInvalidSequenceRangeMsg(), requestInfo);
				}
			}
			oldUomTo = feeMatrixDetail.getUomTo();
			count++;
		}
	}

	/**
	 * Checks whether record with given id is exists or not
	 * 
	 * @param id
	 * @param requestInfo
	 */
	private void validateIdOfFeeMatrices(Long id, String tenantId, RequestInfo requestInfo) {

		boolean isExists = feeMatrixDomainRepository.validateInputId(id, tenantId);
		if (!isExists) {
			throw new InvalidInputException(propertiesManager.getInvalidIdAndTenantIdMsg(), requestInfo);
		}
	}

	private void validateCategoryId(Long id, Long parentId, String tenatId, RequestInfo requestInfo) {

		boolean isExists = feeMatrixDomainRepository.validateCategory(id, parentId, tenatId);
		if (!isExists) {
			throw new InvalidInputException(propertiesManager.getCategoryIdValidationMsg(), requestInfo);
		}
	}

	/**
	 * 
	 * @param financialYearId
	 * 
	 *            This will validate the FinancialYear existance, make
	 *            restTemplate call and check the Existance of financial year
	 */
	private FinancialYearContract validateFinancialYear(Long financialYearId, String tenantId,
			RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);
		return financialRepository.findFinancialYearById(tenantId, financialYearId.toString(), requestInfoWrapper);

	}

	public void save(FeeMatrix feeMatrix) {
		feeMatrixDomainRepository.add(feeMatrix);
	}

	public void addToQue(FeeMatrixRequest request, String key) {
		Map<String, Object> message = new HashMap<>();
		message.put(key, request);
		feeMatrixQueueRepository.add(message);
	}

	private FeeMatrix populateEffectiveFromAndToDates(FeeMatrix feeMatrix, RequestInfoWrapper requestInfoWrapper) {

		FinancialYearContract financialYearContract = validateFinancialYear(Long.valueOf(feeMatrix.getFinancialYear()),
				feeMatrix.getTenantId(), requestInfoWrapper.getRequestInfo());

		if (financialYearContract != null && financialYearContract.getStartingDate() != null) {
			feeMatrix.setEffectiveFrom(financialYearContract.getStartingDate().getTime());
			FeeMatrixEntity feeMatrixEntity = new FeeMatrixEntity().toEntity(feeMatrix);
			FeeMatrixSearchCriteria feeMatrixSeach = buildSearchCriteria(feeMatrixEntity);
			FeeMatrix nextFeeMatrix = feeMatrixDomainRepository.getFeeMatrixForNextFinancialYear(feeMatrixSeach);
			FeeMatrix previousFeeMatrix = feeMatrixDomainRepository
					.getFeeMatrixForPreviousFinancialYear(feeMatrixSeach);
			if (nextFeeMatrix == null) {
				feeMatrix.setEffectiveTo(null);
			} else {
				Date effectiveTo = new Date(nextFeeMatrix.getEffectiveFrom() - (1000 * 60 * 60 * 24));
				feeMatrix.setEffectiveTo(effectiveTo.getTime());
			}
			if (previousFeeMatrix != null) {
				Date effectiveTo = new Date(feeMatrix.getEffectiveFrom() - (1000 * 60 * 60 * 24));
				previousFeeMatrix.setEffectiveTo(effectiveTo.getTime());
				previousFeeMatrix.getAuditDetails().setLastModifiedTime(new Date().getTime());
				String modifiedBy = requestInfoWrapper.getRequestInfo().getUserInfo().getId().toString();
				previousFeeMatrix.getAuditDetails().setLastModifiedBy(modifiedBy);
				feeMatrixDomainRepository.update(previousFeeMatrix);
			}
		}
		return feeMatrix;

	}

	private FeeMatrixRequest buidFeeMatixRequest(List<FeeMatrix> feeMatrices, RequestInfo requestInfo) {
		ModelMapper modelMapper = new ModelMapper();
		Type targetListType = new TypeToken<List<FeeMatrixContract>>() {
		}.getType();
		List<FeeMatrixContract> feeMatricesContact = modelMapper.map(feeMatrices, targetListType);
		FeeMatrixRequest feeMatrixContract = new FeeMatrixRequest();
		feeMatrixContract.setFeeMatrices(feeMatricesContact);
		feeMatrixContract.setRequestInfo(requestInfo);
		return feeMatrixContract;
	}

	public FeeMatrixSearchCriteria buildSearchCriteria(FeeMatrixEntity feeMatrixEntity) {
		FeeMatrixSearchCriteria feeMatrixSearchCriteria = new FeeMatrixSearchCriteria();
		String applcationType = feeMatrixEntity.getApplicationType() == null ? null
				: feeMatrixEntity.getApplicationType().toString();
		feeMatrixSearchCriteria.setApplicationType(applcationType);
		String businessNature = feeMatrixEntity.getBusinessNature() == null ? null
				: feeMatrixEntity.getBusinessNature().toString();
		feeMatrixSearchCriteria.setBusinessNature(businessNature);
		feeMatrixSearchCriteria.setTenantId(feeMatrixEntity.getTenantId());
		feeMatrixSearchCriteria.setCategoryId(feeMatrixEntity.getCategoryId());
		feeMatrixSearchCriteria.setSubCategoryId(feeMatrixEntity.getSubCategoryId());
		feeMatrixSearchCriteria.setEffectiveFrom(feeMatrixEntity.getEffectiveFrom().getTime());
		String feeType = feeMatrixEntity.getFeeType() == null ? null : feeMatrixEntity.getFeeType();
		feeMatrixSearchCriteria.setFeeType(feeType);
		return feeMatrixSearchCriteria;
	}

	public void search(FeeMatrixSearchCriteria feeMatrixSearchCriteria) {
		// TODO Auto-generated method stub

	}
}
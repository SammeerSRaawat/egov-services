package org.egov.tradelicense.domain.service.validator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.egov.models.PropertyResponse;
import org.egov.tl.commons.web.contract.CategoryDetailSearch;
import org.egov.tl.commons.web.contract.CategorySearch;
import org.egov.tl.commons.web.contract.DocumentTypeContract;
import org.egov.tl.commons.web.contract.RequestInfo;
import org.egov.tl.commons.web.requests.DocumentTypeV2Response;
import org.egov.tl.commons.web.requests.RequestInfoWrapper;
import org.egov.tl.commons.web.response.CategorySearchResponse;
import org.egov.tradelicense.common.config.PropertiesManager;
import org.egov.tradelicense.common.domain.exception.AdhaarNotFoundException;
import org.egov.tradelicense.common.domain.exception.AgreeMentDateNotFoundException;
import org.egov.tradelicense.common.domain.exception.AgreeMentNotFoundException;
import org.egov.tradelicense.common.domain.exception.AgreeMentNotValidException;
import org.egov.tradelicense.common.domain.exception.CustomInvalidInputException;
import org.egov.tradelicense.common.domain.exception.EndPointException;
import org.egov.tradelicense.common.domain.exception.IdNotFoundException;
import org.egov.tradelicense.common.domain.exception.InvalidAdminWardException;
import org.egov.tradelicense.common.domain.exception.InvalidCategoryException;
import org.egov.tradelicense.common.domain.exception.InvalidDocumentTypeException;
import org.egov.tradelicense.common.domain.exception.InvalidFeeDetailException;
import org.egov.tradelicense.common.domain.exception.InvalidInputException;
import org.egov.tradelicense.common.domain.exception.InvalidLocalityException;
import org.egov.tradelicense.common.domain.exception.InvalidPropertyAssesmentException;
import org.egov.tradelicense.common.domain.exception.InvalidRevenueWardException;
import org.egov.tradelicense.common.domain.exception.InvalidSubCategoryException;
import org.egov.tradelicense.common.domain.exception.InvalidUomException;
import org.egov.tradelicense.common.domain.exception.InvalidValidityYearsException;
import org.egov.tradelicense.common.domain.exception.LegacyFeeDetailNotFoundException;
import org.egov.tradelicense.common.domain.exception.MandatoryDocumentNotFoundException;
import org.egov.tradelicense.common.domain.exception.OldLicenseNotFoundException;
import org.egov.tradelicense.common.domain.exception.PropertyAssesmentNotFoundException;
import org.egov.tradelicense.domain.model.LicenseFeeDetail;
import org.egov.tradelicense.domain.model.SupportDocument;
import org.egov.tradelicense.domain.model.TradeLicense;
import org.egov.tradelicense.domain.repository.TradeLicenseRepository;
import org.egov.tradelicense.web.contract.FinancialYearContract;
import org.egov.tradelicense.web.repository.BoundaryContractRepository;
import org.egov.tradelicense.web.repository.CategoryContractRepository;
import org.egov.tradelicense.web.repository.DocumentTypeContractRepository;
import org.egov.tradelicense.web.repository.FinancialYearContractRepository;
import org.egov.tradelicense.web.repository.PropertyContractRespository;
import org.egov.tradelicense.web.response.BoundaryResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TradeLicenseServiceValidator {

	@Autowired
	TradeLicenseRepository tradeLicenseRepository;

	@Autowired
	private PropertiesManager propertiesManager;

	@Autowired
	PropertyContractRespository propertyContractRepository;

	@Autowired
	BoundaryContractRepository boundaryContractRepository;

	@Autowired
	CategoryContractRepository categoryContractRepository;

	@Autowired
	DocumentTypeContractRepository documentTypeContractRepository;

	@Autowired
	FinancialYearContractRepository financialYearContractRepository;

	public void validateCreateTradeLicenseRelated(List<TradeLicense> tradeLicenses, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);
		Boolean isNewRecord = true;

		for (TradeLicense tradeLicense : tradeLicenses) {
			// checking the valid from date existance
			
			validateTradeValidFromDate(tradeLicense, requestInfo);
			
			// checking the existance and uniqueness of licensenumber
			validateLicenseNumber(tradeLicense, isNewRecord, requestInfo);
			// checking the agreement details
			validateLicenseAgreementDetails(tradeLicense, requestInfo);
			// checking the property details
			validatePtisDetails(tradeLicense, requestInfo);
			// checking the adhaar details
			validateAdhaarDetails(tradeLicense, requestInfo);
			// checking the trade location details
			validateTradeLocationDetails(tradeLicense, requestInfo);
			// checking revenue ward details
			validateRevenueWardDetails(tradeLicense, requestInfo);
			// checking admin ward details
			validateAdminWardDetails(tradeLicense, requestInfo);
			// checking category Details
			validateTradeCategoryDetails(tradeLicense, requestInfo);
			// checking subCategory Details
			validateTradeSubCategoryDetails(tradeLicense, requestInfo);
			// checking the uom details
			validateTradeUomDetails(tradeLicense, requestInfo);
			// checking support document details
			validateTradeSupportingDocuments(tradeLicense, requestInfo);
			// checking feeDetails
			if (tradeLicense.getIsLegacy()) {
				validateTradeFeeDetails(tradeLicense, requestInfo);
			} else {
				// validate trade commencement date
				setTradeExpiryDateByValidatingCommencementDate(tradeLicense, requestInfo);

			}
		}
	}

	public void validateUpdateTradeLicenseRelated(List<TradeLicense> tradeLicenses, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);
		Boolean isNewRecord = false;

		for (TradeLicense tradeLicense : tradeLicenses) {


			// checking the valid from date existance
			validateTradeValidFromDate(tradeLicense, requestInfo);
			
			// checking the id existance of tradelicense in database
			validateTradeLicenseIdExistance(tradeLicense, requestInfo);
			// checking the eistance and uniqueness of licensenumber
			validateLicenseNumber(tradeLicense, isNewRecord, requestInfo);
			// checking the agreement details
			validateLicenseAgreementDetails(tradeLicense, requestInfo);
			// checking the property details
			validatePtisDetails(tradeLicense, requestInfo);
			// checking the adhaar details
			validateAdhaarDetails(tradeLicense, requestInfo);
			// checking the trade location details
			validateTradeLocationDetails(tradeLicense, requestInfo);
			// checking revenue ward details
			validateRevenueWardDetails(tradeLicense, requestInfo);
			// checking admin ward details
			validateAdminWardDetails(tradeLicense, requestInfo);
			// checking category Details
			validateTradeCategoryDetails(tradeLicense, requestInfo);
			// checking subCategory Details
			validateTradeSubCategoryDetails(tradeLicense, requestInfo);
			// checking the uom details
			validateTradeUomDetails(tradeLicense, requestInfo);
			// checking support document details
			validateTradeSupportingDocuments(tradeLicense, requestInfo);
			// checking feeDetails
			if (tradeLicense.getIsLegacy()) {
				validateTradeFeeDetails(tradeLicense, requestInfo);
			}
			// get the tradeLicense with id and bind non-modifiable fields
			bindTradeNonModifiableFields(tradeLicense, requestInfo);
		}
	}

	/**
	 * 	validate trade valid from date, for legacy throw error if licenseValid from Date is null,
	 * set TradeCommencementDate to the validlicenseFromDate when validlicenseFromDate is null and license is not legacy
	 * @param tradeLicense
	 * @param requestInfo
	 */
	private void validateTradeValidFromDate(TradeLicense tradeLicense, RequestInfo requestInfo) {

		if (tradeLicense.getLicenseValidFromDate() == null) {

			if( tradeLicense.getIsLegacy() ){
				throw new CustomInvalidInputException(propertiesManager.getLicenseValidFromDateNotNullCode(),
						propertiesManager.getLicenseValidFromDateNotNullMsg(), requestInfo);
			}else{
				tradeLicense.setLicenseValidFromDate( tradeLicense.getTradeCommencementDate());
			}
			
		}
	}

	private void validateTradeLicenseIdExistance(TradeLicense tradeLicense, RequestInfo requestInfo) {

		if (tradeLicense.getId() != null) {
			// check id existence in database
			tradeLicenseRepository.validateTradeLicenseId(tradeLicense, requestInfo);
		} else {
			throw new IdNotFoundException(propertiesManager.getOldLicenseIdNotFoundCustomMsg(),
					propertiesManager.getIdField(), requestInfo);
		}
	}

	private void validateLicenseNumber(TradeLicense tradeLicense, Boolean isNewRecord, RequestInfo requestInfo) {

		if (tradeLicense.getIsLegacy()) {

			if (tradeLicense.getOldLicenseNumber() == null || tradeLicense.getOldLicenseNumber().trim().isEmpty()) {
				throw new OldLicenseNotFoundException(propertiesManager.getOldLicenseNumberErrorMsg(), requestInfo);
			}
			// check unique constraint
			tradeLicenseRepository.validateUniqueLicenseNumber(tradeLicense, isNewRecord, requestInfo);
		} else {
			if (tradeLicense.getApplication() == null) {
				throw new InvalidInputException(propertiesManager.getApplicationMissingErr(), requestInfo);
			} else if (tradeLicense.getApplication().getApplicationType() == null) {
				throw new InvalidInputException(propertiesManager.getApplicationTypeMissingErr(), requestInfo);
			}
			if (tradeLicense.getApplication() != null && tradeLicense.getApplication().getApplicationNumber() != null
					&& !tradeLicense.getApplication().getApplicationNumber().isEmpty()) {
				tradeLicenseRepository.validateUniqueApplicationNumber(tradeLicense, isNewRecord, requestInfo);
			}
		}
	}

	private void validateLicenseAgreementDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		if (tradeLicense.getIsPropertyOwner()) {

			if (tradeLicense.getAgreementNo() == null || tradeLicense.getAgreementNo().trim().isEmpty()) {

				throw new AgreeMentNotFoundException(propertiesManager.getAgreementNotFoundErrorMsg(), requestInfo);

			} else if (tradeLicense.getAgreementNo().trim().length() < 4
					|| tradeLicense.getAgreementNo().trim().length() > 20) {

				throw new AgreeMentNotValidException(propertiesManager.getAgreementNoErrorMsg(), requestInfo);
			}

			if (tradeLicense.getAgreementDate() == null) {

				throw new AgreeMentDateNotFoundException(propertiesManager.getAgreementDateErrorMsg(), requestInfo);

			} else if (!tradeLicense.getIsLegacy()) {

				if (tradeLicense.getTradeCommencementDate() != null) {

					if ((tradeLicense.getTradeCommencementDate().compareTo(tradeLicense.getAgreementDate())) < 0) {

						throw new CustomInvalidInputException(propertiesManager.getAgreementDateNotValidCode(),
								propertiesManager.getAgreementDateNotValidMsg(), requestInfo);

					}
				}
			}

		}
	}

	private void validatePtisDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		if (propertiesManager.getPtisValidation()) {

			if (tradeLicense.getPropertyAssesmentNo() == null
					|| tradeLicense.getPropertyAssesmentNo().trim().isEmpty()) {

				throw new PropertyAssesmentNotFoundException(propertiesManager.getPropertyAssesmentNotFoundMsg(),
						requestInfo);

			} else if (tradeLicense.getPropertyAssesmentNo().trim().length() < 15
					|| tradeLicense.getPropertyAssesmentNo().trim().length() > 20) {

				throw new InvalidPropertyAssesmentException(propertiesManager.getPropertyAssesmentNoInvalidErrorMsg(),
						requestInfo);

			} else {

				PropertyResponse propertyResponse = propertyContractRepository.findByAssesmentNo(tradeLicense,
						requestInfoWrapper);

				if (propertyResponse == null || propertyResponse.getProperties() == null
						|| propertyResponse.getProperties().size() == 0) {

					throw new InvalidPropertyAssesmentException(
							propertiesManager.getPropertyAssesmentNoInvalidErrorMsg(), requestInfo);
				}
			}
		}
	}

	private void validateAdhaarDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		if (propertiesManager.getAdhaarValidation()) {

			if (tradeLicense.getAdhaarNumber() == null) {

				throw new AdhaarNotFoundException(propertiesManager.getAadhaarNumberErrorMsg(), requestInfo);
			}
		}
	}

	private void validateTradeLocationDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// locality validation
		if (tradeLicense.getLocalityId() != null) {

			BoundaryResponse boundaryResponse = boundaryContractRepository.findByLocalityId(tradeLicense,
					requestInfoWrapper);

			if (boundaryResponse == null || boundaryResponse.getBoundarys() == null
					|| boundaryResponse.getBoundarys().size() == 0) {

				throw new InvalidLocalityException(propertiesManager.getLocalityErrorMsg(), requestInfo);
			}

		}
	}

	private void validateRevenueWardDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// revenue ward validation
		if (tradeLicense.getRevenueWardId() != null) {

			BoundaryResponse boundaryResponse = boundaryContractRepository.findByRevenueWardId(tradeLicense,
					requestInfoWrapper);

			if (boundaryResponse == null || boundaryResponse.getBoundarys() == null
					|| boundaryResponse.getBoundarys().size() == 0) {

				throw new InvalidRevenueWardException(propertiesManager.getRevenueWardErrorMsg(), requestInfo);
			}

		}
	}

	private void validateAdminWardDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// admin ward validation
		if (tradeLicense.getAdminWardId() != null) {

			BoundaryResponse boundaryResponse = boundaryContractRepository.findByAdminWardId(tradeLicense,
					requestInfoWrapper);

			if (boundaryResponse == null || boundaryResponse.getBoundarys() == null
					|| boundaryResponse.getBoundarys().size() == 0) {

				throw new InvalidAdminWardException(propertiesManager.getAdminWardErrorMsg(), requestInfo);
			}

		}
	}

	private void validateTradeCategoryDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// category validation
		if (tradeLicense.getCategoryId() != null) {

			CategorySearchResponse categoryResponse = categoryContractRepository.findByCategoryId(tradeLicense,
					requestInfoWrapper);

			if (categoryResponse == null || categoryResponse.getCategories() == null
					|| categoryResponse.getCategories().size() == 0) {

				throw new InvalidCategoryException(propertiesManager.getCategoryErrorMsg(), requestInfo);
			}
		}
	}

	private void validateTradeSubCategoryDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// subCategory validation
		if (tradeLicense.getSubCategoryId() != null) {

			CategorySearchResponse categoryResponse = categoryContractRepository.findBySubCategoryId(tradeLicense,
					requestInfoWrapper);

			if (categoryResponse == null || categoryResponse.getCategories() == null
					|| categoryResponse.getCategories().size() == 0) {

				throw new InvalidSubCategoryException(propertiesManager.getSubCategoryErrorMsg(), requestInfo);

			} else {

				Long validityYears = categoryResponse.getCategories().get(0).getValidityYears();

				if (Long.valueOf(validityYears) != Long.valueOf(tradeLicense.getValidityYears())) {

					throw new InvalidValidityYearsException(propertiesManager.getValidatiyYearsMatch(), requestInfo);
				}
			}
		}
	}

	private void validateTradeSupportingDocuments(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// supporting documents validation
		if (!tradeLicense.getIsLegacy()) {

			DocumentTypeV2Response documentTypeMandatoryResponse = documentTypeContractRepository
					.findTradeMandatoryDocuments(tradeLicense, requestInfoWrapper);

			// validating mandatory documents existence
			if (documentTypeMandatoryResponse != null && documentTypeMandatoryResponse.getDocumentTypes() != null
					&& documentTypeMandatoryResponse.getDocumentTypes().size() > 0) {

				for (DocumentTypeContract documentTypeContract : documentTypeMandatoryResponse.getDocumentTypes()) {

					Boolean isMandatoryDocumentExists = Boolean.FALSE;

					if (tradeLicense.getApplication() != null
							&& tradeLicense.getApplication().getSupportDocuments() != null
							&& tradeLicense.getApplication().getSupportDocuments().size() > 0) {

						for (SupportDocument supportDocument : tradeLicense.getApplication().getSupportDocuments()) {

							if (documentTypeContract.getId() == supportDocument.getDocumentTypeId()) {

								isMandatoryDocumentExists = Boolean.TRUE;
							}
						}
					} else {

						throw new MandatoryDocumentNotFoundException(
								propertiesManager.getMandatoryDocumentNotFoundCustomMsg(), requestInfo);
					}

					if (!isMandatoryDocumentExists) {

						throw new MandatoryDocumentNotFoundException(
								propertiesManager.getMandatoryDocumentNotFoundCustomMsg(), requestInfo);
					}
				}
			}

			// validating request supporting documents
			if (tradeLicense.getApplication() != null && tradeLicense.getApplication().getSupportDocuments() != null
					&& tradeLicense.getApplication().getSupportDocuments().size() > 0) {

				for (SupportDocument supportDocument : tradeLicense.getApplication().getSupportDocuments()) {

					DocumentTypeV2Response documentTypeResponse = documentTypeContractRepository
							.findByIdAndTlValues(tradeLicense, supportDocument, requestInfoWrapper);

					if (documentTypeResponse == null || documentTypeResponse.getDocumentTypes() == null
							|| documentTypeResponse.getDocumentTypes().size() == 0) {

						throw new InvalidDocumentTypeException(propertiesManager.getDocumentTypeErrorMsg(),
								requestInfo);
					}
				}

			}

		} else {

			if (tradeLicense.getSupportDocuments() != null && tradeLicense.getSupportDocuments().size() > 0) {
				for (SupportDocument supportDocument : tradeLicense.getSupportDocuments()) {

					DocumentTypeV2Response documentTypeResponse = documentTypeContractRepository
							.findByIdAndTlValues(tradeLicense, supportDocument, requestInfoWrapper);

					if (documentTypeResponse == null || documentTypeResponse.getDocumentTypes() == null
							|| documentTypeResponse.getDocumentTypes().size() == 0) {

						throw new InvalidDocumentTypeException(propertiesManager.getDocumentTypeErrorMsg(),
								requestInfo);
					}
				}

			}
		}

	}

	private void validateTradeUomDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// uom validation
		if (tradeLicense.getUomId() != null) {

			CategorySearchResponse categoryResponse = categoryContractRepository.findBySubCategoryUomId(tradeLicense,
					requestInfoWrapper);

			if (categoryResponse == null || categoryResponse.getCategories() == null
					|| categoryResponse.getCategories().size() == 0) {

				throw new InvalidUomException(propertiesManager.getUomErrorMsg(), requestInfo);

			} else {

				for (CategorySearch category : categoryResponse.getCategories()) {

					if (category.getDetails() == null && category.getDetails().size() == 0) {

						throw new InvalidUomException(propertiesManager.getUomErrorMsg(), requestInfo);

					} else {

						Boolean isExists = false;

						for (CategoryDetailSearch categoryDetail : category.getDetails()) {

							if (categoryDetail.getUomId() == tradeLicense.getUomId()) {

								isExists = true;
							}
						}

						if (!isExists) {

							throw new InvalidUomException(propertiesManager.getUomErrorMsg(), requestInfo);
						}
					}
				}
			}

		}
	}

	private void validateTradeFeeDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		// checking feeDetails existence
		if (tradeLicense.getFeeDetails() == null || tradeLicense.getFeeDetails().size() == 0) {

			throw new LegacyFeeDetailNotFoundException(propertiesManager.getLegacyFeeDetailsNotFoundMsg(), requestInfo);
		}

		if (tradeLicense.getFeeDetails() != null && tradeLicense.getFeeDetails().size() > 0) {

			Long validFrom = tradeLicense.getLicenseValidFromDate();
			Integer validPeriod = null;
			String tenantId = tradeLicense.getTenantId();
			// get the license validity period
			if (tradeLicense.getValidityYears() != null) {

				validPeriod = Integer.valueOf(tradeLicense.getValidityYears().toString());
			}
			// get the system current date
			Long currenDate = (System.currentTimeMillis());
			Calendar today = Calendar.getInstance();
			// if both license valid from date and validity period exists then
			// only proceed for feeDetails validation
			if (validFrom != null && validPeriod != null) {

				FinancialYearContract currentFYResponse = financialYearContractRepository
						.findFinancialYearIdByDate(tenantId, currenDate, requestInfoWrapper);
				FinancialYearContract licenseValidFYResponse = financialYearContractRepository
						.findFinancialYearIdByDate(tenantId, validFrom, requestInfoWrapper);

				String currentFinancialYear = null;
				String licenseValidFromFinancialYear = null;
				Integer currentFinancialFromValue = null;
				Integer licenseValidFinancialFromValue = null;
				Integer actualFeeDetailStartYear = null;
				Integer actualFeeDetailsEndYear = null;
				if (currentFYResponse != null && licenseValidFYResponse != null) {

					// current financial year details
					currentFinancialYear = currentFYResponse.getFinYearRange();
					currentFinancialFromValue = Integer.valueOf(currentFinancialYear.split("-")[0]);
					// license valid financial year details
					licenseValidFromFinancialYear = licenseValidFYResponse.getFinYearRange();
					licenseValidFinancialFromValue = Integer.valueOf(licenseValidFromFinancialYear.split("-")[0]);
					// get the fee details starting year
					if ((currentFinancialFromValue - licenseValidFinancialFromValue) >= 5) {
						actualFeeDetailStartYear = (currentFinancialFromValue - 5);
					} else {
						actualFeeDetailStartYear = licenseValidFinancialFromValue;
					}
					// getting fee details ending year
					actualFeeDetailsEndYear = currentFinancialFromValue;
				} else {

					throw new EndPointException(propertiesManager.getLocationEndPointError(), requestInfo);
				}
				// get the actual fee detail record count
				Integer actualFeeDetailCount = 0;
				for (int i = actualFeeDetailStartYear; i <= actualFeeDetailsEndYear;) {
					actualFeeDetailCount = actualFeeDetailCount + 1;
					i = i + validPeriod;
				}
				// validate given fee detail count
				if (actualFeeDetailCount != tradeLicense.getFeeDetails().size()) {
					throw new InvalidFeeDetailException(propertiesManager.getFeeDetailsErrorMsg(), requestInfo);
				}
				//set the fee details calculation start date and year
				Date tradeValidFromDate = new Date(validFrom);
				today.setTimeInMillis(tradeValidFromDate.getTime());
				if((actualFeeDetailStartYear - licenseValidFinancialFromValue) == 0){
					today.add(Calendar.YEAR, 0);
				} else {
					today.add(Calendar.YEAR, (actualFeeDetailStartYear - licenseValidFinancialFromValue));
				}
				// validate the fee details
				for (int i = 0; i < actualFeeDetailCount; i++) {

					Boolean isFYExists = false;
					today.add(Calendar.YEAR, (i * validPeriod));

					FinancialYearContract feeDetailFYResponse = financialYearContractRepository
							.findFinancialYearIdByDate(tenantId, today.getTimeInMillis(), requestInfoWrapper);
					if (feeDetailFYResponse != null) {
						for (LicenseFeeDetail licenseFeeDetail : tradeLicense.getFeeDetails()) {
							if (licenseFeeDetail.getFinancialYear()
									.equalsIgnoreCase(feeDetailFYResponse.getFinYearRange())) {
								isFYExists = true;
								licenseFeeDetail.setFinancialYear(feeDetailFYResponse.getId().toString());
								// update the new expire date based on fee paid
								if(isFYExists){
									if(i==0){
										if(!licenseFeeDetail.getPaid()){
											today.setTimeInMillis(tradeValidFromDate.getTime());
										}
										today.add(Calendar.YEAR, (validPeriod - 1));
										feeDetailFYResponse = financialYearContractRepository.findFinancialYearIdByDate(tenantId,
												(today.getTimeInMillis()), requestInfoWrapper);
										if (feeDetailFYResponse != null) {
											tradeLicense.setExpiryDate(feeDetailFYResponse.getEndingDate().getTime());
										}
									} else if(licenseFeeDetail.getPaid()){
										today.add(Calendar.YEAR, (validPeriod - 1));
										feeDetailFYResponse = financialYearContractRepository.findFinancialYearIdByDate(tenantId,
												(today.getTimeInMillis()), requestInfoWrapper);
										if (feeDetailFYResponse != null) {
											tradeLicense.setExpiryDate(feeDetailFYResponse.getEndingDate().getTime());
										}
									}
									
								}
							}
						}
						if (!isFYExists) {
							throw new InvalidFeeDetailException(propertiesManager.getFeeDetailYearNotFound(),
									requestInfo);
						}
					} else {
						throw new InvalidFeeDetailException(propertiesManager.getFeeDetailYearNotFound(), requestInfo);
					}

					/*if (i == actualFeeDetailCount - 1) {
						today.setTime(feeDetailFYResponse.getEndingDate());
						today.add(Calendar.YEAR, (validPeriod - 1));
						feeDetailFYResponse = financialYearContractRepository.findFinancialYearIdByDate(tenantId,
								(today.getTimeInMillis()), requestInfoWrapper);
						if (feeDetailFYResponse != null) {
							tradeLicense.setExpiryDate(feeDetailFYResponse.getEndingDate().getTime());
						}
					}*/
					// reset the date to fee details calculation start date
					today.setTimeInMillis(tradeValidFromDate.getTime());
					if((actualFeeDetailStartYear - licenseValidFinancialFromValue) == 0){
						today.add(Calendar.YEAR, 0);
					} else {
						today.add(Calendar.YEAR, (actualFeeDetailStartYear - licenseValidFinancialFromValue));
					}
				}
			}
		}

	}

	private void validateTradeUpdateSupportDocuments(TradeLicense tradeLicense, RequestInfo requestInfo) {

		if (tradeLicense.getSupportDocuments() != null && tradeLicense.getSupportDocuments().size() > 0) {

			for (SupportDocument supportDocument : tradeLicense.getSupportDocuments()) {

				if (tradeLicense.getAuditDetails() != null) {
					supportDocument.setAuditDetails(tradeLicense.getAuditDetails());
				}
				if (supportDocument.getId() != null) {
					// check id existence in database
					// supportDocument.setLicenseId(tradeLicense.getId());
					tradeLicenseRepository.validateTradeLicenseSupportDocumentId(supportDocument, requestInfo);
				} else {
					// get the next sequence of support document id and set it
					// supportDocument.setLicenseId(tradeLicense.getId());
					supportDocument.setId(tradeLicenseRepository.getSupportDocumentNextSequence());
				}
			}
		}
	}

	private void validateTradeUpdateFeeDetails(TradeLicense tradeLicense, RequestInfo requestInfo) {

		if (tradeLicense.getFeeDetails() != null && tradeLicense.getFeeDetails().size() > 0) {

			for (LicenseFeeDetail licenseFeeDetail : tradeLicense.getFeeDetails()) {

				if (tradeLicense.getAuditDetails() != null) {
					licenseFeeDetail.setAuditDetails(tradeLicense.getAuditDetails());
				}
				if (licenseFeeDetail.getId() != null) {
					// check id existence in database
					// licenseFeeDetail.setLicenseId(tradeLicense.getId());
					tradeLicenseRepository.validateTradeLicenseFeeDetailId(licenseFeeDetail, requestInfo);
				} else {
					// get the next sequence of fee detail id and set it
					// licenseFeeDetail.setLicenseId(tradeLicense.getId());
					licenseFeeDetail.setId(tradeLicenseRepository.getFeeDetailNextSequence());
				}
			}
		}
	}

	private void setTradeExpiryDateByValidatingCommencementDate(TradeLicense tradeLicense, RequestInfo requestInfo) {

		RequestInfoWrapper requestInfoWrapper = new RequestInfoWrapper();
		requestInfoWrapper.setRequestInfo(requestInfo);

		String tenantId = tradeLicense.getTenantId();
		String currentFinancialYearRange = null;
		String commencementFinancialYearRange = null;
		Integer currentFinancialFromValue = null;
		Integer previousFinancialFromValue = null;
		Integer nextFinancialFromValue = null;
		Integer commencementDateFinancialFromValue = null;
		// get the trade commencement date
		Long commencementDate = tradeLicense.getTradeCommencementDate();
		// get the system current date
		Long currenDate = (System.currentTimeMillis());
		Calendar today = Calendar.getInstance();
		Date tradeValidFromDate = new Date(commencementDate);
		Integer validPeriod = null;

		// get the license validity period
		if (tradeLicense.getValidityYears() != null) {

			validPeriod = Integer.valueOf(tradeLicense.getValidityYears().toString());
		}

		today.setTimeInMillis(tradeValidFromDate.getTime());

		// get the current financial year
		FinancialYearContract currentFYResponse = financialYearContractRepository.findFinancialYearIdByDate(tenantId,
				currenDate, requestInfoWrapper);

		if (currentFYResponse != null) {

			currentFinancialYearRange = currentFYResponse.getFinYearRange();
			currentFinancialFromValue = Integer.valueOf(currentFinancialYearRange.split("-")[0]);
			previousFinancialFromValue = currentFinancialFromValue - Integer.valueOf(1);
			nextFinancialFromValue = currentFinancialFromValue + Integer.valueOf(1);
		}

		// get the trade commencement date financial year
		FinancialYearContract commencementFYResponse = financialYearContractRepository
				.findFinancialYearIdByDate(tenantId, commencementDate, requestInfoWrapper);

		if (commencementFYResponse != null) {

			commencementFinancialYearRange = commencementFYResponse.getFinYearRange();
			commencementDateFinancialFromValue = Integer.valueOf(commencementFinancialYearRange.split("-")[0]);

			// validate trade commencement date
			if ((commencementDateFinancialFromValue.equals(currentFinancialFromValue))
					|| (commencementDateFinancialFromValue.equals(previousFinancialFromValue))
					|| (commencementDateFinancialFromValue.equals(nextFinancialFromValue))) {

				today.setTime(commencementFYResponse.getEndingDate());

			} else {

				throw new CustomInvalidInputException(propertiesManager.getTradeCommencementDateNotValidCode(),
						propertiesManager.getTradeCommencementDateNotValidMsg(), requestInfo);
			}

		} else {

			throw new CustomInvalidInputException(propertiesManager.getTradeCommencementDateNotValidCode(),
					propertiesManager.getTradeCommencementDateNotValidMsg(), requestInfo);
		}

		// setting expire date
		today.add(Calendar.YEAR, (validPeriod - 1));
		commencementFYResponse = financialYearContractRepository.findFinancialYearIdByDate(tenantId,
				(today.getTimeInMillis()), requestInfoWrapper);
		if (commencementFYResponse != null) {
			tradeLicense.setExpiryDate(commencementFYResponse.getEndingDate().getTime());
		}
	}

	private void bindTradeNonModifiableFields(TradeLicense tradeLicense, RequestInfo requestInfo) {

		// checking support document id's
		validateTradeUpdateSupportDocuments(tradeLicense, requestInfo);
		// checking fee detail id's
		if (tradeLicense.getIsLegacy()) {
			validateTradeUpdateFeeDetails(tradeLicense, requestInfo);
		}

		TradeLicense license = tradeLicenseRepository.findByLicenseId(tradeLicense, requestInfo);

		if (license != null) {

			if(license.getIsLegacy()){
				tradeLicense.setLicenseNumber(license.getLicenseNumber());
				tradeLicense.getApplication().setId(license.getApplication().getId());
			}
			tradeLicense.setApplicationType(license.getApplicationType());
			tradeLicense.setApplicationNumber(license.getApplicationNumber());
			tradeLicense.setTenantId(license.getTenantId());
			tradeLicense.setApplicationDate(license.getApplicationDate());
		}
	}

}
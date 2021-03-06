package org.egov.egf.voucher.persistence.entity;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import org.egov.common.domain.model.Auditable;
import org.egov.common.persistence.entity.AuditableEntity;
import org.egov.egf.voucher.domain.model.VoucherSubType;
import org.egov.egf.voucher.domain.model.VoucherType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class VoucherSubTypeEntity extends AuditableEntity {

	public static final String TABLE_NAME = "egf_vouchersubtype";

	public static final String SEQUENCE_NAME = "seq_egf_vouchersubtype";
	
	private String id;

	private String voucherType;

	private String voucherName;

	private Date cutOffDate;

	private Boolean exclude;

	public VoucherSubType toDomain() {

		VoucherSubType voucherSubType = new VoucherSubType();
		super.toDomain(voucherSubType);

		voucherSubType.setId(this.id);
		voucherSubType.setVoucherType(VoucherType.valueOf(this.voucherType));
		voucherSubType.setVoucherName(this.voucherName);
		voucherSubType.setCutOffDate(this.cutOffDate);
		voucherSubType.setExclude(this.exclude);
		return voucherSubType;
	}

	public VoucherSubTypeEntity toEntity(VoucherSubType voucherSubType) {

		super.toEntity((Auditable) voucherSubType);

		this.id = voucherSubType.getId();
		this.voucherType = voucherSubType.getVoucherType().name();
		this.voucherName = voucherSubType.getVoucherName();
		this.cutOffDate = voucherSubType.getCutOffDate();
		this.exclude = voucherSubType.getExclude();
		
		return this;

	}

}

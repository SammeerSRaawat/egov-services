package org.egov.property.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown=true)
public class Tenant {

	private Long id;
	private String code;
	private String description;
	private String logoId;
	private String imageId;
	private String domainUrl;
	private String type;
	private String twitterUrl;
	private String facebookUrl;
	private String emailId;
	private String address;
	private String contactNumber;
	private String helpLineNumber;
	private City city;
}
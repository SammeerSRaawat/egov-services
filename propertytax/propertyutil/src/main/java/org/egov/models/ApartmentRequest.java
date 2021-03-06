package org.egov.models;

import java.util.List;

import javax.validation.Valid;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
/**
 * 
 * @author Yosadhara
 *
 */
public class ApartmentRequest {

	@JsonProperty("RequestInfo")
	private RequestInfo requestInfo;
	
	@Valid
	private List<Apartment> apartments;
}

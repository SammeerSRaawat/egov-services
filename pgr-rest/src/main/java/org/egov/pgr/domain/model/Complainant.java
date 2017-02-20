package org.egov.pgr.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;

import static org.springframework.util.StringUtils.isEmpty;


//A person who made a complaint
@Value
@AllArgsConstructor
@Builder
public class Complainant {
    private String firstName;
    private String phone;
    private String email;
    private String address;

    public boolean isAbsent() {
        return isEmpty(firstName) || isEmpty(phone) || isEmpty(email);
    }
}

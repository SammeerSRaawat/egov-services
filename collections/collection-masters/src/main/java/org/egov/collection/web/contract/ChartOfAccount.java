package org.egov.collection.web.contract;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Setter
@Getter
@ToString
public class ChartOfAccount {
  private Long id;

  private String glcode;

  private String name;

  private String desciption;

  private Boolean isActiveForPosting;

}


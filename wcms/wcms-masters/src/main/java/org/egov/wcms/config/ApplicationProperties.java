
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

package org.egov.wcms.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;

@Configuration
@PropertySource(value = { "classpath:config/application-config.properties" }, ignoreResourceNotFound = true)
@Order(0)
public class ApplicationProperties {
	private static final String WCMS_SEARCH_PAGESIZE_DEFAULT = "egov.services.wcms.search.pagesize.default";
	public static final String WCMS_SEARCH_PAGENO_MAX = "egov.services.wcms.search.pageno.max";
	public static final String WCMS_SEARCH_PAGESIZE_MAX = "egov.services.wcms.search.pagesize.max";

	@Value("${kafka.topics.usagetype.create.name}")
	private String createUsageTypeTopicName;

	@Value("${kafka.topics.usagetype.update.name}")
	private String updateUsageTypeTopicName;

	@Value("${kafka.topics.category.create.name}")
	private String createCategoryTopicName;

	@Value("${kafka.topics.category.update.name}")
	private String updateCategoryTopicName;

	@Autowired
	private Environment environment;

	public String wcmsSearchPageSizeDefault() {
		return this.environment.getProperty(WCMS_SEARCH_PAGESIZE_DEFAULT);
	}

	public String wcmsSearchPageNumberMax() {
		return this.environment.getProperty(WCMS_SEARCH_PAGENO_MAX);
	}

	public String wcmsSearchPageSizeMax() {
		return this.environment.getProperty(WCMS_SEARCH_PAGESIZE_MAX);
	}


	public String getCreateUsageTypeTopicName() {
		return createUsageTypeTopicName;
	}

	public String getUpdateUsageTypeTopicName() {
		return updateUsageTypeTopicName;
	}

	public String getCreateCategoryTopicName(){
		return createCategoryTopicName;
	}

	public String getUpdateCategoryTopicName(){
		return updateCategoryTopicName;
	}

}
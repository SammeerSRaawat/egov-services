package org.egov.eis.service.helper;

import org.egov.eis.config.ApplicationProperties;
import org.egov.eis.config.PropertiesManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;

@Component
public class AttendanceSearchURLHelper {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private PropertiesManager propertiesManager;

    public String searchURL(final String tenantId, final Date validfromDate, List<Long> employeeIds, final String url) {
        final StringBuilder searchURL = new StringBuilder(url + "?");

        if (tenantId == null)
            return searchURL.toString();
        else
            searchURL.append("tenantId=" + tenantId);

        if (!employeeIds.isEmpty() && null != employeeIds)
            searchURL.append("&emloyeeIds=" + employeeIds);

        if (validfromDate != null && !validfromDate.equals(""))
            searchURL.append("&fromDate=" + validfromDate);

        searchURL.append("&pageSize=" + applicationProperties.hrLeaveSearchPageSizeMax());

        return searchURL.toString();
    }

}
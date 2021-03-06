package org.egov.propertytax.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 
 * @author Yosadhara
 *
 */
@Configuration
@ToString
@NoArgsConstructor
@SuppressWarnings("unused")
@Service
public class PropertiesManager {

	@Autowired
	Environment environment;

	private String serverContextPath;

	private String dbUrl;

	private String driverClassName;

	private String username;

	private String password;

	private String flywayEnabled;

	private String flywayUser;

	private String flywayPassword;

	private String flywayOutOfOrder;

	private String flywayTable;

	private String flywayUrl;

	private String flywayLocations;

	private String createPropertyTaxGenerated;

	private String billingServiceHostName;

	private String billingServiceCreatedDemand;

	private String bootstrapServer;

	private String autoOffsetReset;

	private String createPropertyTaxCalculated;

	private String createPropertyWorkflow;
	
	private String demandBusinessService;
	
	private String dateFormat;

	public String getServerContextPath() {
		return environment.getProperty("server.contextPath");
	}

	public String getDbUrl() {
		return environment.getProperty("spring.datasource.url");
	}

	public String getDriverClassName() {
		return environment.getProperty("spring.datasource.driver-class-name");
	}

	public String getUsername() {
		return environment.getProperty("spring.datasource.username");
	}

	public String getPassword() {
		return environment.getProperty("spring.datasource.password");
	}

	public String getFlywayEnabled() {
		return environment.getProperty("flyway.enabled");
	}

	public String getFlywayUser() {
		return environment.getProperty("flyway.user");
	}

	public String getFlywayPassword() {
		return environment.getProperty("flyway.password");
	}

	public String getFlywayOutOfOrder() {
		return environment.getProperty("flyway.outOfOrder");
	}

	public String getFlywayTable() {
		return environment.getProperty("flyway.table");
	}

	public String getFlywayUrl() {
		return environment.getProperty("flyway.url");
	}

	public String getFlywayLocations() {
		return environment.getProperty("flyway.locations");
	}

	public String getCreatePropertyTaxGenerated() {
		return environment.getProperty("egov.propertytax.property.tax.generated");
	}

	public String getBillingServiceHostName() {
		return environment.getProperty("egov.services.billing_service.hostname");
	}

	public String getBillingServiceCreatedDemand() {
		return environment.getProperty("egov.services.billing_service.createdemand");
	}

	public String getBootstrapServer() {
		return environment.getProperty("spring.kafka.bootstrap.servers");
	}

	public String getAutoOffsetReset() {
		return environment.getProperty("auto.offset.reset.config");
	}

	public String getCreatePropertyTaxCalculated() {
		return environment.getProperty("egov.propertytax.create.tax.calculated");
	}

	public String getCreatePropertyWorkflow() {
		return environment.getProperty("egov.propertytax.create.workflow.started");
	}
	
	public String getDemandBusinessService(){
		return environment.getProperty("businessService");
	}
	
	public String getDateFormat(){
		return environment.getProperty("dd/MM/yyyy hh:mm:ss");
	}
}

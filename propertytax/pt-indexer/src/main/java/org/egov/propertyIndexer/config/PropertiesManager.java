package org.egov.propertyIndexer.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

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
public class PropertiesManager {

	@Autowired
	Environment environment;

	private String bootstrapServer;

	private String consumerOffset;

	private String consumerGroup;

	private String createWorkflow;

	private String updateWorkflow;

	private String approveWorkflow;

	private String esHost;

	private String esPort;

	private String isMultiThread;

	private String timeout;

	private String propertyIndex;

	private String propertyIndexType;

	private String serverContextpath;
	
	private String createTitleTranfer;
	
	private String updateTitleTransfer;
	
	private String titleTransferType;
	
	private String approveTitleTransfer;
	
	private String titleTransferIndex;

	public String getBootstrapServer() {
		return environment.getProperty("spring.kafka.bootstrap.servers");
	}

	public String getConsumerOffset() {
		return environment.getProperty("consumer.offset");
	}

	public String getConsumerGroup() {
		return environment.getProperty("consumer.group");
	}

	public String getCreateWorkflow() {
		return environment.getProperty("egov.propertytax.property.create.workflow.started");
	}

	public String getUpdateWorkflow() {
		return environment.getProperty("egov.propertytax.property.update.workflow.started");
	}

	public String getApproveWorkflow() {
		return environment.getProperty("egov.propertytax.property.update.workflow.approved");
	}

	public String getEsHost() {
		return environment.getProperty("es.host");
	}

	public String getEsPort() {
		return environment.getProperty("es.port");
	}

	public String getIsMultiThread() {
		return environment.getProperty("multiThread");
	}

	public String getTimeout() {
		return environment.getProperty("timeout");
	}

	public String getPropertyIndex() {
		return environment.getProperty("property.index");
	}

	public String getPropertyIndexType() {
		return environment.getProperty("property.index.type");
	}

	public String getServerContextpath() {
		return environment.getProperty("server.contextpath");
	}

	public String getTitleTransferType() {
		return environment.getProperty("titletransfer.index.type");
	}
	
	public String getTitleTransferIndex() {
		return environment.getProperty("titletransfer.index");		
	}

	public String getCreateTitleTranfer() {
		return environment.getProperty("egov.propertytax.property.titletransfer.workflow.created");
	}


	public String getUpdateTitleTransfer() {
		return environment.getProperty("egov.propertytax.property.titletransfer.workflow.updated");
	}


	public String getApproveTitleTransfer() {
		return environment.getProperty("egov.propertytax.property.titletransfer.db.saved");
	}


}
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

package org.egov.collection.consumer;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.egov.collection.config.ApplicationProperties;
import org.egov.collection.service.ReceiptService;
import org.egov.collection.web.contract.ReceiptReq;
import org.egov.collection.web.contract.WorkflowDetailsRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class CollectionConsumer {
	
	public static final Logger logger = LoggerFactory.getLogger(CollectionConsumer.class);
	
	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Autowired 
	private ReceiptService recieptService;	
	
	@KafkaListener(topics = {"${kafka.topics.receipt.create.name}", "${kafka.topics.receipt.cancel.name}",
			"${kafka.topics.stateId.update.name}","${kafka.topics.receipt.update.name}"})
	
	public void listen(final HashMap<String, Object> record, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
	logger.info("Record: "+record.toString());
		final ObjectMapper objectMapper = new ObjectMapper();
		objectMapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
		try {
             if(topic.equals(applicationProperties.getCancelReceiptTopicName())){
				logger.info("Consuming cancel Receipt request");
				recieptService.cancelReceiptBeforeRemittance(objectMapper.convertValue(record, ReceiptReq.class));
			}else if(topic.equals(applicationProperties.getKafkaUpdateStateIdTopic())){
				logger.info("Consuming updateStateId request");
				recieptService.updateStateId(objectMapper.convertValue(record, WorkflowDetailsRequest.class));
			}else if(topic.equals(applicationProperties.getUpdateReceiptTopicName())){
				logger.info("Consuming update Receipt request");
				recieptService.updateReceipt(objectMapper.convertValue(record, WorkflowDetailsRequest.class));
			}
			
		} catch (final Exception e) {
			logger.error("Error while listening to value: "+record+" on topic: "+topic+": ", e.getMessage());
		}
	}

}
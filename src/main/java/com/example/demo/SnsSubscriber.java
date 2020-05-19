package com.example.demo;

import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.model.SubscribeResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import javax.annotation.PreDestroy;

@Slf4j
@Component
public class SnsSubscriber {

	@Autowired
	private AmazonSNS sns;
	@Autowired
	private ApplicationProperties properties;

	private String subscriptionArn;

	public ApplicationProperties.Aws.Sns.Subscription getProperties() {
		return properties.getAws().getSns().getFooSubscription();
	}

	@EventListener(ApplicationReadyEvent.class)
	public void subscribe() {
		log.info("Subscribing");
		SubscribeResult subscribeResult = sns.subscribe(getProperties().getTopicArn(), getProperties().getProtocol(), getProperties().getEndpointBaseUri() + getProperties().getEndpointPath());
		subscriptionArn = subscribeResult.getSubscriptionArn();

	}

	@PreDestroy
	public void unsubscribe() {
		log.info("Unsubscribing");
		sns.unsubscribe(subscriptionArn);
	}

}

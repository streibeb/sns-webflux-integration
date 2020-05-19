package com.example.demo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.aws.inbound.SnsInboundChannelAdapter;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.web.HttpRequestHandler;

import java.util.concurrent.TimeUnit;

@Configuration
public class SnsConfiguration {

	@Autowired
	private ApplicationProperties properties;

	public ApplicationProperties.Aws.Sns.Subscription getSubscriptionProperties() {
		return properties.getAws().getSns().getFooSubscription();
	}

	@Bean
	public AmazonSNSAsync amazonSNS() {
		com.amazonaws.ClientConfiguration clientConfiguration = new com.amazonaws.ClientConfiguration()
				.withSocketTimeout((int) TimeUnit.SECONDS.toMillis(70));

		return AmazonSNSAsyncClientBuilder.standard()
				.withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials("accesskey", "secretkey")))
				.withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration("http://localhost:4575/", "us-east-1"))
				.withClientConfiguration(clientConfiguration)
				.build();
	}

	@Bean
	public NotificationMessagingTemplate notificationMessagingTemplate(AmazonSNS amazonSNS) {
		return new NotificationMessagingTemplate(amazonSNS);
	}

	@Bean("channel")
	public PublishSubscribeChannel channel() {
		return MessageChannels.publishSubscribe("channel").get();
	}

	@Bean
	public HttpRequestHandler snsMessageDrivenChannelAdapter() {
		SnsInboundChannelAdapter adapter = new SnsInboundChannelAdapter(amazonSNS(), getSubscriptionProperties().getEndpointPath());
		adapter.setRequestChannel(channel());
		adapter.setHandleNotificationStatus(true);
		return adapter;
	}

}

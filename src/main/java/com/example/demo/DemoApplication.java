package com.example.demo;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sns.AmazonSNS;
import com.amazonaws.services.sns.AmazonSNSAsync;
import com.amazonaws.services.sns.AmazonSNSAsyncClientBuilder;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.aws.messaging.core.NotificationMessagingTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.integration.aws.inbound.SnsInboundChannelAdapter;
import org.springframework.integration.channel.PublishSubscribeChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.web.HttpRequestHandler;

import java.util.concurrent.TimeUnit;

@SpringBootApplication
@EnableIntegration
public class DemoApplication {

	@Bean
	@Primary
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
		SnsInboundChannelAdapter adapter = new SnsInboundChannelAdapter(amazonSNS(), "/foo");
		adapter.setRequestChannel(channel());
		adapter.setHandleNotificationStatus(true);
		return adapter;
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

}

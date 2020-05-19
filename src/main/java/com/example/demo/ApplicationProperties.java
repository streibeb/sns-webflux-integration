package com.example.demo;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("app")
@Data
public class ApplicationProperties {

	private Aws aws = new Aws();

	@Data
	public static class Aws {

		private Sns sns = new Sns();

		@Data
		public static class Sns {

			private Subscription fooSubscription = new Subscription();

			@Data
			public static class Subscription {

				private String topicArn;
				private String protocol;
				private String endpointBaseUri;
				private String endpointPath = "/foo";

			}

		}
	}

}

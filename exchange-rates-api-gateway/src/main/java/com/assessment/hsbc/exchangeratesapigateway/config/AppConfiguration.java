package com.assessment.hsbc.exchangeratesapigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.assessment.hsbc.exchangeratesapigateway.filters.SimpleFilter;

@Configuration
public class AppConfiguration {

	@Bean
	public SimpleFilter simpleFilter() {
		return new SimpleFilter();
	}
}

package com.example.demo.configuration;

import com.example.demo.util.MetricsFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MetricsConfiguration {

    @Bean
    public FilterRegistrationBean<MetricsFilter> loggingFilter(MetricsFilter metricsFilter) {
        FilterRegistrationBean<MetricsFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(metricsFilter);
        registrationBean.addUrlPatterns("/*");
        return registrationBean;
    }
}
package com.project2.global.app;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

	@Value("${custom.file.upload-dir}")
	private String uploadDir;

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/" + uploadDir + "**")
			.addResourceLocations("file:" + uploadDir)
			.setCachePeriod(3600)
			.resourceChain(true);

		registry.addResourceHandler("/**")
			.addResourceLocations("classpath:/static/");
	}
}
/**
 * 
 */
package com.saroj.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

/**
 * @author sarojrout
 *
 */
@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.saroj.controller")
public class WebConfig extends WebMvcConfigurerAdapter {

	@Override public void configureMessageConverters(List<HttpMessageConverter<?>> converters){
		MappingJackson2HttpMessageConverter converter=new MappingJackson2HttpMessageConverter();
		  List<MediaType> mediatypes=new ArrayList<MediaType>();
		  mediatypes.add(MediaType.APPLICATION_JSON);
		  converter.setSupportedMediaTypes(mediatypes);
		  converters.add(converter);
		}
	
	@Bean
	public InternalResourceViewResolver viewResolver() {
		InternalResourceViewResolver resolver = new InternalResourceViewResolver();
		resolver.setPrefix("/WEB-INF/pages/");
		resolver.setSuffix(".jsp");
		return resolver;
	}

}

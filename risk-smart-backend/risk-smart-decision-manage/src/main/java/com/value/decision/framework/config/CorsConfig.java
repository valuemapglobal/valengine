package com.value.decision.framework.config;

import org.springframework.boot.web.servlet.MultipartConfigFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import jakarta.servlet.MultipartConfigElement;
import java.io.File;


/**
 * 跨域配置
 *
 * @author vlauemap team
 * @since 2026/01/22
 */
@Configuration
public class CorsConfig  {
	private CorsConfiguration buildConfig() {
		CorsConfiguration config = new CorsConfiguration();
		config.addAllowedOriginPattern("*");// 允许域名
		config.addAllowedHeader("*");// 允许header设置
		config.addAllowedMethod("*");// 任何方法,post get 等
		// allowCredential 需设置为true
		config.setAllowCredentials(true);
		return config;
	}

	@Bean
	public CorsFilter corsFilter() {
		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", buildConfig());
		return new CorsFilter(source);
	}
	
    /**
    *文件临时上传路径
    */
  @Bean
  MultipartConfigElement multipartConfigElement() {
     MultipartConfigFactory factory = new MultipartConfigFactory();
     String location  = System.getProperty("user.dir") +"/data/tmp";
     File tmpFile   =new File (location);
       if(!tmpFile.exists()){
        tmpFile.mkdirs();
         }
     factory.setLocation(location);
     return factory.createMultipartConfig();
 }  
}
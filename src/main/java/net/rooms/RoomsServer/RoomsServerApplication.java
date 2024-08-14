package net.rooms.RoomsServer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.boot.web.servlet.ServletListenerRegistrationBean;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@ServletComponentScan
public class RoomsServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(RoomsServerApplication.class, args);
	}

	@Bean
	public ServletListenerRegistrationBean<CustomSessionListener> sessionListener() {
		return new ServletListenerRegistrationBean<>(new CustomSessionListener());
	}
}

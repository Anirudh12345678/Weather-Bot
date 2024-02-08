package com.example.WeatherMail;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import java.io.IOException;
import java.net.URISyntaxException;

@SpringBootApplication
public class WeatherMailApplication{
	@Autowired
	private Weather weather;
	public static void main(String[] args) {
		SpringApplication.run(WeatherMailApplication.class, args);
	}
	@EventListener(ApplicationReadyEvent.class)
	public void triggerMail() throws MessagingException, URISyntaxException, IOException {
		weather.mail();
	}
}

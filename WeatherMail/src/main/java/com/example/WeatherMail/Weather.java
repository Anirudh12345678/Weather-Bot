package com.example.WeatherMail;

import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;

@Service
@Component
public class Weather {
//    @Autowired
//    private JavaMailSender sender;
//    @Autowired
//    private MailProperties mailProperties;

    @Autowired
    private WeatherManager manager;
    public void mail() throws MessagingException, URISyntaxException, IOException {
        manager.createEndPoint();
    }

}

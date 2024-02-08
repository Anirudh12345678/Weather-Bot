package com.example.WeatherMail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Calendar;

@Service
@Component
public class WeatherManager {
    @Autowired

    private JavaMailSender sender;
    @Autowired

    private MailProperties mailProperties;
    static String endPoint = "https://weather.visualcrossing.com/VisualCrossingWebServices/rest/services/timeline/";
    static String Location = "";
    static String unitGroup = "metric";
    static String Key = "";
    static String time = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss").format(Calendar.getInstance().getTime()).replace('_','T');

    public void createEndPoint() throws URISyntaxException, IOException, MessagingException {
        StringBuilder req = new StringBuilder(endPoint);
        req.append(Location).append('/').append(time);
        URIBuilder builder = new URIBuilder(req.toString()).setParameter("unitGroup",unitGroup).setParameter("key",Key).setParameter("contentType","json").setParameter("elements","tempmax,tempmin,description");
        accessAPI(builder);
    }

    public void accessAPI(URIBuilder builder) throws IOException, URISyntaxException, MessagingException {
        System.out.println(builder.toString());
        HttpGet get = new HttpGet(builder.build());

        CloseableHttpClient httpclient = HttpClients.createDefault();

        CloseableHttpResponse response = httpclient.execute(get);
        String rawResult;
        try {
            if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
                System.out.printf("Bad response status code:%d%n",
                        response.getStatusLine().getStatusCode());
                return;
            }
            HttpEntity entity = response.getEntity();
            rawResult = EntityUtils.toString(entity, StandardCharsets.UTF_8);
        } finally {
            response.close();
        }
        //parseTimelineJson(rawResult);
        generateMessage(rawResult);
    }

    public void generateMessage(String res) throws MessagingException {
        StringBuilder message = new StringBuilder();
        JSONObject response = new JSONObject(res);
        message.append("Your forecast for today:\n");
        message.append(response.getString("description")+'\n');
        JSONArray arr = response.getJSONArray("days");
        message.append("Temperature may reach upto : " + (arr.getJSONObject(0).getInt("tempmax")) + " C" + '\n');
        message.append("And may reach as low as : "+ (arr.getJSONObject(0).getInt("tempmin")) + " C");
        sendMail(message.toString());
    }
    public void sendMail(String mes) throws MessagingException {
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage,true);
        helper.setSubject("Today's Forecast");
        helper.setFrom(mailProperties.getUsername());
        helper.setText(mes);
        String [] recepients = {};
        helper.setTo(recepients);
        sender.send(mimeMessage);
        System.out.println("Mail sent!");
    }
}

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
    static String Location = "Chennai";
    static String unitGroup = "metric";
    static String Key = "5DJFECGBDEDQYRDLBQF9K9NSC";
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
        String [] recepients = {"anisiva2005@gmail.com","aniuma05@gmail.com","smahathi99@gmail.com","ksridhar2005@gmail.com"};
        helper.setTo(recepients);
        sender.send(mimeMessage);
        System.out.println("Mail sent!");
    }
//        String recipient = "anisiva2005@gmail.com";
//
//        // email ID of  Sender.
//        String sender = "anisiva2005@gmail.com";
//
//        // using host as localhost
//        String host = "192.168.0.103";
//
//        // Getting system properties
//        Properties properties = System.getProperties();
//
//        // Setting up mail server
//        properties.setProperty("mail.smtp.host", host);
//        properties.put("mail.smtp.port","25");
//        // creating session object to get properties
//        Session session = Session.getDefaultInstance(properties);
//        try
//        {
//            // MimeMessage object.
//            MimeMessage message = new MimeMessage(session);
//
//            // Set From Field: adding senders email to from field.
//            message.setFrom(new InternetAddress(sender));
//
//            // Set To Field: adding recipient's email to from field.
//            message.addRecipient(Message.RecipientType.TO, new InternetAddress(recipient));
//
//            // Set Subject: subject of the email
//            message.setSubject("Todays Forecast");
//
//            // set body of the email.
//            message.setText(mes);
//
//            // Send email.
//            Transport.send(message);
//            System.out.println("Mail successfully sent");
//        }
//        catch (MessagingException mex)
//        {
//            mex.printStackTrace();
//        }
//    }
}

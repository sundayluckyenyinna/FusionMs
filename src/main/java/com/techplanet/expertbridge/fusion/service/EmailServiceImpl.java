package com.techplanet.expertbridge.fusion.service;

// Importing required classes
import com.google.gson.Gson;
import com.techplanet.expertbridge.fusion.constant.ResponseCode;
import com.techplanet.expertbridge.fusion.payload.EmailDetailsPayload;
import com.techplanet.expertbridge.fusion.payload.GenericPayload;
import com.techplanet.expertbridge.fusion.payload.OmniResponsePayload;
import com.techplanet.expertbridge.fusion.security.AesService;
import java.io.File;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

// Annotation
@Service
// Class
// Implementing EmailService interface
public class EmailServiceImpl implements EmailService { //0000181002

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String sender;
    
    Gson gson;
    AesService aesService;

    // Method 1
    // To send a simple email
    @Override
    public String sendSimpleMail(String token, EmailDetailsPayload details) {

        // Try block to check for exceptions
        String message = "Error while Sending Mail";
        OmniResponsePayload omniResponse = new OmniResponsePayload();
        try {

            // Creating a simple mail message
            SimpleMailMessage mailMessage = new SimpleMailMessage();

            // Setting up necessary details
            mailMessage.setFrom(sender);
            mailMessage.setTo(details.getRecipient());
            mailMessage.setText(details.getMsgBody());
            mailMessage.setSubject(details.getSubject());

            // Sending the mail
            javaMailSender.send(mailMessage);
            message = "Mail Sent Successfully...";
            omniResponse.setResponseCode(ResponseCode.SUCCESS_CODE.getResponseCode());
        } // Catch block to handle the exceptions
        catch (MailException e) {
            omniResponse.setResponseCode(ResponseCode.FAILED_TRANSACTION.getResponseCode());
        }
        
        
        omniResponse.setResponseMessage(message);
        GenericPayload oGenericPayload = new GenericPayload();
        oGenericPayload.setResponse(aesService.encrypt(gson.toJson(omniResponse), details.getAppUser()));
        return gson.toJson(oGenericPayload);
    }

    // Method 2
    // To send an email with attachment
    @Override
    public String sendMailWithAttachment(String token, EmailDetailsPayload details) {
        // Creating a mime message
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper;

        try {

            // Setting multipart as true for attachments to
            // be send
            mimeMessageHelper = new MimeMessageHelper(mimeMessage, true);
            mimeMessageHelper.setFrom(sender);
            mimeMessageHelper.setTo(details.getRecipient());
            mimeMessageHelper.setText(details.getMsgBody());
            mimeMessageHelper.setSubject(details.getSubject());

            // Adding the attachment
            FileSystemResource file = new FileSystemResource(new File(details.getAttachment()));

            mimeMessageHelper.addAttachment(file.getFilename(), file);

            // Sending the mail
            javaMailSender.send(mimeMessage);
            return "Mail sent Successfully";
        } // Catch block to handle MessagingException
        catch (MessagingException e) {

            // Display message when exception occurred
            return "Error while sending mail!!!";
        }
    }
}

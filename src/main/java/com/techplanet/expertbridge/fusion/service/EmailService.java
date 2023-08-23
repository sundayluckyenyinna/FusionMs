package com.techplanet.expertbridge.fusion.service;

// Importing required classes

import com.techplanet.expertbridge.fusion.payload.EmailDetailsPayload;


// Interface
public interface EmailService {

	// Method
	// To send a simple email
	String sendSimpleMail(String token,EmailDetailsPayload details);

	// Method
	// To send an email with attachment
	String sendMailWithAttachment(String token,EmailDetailsPayload details);
}

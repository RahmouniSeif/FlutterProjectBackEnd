package com.campify.campifybackend.campifyBase.mailSender.services;

import com.campify.campifybackend.campifyBase.mailSender.models.MailRequest;
import jakarta.mail.MessagingException;

public interface MailService {
    void sendEmail(MailRequest request) throws MessagingException;
}


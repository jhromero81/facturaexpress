package com.codewise.facturaexpress.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean enviarFactura(String destinatario, String asunto, String cuerpo, byte[] pdfAdjunto, String nombrePdf) {
        if (destinatario == null || destinatario.isBlank()) return false;
        if (mailSender == null) return false;
        try {
            MimeMessage msg = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(msg, true, "UTF-8");
            helper.setTo(destinatario);
            helper.setSubject(asunto != null ? asunto : "Factura Electronica");
            helper.setText(cuerpo != null ? cuerpo : "Adjunto encontrara su factura electronica.", true);
            if (pdfAdjunto != null) {
                helper.addAttachment(nombrePdf != null ? nombrePdf : "factura.pdf", () -> new java.io.ByteArrayInputStream(pdfAdjunto));
            }
            mailSender.send(msg);
            return true;
        } catch (Exception e) {
            System.err.println("Error al enviar correo a " + destinatario + ": " + e.getMessage());
            return false;
        }
    }
}

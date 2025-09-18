package com.mycompany.proyecto_seguimiento.clases;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailUtils {

    public static void enviarCodigo(String destinatario, String codigo) {
        final String remitente = Config.get("email.remitente");
        final String clave = Config.get("email.clave");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject("Código de verificación");
            mensaje.setText("Su código de verificación es: " + codigo);

            Transport.send(mensaje);
            System.out.println("Correo enviado con éxito");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }
    
    public static void enviarCorreo(String destinatario, String asunto, String cuerpo) {
        final String remitente = Config.get("email.remitente");
        final String clave = Config.get("email.clave");

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(remitente, clave);
            }
        });

        try {
            Message mensaje = new MimeMessage(session);
            mensaje.setFrom(new InternetAddress(remitente));
            mensaje.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinatario));
            mensaje.setSubject(asunto);
            mensaje.setText(cuerpo);

            Transport.send(mensaje);
            System.out.println("Correo enviado con éxito");

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

}

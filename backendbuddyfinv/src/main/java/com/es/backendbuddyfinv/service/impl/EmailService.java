package com.es.backendbuddyfinv.service.impl;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    public EmailService(JavaMailSender mailSender){
        this.mailSender = mailSender;
    }

    public void enviarCorreoBienvenida(String destinatario, String nombre, String negocio){
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("¡Bienvenido a BuddyFinv!");
        mensaje.setText("Hola "+ nombre +",\n\n"
                + "Tu negocio " + negocio + " ha sido registrado exitosamente en BuddyFinv.\n"
                + "Ya puedes iniciar sesión y empezar a administrar tu negocio.\n\n"
                + "¡Gracias por confiar en nosotros!\n\n"
                + "El equipo de BuddyFinv");
        mensaje.setFrom("buddyfinv.software@gmail.com");
        mailSender.send(mensaje);
    }

    public void enviarCodigoRecuperacion(String destinatario, String codigo){
        SimpleMailMessage mensaje = new SimpleMailMessage();
        mensaje.setTo(destinatario);
        mensaje.setSubject("Código de recuperación - BuddyFinv");
        mensaje.setText("Has solicitado recuperar tu contraseña.\n\n" + 
                "Tu código de verificación es: " + codigo + "\n\n" + 
                "Este código expira en 5 minutos. Si no solicitaste esto, ignora este correo.");
        mensaje.setFrom("buddyfinv.software@gmail.com");
        mailSender.send(mensaje);
    }
    
}

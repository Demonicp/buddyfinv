package com.es.backendbuddyfinv.service.impl;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.es.backendbuddyfinv.model.PasswordResetToken;
import com.es.backendbuddyfinv.model.Usuario;
import com.es.backendbuddyfinv.repository.UsuarioRepository;

@Service
public class PasswordResetService {

    private final PasswordEncoder passwordEncoder;
    private final Map<String, PasswordResetToken> tokens = new ConcurrentHashMap<>();
    private final Random random = new Random();

    //Configurables
    private final int CODE_DIGITS = 6;
    private final long CODE_TTL_MILLIS = 5 * 60 * 1000L; // 5 minutos
    private final int MAX_ATTEMPTS = 3;
    private final long COOLDOWN_MILLIS = 30 * 1000L; // 30 segundos

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private EmailService emailService;

    PasswordResetService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Genera el código, guarda el token en memoria y (si existe usuario) envía correo.
     * Devuelve true si se generó (si existía usuario), false si no existía.
     * A nivel API frontend siempre se devuelve mensaje genérico.
     */

    public void requestReset(String usernameOrEmail){
        Optional<Usuario> opt = usuarioRepository.findByUsuarioOrEmail(usernameOrEmail,usernameOrEmail );
        String code = generateCode();
        long expiresAt = Instant.now().toEpochMilli()+ CODE_TTL_MILLIS;
        PasswordResetToken token = new PasswordResetToken(code, expiresAt, MAX_ATTEMPTS);
        tokens.put(usernameOrEmail.toLowerCase(), token);

        if(opt.isPresent()){
            Usuario user = opt.get();
            // enviar correo con el codigo
            try{
                emailService.enviarCodigoRecuperacion(user.getEmail(), code);
            }catch(Exception e){
                //loggear, pero no devolver detalles al cliente
                e.printStackTrace();
            }
        }
        // Si no existe, no hacemos nada visible (evita user enumeration)
    }

    /**
     * Verifica el código. Lanza RuntimeException con mensajes controlados para frontend.
     */

    public void verifyCode(String usernameOrEmail, String code){
        String key = usernameOrEmail.toLowerCase();
        PasswordResetToken token = tokens.get(key);
        long now = Instant.now().toEpochMilli();

        if(token == null){
            throw new RuntimeException("El código ingresado es incorrecto.");
        }
        // cooldown check
        if(token.getCooldownUntil() > now){
            long secs = (token.getCooldownUntil()-now)/1000;
            throw new RuntimeException("Límite de intentos alcanzados, por favor intentelo de nuevo en "+ secs +" segundos.");
        }

        if(token.getExpiresAt()< now){
            tokens.remove(key);
            throw new RuntimeException("El código ha expirado, solicite uno nuevo. ");
        }
        if(token.isVerified()) return;

        if(!token.getCode().equals(code)){
            token.setAttemptsLeft(token.getAttemptsLeft()-1);
            if(token.getAttemptsLeft() <= 0){
                token.setCooldownUntil(now + COOLDOWN_MILLIS);
                token.setAttemptsLeft(MAX_ATTEMPTS);// reset attemps after cooldown
            }
            tokens.put(key, token);
            throw new RuntimeException("El código ingresado es incorrecto.");
        }

        //codigo correcto
        token.setVerified(true);
        tokens.put(key, token);
    }

    /**
     * Actualiza la contraseña si el código fue verificado.
     */

    public void updatePassword(String usernameOrEmail, String newPassword, UsuarioService usuarioService){
        String key = usernameOrEmail.toLowerCase();
        PasswordResetToken token = tokens.get(key);
        if(token == null || !token.isVerified()){
            throw new RuntimeException("Proceso de verificacion incompleto");
        }

        Optional<Usuario> opt = usuarioRepository.findByUsuarioOrEmail(usernameOrEmail,usernameOrEmail );
        if (opt.isEmpty()){
            // por seguridad, borrar token y devolver mensaje genérico
            tokens.remove(key);
            throw new RuntimeException("No se pudo actualizar la contraseña.");
        }
        Usuario usuario = opt.get();
        usuarioService.actualizarPassword(usuario, newPassword);

        //invalidar token
        tokens.remove(key);
    }
    private String generateCode(){
        // genera CODE_DIGITS dígitos
        int bound = (int) Math.pow(10, CODE_DIGITS);
        int number = random.nextInt(bound- (bound/10))+ (bound/10); //evita ceros adelante
        return String.valueOf(number);
    }
}

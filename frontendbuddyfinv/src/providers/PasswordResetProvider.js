// src/providers/PasswordResetProvider.js
import axios from 'axios';

const API_URL = "http://localhost:8080/auth/password";

class PasswordResetProvider {
    
    /**
     * 1. Solicitar envío del código
     * Siempre devuelve un mensaje genérico, por seguridad.
     */
    async requestPasswordReset(usernameOrEmail) {
        try {
            const response = await axios.post(`${API_URL}/request`, {
                usernameOrEmail: usernameOrEmail
            });
            return response.data; 
        } catch (error) {
            // Siempre enviar mensaje genérico
            return "Si existe una cuenta asociada, se enviará un código al correo registrado.";
        }
    }

    /**
     * 2. Verificar el código
     * Si el código es incorrecto, backend devuelve mensajes útiles.
     */
    async verifyResetCode(usernameOrEmail, code) {
        try {
            const response = await axios.post(`${API_URL}/verify-code`, {
                usernameOrEmail: usernameOrEmail,
                code: code
            });
            return {
                ok: true,
                message: response.data
            };
        } catch (error) {
            return {
                ok: false,
                message: error.response?.data || "Error verificando el código."
            };
        }
    }

    /**
     * 3. Registrar nueva contraseña
     * Requiere que el código ya haya sido verificado
     */
    async updatePassword(usernameOrEmail, newPassword, confirmPassword) {
        try {
            const response = await axios.post(`${API_URL}/update`, {
                usernameOrEmail,
                newPassword,
                confirmPassword
            });

            return {
                ok: true,
                message: response.data
            };
        } catch (error) {
            return {
                ok: false,
                message: error.response?.data || "No se pudo actualizar la contraseña."
            };
        }
    }
}

export default new PasswordResetProvider();

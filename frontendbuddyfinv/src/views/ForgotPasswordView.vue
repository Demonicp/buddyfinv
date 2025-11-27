<template>
  <div class="forgot-container">

    <h2 class="logo">BUDDYFINV</h2>

    <div class="forgot-box">
      <h2>Recuperar contraseña</h2>

      <div class="input-group">
        <input
          type="text"
          v-model="usernameOrEmail"
          placeholder="Ingrese su usuario o correo"
        />
      </div>

      <button @click="sendCode" :disabled="loading">
        {{ loading ? "Enviando..." : "Enviar código" }}
      </button>

      <p v-if="message" class="info">{{ message }}</p>

      <div class="links">
        <router-link to="/">Volver al inicio de sesión</router-link>
      </div>
    </div>

  </div>
</template>

<script>
import PasswordResetProvider from "@/providers/PasswordResetProvider";

export default {
  name: "ForgotPasswordView",

  data() {
    return {
      usernameOrEmail: "",
      message: "",
      loading: false,
    };
  },

  methods: {
    async sendCode() {
      if (!this.usernameOrEmail.trim()) {
        this.message = "Ingrese un usuario o correo.";
        return;
      }

      this.loading = true;
      this.message = "";

      const resp = await PasswordResetProvider.requestPasswordReset(
        this.usernameOrEmail
      );

      this.message = resp;

      this.loading = false;

      setTimeout(() => {
        this.$router.push({
          name: "verify-code",
          query: { u: this.usernameOrEmail },
        });
      }, 1000);
    },
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');

.forgot-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  text-align: center;
  justify-content: center;
  height: 100vh;
  width: 100vw;
  background-image: url('@/assets/FondoLogin.png');
  background-size: cover;
  background-position: center;
  background-repeat: no-repeat;
  font-family: 'Outfit', sans-serif;
  overflow: hidden;
}

.logo {
  font-family: 'Outfit', sans-serif;
  font-weight: 900;
  font-size: 3.4rem;
  color: coral;
}

.forgot-box {
  font-family: 'Outfit', sans-serif;
  background: rgba(255, 255, 255, 0.85);
  font-weight: 800;
  padding: 20px;
  border-radius: 30px;
  width: 360px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

h2 {
  font-family: 'Outfit', sans-serif;
  text-align: center;
  font-weight: 700;
  font-size: 2rem;
  color: #575757;
  margin-bottom: 25px;
}

.input-group {
  width: 100%;
  margin-bottom: 20px;
}

.input-group input {
  width: 100%;
  padding: 12px;
  border: 1px solid #ccc;
  border-radius: 8px;
  background-color: #f5f5f5;
}

.input-group input::placeholder {
  color: #999;
}

button {
  width: 100%;
  padding: 12px;
  background-color: #ff7b00;
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  transition: 0.3s;
  margin-top: 10px;
}

button:hover:enabled {
  background-color: #ff7b00;
}

button:disabled {
  background-color: #ccc;
  cursor: not-allowed;
}

.info {
  margin-top: 10px;
  color: #333;
  font-size: 0.9em;
}

.links {
  margin-top: 15px;
}

.links a {
  color: #004aad;
  text-decoration: none;
}
</style>

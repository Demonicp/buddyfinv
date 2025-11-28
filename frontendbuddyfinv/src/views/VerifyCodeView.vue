<template>
  <div class="verify-container">

    <h2 class="logo">BUDDYFINV</h2>

    <div class="verify-box">
      <h2>Verificar código</h2>

      <div class="input-group">
  <input
    type="text"
    v-model="code"
    maxlength="6"
    placeholder="Código de verificación"
    @input="code = code.replace(/[^0-9]/g, '')"
  />
</div>

      <button @click="verify" :disabled="loading">
        {{ loading ? "Verificando..." : "Verificar código" }}
      </button>

      <p v-if="message" :class="ok ? 'success' : 'error'">
        {{ message }}
      </p>

      <div class="links">
        <router-link :to="{ name: 'ForgotPassword' }">Reenviar código</router-link>
      </div>
    </div>

  </div>
</template>

<script>
import PasswordResetProvider from "@/providers/PasswordResetProvider";

export default {
  name: "VerifyCodeView",

  data() {
    return {
      code: "",
      loading: false,
      ok: false,
      message: "",
      usernameOrEmail: "",
    };
  },

  mounted() {
    this.usernameOrEmail = this.$route.query.u || "";
  },

  methods: {
    async verify() {
      if (!this.code.trim()) {
        this.message = "Ingrese el código.";
        return;
      }

      this.loading = true;
      this.message = "";

      const resp = await PasswordResetProvider.verifyResetCode(
        this.usernameOrEmail,
        this.code
      );

      this.ok = resp.ok;
      this.message = resp.message;
      this.loading = false;

      if (resp.ok) {
        setTimeout(() => {
          this.$router.push({
            name: "change-password",
            query: { u: this.usernameOrEmail },
          });
        }, 1000);
      }
    },
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');

.verify-container {
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

.verify-box {
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

.error {
  margin-top: 10px;
  color: red;
  font-size: 0.9em;
}

.success {
  margin-top: 10px;
  color: green;
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

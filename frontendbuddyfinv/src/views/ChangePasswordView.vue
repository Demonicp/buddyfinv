<template>
  <div class="change-container">

    <h2 class="logo">BUDDYFINV</h2>

    <div class="change-box">
      <h2>Cambiar contraseña</h2>

      <div class="input-group password-group">
        <label>Nueva contraseña</label>
        <div class="password-wrapper">
          <input
            :type="showPassword ? 'text' : 'password'"
            v-model="newPassword"
            placeholder="Ingresa tu nueva contraseña"
          />
          <span class="toggle" @click="showPassword = !showPassword">
            {{ showPassword ? '🙈' : '👁️' }}
          </span>
        </div>
      </div>

      <div class="input-group password-group">
        <label>Confirmar contraseña</label>
        <div class="password-wrapper">
          <input
            :type="showConfirm ? 'text' : 'password'"
            v-model="confirmPassword"
            placeholder="Confirma tu contraseña"
          />
          <span class="toggle" @click="showConfirm = !showConfirm">
            {{ showConfirm ? '🙈' : '👁️' }}
          </span>
        </div>
      </div>


      <button @click="update" :disabled="loading">
        {{ loading ? "Guardando..." : "Registrar nueva contraseña" }}
      </button>

      <p v-if="message" :class="ok ? 'success' : 'error'">
        {{ message }}
      </p>
    </div>

  </div>
</template>

<script>
import PasswordResetProvider from "@/providers/PasswordResetProvider";

export default {
  name: "ChangePasswordView",

  data() {
    return {
      newPassword: "",
      confirmPassword: "",
      loading: false,
      ok: false,
      message: "",
      usernameOrEmail: "",
      showPassword: false,
      showConfirm: false,
    };
  },

  mounted() {
    this.usernameOrEmail = this.$route.query.u || "";
  },

  methods: {
    async update() {
      if (!this.newPassword || !this.confirmPassword) {
        this.message = "Complete todos los campos.";
        return;
      }

      this.loading = true;
      this.message = "";

      const resp = await PasswordResetProvider.updatePassword(
        this.usernameOrEmail,
        this.newPassword,
        this.confirmPassword
      );

      this.ok = resp.ok;
      this.message = resp.message;

      this.loading = false;

      if (resp.ok) {
        setTimeout(() => {
          this.$router.push({ name: "Login" });
        }, 1200);
      }
    },
  },
};
</script>

<style scoped>
@import url('https://fonts.googleapis.com/css2?family=Outfit:wght@100..900&display=swap');

.change-container {
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

.change-box {
  font-family: 'Outfit', sans-serif;
  background: rgba(255, 255, 255, 0.85);
  font-weight: 800;
  padding: 25px;
  border-radius: 30px;
  width: 360px;
  box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
}

h2 {
  font-weight: 700;
  font-size: 2rem;
  color: #575757;
  margin-bottom: 20px;
}

.input-group {
  width: 100%;
  text-align: left;
  margin-bottom: 15px;
}

.input-group label {
  font-size: 0.9rem;
  color: #444;
  font-weight: 600;
}

.input-group input {
  width: 100%;
  padding: 12px;
  margin-top: 5px;
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
.password-wrapper {
  position: relative;
}

.password-wrapper input {
  padding-right: 40px; /* espacio para el botón */
}

.toggle {
  position: absolute;
  right: 10px;
  top: 50%;
  transform: translateY(-50%);
  cursor: pointer;
  font-size: 1.2rem;
}

</style>

  
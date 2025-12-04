<template>
  <div class="crear-venta-wrapper">
    <div class="crear-venta-card">
      <div class="registarventacard">
      <h2 class="crear-venta-title">Registrar Venta</h2>
      </div>


      <VentaForm /> 

    </div>
  </div>
</template>

<script>
import VentaForm from '@/components/VentaForm.vue' // ajusta path si hace falta

export default {
  name: 'CrearVentaView',
  components: { VentaForm }
}
</script>

<style scoped>
.crear-venta-title {
  text-align: center;
  color: #ff8d2a;
  font-size: 50px;

  font-weight: bold;
}
.crear-venta-card {
  background: #fff;
  padding: 2%;
  height: 100%;
  justify-content: center;
  border-radius: 10px;
 
  box-shadow: 0 4px 18px rgba(0,0,0,0.06);
  font-size: medium;
}

.registarventacard {
  margin-bottom: 20px;
  padding-top: 20px;

}

</style>
import { defineStore } from "pinia";
import { ProductoProvider } from "@/providers/ProductoProvider";

export const useProductoStore = defineStore("producto", {
  state: () => ({
    productos: []
  }),

  actions: {
    async cargarProductos() {
      try {
        // Cargar productos del usuario autenticado basado en el token
        this.productos = await ProductoProvider.getByUsuario();
      } catch (error) {
        console.error("Error cargando productos:", error.message);
        // Si falla, intentar cargar todos los productos como fallback
        try {
          this.productos = await ProductoProvider.getAll();
        } catch (fallbackError) {
          console.error("Error cargando todos los productos:", fallbackError.message);
          this.productos = [];
        }
      }
    }
  }
});
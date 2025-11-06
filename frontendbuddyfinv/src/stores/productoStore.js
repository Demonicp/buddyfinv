import { defineStore } from "pinia";
import { ProductoProvider } from "@/providers/ProductoProvider";

export const useProductoStore = defineStore("producto", {
  state: () => ({
    productos: []
  }),

  actions: {
    async cargarProductos() {
      try {
        this.productos = await ProductoProvider.getAll();
      } catch (error) {
        console.error("Error cargando productos:", error.message);
      }
    }
  }
});
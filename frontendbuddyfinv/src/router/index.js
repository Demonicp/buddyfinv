import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '../layouts/DashboardLayout.vue'
import DashboardOpciones from '@/views/DashboardOpciones.vue'
import IngresoTable from '../components/IngresoTable.vue'
import ProductoView from '../views/ProductoView.vue'
import VentaView from '../views/VentaView.vue'
//import TestProducto from '../views/TestProducto.vue'


const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/LoginView.vue')
  },
  {
    path: '/dashboard',
    component: DashboardLayout,
    children: [
      {
        path: 'dashboard',
        component: DashboardOpciones,
        children: [
          { path: 'ventas', name: 'Ventas', component: VentaView },
          { path: 'ingresos', name: 'Ingresos', component: IngresoTable },
          // puedes agregar egresos aquí también
        ]
      },
      { path: 'inventario', name: 'inventario', component: ProductoView }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

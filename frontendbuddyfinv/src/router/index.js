import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '../layouts/DashboardLayout.vue'
import DashboardOpciones from '@/views/DashboardOpciones.vue'
import IngresoTable from '../components/IngresoTable.vue'
import ProductoView from '../views/InvProductoView.vue'
import VentaView from '../views/VentaView.vue'

//import TestProducto from '../views/TestProducto.vue'

import LoginView from '@/views/LoginView.vue'
import EgresosTable from '@/components/EgresosTable.vue'
import EgresoView from '@/views/EgresoView.vue'
import InvProductoView from '../views/InvProductoView.vue'
import DashboardInventario from '../views/DashboardInventario.vue'

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
    redirect: '/dashboard/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'dashboard',
        component: DashboardOpciones
      },
      { path: 'ventas', name: 'Ventas', component: VentaView },
      { path: 'ingresos', name: 'Ingresos', component: IngresoTable },
      { path: 'egresos', name: 'Egresos', component: EgresoView },
      { path: 'inventario', name: 'inventario', component: ProductoView }
    ]
  },  {
    path: '/dashboardInv',
    component: DashboardLayout,
    redirect: '/dashboardInv/dashboardInv',
    children: [
      {
        path: 'dashboardInv',
        name: 'dashboardInv',
        component: DashboardInventario
      },
      { path: 'verinventario', name: 'VerInventario', component: InvProductoView },
    ]
  }

  
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '../layouts/DashboardLayout.vue'
import DashboardOpciones from '@/views/DashboardOpciones.vue'
import IngresoTable from '../components/IngresoTable.vue'
import VentaView from '../views/VentaView.vue'
import AgregarProductoView from '../views/AgregarProductoView.vue'
import ModificarProductoView from '../views/ModificarProductoView.vue'
import ReabastecerProductoView from '../views/ReabastecerProductoView.vue'
//import TestProducto from '../views/TestProducto.vue'
import EgresoView from '@/views/EgresoView.vue'
import InvProductoView from '../views/InvProductoView.vue'
import DashboardInventario from '../views/DashboardInventario.vue'
import ModificarProductoView from '@/views/ModificarProductoView.vue'

const routes = [
  {
    path: '/',
    redirect: '/login'
  },
  {
    path: '/registro',
    name: 'Registro',
    component: () => import('../views/RegistroView.vue')
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
      { path: 'dashboard', name: 'dashboard', component: DashboardOpciones },
      { path: 'inventario', name: 'inventario', component: DashboardInventario },
      { path: 'ventas', name: 'Ventas', component: VentaView },
      { path: 'ingresos', name: 'Ingresos', component: IngresoTable },
      { path: 'egresos', name: 'Egresos', component: EgresoView },
      { path: 'VerInventario', name: 'VerInventario', component: InvProductoView },
      {path:'ModificarProducto',name:'ModificarProducto',component:ModificarProductoView}
    ]
  }
  

]
  



const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router

import { createRouter, createWebHistory } from 'vue-router'
import DashboardLayout from '../layouts/DashboardLayout.vue'
import DashboardOpciones from '@/views/DashboardOpciones.vue'
import IngresoTable from '../components/IngresoTable.vue'
import ProductoView from '../views/InvProductoView.vue'
import VentaView from '../views/VentaView.vue'
import AgregarProductoView from '../views/AgregarProductoView.vue'
import ModificarProductoView from '../views/ModificarProductoView.vue'
import ReabastecerProductoView from '../views/ReabastecerProductoView.vue'

import EgresoView from '@/views/EgresoView.vue'
import InvProductoView from '../views/InvProductoView.vue'
import DashboardInventario from '../views/DashboardInventario.vue'
import DashBoardConfiguracionView from '../views/DashBoardConfiguracionView.vue'
import ConfiguracionEliminarUsuario from '../components/configuracionEliminarUsuario.vue'
import AgregarEmpleadoView from '@/views/AgregarEmpleadoView.vue'
import ListaEmpleadosView from '@/views/ListaEmpleadosView.vue'


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
  meta: { requiresAuth: true }, // ← aquí
  children: [
    { path: 'dashboard', name: 'dashboard', component: DashboardOpciones, meta: { requiresAuth: true } },
    { path: 'ventas', name: 'Ventas', component: VentaView, meta: { requiresAuth: true } },
    { path: 'ingresos', name: 'Ingresos', component: IngresoTable, meta: { requiresAuth: true } },
    { path: 'egresos', name: 'Egresos', component: EgresoView, meta: { requiresAuth: true } },
    {
      path: 'inventario',
      name: 'inventario',
      component: ProductoView,
      meta: { requiresAuth: true },
      children: [
        { path: 'agregarproducto', name: 'AgregarProducto', component: AgregarProductoView, meta: { requiresAuth: true } },
        { path: 'modificarproducto', name: 'ModificarProducto', component: ModificarProductoView, meta: { requiresAuth: true } },
        { path: 'reabastecerproducto', name: 'ReabastecerProducto', component: ReabastecerProductoView, meta: { requiresAuth: true } }
      ]
    }
  ]
}
,  {
  path: '/dashboardInv',
  component: DashboardLayout,
  redirect: '/dashboardInv/dashboardInv',
  meta: { requiresAuth: true },
  children: [
    {
      path: 'dashboardInv',
      name: 'dashboardInv',
      component: DashboardInventario,
      meta: { requiresAuth: true }
    },
    {
      path: 'verinventario',
      name: 'VerInventario',
      component: InvProductoView,
      meta: { requiresAuth: true }
    },
    {
      path: 'añadirProducto',
      name: 'AñadirProducto',
      component: AgregarProductoView,
      meta: { requiresAuth: true }
    },
    {
      path: 'modificarproducto',
      name: 'ModificarProducto',
      component: ModificarProductoView,
      meta: { requiresAuth: true }
    },
    {
      path: 'reabastecerproducto',
      name: 'ReabastecerProducto',
      component: ReabastecerProductoView,
      meta: { requiresAuth: true }
    }
  ]
  /* 
    JUAAN DAVIIIIIID implemento esta ruta que me abre el dashboard config y despues configure para que me redirija a la ruta(eliminar usuario)
*/

},{
  path: '/dashboardConfig',
  component: DashboardLayout,
  redirect: '/dashboardConfig/configuracion',
  meta: { requiresAuth: true },
  children: [
    {
      path: 'configuracion',
      name: 'dashboardConfiguracion',
      component: DashBoardConfiguracionView,
      meta: { requiresAuth: true }
    },
    {
      path: 'eliminarusuario',
      name: 'Usuario',
      component: ConfiguracionEliminarUsuario,
      meta: { requiresAuth: true }
    },
    {
      path: 'usuarios/empleados',
      name: 'ListaEmpleados',
      component: ListaEmpleadosView,
      meta: {requiresAuth: true}
    },
    {
      path: 'usuarios/agregar',
      name: 'AgregarEmpleado',
      component: AgregarEmpleadoView,
      meta: { requiresAuth: true }
    }

  ]
},


  {
    path: '/dashboard',
    component: DashboardLayout,
    redirect: '/dashboard/dashboard',
    meta: { requiresAuth: true },
    children: [
      { path: 'dashboard', name: 'dashboard', component: DashboardOpciones, meta: { requiresAuth: true } },

      // Ventas listado global (opcional). Si prefieres mantener solo la ruta dentro de acciones, puedes comentar/eliminar esta línea.
      // { path: 'ventas', name: 'Ventas', component: VentaView, meta: { requiresAuth: true } },

      // Acciones (parent) con children: RegistrarVentas y listado de ventas dentro de Acciones
      {
        path: 'acciones',
        name: 'acciones',
        component: () => import('@/views/AccionesView.vue'),
        meta: { requiresAuth: true },
        children: [
          {
            path: 'registrar-ventas',
            name: 'RegistrarVentas',
            component: () => import('@/views/CrearVentaView.vue'),
            meta: { requiresAuth: true }
          },
          {
            path: 'ventas',
            name: 'Ventas',
            component: () => import('@/views/VentaView.vue'),
            meta: { requiresAuth: true }
          }
        ]
      },

      { path: 'ingresos', name: 'Ingresos', component: IngresoTable, meta: { requiresAuth: true } },
      { path: 'egresos', name: 'Egresos', component: EgresoView, meta: { requiresAuth: true } },

      {
        path: 'inventario',
        name: 'inventario',
        component: ProductoView,
        meta: { requiresAuth: true },
        children: [
          { path: 'agregarproducto', name: 'AgregarProducto', component: AgregarProductoView, meta: { requiresAuth: true } },
          { path: 'modificarproducto', name: 'ModificarProducto', component: ModificarProductoView, meta: { requiresAuth: true } },
          { path: 'reabastecerproducto', name: 'ReabastecerProducto', component: ReabastecerProductoView, meta: { requiresAuth: true } }
        ]
      }
    ]
  },
  {
    path: '/dashboardInv',
    component: DashboardLayout,
    redirect: '/dashboardInv/dashboardInv',
    meta: { requiresAuth: true },
    children: [
      {
        path: 'dashboardInv',
        name: 'dashboardInv',
        component: DashboardInventario,
        meta: { requiresAuth: true }
      },
      {
        path: 'verinventario',
        name: 'VerInventario',
        component: InvProductoView,
        meta: { requiresAuth: true }
      },
      {
        path: 'añadirProducto',
        name: 'AñadirProducto',
        component: AgregarProductoView,
        meta: { requiresAuth: true }
      },
      {
        path: 'modificarproducto',
        name: 'ModificarProducto',
        component: ModificarProductoView,
        meta: { requiresAuth: true }
      },
      {
        path: 'reabastecerproducto',
        name: 'ReabastecerProducto',
        component: ReabastecerProductoView,
        meta: { requiresAuth: true }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token')

  if (to.meta.requiresAuth && !token) {
    next('/login')
  } else {
    next()
  }
})

export default router
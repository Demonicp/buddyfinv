<template>
  <div class="producto-selector">
    <input
      v-model="term"
      @input="onInput"
      :placeholder="placeholder"
      class="ps-input"
      autocomplete="off"
    />

    <ul v-if="open && results.length" class="ps-list">
      <li v-for="item in results" :key="item.id" class="ps-item" @click="select(item)">
        <div class="ps-row">
          <div class="ps-name">{{ item.nombre }}</div>
          <div class="ps-meta">
            <span v-if="item.precio" class="ps-price">{{ formatPrecio(item.precio) }}</span>
            <span class="ps-stock">stock: {{ item.cantidadDisponible ?? 0 }}</span>
          </div>
        </div>
      </li>
    </ul>

    <div v-if="loading" class="ps-loading">Cargando...</div>
    <div v-if="error" class="ps-error">{{ error }}</div>
    <div v-if="!loading && !open && showNoResults && termTrimmed" class="ps-empty">No se encontraron productos</div>
  </div>
</template>

<script>
import { ref, watch, onMounted, onBeforeUnmount } from 'vue'
import { ProductoProvider } from '@/providers/ProductoProvider' // <-- cambiado

export default {
  name: 'ProductoSelector',
  props: {
    modelValue: { type: Object, default: null },
    placeholder: { type: String, default: 'Buscar producto por nombre o id...' },
    limit: { type: Number, default: 12 },
    debounceMs: { type: Number, default: 300 },
    showNoResults: { type: Boolean, default: true }
  },
  emits: ['update:modelValue', 'select'],
  setup(props, { emit }) {
    const term = ref('')
    const results = ref([])
    const loading = ref(false)
    const error = ref(null)
    const open = ref(false)
    const timer = ref(null)

    const formatPrecio = (p) => {
      if (p == null) return ''
      return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(p)
    }

    const termTrimmed = () => (term.value || '').trim()

    const clear = () => {
      results.value = []
      open.value = false
      error.value = null
    }

    const doSearch = async (q) => {
      if (!q || q.trim().length === 0) {
        clear()
        return
      }
      loading.value = true
      error.value = null
      try {
        if (/^\d+$/.test(q.trim())) {
          try {
            const item = await ProductoProvider.buscarPorId(q.trim()) // <-- uso del objeto
            results.value = item ? [item] : []
            open.value = results.value.length > 0
            loading.value = false
            return
          } catch (err) {
            if (err.status && err.status !== 404) throw err
          }
        }
        const data = await ProductoProvider.search(q, props.limit) // <-- uso del objeto
        results.value = Array.isArray(data) ? data : []
        open.value = results.value.length > 0
      } catch (err) {
        error.value = err.message || 'Error en búsqueda'
      } finally {
        loading.value = false
      }
    }

    const onInput = () => {
      clearTimeout(timer.value)
      timer.value = setTimeout(() => doSearch(term.value), props.debounceMs)
    }

    const select = (item) => {
      emit('update:modelValue', item)
      emit('select', item)
      open.value = false
      term.value = item.nombre || ''
      results.value = []
    }

    onMounted(() => {
      if (props.modelValue) term.value = props.modelValue.nombre || ''
    })

    onBeforeUnmount(() => {
      clearTimeout(timer.value)
    })

    watch(() => props.modelValue, (nv) => {
      if (!nv) term.value = ''
    })

    return { term, results, loading, error, open, onInput, select, formatPrecio, termTrimmed }
  }
}
</script>

<style scoped>
.producto-selector { position: relative; max-width: 520px; }
.ps-input { width: 100%; padding: 8px 10px; box-sizing: border-box; }
.ps-list { position: absolute; left: 0; right: 0; background: #fff; border: 1px solid #ddd; margin: 0; padding: 0; list-style: none; max-height: 260px; overflow: auto; z-index: 50; }
.ps-item { padding: 8px 10px; cursor: pointer; border-bottom: 1px solid #f3f3f3; }
.ps-item:hover { background: #f5f5f5; }
.ps-row { display:flex; justify-content: space-between; align-items:center; gap: 12px; }
.ps-name { font-weight: 600; }
.ps-meta { font-size: 0.9em; color: #666; display:flex; gap:8px; align-items:center; }
.ps-price { color: #2b7a2b; }
.ps-stock { color: #444; }
.ps-loading, .ps-error, .ps-empty { margin-top: 6px; font-size: 0.9em; color: #666; }
.ps-error { color: #c94444; }
</style>
<template>
  <button
    :class="btnClass"
    :disabled="loading"
    @click="handleClick"
    aria-label="Descargar detalle de venta en PDF"
    type="button"
  >
    <span v-if="loading">Generando PDF…</span>
    <span v-else><slot>Descargar PDF</slot></span>
  </button>
</template>

<script>
export default {
  name: 'VentaPdfButton',
  props: {
    venta: { type: Object, required: false, default: null },
    filenamePrefix: { type: String, default: 'venta' },
    includeLogo: { type: Boolean, default: false },
    logoBase64: { type: String, default: '' },
    btnClass: { type: String, default: 'btn-primary' }
  },
  data() {
    return { loading: false }
  },
  methods: {
    formatPrecio(value) {
      const n = Number(value || 0)
      try {
        return new Intl.NumberFormat('es-CO', { style: 'currency', currency: 'COP' }).format(n)
      } catch {
        return `$${n.toFixed(0)}`
      }
    },

    async handleClick() {
      this.loading = true
      this.$emit('pdf-start', this.venta)
      try {
        await this.descargarVentaPDF(this.venta)
        this.$emit('pdf-success', this.venta)
      } catch (err) {
        console.error('Error generando PDF:', err)
        this.$emit('pdf-error', err)
      } finally {
        this.loading = false
      }
    },

    // Método público que también puedes invocar desde $refs
    async descargarVentaPDF(venta) {
        if (!venta) {
            throw new Error('Venta inválida para generar PDF')
        }

      // Carga dinámica para evitar problemas con distintos empaquetados/bundlers
      let jsPDFModule = null
      try {
        jsPDFModule = await import('jspdf')
      } catch (e) {
        // fallback a UMD si el import anterior falla
        try {
          jsPDFModule = await import('jspdf/dist/jspdf.umd.min.js')
        } catch (err) {
          jsPDFModule = null
        }
      }

      if (!jsPDFModule) {
        throw new Error('No se pudo cargar la librería jsPDF')
      }

      // obtener constructor jsPDF (soporta { jsPDF } o default)
      const jsPDF = jsPDFModule.jsPDF || jsPDFModule.default || jsPDFModule

      // intentar cargar jspdf-autotable (registra doc.autoTable)
      try {
        await import('jspdf-autotable')
      } catch (e) {
        console.warn('No se pudo cargar jspdf-autotable, se usará fallback de tabla simple', e)
      }

      // crear documento
      const doc = new jsPDF({ unit: 'pt', format: 'a4' })
      const margin = 40
      let cursorY = 40


      // Título
      doc.setFontSize(16)
      doc.setTextColor('#e67e22')
      try { doc.setFont('helvetica', 'bold') } catch {}
      doc.text('Detalle de venta', margin, cursorY)
      cursorY += 18

      // Datos básicos
      const id = venta.idVenta || venta.id || '—'
      const cliente = venta.cliente || '—'
      const fecha = venta.fecha ? new Date(venta.fecha).toLocaleString() : '—'
      const total = this.formatPrecio(venta.total || 0)

      doc.setFontSize(10)
      doc.setTextColor('#111')
      try { doc.setFont('helvetica', 'normal') } catch {}
      doc.text(`ID: ${id}`, margin, cursorY); cursorY += 14
      doc.text(`Cliente: ${cliente}`, margin, cursorY); cursorY += 14
      // AGREGADO: imprimir ID del empleado que realizó la venta (empleadoId)
      const empleadoId = venta.empleadoId || venta.usuarioId || '—'
      doc.text(`Empleado ID: ${empleadoId}`, margin, cursorY); cursorY += 14
      doc.text(`Fecha: ${fecha}`, margin, cursorY); cursorY += 14
      doc.text(`Total: ${total}`, margin, cursorY); cursorY += 18

      // Preparar tabla
      const head = [['Producto', 'Cantidad', 'Precio unitario', 'Subtotal']]
      // AGREGADO: soporte para diferentes nombres de propiedad en el objeto venta
      const detalles = venta.detalles || venta.productos || venta.detallesVentas || []
      const body = (detalles || []).map(d => {
        const nombre = d.nombreProducto || d.nombre || d.productoNombre || `Producto ${d.productoId || '—'}`
        const cantidad = d.cantidad ?? 0
        const precio = d.precioUnitario ?? d.precio ?? 0
        const subtotal = Number(precio) * Number(cantidad)
        return [nombre, String(cantidad), this.formatPrecio(precio), this.formatPrecio(subtotal)]
      })

      // Si autoTable está disponible, usarlo; si no, fallback simple
      if (typeof doc.autoTable === 'function') {
        doc.autoTable({
          head,
          body,
          startY: cursorY,
          margin: { left: margin, right: margin },
          styles: { font: 'helvetica', fontSize: 10, cellPadding: 6, textColor: '#111' },
          headStyles: { fillColor: [248,196,113], textColor: [77,44,12], fontStyle: 'bold' },
          alternateRowStyles: { fillColor: [253,246,236] },
          didDrawPage: () => {
            const pageCount = doc.internal.getNumberOfPages()
            const pageSize = doc.internal.pageSize
            const pageHeight = pageSize.height ? pageSize.height : pageSize.getHeight()
            doc.setFontSize(9)
            doc.setTextColor('#777')
            const pageNumber = doc.internal.getCurrentPageInfo().pageNumber
            doc.text(`Página ${pageNumber} / ${pageCount}`, margin, pageHeight - 20)
          }
        })
      } else {
        // fallback simple: escribir filas manualmente
        doc.setFontSize(10)
        const rowHeight = 16
        doc.text(head[0].join('  |  '), margin, cursorY); cursorY += rowHeight
        for (const row of body) {
          // dividir texto largo si es necesario
          const line = row.join('  |  ')
          const split = doc.splitTextToSize(line, doc.internal.pageSize.getWidth() - margin * 2)
          split.forEach(part => {
            doc.text(part, margin, cursorY)
            cursorY += rowHeight
            if (cursorY > doc.internal.pageSize.getHeight() - 60) {
              doc.addPage()
              cursorY = 40
            }
          })
        }
      }

      // Totales al final
      const finalY = doc.lastAutoTable ? doc.lastAutoTable.finalY + 12 : cursorY + 12
      try { doc.setFont('helvetica', 'bold') } catch {}
      doc.setTextColor('#e67e22')
      doc.text('Total a pagar:', margin, finalY)
      try { doc.setFont('helvetica', 'bold') } catch {}
      doc.setTextColor('#111')
      doc.text(this.formatPrecio(venta.total || 0), margin + 120, finalY)

      // Guardar archivo
      const filename = `${this.filenamePrefix}-${id}.pdf`
      doc.save(filename)
    }
  }
}
</script>

<style scoped>
button { cursor: pointer; }
</style>
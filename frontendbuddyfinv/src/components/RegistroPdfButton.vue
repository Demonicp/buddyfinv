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
import { jsPDF } from 'jspdf'
import autoTable from 'jspdf-autotable'

export default {
  name: 'RegistroPdfButton',
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

      // crear documento
      const doc = new jsPDF({ unit: 'pt', format: 'a4' })
      const pageWidth = doc.internal.pageSize.getWidth()
      const margin = 40
      let cursorY = 40

      // --- ENCABEZADO (2 Columnas) ---
      
      // Columna Izquierda: Título y Empresa
      doc.setFontSize(22)
      doc.setTextColor('#e67e22') // Naranja corporativo
      try { doc.setFont('helvetica', 'bold') } catch {}
      doc.text('BUDDYFINV', margin, cursorY)
      cursorY += 20
      
      doc.setFontSize(12)
      doc.setTextColor('#555')
      try { doc.setFont('helvetica', 'normal') } catch {}
      doc.text('Detalle de Venta', margin, cursorY)
      
      // Datos para Columna Derecha
      const id = venta.idVenta || venta.id || '—'
      const fecha = venta.fecha ? new Date(venta.fecha).toLocaleString() : '—'
      const empleadoId = venta.empleadoId || venta.usuarioId || venta.atendidoPorId || '—'
      
      // Columna Derecha: Metadatos (Alineado a la derecha)
      const rightColX = pageWidth - margin
      let rightColY = 40
      
      doc.setFontSize(10)
      doc.setTextColor('#333')
      
      // Función helper para alinear texto a la derecha
      const writeRightAligned = (text, y) => {
        const textWidth = doc.getTextWidth(text)
        doc.text(text, rightColX - textWidth, y)
      }

      try { doc.setFont('helvetica', 'bold') } catch {}
      writeRightAligned(`Venta #${id}`, rightColY); rightColY += 14
      
      try { doc.setFont('helvetica', 'normal') } catch {}
      writeRightAligned(`Fecha: ${fecha}`, rightColY); rightColY += 14
      writeRightAligned(`Atendido por: ${empleadoId}`, rightColY); rightColY += 14

      // Separador
      cursorY = Math.max(cursorY, rightColY) + 10
      doc.setDrawColor(230, 230, 230)
      doc.line(margin, cursorY, pageWidth - margin, cursorY)
      cursorY += 20

      // --- INFORMACIÓN DEL CLIENTE ---
      const cliente = venta.cliente || '—'
      
      doc.setFontSize(10)
      doc.setTextColor('#7f8c8d') // Gris suave para etiquetas
      doc.text('CLIENTE', margin, cursorY)
      cursorY += 12
      
      doc.setFontSize(12)
      doc.setTextColor('#111') // Negro para valor
      try { doc.setFont('helvetica', 'bold') } catch {}
      doc.text(cliente, margin, cursorY)
      cursorY += 25

      // --- TABLA DE PRODUCTOS ---
      const head = [['Producto', 'Cant.', 'Precio Unit.', 'Subtotal']]
      const detalles = venta.detalles || venta.productos || venta.detallesVentas || []
      const body = (detalles || []).map(d => {
        const nombre = d.nombreProducto || d.nombre || d.productoNombre || `Producto ${d.productoId || '—'}`
        const cantidad = d.cantidad ?? 0
        const precio = d.precioUnitario ?? d.precio ?? 0
        const subtotal = Number(precio) * Number(cantidad)
        return [nombre, String(cantidad), this.formatPrecio(precio), this.formatPrecio(subtotal)]
      })

      // Usar autoTable directamente (importado estáticamente)
      autoTable(doc, {
        head,
        body,
        startY: cursorY,
        margin: { left: margin, right: margin },
        theme: 'grid',
        styles: { 
          font: 'helvetica', 
          fontSize: 10, 
          cellPadding: 8, 
          textColor: '#333',
          lineColor: [230, 230, 230],
          lineWidth: 0.5
        },
        headStyles: { 
          fillColor: [255, 255, 255], 
          textColor: '#e67e22', 
          fontStyle: 'bold',
          lineWidth: 0, 
          borderBottomWidth: 2,
          borderColor: [230, 126, 34] 
        },
        columnStyles: {
          0: { cellWidth: 'auto' }, 
          1: { cellWidth: 40, halign: 'center' }, 
          2: { cellWidth: 80, halign: 'right' }, 
          3: { cellWidth: 80, halign: 'right' }  
        }
      })
      cursorY = doc.lastAutoTable.finalY + 20

      // --- TOTALES ---
      const total = this.formatPrecio(venta.total || 0)
      
      // Caja de totales
      const boxWidth = 200
      const boxX = pageWidth - margin - boxWidth
      
      // Fondo suave para totales
      doc.setFillColor(253, 246, 236)
      doc.rect(boxX, cursorY, boxWidth, 40, 'F')
      
      doc.setFontSize(12)
      doc.setTextColor('#e67e22')
      try { doc.setFont('helvetica', 'bold') } catch {}
      
      // Texto "Total"
      doc.text('Total a Pagar:', boxX + 10, cursorY + 25)
      
      // Valor Total (Alineado a derecha dentro de la caja)
      doc.setTextColor('#111')
      const totalWidth = doc.getTextWidth(total)
      doc.text(total, boxX + boxWidth - totalWidth - 10, cursorY + 25)

      // Guardar archivo
      const filename = `${this.filenamePrefix}-${id}.pdf`
      doc.save(filename)
    }
  }
}
</script>

<style scoped>
button { 
  cursor: pointer;
  height: fit-content;
  
  
}
</style>


const API_BASE_URL = import.meta?.env?.VITE_API_BASE_URL || 'http://localhost:8080';
const INGRESOS_BASE = `${API_BASE_URL}/ingresos`;

async function handleResponse(response) {
	if (!response.ok) {
		const text = await response.text().catch(() => '');
		const message = text || `${response.status} ${response.statusText}`;
		throw new Error(message);
	}
	const contentType = response.headers.get('content-type') || '';
	if (contentType.includes('application/json')) {
		return response.json();
	}
	return response.text();
}

export const IngresoProvider = {
	/**
	 * Verifica si el backend está funcionando correctamente.
	 * GET /ingresos/test
	 * Devuelve: string de confirmación
	 */
	async test() {
		const res = await fetch(`${INGRESOS_BASE}/test`, {
			method: 'GET',
			credentials: 'include',
		});
		return handleResponse(res);
	},

	/**
	 * Obtiene todos los ingresos diarios.
	 * GET /ingresos/all
	 * Devuelve: List<IngresoDTO> con idIngreso, fecha, totalDiario, totalFacturas, nombreEmpleado
	 */
	async getAll() {
		const res = await fetch(`${INGRESOS_BASE}/all`, {
			method: 'GET',
			credentials: 'include',
		});
		return handleResponse(res);
	},

	/**
	 * Obtiene ingresos filtrados por rango de fechas.
	 * GET /ingresos/filtrar?inicio=YYYY-MM-DD&fin=YYYY-MM-DD
	 * Devuelve: List<IngresoDTO> con idIngreso, fecha, totalDiario, totalFacturas, nombreEmpleado
	 */
	async getByRango(fechaInicio, fechaFin) {
		const res = await fetch(`${INGRESOS_BASE}/filtrar?inicio=${fechaInicio}&fin=${fechaFin}`, {
			method: 'GET',
			credentials: 'include',
		});
		return handleResponse(res);
	}
};

export default IngresoProvider;
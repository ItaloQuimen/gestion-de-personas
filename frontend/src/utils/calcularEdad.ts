function esAnioBisiesto(anio: number) {
  return anio % 4 === 0 && (anio % 100 !== 0 || anio % 400 === 0)
}

function crearFechaLocal(fecha: string) {
  const partes = fecha.split('-').map(Number)
  const [anio, mes, dia] = partes
  const fechaLocal = new Date(anio, mes - 1, dia)

  if (
    partes.length !== 3
    || fechaLocal.getFullYear() !== anio
    || fechaLocal.getMonth() !== mes - 1
    || fechaLocal.getDate() !== dia
  ) {
    throw new RangeError('La fecha de nacimiento no es válida.')
  }

  return fechaLocal
}

function inicioDelDia(fecha: Date) {
  return new Date(fecha.getFullYear(), fecha.getMonth(), fecha.getDate())
}

export function calcularEdad(fechaNacimiento: string, fechaActual = new Date()) {
  const nacimiento = crearFechaLocal(fechaNacimiento)
  const hoy = inicioDelDia(fechaActual)

  if (nacimiento > hoy) {
    throw new RangeError('La fecha de nacimiento no puede ser futura.')
  }

  const anioActual = hoy.getFullYear()
  const nacioEl29DeFebrero = nacimiento.getMonth() === 1 && nacimiento.getDate() === 29
  const cumpleaniosEl1DeMarzo = nacioEl29DeFebrero && !esAnioBisiesto(anioActual)
  const mesCumpleanios = cumpleaniosEl1DeMarzo
    ? 2
    : nacimiento.getMonth()
  const diaCumpleanios = cumpleaniosEl1DeMarzo
    ? 1
    : nacimiento.getDate()
  const cumpleaniosActual = new Date(anioActual, mesCumpleanios, diaCumpleanios)
  const edadBase = anioActual - nacimiento.getFullYear()

  return hoy < cumpleaniosActual ? edadBase - 1 : edadBase
}

import { startTransition, useEffect, useState, type FormEvent } from 'react'
import { actualizarPersona, crearPersona } from '../services/personasApi'
import type { Persona } from '../types/persona'

type FormularioPersonaProps = {
  personaEnEdicion: Persona | null
  onPersonaGuardada: (persona: Persona) => void
  onCancelarEdicion: () => void
}

type DatosFormulario = Omit<Persona, 'id'>

const formularioInicial: DatosFormulario = {
  rut: '',
  nombre: '',
  apellido: '',
  fechaNacimiento: '',
  direccion: { calle: '', comuna: '', region: '' },
}

function esFechaPasada(fecha: string) {
  const hoy = new Date()
  const fechaIngresada = new Date(`${fecha}T00:00:00`)
  const inicioDeHoy = new Date(hoy.getFullYear(), hoy.getMonth(), hoy.getDate())
  return fechaIngresada < inicioDeHoy
}

function validarFormulario(datos: DatosFormulario): string | null {
  const campos = [
    datos.rut,
    datos.nombre,
    datos.apellido,
    datos.fechaNacimiento,
    datos.direccion.calle,
    datos.direccion.comuna,
    datos.direccion.region,
  ]

  if (campos.some((campo) => campo.trim() === '')) {
    return 'Completa todos los campos.'
  }

  if (!esFechaPasada(datos.fechaNacimiento)) {
    return 'La fecha de nacimiento debe ser anterior a hoy.'
  }

  return null
}

export function FormularioPersona({ personaEnEdicion, onPersonaGuardada, onCancelarEdicion }: FormularioPersonaProps) {
  const [datos, setDatos] = useState<DatosFormulario>(formularioInicial)
  const [enviando, setEnviando] = useState(false)
  const [mensaje, setMensaje] = useState<string | null>(null)
  const [error, setError] = useState<string | null>(null)

  useEffect(() => {
    if (personaEnEdicion) {
      startTransition(() => {
        setDatos({
          rut: personaEnEdicion.rut,
          nombre: personaEnEdicion.nombre,
          apellido: personaEnEdicion.apellido,
          fechaNacimiento: personaEnEdicion.fechaNacimiento,
          direccion: personaEnEdicion.direccion,
        })
        setMensaje(null)
        setError(null)
      })
    } else {
      startTransition(() => setDatos(formularioInicial))
    }
  }, [personaEnEdicion])

  function actualizarCampo(campo: keyof DatosFormulario, valor: string) {
    setDatos((actuales) => ({ ...actuales, [campo]: valor }))
  }

  function actualizarDireccion(campo: keyof DatosFormulario['direccion'], valor: string) {
    setDatos((actuales) => ({
      ...actuales,
      direccion: { ...actuales.direccion, [campo]: valor },
    }))
  }

  async function manejarEnvio(evento: FormEvent<HTMLFormElement>) {
    evento.preventDefault()
    setMensaje(null)
    setError(null)

    const errorDeValidacion = validarFormulario(datos)
    if (errorDeValidacion) {
      setError(errorDeValidacion)
      return
    }

    setEnviando(true)
    try {
      if (personaEnEdicion) {
        const personaGuardada = await actualizarPersona(personaEnEdicion.id, datos)
        onPersonaGuardada(personaGuardada)
        setMensaje('Persona actualizada correctamente.')
      } else {
        const solicitud = await crearPersona(datos)
        setMensaje(`Solicitud de creación recibida (${solicitud.solicitudId}).`)
      }
      setDatos(formularioInicial)
    } catch {
      setError(personaEnEdicion ? 'No fue posible actualizar la persona.' : 'No fue posible crear la persona.')
    } finally {
      setEnviando(false)
    }
  }

  function manejarCancelacion() {
    setDatos(formularioInicial)
    setMensaje(null)
    setError(null)
    onCancelarEdicion()
  }

  return (
    <section className="form-section" aria-labelledby="form-title">
      <h2 id="form-title">{personaEnEdicion ? 'Editar persona' : 'Registrar persona'}</h2>
      <form onSubmit={manejarEnvio}>
        <div className="form-grid">
          <label>RUT<input required value={datos.rut} onChange={(event) => actualizarCampo('rut', event.target.value)} /></label>
          <label>Nombre<input required value={datos.nombre} onChange={(event) => actualizarCampo('nombre', event.target.value)} /></label>
          <label>Apellido<input required value={datos.apellido} onChange={(event) => actualizarCampo('apellido', event.target.value)} /></label>
          <label>Fecha de nacimiento<input required type="date" value={datos.fechaNacimiento} onChange={(event) => actualizarCampo('fechaNacimiento', event.target.value)} /></label>
          <label>Calle<input required value={datos.direccion.calle} onChange={(event) => actualizarDireccion('calle', event.target.value)} /></label>
          <label>Comuna<input required value={datos.direccion.comuna} onChange={(event) => actualizarDireccion('comuna', event.target.value)} /></label>
          <label>Región<input required value={datos.direccion.region} onChange={(event) => actualizarDireccion('region', event.target.value)} /></label>
        </div>
        <button type="submit" disabled={enviando}>{enviando ? 'Guardando...' : personaEnEdicion ? 'Guardar cambios' : 'Guardar persona'}</button>
        {personaEnEdicion && <button type="button" onClick={manejarCancelacion} disabled={enviando}>Cancelar</button>}
        {mensaje && <p className="form-success" role="status">{mensaje}</p>}
        {error && <p className="form-error" role="alert">{error}</p>}
      </form>
    </section>
  )
}

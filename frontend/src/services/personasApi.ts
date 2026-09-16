import type { Persona } from '../types/persona'

const PERSONAS_ENDPOINT = '/api/personas'

export type SolicitudCreacion = {
  solicitudId: string
  estado: 'PENDIENTE'
}

export async function obtenerPersonas(): Promise<Persona[]> {
  const response = await fetch(PERSONAS_ENDPOINT)

  if (!response.ok) {
    throw new Error(`La consulta de personas respondió ${response.status}.`)
  }

  return response.json() as Promise<Persona[]>
}

export async function crearPersona(persona: Omit<Persona, 'id'>): Promise<SolicitudCreacion> {
  const response = await fetch(PERSONAS_ENDPOINT, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(persona),
  })

  if (!response.ok) {
    throw new Error(`La creación de la persona respondió ${response.status}.`)
  }

  return response.json() as Promise<SolicitudCreacion>
}

export async function actualizarPersona(id: number, persona: Omit<Persona, 'id'>): Promise<Persona> {
  const response = await fetch(`${PERSONAS_ENDPOINT}/${id}`, {
    method: 'PUT',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(persona),
  })

  if (!response.ok) {
    throw new Error(`La actualización de la persona respondió ${response.status}.`)
  }

  return response.json() as Promise<Persona>
}

export async function eliminarPersona(id: number): Promise<void> {
  const response = await fetch(`${PERSONAS_ENDPOINT}/${id}`, { method: 'DELETE' })

  if (!response.ok) {
    throw new Error(`La eliminación de la persona respondió ${response.status}.`)
  }
}

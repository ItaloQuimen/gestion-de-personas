import type { Persona } from '../types/persona'

const PERSONAS_ENDPOINT = '/api/personas'

export async function obtenerPersonas(): Promise<Persona[]> {
  const response = await fetch(PERSONAS_ENDPOINT)

  if (!response.ok) {
    throw new Error(`La consulta de personas respondió ${response.status}.`)
  }

  return response.json() as Promise<Persona[]>
}

import type { Persona } from '../types/persona'

export const samplePeople: Persona[] = [
  {
    id: 1,
    nombre: 'Camila',
    apellido: 'Rojas',
    rut: '12.345.678-5',
    fechaNacimiento: '1990-04-12',
    direccion: {
      calle: 'Avenida Siempre Viva 742',
      comuna: 'Providencia',
      region: 'Metropolitana',
    },
  },
  {
    id: 2,
    nombre: 'Diego',
    apellido: 'Fuentes',
    rut: '16.789.012-3',
    fechaNacimiento: '1987-11-03',
    direccion: {
      calle: 'Calle Los Pinos 184',
      comuna: 'Viña del Mar',
      region: 'Valparaíso',
    },
  },
]

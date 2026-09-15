export type Direccion = {
  calle: string
  comuna: string
  region: string
}

export type Persona = {
  id: number
  nombre: string
  apellido: string
  rut: string
  fechaNacimiento: string
  direccion: Direccion
}

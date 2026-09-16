import type { Persona } from '../types/persona'
import { calcularEdad } from '../utils/calcularEdad'

type TarjetaPersonaProps = {
  persona: Persona
  onEditar: (persona: Persona) => void
  onEliminar: (persona: Persona) => void
}

export function TarjetaPersona({ persona, onEditar, onEliminar }: TarjetaPersonaProps) {
  const edad = calcularEdad(persona.fechaNacimiento)

  return (
    <li className="person-card">
      <h2>{persona.nombre} {persona.apellido}</h2>
      <dl className="person-details">
        <div>
          <dt>RUT</dt>
          <dd>{persona.rut}</dd>
        </div>
        <div>
          <dt>Fecha de nacimiento</dt>
          <dd>{persona.fechaNacimiento}</dd>
        </div>
        <div>
          <dt>Edad</dt>
          <dd>{edad} {edad === 1 ? 'año' : 'años'}</dd>
        </div>
        <div>
          <dt>Dirección</dt>
          <dd>{persona.direccion.calle}</dd>
        </div>
        <div>
          <dt>Comuna y región</dt>
          <dd>{persona.direccion.comuna}, {persona.direccion.region}</dd>
        </div>
      </dl>
      <button type="button" onClick={() => onEditar(persona)}>Editar</button>
      <button type="button" onClick={() => onEliminar(persona)}>Eliminar</button>
    </li>
  )
}

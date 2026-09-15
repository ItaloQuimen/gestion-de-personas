import { ListaPersonas } from './components/ListaPersonas'
import { samplePeople } from './data/samplePeople'
import './App.css'

function App() {
  return (
    <main className="app-shell">
      <header className="app-header">
        <p className="eyebrow">Gestión de personas</p>
        <h1>Personas</h1>
        <p className="intro">Consulta la información de las personas registradas.</p>
      </header>
      <ListaPersonas personas={samplePeople} />
    </main>
  )
}

export default App

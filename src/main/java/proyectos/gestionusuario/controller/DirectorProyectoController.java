package proyectos.gestionusuario.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import proyectos.gestionusuario.model.DirectorProyecto;
import proyectos.gestionusuario.repository.DirectorProyectoRepository;

import java.util.List;

@RestController
@RequestMapping("/directores")
@CrossOrigin(origins = "*")
public class DirectorProyectoController {

    @Autowired
    private DirectorProyectoRepository directorRepository;

    // GET - Obtener todos los directores
    @GetMapping
    public ResponseEntity<List<DirectorProyecto>> obtenerTodos() {
        List<DirectorProyecto> directores = directorRepository.findAll();
        return ResponseEntity.ok(directores);
    }

    // GET - Obtener director por ID
    @GetMapping("/{id}")
    public ResponseEntity<DirectorProyecto> obtenerPorId(@PathVariable Long id) {
        return directorRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // POST - Crear nuevo director
    @PostMapping
    public ResponseEntity<DirectorProyecto> crear(@RequestBody DirectorProyecto director) {
        DirectorProyecto saved = directorRepository.save(director);
        return ResponseEntity.ok(saved);
    }

    // PUT - Actualizar director existente
    @PutMapping("/{id}")
    public ResponseEntity<DirectorProyecto> actualizar(@PathVariable Long id, @RequestBody DirectorProyecto directorActualizado) {
        return directorRepository.findById(id)
                .map(director -> {
                    director.setNombre(directorActualizado.getNombre());
                    director.setCorreoInstitucional(directorActualizado.getCorreoInstitucional());
                    director.setTelefono(directorActualizado.getTelefono());
                    director.setDepartamento(directorActualizado.getDepartamento());
                    director.setAreaInvestigacion(directorActualizado.getAreaInvestigacion());
                    DirectorProyecto actualizado = directorRepository.save(director);
                    return ResponseEntity.ok(actualizado);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // DELETE - Eliminar director
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        if (directorRepository.existsById(id)) {
            directorRepository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}

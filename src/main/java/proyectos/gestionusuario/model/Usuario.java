package proyectos.gestionusuario.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;

@Entity
@Table(name = "usuarios")
@Inheritance(strategy = InheritanceType.JOINED) // Crea tablas específicas unidas por ID
public abstract class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String usuario;
    private String password;
    private String cedula;
    private String nombre;
    private String correoInstitucional;
    private String telefono;
    private String departamento;

    public Usuario() {}

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getCedula() { return cedula; }
    public void setCedula(String cedula) { this.cedula = cedula; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getCorreoInstitucional() { return correoInstitucional; }
    public void setCorreoInstitucional(String correoInstitucional) { this.correoInstitucional = correoInstitucional; }

    public String getTelefono() { return telefono; }
    public void setTelefono(String telefono) { this.telefono = telefono; }

    public String getDepartamento() { return departamento; }
    public void setDepartamento(String departamento) { this.departamento = departamento; }

    // Métodos del diagrama
    public boolean verificarPermisos() { return true; }
    public void cambiarPassword(String nuevaPassword) { this.password = nuevaPassword; }
    public void actualizarCorreo(String nuevoCorreo) { this.correoInstitucional = nuevoCorreo; }
}
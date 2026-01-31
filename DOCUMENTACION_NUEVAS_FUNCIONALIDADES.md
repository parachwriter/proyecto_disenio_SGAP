# 📋 Documentación de Nuevas Funcionalidades - SGAP

## ✅ Resumen de Cambios Implementados

Se han implementado las siguientes pestañas y funcionalidades en el frontend y backend del Sistema de Gestión de Asistentes de Proyectos (SGAP):

---

## 🎯 1. Nuevas Páginas HTML (Frontend)

### 1.1 **Directores de Proyecto** 
- **Archivo:** `directores-proyecto.html`
- **Ubicación:** `/src/main/resources/static/directores-proyecto.html`
- **Funcionalidades:**
  - 📋 Listar todos los directores
  - ➕ Crear nuevo director
  - ✏️ Editar director existente
  - 🗑️ Eliminar director
  - Modal para entrada de datos
  - Campos: Nombre, Correo, Teléfono, Departamento

**API Endpoints utilizados:**
```
GET    /directores              - Obtener todos los directores
POST   /directores              - Crear nuevo director
PUT    /directores/{id}         - Actualizar director
DELETE /directores/{id}         - Eliminar director
GET    /directores/{id}         - Obtener director por ID
```

---

### 1.2 **Asistentes de Proyecto**
- **Archivo:** `asistentes-proyecto.html`
- **Ubicación:** `/src/main/resources/static/asistentes-proyecto.html`
- **Funcionalidades:**
  - 📋 Listar asistentes, ayudantes y técnicos
  - 🔍 Filtrar por proyecto y tipo de integrante
  - ➕ Registrar nuevo asistente
  - ✏️ Editar asistente
  - 🗑️ Eliminar/dar de baja asistente
  - Indicadores visuales de tipo y estado
  - Modal para entrada de datos

**API Endpoints utilizados:**
```
GET    /nomina/asistentes                    - Listar asistentes
GET    /nomina/asistentes/{id}               - Obtener asistente
POST   /nomina/asistentes/registrar          - Registrar nuevo asistente
DELETE /nomina/asistentes/{id}               - Dar de baja asistente
GET    /proyectos                            - Cargar lista de proyectos
```

---

### 1.3 **Consultar Proyectos**
- **Archivo:** `consultar-proyectos.html`
- **Ubicación:** `/src/main/resources/static/consultar-proyectos.html`
- **Funcionalidades:**
  - 🎯 Vista en grid/tarjetas de proyectos
  - 🔍 Buscar por nombre
  - 🏷️ Filtrar por tipo
  - 📊 Ver detalles completos de cada proyecto
  - 👥 Listar integrantes del proyecto
  - Información: Presupuesto, fechas, director, duración

**API Endpoints utilizados:**
```
GET    /proyectos                - Obtener todos los proyectos
```

---

### 1.4 **Consultar Nómina**
- **Archivo:** `consultar-nomina.html`
- **Ubicación:** `/src/main/resources/static/consultar-nomina.html`
- **Funcionalidades:**
  - 📊 Tabla de reportes de nómina
  - 🔍 Filtrar por proyecto, mes, año y estado
  - 📈 Estadísticas: Total, Aprobados, Pendientes, Rechazados
  - 👁️ Ver detalles de cada reporte
  - 📋 Listar integrantes en nómina
  - Estados: Aprobado, Pendiente, Rechazado

**API Endpoints utilizados:**
```
GET    /nomina                                - Obtener todas las nóminas
GET    /nomina/proyecto/{proyectoId}         - Nóminas por proyecto
```

---

## 🔧 2. Backend - Nuevos Controladores REST

### 2.1 **DirectorProyectoController**
- **Archivo:** `DirectorProyectoController.java`
- **Ubicación:** `/src/main/java/proyectos/gestionusuario/controller/`
- **Base URL:** `/directores`

**Endpoints:**
```java
GET    /directores              // Obtener todos los directores
GET    /directores/{id}         // Obtener por ID
POST   /directores              // Crear nuevo
PUT    /directores/{id}         // Actualizar
DELETE /directores/{id}         // Eliminar
```

---

### 2.2 **Extensión GestionAsistenteController**
- **Archivo:** `GestionAsistenteController.java`
- **Base URL:** `/nomina`

**Nuevos Endpoints:**
```java
GET    /nomina                                // Obtener todas las nóminas
GET    /nomina/asistentes                     // Listar asistentes
GET    /nomina/asistentes/{id}                // Obtener asistente
POST   /nomina/asistentes/registrar           // Registrar asistente
DELETE /nomina/asistentes/{id}                // Dar de baja
GET    /nomina/proyecto/{proyectoId}          // Nóminas por proyecto
```

---

## 📊 3. Backend - Cambios en Servicios

### 3.1 **ServicioGestionAsistente**
- **Nuevos métodos:**
  - `obtenerTodasLasNominas()` - Retorna todos los reportes
  - `obtenerNominasPorProyecto(Long proyectoId)` - Filtro por proyecto

---

## 🗄️ 4. Backend - Cambios en Repositorios

### 4.1 **NominaRepository**
- **Nuevo método:**
  ```java
  @Query("SELECT r FROM ReporteNomina r WHERE r.proyecto.id = :idProyecto ...")
  List<ReporteNomina> findByProyectoId(@Param("idProyecto") Long idProyecto);
  ```

---

## 📝 5. Backend - Cambios en Modelos

### 5.1 **Usuario.java**
- **Campos agregados:**
  - `String telefono`
  - `String departamento`
- **Getters/Setters agregados para estos campos**

---

## 🎨 6. Dashboard Actualizado

### 6.1 **dashboard.html**
- Se reemplazaron los `alert()` con enlaces directos a las nuevas páginas
- **Cambios:**
  - "Directores de Proyecto" → `directores-proyecto.html`
  - "Asistentes de Proyecto" → `asistentes-proyecto.html`
  - Se agregó "Consultar Proyectos" → `consultar-proyectos.html`
  - Se agregó "Consultar Nómina" → `consultar-nomina.html`

---

## 🚀 7. Cómo Usar las Nuevas Funcionalidades

### 7.1 **Gestionar Directores**
1. Ir a Dashboard → "Directores de Proyecto"
2. Ver lista de directores registrados
3. Usar "+ Nuevo Director" para crear
4. "Editar" o "Eliminar" para modificar

### 7.2 **Gestionar Asistentes**
1. Ir a Dashboard → "Asistentes de Proyecto"
2. Filtrar por proyecto o tipo de integrante
3. "+ Nuevo Asistente" para registrar
4. Seleccionar proyecto y tipo

### 7.3 **Consultar Proyectos**
1. Ir a Dashboard → "Consultar Proyectos"
2. Buscar por nombre o filtrar por tipo
3. Click en "Ver Detalles" para información completa
4. Ver integrantes del proyecto

### 7.4 **Consultar Nómina**
1. Ir a Dashboard → "Consultar Nómina"
2. Aplicar filtros (proyecto, mes, año, estado)
3. Ver estadísticas en tarjetas
4. Click en "Ver" para detalles de cada reporte

---

## ⚙️ 8. Requisitos Técnicos

### Base de Datos
- ✅ Se crean automáticamente con `spring.jpa.hibernate.ddl-auto=update`
- Nuevas columnas en tablas:
  - `usuarios`: `telefono`, `departamento`
  - `reporte_nomina`: `id_proyecto` (relación)

### Dependencias Utilizadas
- Spring Boot 3.4.2
- Spring Data JPA
- Spring Web
- SQLite
- Jakarta Persistence

### CORS
- ✅ Configurado en todos los controladores con `@CrossOrigin(origins = "*")`
- Permite solicitudes desde cualquier origen

---

## 🔗 9. Integración Frontend-Backend

### URL Base
```javascript
const API_BASE = 'http://localhost:8080';
```

### Todas las peticiones incluyen:
- `Content-Type: application/json` para POST/PUT
- Manejo de errores con `try-catch`
- Validación de respuestas HTTP
- Mostrar mensajes de error en UI

---

## 📋 10. Estructura de Datos JSON

### Ejemplo Director
```json
{
  "id": 1,
  "nombre": "Dr. Juan Pérez",
  "correoInstitucional": "juan.perez@universidad.edu",
  "telefono": "+57 3001234567",
  "departamento": "Ingeniería de Sistemas"
}
```

### Ejemplo Asistente
```json
{
  "id": 5,
  "nombre": "Carlos Rodríguez",
  "cedula": "1001234567",
  "fechaNacimiento": "2001-05-15",
  "tipo": "ASISTENTE",
  "estado": "ACTIVO",
  "proyecto": {
    "id": 1,
    "nombre": "Proyecto de Investigación"
  }
}
```

### Ejemplo Reporte Nómina
```json
{
  "idReporte": 10,
  "proyecto": { "id": 1, "nombre": "..." },
  "mes": 1,
  "anio": 2025,
  "estado": "APROBADO",
  "fechaRegistro": "2025-01-31",
  "listaIntegrantes": [...]
}
```

---

## 🐛 Troubleshooting

### Error: "No se puede conectar al servidor"
- Verificar que la aplicación está corriendo en `localhost:8080`
- Revisar que los puertos no están ocupados
- Verificar CORS en los controladores

### Error: "404 Not Found"
- Verificar la URL exacta del endpoint
- Asegurar que la aplicación está compilada
- Revisar los mappings en los controladores

### Error: "Campo no encontrado"
- Verificar que los campos existen en el modelo
- Ejecutar `mvn clean compile`
- Revisar nombres exactos (case-sensitive)

---

## ✨ Próximas Mejoras Sugeridas

1. Agregar validación de email
2. Implementar paginación en tablas
3. Agregar búsqueda avanzada
4. Implementar auditoría de cambios
5. Agregar exportación a Excel/PDF
6. Mejorar diseño responsivo mobile
7. Implementar autenticación OAuth
8. Agregar estadísticas gráficas

---

## 📞 Contacto y Soporte

Para dudas sobre la implementación, consultar la documentación del proyecto en el README.md principal.

---

**Fecha de Implementación:** 31 de Enero de 2026  
**Estado:** ✅ Completo y funcional

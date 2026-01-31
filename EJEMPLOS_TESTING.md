# 📋 Ejemplos de Testing con cURL y Postman

## 🔗 Directores API

### 1. Crear Director
```bash
curl -X POST http://localhost:8080/directores \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Dr. Juan Pérez",
    "correoInstitucional": "juan.perez@universidad.edu",
    "telefono": "+57 3001234567",
    "departamento": "Ingeniería de Sistemas",
    "areaInvestigacion": "Ciencias de Datos"
  }'
```

**Respuesta Esperada (201):**
```json
{
  "id": 1,
  "nombre": "Dr. Juan Pérez",
  "correoInstitucional": "juan.perez@universidad.edu",
  "telefono": "+57 3001234567",
  "departamento": "Ingeniería de Sistemas",
  "areaInvestigacion": "Ciencias de Datos"
}
```

---

### 2. Obtener Todos los Directores
```bash
curl -X GET http://localhost:8080/directores \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (200):**
```json
[
  {
    "id": 1,
    "nombre": "Dr. Juan Pérez",
    "correoInstitucional": "juan.perez@universidad.edu",
    "telefono": "+57 3001234567",
    "departamento": "Ingeniería de Sistemas",
    "areaInvestigacion": "Ciencias de Datos"
  },
  {
    "id": 2,
    "nombre": "Dra. María García",
    "correoInstitucional": "maria.garcia@universidad.edu",
    "telefono": "+57 3009876543",
    "departamento": "Ingeniería Civil",
    "areaInvestigacion": "Sostenibilidad"
  }
]
```

---

### 3. Obtener Director por ID
```bash
curl -X GET http://localhost:8080/directores/1 \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (200):**
```json
{
  "id": 1,
  "nombre": "Dr. Juan Pérez",
  "correoInstitucional": "juan.perez@universidad.edu",
  "telefono": "+57 3001234567",
  "departamento": "Ingeniería de Sistemas",
  "areaInvestigacion": "Ciencias de Datos"
}
```

---

### 4. Actualizar Director
```bash
curl -X PUT http://localhost:8080/directores/1 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Dr. Juan Carlos Pérez",
    "correoInstitucional": "juancarlos.perez@universidad.edu",
    "telefono": "+57 3001234568",
    "departamento": "Ingeniería de Sistemas",
    "areaInvestigacion": "Inteligencia Artificial"
  }'
```

**Respuesta Esperada (200):**
```json
{
  "id": 1,
  "nombre": "Dr. Juan Carlos Pérez",
  "correoInstitucional": "juancarlos.perez@universidad.edu",
  "telefono": "+57 3001234568",
  "departamento": "Ingeniería de Sistemas",
  "areaInvestigacion": "Inteligencia Artificial"
}
```

---

### 5. Eliminar Director
```bash
curl -X DELETE http://localhost:8080/directores/1 \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (204):** Sin contenido

---

## 🔗 Nómina API

### 1. Obtener Todas las Nóminas
```bash
curl -X GET http://localhost:8080/nomina \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (200):**
```json
[
  {
    "idReporte": 1,
    "mes": 1,
    "anio": 2025,
    "fechaRegistro": "2025-01-31",
    "estado": "APROBADO",
    "proyecto": {
      "id": 1,
      "nombre": "Investigación en Ciencias de Datos"
    },
    "listaIntegrantes": [
      {
        "id": 5,
        "nombre": "Carlos Rodríguez",
        "cedula": "1001234567",
        "tipo": "ASISTENTE",
        "estado": "ACTIVO"
      }
    ]
  }
]
```

---

### 2. Obtener Nóminas por Proyecto
```bash
curl -X GET http://localhost:8080/nomina/proyecto/1 \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (200):**
```json
[
  {
    "idReporte": 1,
    "mes": 1,
    "anio": 2025,
    "fechaRegistro": "2025-01-31",
    "estado": "APROBADO",
    "proyecto": {
      "id": 1,
      "nombre": "Investigación en Ciencias de Datos"
    },
    "listaIntegrantes": [...]
  },
  {
    "idReporte": 2,
    "mes": 2,
    "anio": 2025,
    "fechaRegistro": "2025-02-28",
    "estado": "PENDIENTE",
    "proyecto": {
      "id": 1,
      "nombre": "Investigación en Ciencias de Datos"
    },
    "listaIntegrantes": [...]
  }
]
```

---

### 3. Obtener Asistente por ID
```bash
curl -X GET http://localhost:8080/nomina/asistentes/5 \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (200):**
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
    "nombre": "Investigación en Ciencias de Datos"
  }
}
```

---

### 4. Registrar Nuevo Asistente
```bash
curl -X POST http://localhost:8080/nomina/asistentes/registrar \
  -H "Content-Type: application/json" \
  -d '{
    "idProyecto": 1,
    "nombre": "Pedro González",
    "cedula": "1009876543",
    "fechaNacimiento": "2002-03-20"
  }'
```

**Respuesta Esperada (200):**
```json
{
  "id": 6,
  "nombre": "Pedro González",
  "cedula": "1009876543",
  "fechaNacimiento": "2002-03-20",
  "tipo": "ASISTENTE",
  "estado": "ACTIVO",
  "proyecto": {
    "id": 1,
    "nombre": "Investigación en Ciencias de Datos"
  }
}
```

---

### 5. Dar de Baja Asistente
```bash
curl -X DELETE http://localhost:8080/nomina/asistentes/5 \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (204):** Sin contenido

---

## 🔗 Proyectos API

### 1. Obtener Todos los Proyectos
```bash
curl -X GET http://localhost:8080/proyectos \
  -H "Content-Type: application/json"
```

**Respuesta Esperada (200):**
```json
[
  {
    "id": 1,
    "nombre": "Investigación en Ciencias de Datos",
    "presupuesto": 50000.0,
    "maxAsistentes": 5,
    "fechaInicio": "2024-01-15",
    "fechaFin": "2025-12-31",
    "duracionMeses": 24,
    "lineaInvestigacion": "Análisis de Datos",
    "tipo": "ProyectoInvestigacion",
    "director": {
      "id": 1,
      "nombre": "Dr. Juan Pérez"
    },
    "listaIntegrantes": [
      {
        "id": 5,
        "nombre": "Carlos Rodríguez",
        "cedula": "1001234567",
        "tipo": "ASISTENTE"
      }
    ]
  }
]
```

---

## 📝 Postman Collection

### Crear Proyecto en Postman

1. **Nueva Request**
   - Método: `GET`
   - URL: `http://localhost:8080/directores`
   - Header: `Content-Type: application/json`
   - Click "Send"

2. **Crear Director**
   - Método: `POST`
   - URL: `http://localhost:8080/directores`
   - Header: `Content-Type: application/json`
   - Body (raw JSON):
   ```json
   {
     "nombre": "Dr. Test",
     "correoInstitucional": "test@universidad.edu",
     "telefono": "+57 3001234567",
     "departamento": "Pruebas"
   }
   ```
   - Click "Send"

---

## 🧪 Escenarios de Testing

### Escenario 1: Flujo Completo Director
1. **GET /directores** → Ver lista (vacía inicialmente)
2. **POST /directores** → Crear director
3. **GET /directores** → Ver lista actualizada
4. **GET /directores/{id}** → Ver detalles
5. **PUT /directores/{id}** → Actualizar
6. **DELETE /directores/{id}** → Eliminar

### Escenario 2: Nómina por Proyecto
1. **GET /proyectos** → Obtener IDs de proyectos
2. **GET /nomina/proyecto/{id}** → Ver nóminas
3. **Validar**: Las nóminas pertenecen al proyecto especificado

### Escenario 3: Registro de Asistentes
1. **GET /nomina/asistentes** → Ver lista
2. **POST /nomina/asistentes/registrar** → Crear
3. **GET /nomina/asistentes/{id}** → Verificar
4. **DELETE /nomina/asistentes/{id}** → Eliminar

---

## ❌ Casos de Error Esperados

### Error 400: Bad Request
```bash
curl -X POST http://localhost:8080/directores \
  -H "Content-Type: application/json" \
  -d '{"nombre": ""}' # Falta correo
```

**Respuesta (400):**
```json
{
  "error": "Validación fallida"
}
```

---

### Error 404: Not Found
```bash
curl -X GET http://localhost:8080/directores/9999
```

**Respuesta (404):** Vacío o null

---

### Error 409: Duplicate
```bash
curl -X POST http://localhost:8080/directores \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Dr. Juan Pérez",
    "correoInstitucional": "juan.perez@universidad.edu",
    "telefono": "+57 3001234567",
    "departamento": "Ingeniería de Sistemas"
  }'
# Si ya existe el correo
```

**Respuesta (409):**
```json
{
  "error": "El director ya existe"
}
```

---

## 🔍 Tips de Debugging

### 1. Ver Respuesta Completa
```bash
curl -v http://localhost:8080/directores
```

### 2. Ver Headers
```bash
curl -i http://localhost:8080/directores
```

### 3. Pretty Print JSON
```bash
curl -s http://localhost:8080/directores | jq
```

### 4. Guardar Respuesta en Archivo
```bash
curl -s http://localhost:8080/directores > respuesta.json
```

### 5. Con Timing
```bash
curl -w "@curl-format.txt" -o /dev/null -s http://localhost:8080/directores
```

---

## 📊 Usando Insomnia o Postman

### Importar Collection
1. Copiar URLs de arriba
2. En Postman/Insomnia: Import → Paste
3. Configurar variables:
   - `{{base_url}}` = `http://localhost:8080`
   - `{{director_id}}` = ID del director

### Variables Recomendadas
```
base_url = http://localhost:8080
director_id = 1
proyecto_id = 1
asistente_id = 5
```

---

## ✅ Checklist de Testing Completo

- [ ] GET /directores
- [ ] POST /directores
- [ ] GET /directores/{id}
- [ ] PUT /directores/{id}
- [ ] DELETE /directores/{id}
- [ ] GET /nomina
- [ ] GET /nomina/proyecto/{id}
- [ ] GET /nomina/asistentes
- [ ] GET /nomina/asistentes/{id}
- [ ] POST /nomina/asistentes/registrar
- [ ] DELETE /nomina/asistentes/{id}
- [ ] GET /proyectos

---

**Fecha**: 31 de Enero, 2026  
**Versión**: 1.0.0

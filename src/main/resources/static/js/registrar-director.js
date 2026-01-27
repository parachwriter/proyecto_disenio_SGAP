/**
 * Lógica para registrar un Director de Proyecto.
 * Se conecta con LoginController.java -> @PostMapping("/directores")
 */

document.addEventListener("DOMContentLoaded", () => {
    // 1. SEGURIDAD: Solo el Jefe de Departamento (admin) puede ver esto
    const sesion = localStorage.getItem("sesion");
    if (sesion !== "admin") {
        alert("Acceso denegado. Solo el Jefe de Departamento puede registrar directores.");
        window.location.href = "login.html";
    }
});

document.getElementById('formDirector').addEventListener('submit', async (e) => {
    e.preventDefault(); // Evita que se recargue la página

    // Referencias a elementos visuales
    const btnSubmit = document.querySelector('.btn-submit');
    const form = document.getElementById('formDirector');

    // Estado visual de "Cargando"
    btnSubmit.disabled = true;
    btnSubmit.textContent = "Procesando...";

    // 2. CAPTURA DE DATOS
    // Nota: Las claves (keys) deben coincidir con tu clase DirectorProyecto en Java
    const datosDirector = {
        nombre: document.getElementById('nombre').value,
        cedula: document.getElementById('cedula').value,
        correoInstitucional: document.getElementById('correo').value,
        arealnvestigacion: document.getElementById('area').value // Ojo: revisa si en Java es 'arealnvestigacion' o 'areaInvestigacion'
    };

    try {
        // 3. ENVÍO AL BACKEND
        // La ruta es /auth/directores porque LoginController tiene @RequestMapping("/auth")
        const response = await fetch('/auth/directores', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(datosDirector)
        });

        const mensajeServidor = await response.text();

        if (response.ok) {
            // ÉXITO (Código 200)
            alert("✅ ¡Éxito! " + mensajeServidor);
            form.reset(); // Limpia el formulario para registrar otro
        } else {
            // ERROR DE NEGOCIO (Código 400, ej: correo duplicado)
            alert("⚠️ Error: " + mensajeServidor);
        }

    } catch (error) {
        // ERROR DE CONEXIÓN (Servidor apagado o red)
        console.error("Error crítico:", error);
        alert("❌ Error de conexión. Verifique que el Backend (Java) esté ejecutándose.");
    } finally {
        // Restaurar botón
        btnSubmit.disabled = false;
        btnSubmit.textContent = "Registrar Director";
    }
});
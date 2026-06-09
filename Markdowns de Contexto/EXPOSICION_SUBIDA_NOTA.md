# AutoReach — Mejoras Técnicas Recientes

**Asignatura / Proyecto:** Desarrollo de Interfaces  
**Alumno:** Raidel  
**Fecha:** Junio 2026

---

## 1. Corrección del flujo de edición de agentes

**Problema:** El método `editarAgente()` cargaba los datos en el formulario y acto seguido lanzaba el diálogo de confirmación de forma bloqueante. El usuario no podía modificar ningún campo antes de confirmar, por lo que siempre se guardaban los mismos datos sin cambios.

**Solución:** Separación en dos fases mediante un campo de estado `private Agente agenteEnEdicion`:

```
Flujo antiguo (roto):
  Clic Editar → Carga datos → Diálogo inmediato → Guarda sin cambios

Flujo nuevo (correcto):
  Clic Editar → Carga datos → Usuario modifica → Clic Guardar → Guarda
```

- `editarAgente()` solo carga datos y guarda la referencia al agente
- `guardarAgente()` detecta si hay edición activa y actúa en consecuencia (crear vs actualizar)
- `cancelarNuevoAgente()` limpia el estado correctamente

---

## 2. Validación visual en el formulario de agentes

**Problema:** El resto de formularios de la app muestran borde rojo y efecto de vibración en campos inválidos. El formulario de agentes solo mostraba un `AlertDialog` genérico sin marcar los campos problemáticos.

**Solución:** Se añadieron llamadas a `aplicarEfectoShake()` y `aplicarEstiloError()` (ya existentes en el controlador) en cada punto de validación de `guardarAgente()`:

| Campo | Condición | Efecto visual |
|-------|-----------|---------------|
| Usuario, Nombre, Apellidos | Vacío | Vibración + borde rojo |
| Contraseña | Vacía al crear / < 6 caracteres | Vibración + borde rojo |
| Teléfono | No cumple regex 9–15 dígitos | Vibración + borde rojo |
| Email | No cumple regex de email | Vibración + borde rojo |

---

## 3. Envío de contraseña en edición de agentes

**Problema:** `Conexion.editarAgente()` construía el cuerpo del `PUT /api/agentes/{id}` sin incluir el campo `password`. El formulario mostraba el campo con el prompt "Dejar vacío para no cambiar", generando una expectativa que nunca se cumplía.

**Solución:**
- Firma del método cambiada a `editarAgente(Agente a, String nuevaPassword)`
- El campo `password` solo se incluye en el body si `nuevaPassword` no es nulo ni vacío
- Compatible con el comportamiento del backend: si no llega el campo, no toca el hash almacenado

---

## 4. Integración del Chat con Inteligencia Artificial (Groq)

La mejora más significativa de la entrega. Se añadió un panel de chat accesible desde la barra lateral que permite al operador consultar los datos del sistema en lenguaje natural.

### Arquitectura

```
Usuario escribe pregunta
        ↓
controladorApp.enviarMensajeChat()  [hilo de fondo]
        ↓
GeminiService.enviarMensaje()
        ↓
POST https://api.groq.com/openai/v1/chat/completions
        ↓
LLaMA 3.3 70B (128k contexto, gratuito, sin restricciones UE)
        ↓
Platform.runLater → burbuja de respuesta en el chat
```

### Contexto en tiempo real

Al abrir el chat, se construye un prompt de sistema con todos los datos actuales de la BD:

- Clientes: ID, nombre, DNI, teléfono, email, dirección
- Coches disponibles: matrícula, marca, modelo, año, color, km, precio
- Alquileres activos: matrícula, nombre del cliente (cruzado por DNI), fechas
- Agentes: ID, nombre, usuario, estado activo
- Transacciones de venta y alquiler: cliente, **agente responsable**, monto, estado de pago, fecha

Esto permite responder preguntas complejas como:
- *"¿Qué agente ha gestionado más ventas?"*
- *"¿Cuántos coches BMW están disponibles y a qué precio?"*
- *"¿Hay alquileres que venzan esta semana?"*

### Problemas resueltos durante el desarrollo

| Problema | Causa | Solución |
|----------|-------|---------|
| Respuestas en inglés | Contexto vacío al enviar el primer mensaje | Input bloqueado hasta que el contexto termine de cargarse |
| Asteriscos de markdown visibles | El modelo usa `**negrita**` que JavaFX Label no renderiza | Regla explícita en el prompt: "usa solo texto plano, prohibido markdown" |
| Respuestas fuera del ámbito | Sin restricción en el prompt | Regla: si la pregunta no es sobre AutoReach, responde con mensaje fijo |
| Scroll sin bajar al último mensaje | `setVvalue(1.0)` antes de que el layout termine | Listener en `vboxMensajes.heightProperty()` que fuerza el scroll tras el render |
| Modelo no conocía al agente de cada transacción | `Transaccion` no tenía campo agente + solo se cargaban transacciones pendientes | Campo `agente` añadido al modelo + nuevo método `obtenerTodasLasTransacciones()` |

### Selección del proveedor

| Proveedor | Resultado | Motivo |
|-----------|-----------|--------|
| Google Gemini 2.0 Flash | ❌ | Tier gratuito bloqueado en España/UE |
| OpenRouter | ❌ | Requiere crédito mínimo para modelos gratuitos |
| Anthropic Claude API | ❌ | Créditos API separados de la suscripción Claude.ai |
| **Groq** | **✅** | Gratuito, sin restricciones UE, 14.400 req/día |

---

## 5. Extensión del modelo `Transaccion` con datos de agente

**Problema:** El modelo `Transaccion` no tenía campo de agente. La respuesta de la API sí incluye el agente que procesó cada operación, pero no se estaba parseando.

**Solución en dos archivos:**

`Transaccion.java` — campo `agente` con constructor compatible (el de 7 parámetros sigue funcionando):
```java
public Transaccion(int id, String tipo, String fecha, String cliente,
                   String matricula, String monto, String estadoPago, String agente)
public String getAgente() { return agente; }
```

`Conexion.java` — nuevo método `obtenerTodasLasTransacciones(String tipo)`:
- Sin filtro de estado (el método anterior solo traía las "Pendientes")
- Parsea el campo `agente` como objeto JSON o como `agente_id` numérico
- Usado exclusivamente para construir el contexto del chat

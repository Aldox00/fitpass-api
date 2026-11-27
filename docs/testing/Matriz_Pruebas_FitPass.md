# Matriz de Pruebas — API FitPass

- **Ambiente:** `https://fitpass-api-production.up.railway.app`
- **Evidencias:** `docs/testing/evidencias/` (`CP-01.png` a `CP-14.png`)
- **Autenticación:** `Bearer Token` en endpoints protegidos; `No Auth` en endpoints públicos.

## Resultados de Casos de Prueba

| ID | Categoría | Endpoint / Método | Resultado Esperado | Resultado Obtenido | Estado | Evidencia |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| `CP-01` | Camino Feliz / Auth | `POST /api/auth/login` | `200 OK` con token JWT | `200 OK` con datos de usuario y token válido | `PASS` | `CP-01.png` |
| `CP-02` | Camino Feliz | `GET /api/v1/gimnasios` | `200 OK` con listado de gimnasios | `200 OK` con gimnasios registrados | `PASS` | `CP-02.png` |
| `CP-03` | Camino Feliz | `POST /api/v1/apartados` | `201 Created` al apartar producto válido | `201 Created` (`157 ms`) — `"Producto apartado correctamente"` (`id: 10`, `estado: "PENDIENTE"`) | `PASS` | `CP-03.png` |
| `CP-04` | Validación / Esquemas | `POST /api/auth/register` | `400 Bad Request` por email inválido y password vacío | `201 Created` (`447 ms`) — registró usuario (`id: 7`) con `email: "josue.com"` y `password: ""` | `FAIL` | `CP-04.png` |
| `CP-05` | Validación / Esquemas | `POST /api/v1/apartados` | `400 Bad Request` por tipo de dato inválido (`cantidad: "dos"`) | `404 Not Found` (`116 ms`) con JSON controlado (`JSON parse error... Integer` desde `"dos"`), sin traza | `OBSERVACIÓN` | `CP-05.png` |
| `CP-06` | Validación / Esquemas | `POST /api/auth/login` | `400 Bad Request` por JSON mal formado | `404 Not Found` (`363 ms`) con JSON controlado (`JSON parse error...`), sin traza | `OBSERVACIÓN` | `CP-06.png` |
| `CP-07` | Lógica de Negocio | `POST /api/auth/register` | `400 Bad Request` / `409 Conflict` por correo duplicado | `400 Bad Request` (`373 ms`) — `"El correo ya está registrado en FitPass"` | `PASS` | `CP-07.png` |
| `CP-08` | Lógica de Negocio | `GET /api/v1/gimnasios/99999` | `404 Not Found` por ID inexistente | `500 Internal Server Error` (`365 ms`) — `"Ocurrió un error interno en el servidor."`, sin exponer traza | `FAIL` | `CP-08.png` |
| `CP-09` | Lógica de Negocio | `POST /api/v1/apartados` | `400 Bad Request` por stock insuficiente (`cantidad: 9999`) | `400 Bad Request` (`561 ms`) — `"Stock insuficiente. Disponible: 13"` | `PASS` | `CP-09.png` |
| `CP-10` | Seguridad / Auth | Endpoint protegido (`No Auth`) | `401 Unauthorized` sin token | `401 Unauthorized` al omitir `Bearer Token` | `PASS` | `CP-10.png` |
| `CP-11` | Seguridad / Roles | `POST /api/v1/gimnasios` (`SOCIO`) | `403 Forbidden` por falta de privilegios | `403 Forbidden` (`468 ms`) — `"No tienes permisos para este recurso"` | `PASS` | `CP-11.png` |
| `CP-12` | Seguridad / Inyección | `POST /api/auth/login` | Rechazo seguro ante payload SQLi (`' OR 1=1 --`) | `400 Bad Request` (`378 ms`) — `"Credenciales inválidas"`, sin bypass ni error SQL | `PASS` | `CP-12.png` |
| `CP-13` | Seguridad / Info | `GET /api/v1/gimnasios/abc` | Error controlado sin *stack trace* ni datos sensibles | `500 Internal Server Error` (`383 ms`) — `"Ocurrió un error interno en el servidor."`, sin traza ni tablas | `OBSERVACIÓN` | `CP-13.png` |
| `CP-14` | Buenas Prácticas REST | `GET /api/v1/gimnasios` | `200 OK` con `Content-Type: application/json` | `200 OK` (`375 ms`) con `Content-Type: application/json` y headers de seguridad (`nosniff`, `DENY`) | `PASS` | `CP-14.png` |

## Bitácora de Hallazgos

1. **Registro con datos inválidos (`CP-04`):** `POST /api/auth/register` aceptó `email: "josue.com"` y `password: ""` devolviendo `201 Created`. *Recomendación:* aplicar validaciones en el DTO de registro (`@Email`, `@NotBlank`, longitud mínima de contraseña) y activar `@Valid` en el controlador.
2. **Código HTTP en errores de deserialización JSON (`CP-05`, `CP-06`):** tanto un tipo incorrecto (`cantidad: "dos"`) como un JSON sin cerrar devolvieron `404 Not Found` en lugar de `400 Bad Request`. Aunque el cuerpo JSON es limpio y no expone *stack traces*, conviene mapear esos errores de lectura/deserialización a `400 Bad Request`.
3. **Código HTTP en recurso no encontrado o parámetro inválido (`CP-08`, `CP-13`):** consultar `/api/v1/gimnasios/99999` o `/api/v1/gimnasios/abc` respondió `500 Internal Server Error` con mensaje genérico (sin fuga de trazas ni estructura de BD). *Recomendación:* mapear recurso no encontrado a `404 Not Found` y error de conversión de parámetro de ruta a `400 Bad Request`.
4. **Limpieza opcional de datos de prueba:** si deseas mantener limpia la base de datos tras las pruebas, considera eliminar el usuario de prueba `id: 7` (`"josue.com"`) y el apartado `id: 10` creado durante `CP-03`.
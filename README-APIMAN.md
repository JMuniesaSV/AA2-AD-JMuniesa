# Configuración de APIMan para transportFleet

Esta guía explica cómo publicar la API `transportFleet` en APIMan, aplicar políticas de seguridad y consumirla desde Postman.

---

## Paso 1: Arrancar APIMan con Docker

```bash
# Desde la carpeta raíz del proyecto
docker compose --env-file .env.apiman -f docker-compose.apiman.yml down -v
docker compose --env-file .env.apiman -f docker-compose.apiman.yml pull
docker compose --env-file .env.apiman -f docker-compose.apiman.yml up -d
```

Si tu instalación usa el comando antiguo, sustituye `docker compose` por `docker-compose`.

Espera ~60-120 segundos hasta que el stack arranque. Después accede a:

- **Consola de gestión**: `http://localhost:8081/apimanui`
  - Usuario: `admin`
  - Contraseña: `admin123!`
- **Gateway**: `http://localhost:8082`
- **Keycloak** (login interno): `http://localhost:8085`

> [!IMPORTANT]
> Asegúrate de que la aplicación `transportFleet` también esté corriendo en `http://localhost:8080` antes de continuar.

---

## Paso 2: Publicar la API en APIMan

1. Entra en la consola de gestión en `http://localhost:8081/apimanui`
2. Crea una **Organización** → nombre: `svalero`
3. Entra en la organización → **APIs → Nueva API**:
   - **Nombre**: `transportFleet`
   - **Versión**: `1.0`
4. En la pestaña **Implementation**, configura el backend:
   - **Endpoint**: `http://host.docker.internal:8080`  *(desde Docker, apunta a tu localhost)*
   - **Tipo**: `REST`
5. Ve a la pestaña **Plans → Add Plan** y crea:
   - **Nombre**: `PlanBasico`  
   - Añade las políticas (ver Paso 3)
6. Asigna el plan a la API y **Publica la API** (botón `Publish`)

---

## Paso 3: Configurar las Políticas

### Política 1: Rate Limiting (Límite de Peticiones)
Limita el número de peticiones que un cliente puede hacer para evitar abuso.

1. Dentro de la API → pestaña **Policies → Add Policy**
2. Selecciona: **Rate Limiting Policy**
3. Configura:
   - **Limit**: `100`
   - **Granularity**: `Api`
   - **Period**: `Minute`
4. Guarda

> Con esta política, cada API Key solo podrá hacer 100 peticiones por minuto.

### Política 2: IP Allowlist / Blocklist (Lista de IPs)
Restringe qué IPs pueden acceder a la API.

1. **Policies → Add Policy**
2. Selecciona: **IP Allowlist Policy**
3. Introduce las IPs autorizadas (ej: `127.0.0.1`, tu IP de red local)
4. Guarda y **re-publica** la API

---

## Paso 4: Crear un Cliente y Obtener el API Token

1. En la organización `svalero` → **Client Apps → Nuevo Client App**:
   - **Nombre**: `postman-client`
   - **Versión**: `1.0`
2. Entra al client app → **APIs → Add API**:
   - Selecciona `transportFleet 1.0` con el plan `PlanBasico`
3. **Registra** el client app (botón `Register`)
4. Copia el valor de **API Key** generado

La URL del gateway tendrá el formato:
```
http://localhost:8082/svalero/transportFleet/1.0/v2/drivers
```

---

## Paso 5: Consumir la API desde Postman via Gateway

Todas las peticiones al gateway deben incluir la cabecera:

| Header    | Value                  |
|-----------|------------------------|
| `X-API-Key` | `<tu-api-key-copiada>` |

Ejemplo de petición GET:
```
GET http://localhost:8082/svalero/transportFleet/1.0/v2/drivers
X-API-Key: <tu-api-key>
```

---

## Resumen de la Arquitectura con APIMan

```
Postman / Cliente
      │  X-API-Key: <token>
      ▼
[APIMan Gateway :8082]
  │  Política 1: Rate Limiting (100 req/min)
  │  Política 2: IP Allowlist
      ▼
[Spring Boot transportFleet :8080]
      ▼
[H2 / MariaDB]
```

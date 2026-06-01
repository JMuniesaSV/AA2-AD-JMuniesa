# Despliegue de transportFleet en AWS EC2

Esta guía explica cómo desplegar la API `transportFleet` en una instancia EC2 de AWS usando Docker.

---

## Requisitos Previos

- Cuenta de AWS con permisos para crear instancias EC2
- Par de claves SSH creado en AWS (`.pem`)
- Repositorio del proyecto en GitHub

---

## Paso 1: Crear la Instancia EC2

1. Entra en la **Consola de AWS → EC2 → Lanzar instancia**
2. Configura:
   - **Nombre**: `transportFleet-server`
   - **AMI**: `Amazon Linux 2023` (64-bit x86)
   - **Tipo de instancia**: `t2.micro` (Free Tier)
   - **Par de claves**: selecciona o crea uno `.pem`
3. En **Configuración de red → Reglas de grupos de seguridad**, abre los puertos:
   | Puerto | Protocolo | Origen      | Para qué                   |
   |--------|-----------|-------------|----------------------------|
   | 22     | TCP       | Mi IP       | Conexión SSH               |
   | 8080   | TCP       | 0.0.0.0/0   | API Spring Boot            |
   | 3306   | TCP       | 0.0.0.0/0   | MariaDB (opcional, solo si acceso externo) |
4. Lanza la instancia y anota la **IP pública**.

---

## Paso 2: Conectarse a la Instancia por SSH

```bash
chmod 400 tu-clave.pem
ssh -i "tu-clave.pem" ec2-user@<IP-PUBLICA-EC2>
```

---

## Paso 3: Ejecutar el Script de Despliegue

1. Edita el script `deploy-ec2.sh` y cambia `REPO_URL` con la URL de tu repositorio GitHub
2. Copia el script a la instancia:
   ```bash
   scp -i "tu-clave.pem" deploy-ec2.sh ec2-user@<IP-PUBLICA-EC2>:~/
   ```
3. En la instancia EC2, ejecuta:
   ```bash
   chmod +x deploy-ec2.sh
   ./deploy-ec2.sh
   ```

El script instalará Docker, Git, clonará el repositorio y levantará la aplicación con Docker Compose.

---

## Paso 4: Verificar el Despliegue

Desde tu máquina local, comprueba que la API responde:

```bash
curl http://<IP-PUBLICA-EC2>:8080/v2/drivers
```

O abre en el navegador: `http://<IP-PUBLICA-EC2>:8080/v2/drivers`

---

## Comandos Útiles en la Instancia

```bash
# Ver logs de la aplicación
docker-compose -f docker-compose.dev.yaml logs -f app

# Parar los servicios
docker-compose -f docker-compose.dev.yaml down

# Reiniciar los servicios
docker-compose -f docker-compose.dev.yaml restart
```

---

## Arquitectura en AWS

```
Internet
   │
   ▼
[Security Group]
   │  :8080
   ▼
[EC2 - Amazon Linux 2023]
  ├── [Docker: Spring Boot :8080]  (perfil: prod)
  └── [Docker: MariaDB :3308]
```

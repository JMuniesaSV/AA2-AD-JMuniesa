#!/bin/bash
# =============================================================
# Script de despliegue de transportFleet en AWS EC2 (Amazon Linux 2023)
# Ejecutar en la instancia EC2 como usuario ec2-user
# =============================================================

set -e  # Salir si cualquier comando falla

echo "========================================"
echo " Iniciando despliegue de transportFleet"
echo "========================================"

# --- 1. Instalar Docker si no está instalado ---
if ! command -v docker &> /dev/null; then
    echo "[1/6] Instalando Docker..."
    sudo dnf update -y
    sudo dnf install -y docker
    sudo systemctl start docker
    sudo systemctl enable docker
    sudo usermod -aG docker ec2-user
    echo "     Docker instalado correctamente."
else
    echo "[1/6] Docker ya está instalado. Continuando..."
fi

# --- 2. Instalar Docker Compose si no está instalado ---
if ! command -v docker-compose &> /dev/null; then
    echo "[2/6] Instalando Docker Compose..."
    sudo curl -L "https://github.com/docker/compose/releases/latest/download/docker-compose-$(uname -s)-$(uname -m)" \
        -o /usr/local/bin/docker-compose
    sudo chmod +x /usr/local/bin/docker-compose
    echo "     Docker Compose instalado correctamente."
else
    echo "[2/6] Docker Compose ya está instalado. Continuando..."
fi

# --- 3. Instalar Git si no está instalado ---
if ! command -v git &> /dev/null; then
    echo "[3/6] Instalando Git..."
    sudo dnf install -y git
else
    echo "[3/6] Git ya está instalado. Continuando..."
fi

# --- 4. Clonar o actualizar el repositorio ---
REPO_DIR="/home/ec2-user/transportFleet"
REPO_URL="https://github.com/TU_USUARIO/TU_REPOSITORIO.git"  # <-- CAMBIAR

if [ -d "$REPO_DIR" ]; then
    echo "[4/6] Actualizando repositorio..."
    cd "$REPO_DIR"
    git pull origin main
else
    echo "[4/6] Clonando repositorio..."
    git clone "$REPO_URL" "$REPO_DIR"
    cd "$REPO_DIR"
fi

# --- 5. Levantar la base de datos y la aplicación con Docker Compose ---
echo "[5/6] Levantando servicios con Docker Compose..."
docker-compose -f docker-compose.dev.yaml down --remove-orphans || true
docker-compose -f docker-compose.dev.yaml up -d --build

# --- 6. Comprobar que el servicio está disponible ---
echo "[6/6] Esperando que la aplicación arranque..."
sleep 30
if curl -sf http://localhost:8080/drivers > /dev/null; then
    echo ""
    echo "========================================"
    echo " Despliegue EXITOSO en EC2"
    echo " API disponible en http://$(curl -s http://169.254.169.254/latest/meta-data/public-ipv4):8080"
    echo "========================================"
else
    echo ""
    echo "[ERROR] La aplicación no respondió a tiempo. Revisa los logs con:"
    echo "        docker-compose -f docker-compose.dev.yaml logs app"
    exit 1
fi

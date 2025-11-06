#!/bin/bash
echo "Iniciando o provisionamento dos recursos no Azure..."

export RG="rg-iottu"
export LOCATION="brazilsouth"
export SERVER_NAME="postgres-iottu"
export USERNAME="lcasql"
export PASSWORD="Challenge!DD25"
export DBNAME="iottu_postgres_db"

# 1 Criar o Grupo de Recursos (comando inalterado)
az group create \
  --name "$RG" \
  --location "$LOCATION"

# 2 Criar o servidor PostgreSQL Flexible Server
echo "--> Criando o Servidor PostgreSQL: $SERVER_NAME"
az postgres flexible-server create \
  --resource-group "$RG" \
  --name "$SERVER_NAME" \
  --location "$LOCATION" \
  --admin-user "$USERNAME" \
  --admin-password "$PASSWORD" \
  --sku-name Standard_B1ms \
  --tier Burstable 
  --public-access all
  
# 3 Criar o banco de dados dentro do servidor
echo "--> Criando o Banco de Dados: $DBNAME"
az postgres flexible-server db create \
  --resource-group "$RG" \
  --server-name "$SERVER_NAME" \
  --database-name "$DBNAME"

# 4 Criar a regra de firewall
echo "--> Configurando a regra de firewall 'AllowAll' para permitir acesso de qualquer IP"
az postgres flexible-server firewall-rule create \
  --resource-group "$RG" \
  --name "$SERVER_NAME" \
  --rule-name AllowAll \
  --start-ip-address 0.0.0.0 \
  --end-ip-address 255.255.255.255
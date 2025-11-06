#!/bin/bash

# Variáveis

# Altere todos os rm558868 e seu Repositório
#
# Utilizem Regiões diferentes, espalhem a criação do App:
#
# brazilsouth
# eastus
# eastus2
# westus
# westus2
export RESOURCE_GROUP_NAME="rg-iottu"
export WEBAPP_NAME="iottu-java"
export APP_SERVICE_PLAN="planIottu"
# Altere a sua região conforme orientação do Professor
export LOCATION="brazilsouth"
export RUNTIME="JAVA:17-java17"
export APP_INSIGHTS_NAME="ai-iottu"

# Criar Application Insights
az monitor app-insights component create \
  --app "$APP_INSIGHTS_NAME" \
  --location "$LOCATION" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --application-type web

# Criar o Plano de Serviço
az appservice plan create \
  --name "$APP_SERVICE_PLAN" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --location "$LOCATION" \
  --sku F1 \
  --is-linux

# Criar o Serviço de Aplicativo
az webapp create \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --plan "$APP_SERVICE_PLAN" \
  --runtime "$RUNTIME"

# Habilita a autenticação Básica (SCM)
az resource update \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --namespace Microsoft.Web \
  --resource-type basicPublishingCredentialsPolicies \
  --name scm \
  --parent sites/"$WEBAPP_NAME" \
  --set properties.allow=true

# Recuperar a String de Conexão do Application Insights
CONNECTION_STRING=$(az monitor app-insights component show \
  --app "$APP_INSIGHTS_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --query connectionString \
  --output tsv)

# Configurar as Variáveis de Ambiente necessárias do nosso App e do Application Insights
az webapp config appsettings set \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME" \
  --settings \
    APPLICATIONINSIGHTS_CONNECTION_STRING="$CONNECTION_STRING" \
    ApplicationInsightsAgent_EXTENSION_VERSION="~3" \
    XDT_MicrosoftApplicationInsights_Mode="Recommended" \
    XDT_MicrosoftApplicationInsights_PreemptSdk="1" \
    SPRING_DATASOURCE_USERNAME="lcasql" \
    SPRING_DATASOURCE_PASSWORD="Challenge!DD25" \
    SPRING_DATASOURCE_URL="jdbc:postgresql://postgres-iottu.postgres.database.azure.com:5432/iottu_postgres_db?sslmode=require"

# Reiniciar o Web App
az webapp restart \
  --name "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME"

# Criar a conexão do nosso Web App com o Application Insights
az monitor app-insights component connect-webapp \
  --app "$APP_INSIGHTS_NAME" \
  --web-app "$WEBAPP_NAME" \
  --resource-group "$RESOURCE_GROUP_NAME"
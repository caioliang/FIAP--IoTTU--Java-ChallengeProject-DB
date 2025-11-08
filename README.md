# IoTTU - Sistema de Gerenciamento de Pátios de Motocicletas - Version DB ORACLE

## 📋 Descrição do Projeto

IoTTU é um sistema integrado de gerenciamento de motocicletas em pátios, desenvolvido para a FIAP. O projeto combina tecnologias de IoT (Internet das Coisas) com uma aplicação web robusta e uma API REST para dispositivos móveis. O sistema permite o rastreamento em tempo real de motocicletas através de tags RFID e Wi-Fi, utilizando comunicação MQTT para receber dados de dispositivos IoT.

## 📋 DB-ORACLE - Requisitos:
- **Conta Oracle Fiap**: Declarar variaveis de ambiente: ORACLE_USER e ORACLE_PASSWORD

## 📋 DB-ORACLE - API REST:
- **/yards/vagas/{id}**: Vagas disponíveis
- **/yards/json/{id}}**: Json das motos de um pátio
- **/yards/relatorio**: Relatorio em JSON dos status das motos de todos os patios cadastrados



## 👥 Autores

- **Allan Brito Moreira** - RM558948
- **Caio Liang** - RM558868
- **Levi Magni** - RM98276


## 🚀 Funcionalidades

### 📱 Interface Web (Thymeleaf)
- **Dashboard Principal**: Visualização geral do sistema
- **Gerenciamento de Usuários**: CRUD completo com controle de permissões (ADMIN/USER)
- **Gerenciamento de Pátios**: Cadastro e controle de pátios com capacidade e localização
- **Gerenciamento de Motocicletas**: Registro de motos com placa, chassi, modelo e número do motor
- **Gerenciamento de Tags**: Controle de tags RFID/Wi-Fi associadas às motocicletas
- **Gerenciamento de Antenas**: Cadastro de antenas com coordenadas GPS
- **Mapa do Pátio**: Visualização geográfica das motos e antenas em tempo real
- **Autenticação OAuth2**: Login com GitHub e Google
- **Internacionalização**: Suporte para português (pt_BR) e inglês (en_US)

### 🔌 API REST (Mobile)
Endpoints disponíveis para integração mobile:

#### Autenticação
- `POST /api/v1/auth/login` - Login de usuários

#### Usuários
- `GET /api/v1/users` - Listar usuários
- `GET /api/v1/users/{id}` - Buscar usuário por ID
- `POST /api/v1/users` - Criar usuário
- `PUT /api/v1/users/{id}` - Atualizar usuário
- `DELETE /api/v1/users/{id}` - Deletar usuário

#### Pátios
- `GET /api/v1/yards` - Listar pátios (suporta filtro por userId)
- `GET /api/v1/yards/{id}` - Buscar pátio por ID
- `GET /api/v1/yards/{id}/map` - Obter dados do mapa do pátio
- `POST /api/v1/yards` - Criar pátio
- `PUT /api/v1/yards/{id}` - Atualizar pátio
- `DELETE /api/v1/yards/{id}` - Deletar pátio

#### Motocicletas
- `GET /api/v1/motorcycles` - Listar motocicletas (suporta filtro por userId)
- `GET /api/v1/motorcycles/{id}` - Buscar motocicleta por ID
- `POST /api/v1/motorcycles` - Criar motocicleta
- `PUT /api/v1/motorcycles/{id}` - Atualizar motocicleta
- `DELETE /api/v1/motorcycles/{id}` - Deletar motocicleta

#### Tags
- `GET /api/v1/tags` - Listar tags
- `GET /api/v1/tags/available` - Listar tags disponíveis
- `GET /api/v1/tags/{id}` - Buscar tag por ID
- `POST /api/v1/tags` - Criar tag
- `PUT /api/v1/tags/{id}` - Atualizar tag
- `DELETE /api/v1/tags/{id}` - Deletar tag

#### Antenas
- `GET /api/v1/antennas` - Listar antenas (suporta filtro por yardId)
- `GET /api/v1/antennas/{id}` - Buscar antena por ID
- `POST /api/v1/antennas` - Criar antena
- `PUT /api/v1/antennas/{id}` - Atualizar antena
- `DELETE /api/v1/antennas/{id}` - Deletar antena

**Documentação Swagger**: Disponível em `/swagger-ui.html` quando o servidor estiver rodando.

### 📡 Sistema IoT (MQTT)

#### Arquitetura IoT
O sistema utiliza o protocolo MQTT para comunicação em tempo real com dispositivos IoT instalados nos pátios. A arquitetura é composta por:

1. **Broker MQTT**: Servidor de mensageria que recebe dados dos dispositivos
2. **Dispositivos IoT**: 
   - Tags RFID/Wi-Fi nas motocicletas
   - Antenas receptoras nos pátios
3. **Backend Java**: Processa mensagens MQTT e atualiza o banco de dados

#### Configuração MQTT

No arquivo `application.properties`:
```properties
mqtt.broker.url=tcp://${SEU_IP:127.0.0.1}:1883
mqtt.client.id=iottu-backend-client
mqtt.enabled=false
```

- **mqtt.broker.url**: Endereço do broker MQTT (padrão: localhost:1883)
- **mqtt.client.id**: Identificador único do cliente
- **mqtt.enabled**: Habilita/desabilita a funcionalidade MQTT (padrão: false)

> ⚠️ **IMPORTANTE**: Para utilizar a funcionalidade IoT/MQTT, é obrigatório alterar `mqtt.enabled=false` para `mqtt.enabled=true` no arquivo `application.properties`. Sem essa alteração, o sistema não irá conectar ao broker MQTT nem processar mensagens dos dispositivos IoT.

#### Tópico MQTT
- **Tópico**: `fiap/iot/moto`
- **QoS**: 1 (Garantia de entrega mínima de uma vez)

#### Formato das Mensagens

##### Payload de Motocicletas
```json
{
  "motos": [
    {
      "status": "Ativa",
      "alerta": "Movimento não autorizado",
      "id_status": 1,
      "id_patio": 1,
      "placa_moto": "ABC1234",
      "chassi_moto": "9BWZZZ377VT004251",
      "nr_motor_moto": "MT123456",
      "modelo_moto": "Honda CG 160",
      "codigo_rfid_tag": "RFID123",
      "ssid_wifi_tag": "WIFI_TAG_001",
      "x": 100.5,
      "y": 200.3,
      "latitude": -23.550520,
      "longitude": -46.633308
    }
  ]
}
```

##### Payload de Antenas
```json
{
  "antenas": [
    {
      "id_antena": 1,
      "id_patio": 1,
      "codigo_antena": "ANT001",
      "latitude_antena": -23.550520,
      "longitude_antena": -46.633308,
      "x": 50.0,
      "y": 100.0
    }
  ]
}
```

#### Processamento de Dados

O sistema processa automaticamente os dados recebidos via MQTT:

1. **MqttListener** escuta o tópico `fiap/iot/moto`
2. Identifica o tipo de payload (motocicletas ou antenas)
3. Valida e deserializa os dados JSON
4. Atualiza/cria registros no banco de dados
5. Registra logs de todas as operações

#### Como Habilitar o MQTT

Para ativar a funcionalidade MQTT:

1. Configure um broker MQTT (ex: Mosquitto, HiveMQ)
2. No `application.properties`, **altere obrigatoriamente**:
   ```properties
   mqtt.enabled=true
   mqtt.broker.url=tcp://SEU_BROKER_IP:1883
   ```
   > 🔴 **ATENÇÃO**: A configuração `mqtt.enabled=true` é essencial! Por padrão, o valor é `false` e o sistema MQTT não será iniciado.
3. Reinicie a aplicação

#### Testando MQTT

Você pode testar enviando mensagens usando um cliente MQTT:

```bash
# Exemplo com mosquitto_pub
mosquitto_pub -h localhost -t "fiap/iot/moto" -m '{"motos":[{"placa_moto":"ABC1234","modelo_moto":"Honda CG"}]}'
```

## 🛠️ Tecnologias Utilizadas

### Backend
- **Java 17** - Linguagem de programação
- **Spring Boot 3.5.6** - Framework principal
- **Spring Data JPA** - Persistência de dados
- **Spring Security** - Segurança e autenticação
- **Spring OAuth2 Client** - Autenticação com GitHub e Google
- **Spring Web** - API REST
- **Spring Cache** - Cache de dados

### Frontend
- **Thymeleaf** - Template engine
- **Thymeleaf Layout Dialect** - Layout management
- **HTML/CSS/JavaScript** - Interface web
- **Bootstrap** (via CSS customizado) - Estilização

### Banco de Dados
- **PostgreSQL** - Banco de dados relacional
- **Flyway** - Versionamento de banco de dados

### IoT
- **Eclipse Paho MQTT Client 1.2.5** - Cliente MQTT para comunicação IoT
- **Protocol MQTT** - Comunicação com dispositivos IoT

### Documentação
- **SpringDoc OpenAPI 2.6.0** - Documentação automática da API (Swagger)

### Ferramentas de Desenvolvimento
- **Lombok** - Redução de código boilerplate
- **Spring Boot DevTools** - Hot reload durante desenvolvimento
- **Docker Compose** - Containerização do PostgreSQL
- **Gradle** - Gerenciamento de dependências e build

### Validação
- **Jakarta Validation** - Validação de dados

## 📁 Estrutura do Projeto

```
FIAP--IoTTU--Java-ChallengeProject/
│
├── src/
│   ├── main/
│   │   ├── java/br/com/fiap/iottu/
│   │   │   ├── antenna/              # Gerenciamento de antenas
│   │   │   │   ├── Antenna.java
│   │   │   │   ├── AntennaController.java
│   │   │   │   ├── AntennaRepository.java
│   │   │   │   └── AntennaService.java
│   │   │   │
│   │   │   ├── api/                  # Controllers REST para mobile
│   │   │   │   ├── AntennaRestController.java
│   │   │   │   ├── AuthRestController.java
│   │   │   │   ├── MotorcycleRestController.java
│   │   │   │   ├── TagRestController.java
│   │   │   │   ├── UserRestController.java
│   │   │   │   └── YardRestController.java
│   │   │   │
│   │   │   ├── auth/                 # Autenticação web
│   │   │   │   └── AuthController.java
│   │   │   │
│   │   │   ├── config/               # Configurações
│   │   │   │   ├── CorsConfig.java
│   │   │   │   ├── I18nConfiguration.java
│   │   │   │   ├── MqttConfig.java
│   │   │   │   └── SecurityConfiguration.java
│   │   │   │
│   │   │   ├── dto/                  # Data Transfer Objects
│   │   │   │   ├── AntenaDataDTO.java
│   │   │   │   ├── AntenasPayloadDTO.java
│   │   │   │   ├── AntennaRequestDTO.java
│   │   │   │   ├── MotorcycleDataDTO.java
│   │   │   │   ├── MotorcycleRequestDTO.java
│   │   │   │   ├── MotorcyclesPayloadDTO.java
│   │   │   │   ├── TagRequestDTO.java
│   │   │   │   ├── UserRequestDTO.java
│   │   │   │   └── YardRequestDTO.java
│   │   │   │
│   │   │   ├── helper/               # Classes auxiliares
│   │   │   │   └── MessageHelper.java
│   │   │   │
│   │   │   ├── home/                 # Controller da home
│   │   │   │   └── HomeController.java
│   │   │   │
│   │   │   ├── motorcycle/           # Gerenciamento de motocicletas
│   │   │   │   ├── Motorcycle.java
│   │   │   │   ├── MotorcycleController.java
│   │   │   │   ├── MotorcycleRepository.java
│   │   │   │   └── MotorcycleService.java
│   │   │   │
│   │   │   ├── motorcyclestatus/     # Status das motocicletas
│   │   │   │   ├── MotorcycleStatus.java
│   │   │   │   ├── MotorcycleStatusRepository.java
│   │   │   │   └── MotorcycleStatusService.java
│   │   │   │
│   │   │   ├── mqtt/                 # Comunicação MQTT/IoT
│   │   │   │   ├── MqttConfiguration.java
│   │   │   │   └── MqttListener.java
│   │   │   │
│   │   │   ├── tag/                  # Gerenciamento de tags
│   │   │   │   ├── Tag.java
│   │   │   │   ├── TagController.java
│   │   │   │   ├── TagRepository.java
│   │   │   │   └── TagService.java
│   │   │   │
│   │   │   ├── user/                 # Gerenciamento de usuários
│   │   │   │   ├── User.java
│   │   │   │   ├── UserController.java
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── UserService.java
│   │   │   │
│   │   │   ├── validation/           # Validações customizadas
│   │   │   │   ├── OnCreate.java
│   │   │   │   └── OnUpdate.java
│   │   │   │
│   │   │   ├── yard/                 # Gerenciamento de pátios
│   │   │   │   ├── Yard.java
│   │   │   │   ├── YardController.java
│   │   │   │   ├── YardMapDTO.java
│   │   │   │   ├── YardMapService.java
│   │   │   │   ├── YardRepository.java
│   │   │   │   └── YardService.java
│   │   │   │
│   │   │   └── IoTtuApplication.java # Classe principal
│   │   │
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── messages_en_US.properties
│   │       ├── messages_pt_BR.properties
│   │       │
│   │       ├── db/migration/         # Scripts Flyway
│   │       │   ├── V1__create_initial_tables.sql
│   │       │   ├── V2__Add_foreign_keys.sql
│   │       │   ├── V3__Necessary_inserts.sql
│   │       │   └── V4__Insert_admin_user.sql
│   │       │
│   │       ├── static/
│   │       │   ├── css/
│   │       │   │   └── style.css
│   │       │   └── js/
│   │       │
│   │       └── templates/            # Templates Thymeleaf
│   │           ├── home.html
│   │           ├── layout.html
│   │           ├── login.html
│   │           ├── antenna/
│   │           │   ├── form.html
│   │           │   └── list.html
│   │           ├── auth/
│   │           │   └── register.html
│   │           ├── motorcycle/
│   │           │   ├── form.html
│   │           │   └── list.html
│   │           ├── tag/
│   │           │   ├── form.html
│   │           │   └── list.html
│   │           ├── user/
│   │           │   ├── form.html
│   │           │   ├── list.html
│   │           │   └── profile.html
│   │           └── yard/
│   │               ├── form.html
│   │               ├── list.html
│   │               └── map.html
│   │
│   └── test/
│       └── java/br/com/fiap/iottu/
│           └── IoTtuApplicationTests.java
│
├── build.gradle                      # Configuração Gradle
├── compose.yaml                      # Docker Compose (PostgreSQL)
├── gradlew                          # Gradle Wrapper (Unix)
├── gradlew.bat                      # Gradle Wrapper (Windows)
├── settings.gradle                  # Configurações do Gradle
└── README.md                        # Este arquivo
```

## 🔧 Como Rodar o Projeto

### Pré-requisitos

- **Java 17** ou superior
- **Docker** e **Docker Compose** (para o PostgreSQL)
- **Gradle** (ou use o wrapper incluído)
- **(Opcional)** Broker MQTT se quiser testar a funcionalidade IoT

### Passo a Passo

#### 1. Clone o Repositório
```bash
git clone <url-do-repositorio>
cd FIAP--IoTTU--Java-ChallengeProject
```

#### 2. Inicie o Banco de Dados PostgreSQL
```bash
docker-compose up -d
```

Isso iniciará um container PostgreSQL com as seguintes configurações:
- **Host**: localhost
- **Porta**: 5432
- **Database**: iottu
- **Usuário**: iottu
- **Senha**: iottu

#### 3. Configure as Variáveis de Ambiente (Opcional)

Para OAuth2 (GitHub e Google), configure as variáveis:

**Windows (CMD):**
```cmd
set GITHUB_CLIENT_ID=seu_client_id
set GITHUB_CLIENT_SECRET=seu_client_secret
set GOOGLE_CLIENT_ID=seu_client_id
set GOOGLE_CLIENT_SECRET=seu_client_secret
```

**Windows (PowerShell):**
```powershell
$env:GITHUB_CLIENT_ID="seu_client_id"
$env:GITHUB_CLIENT_SECRET="seu_client_secret"
$env:GOOGLE_CLIENT_ID="seu_client_id"
$env:GOOGLE_CLIENT_SECRET="seu_client_secret"
```

Para MQTT (se desejar habilitar):
```cmd
set SEU_IP=192.168.1.100
```

#### 4. Compile o Projeto
```bash
# Windows
gradlew.bat build

# Linux/Mac
./gradlew build
```

#### 5. Execute a Aplicação
```bash
# Windows
gradlew.bat bootRun

# Linux/Mac
./gradlew bootRun
```

Ou execute diretamente o JAR gerado:
```bash
java -jar build/libs/IoTTU-0.0.1-SNAPSHOT.jar
```

#### 6. Acesse a Aplicação

- **Interface Web**: http://localhost:8080
- **API REST**: http://localhost:8080/api/v1
- **Swagger UI**: http://localhost:8080/swagger-ui.html

### Credenciais Padrão

Após a primeira execução, um usuário administrador é criado automaticamente:
- **Email**: admin@iottu.com
- **Senha**: admin123
- **Role**: ADMIN

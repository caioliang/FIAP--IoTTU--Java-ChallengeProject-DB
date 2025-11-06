CREATE TABLE TB_STATUS_MOTO (
    id_status SERIAL PRIMARY KEY,
    descricao_status VARCHAR(50) NOT NULL
);

CREATE TABLE TB_USUARIO (
    id_usuario SERIAL PRIMARY KEY,
    nome_usuario VARCHAR(100) NOT NULL,
    email_usuario VARCHAR(100) NOT NULL UNIQUE,
    senha_usuario VARCHAR(100) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'USER'
);

CREATE TABLE TB_PATIO (
    id_patio SERIAL PRIMARY KEY,
    id_usuario INTEGER NOT NULL,
    cep_patio VARCHAR(9),
    numero_patio VARCHAR(10),
    cidade_patio VARCHAR(50),
    estado_patio VARCHAR(2),
    capacidade_patio INTEGER
);

CREATE TABLE TB_MOTO (
    id_moto SERIAL PRIMARY KEY,
    id_status INTEGER NOT NULL,
    id_patio INTEGER NOT NULL,
    placa_moto VARCHAR(10) UNIQUE,
    chassi_moto VARCHAR(20) UNIQUE,
    nr_motor_moto VARCHAR(50) UNIQUE,
    modelo_moto VARCHAR(50) NOT NULL
);

CREATE TABLE TB_ANTENA (
    id_antena SERIAL PRIMARY KEY,
    id_patio INTEGER NOT NULL,
    codigo_antena VARCHAR(50) NOT NULL,
    latitude_antena NUMERIC(10, 6) NOT NULL,
    longitude_antena NUMERIC(10, 6) NOT NULL
);

CREATE TABLE TB_TAG (
    id_tag SERIAL PRIMARY KEY,
    codigo_rfid_tag VARCHAR(50) NOT NULL,
    ssid_wifi_tag VARCHAR(50) NOT NULL,
    latitude_tag NUMERIC(10, 6) NOT NULL,
    longitude_tag NUMERIC(10, 6) NOT NULL,
    data_hora_tag TIMESTAMP NOT NULL
);

CREATE TABLE TB_MOTO_TAG (
    id_moto INTEGER NOT NULL,
    id_tag INTEGER NOT NULL,
    PRIMARY KEY (id_moto, id_tag)
);

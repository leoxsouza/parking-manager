# Parking Manager

Sistema de gerenciamento de estacionamento desenvolvido com Kotlin e Spring Boot, utilizando abordagem reativa com Spring WebFlux e R2DBC.

## Sobre o Projeto

O Parking Manager é uma aplicação para gerenciamento de estacionamentos que recebe eventos de um simulador de garagem via webhooks. A aplicação processa diferentes tipos de eventos (ENTRY, EXIT, PARKED) utilizando o padrão Strategy para lidar com cada tipo de evento de forma específica.

### Tecnologias Utilizadas

- **Kotlin**: Linguagem de programação principal
- **Spring Boot 3.5.0**: Framework para desenvolvimento da aplicação
- **Spring WebFlux**: Para programação reativa
- **R2DBC**: Para acesso reativo ao banco de dados
- **PostgreSQL**: Banco de dados relacional
- **Docker**: Para containerização da aplicação
- **Coroutines**: Para programação assíncrona em Kotlin

## Arquitetura

A aplicação segue uma arquitetura baseada em:

1. **Controllers**: Recebem requisições HTTP, incluindo webhooks do simulador de garagem
2. **DTOs**: Objetos de transferência de dados para serialização/deserialização JSON
3. **Services**: Contêm a lógica de negócio
4. **Strategy Pattern**: Implementado para processar diferentes tipos de eventos de estacionamento
5. **Configurações**: Configurações específicas para Jackson, R2DBC, etc.

## Executando a Aplicação

### Pré-requisitos

- Docker e Docker Compose instalados

### Passos para Execução

1. Clone o repositório:
   ```bash
   git clone https://github.com/seu-usuario/parking-manager.git
   cd parking-manager
   ```

2. Execute a aplicação usando Docker Compose:
   ```bash
   docker-compose up --build
   ```

3. A aplicação estará disponível em:
   - API principal: http://localhost:3003
   - Simulador de garagem: http://localhost:3000

### Containers Docker

O projeto utiliza três containers Docker:

1. **parking-app**: A aplicação Spring Boot
2. **parking-db**: Banco de dados PostgreSQL
3. **garage-sim**: Simulador de garagem que envia webhooks para a aplicação

O simulador de garagem compartilha o namespace de rede com o container da aplicação (`network_mode: "service:app"`), permitindo que ele envie webhooks para `http://localhost:3003/webhook`.

## Endpoints

- **POST /webhook**: Recebe eventos do simulador de garagem no formato:
  ```json
  {
    "license_plate": "ABC1234",
    "entry_time": "2025-06-22T22:07:29",
    "event_type": "ENTRY"
  }
  ```

## Estrutura de Eventos

A aplicação processa três tipos de eventos:

1. **ENTRY**: Quando um veículo entra no estacionamento
2. **EXIT**: Quando um veículo sai do estacionamento
3. **PARKED**: Quando um veículo está estacionado

## Depuração

Para visualizar os logs da aplicação:

```bash
docker logs parking-app
```

## Parando a Aplicação

Para parar todos os containers:

```bash
docker-compose down
```

Para parar e remover volumes (isso apagará os dados do banco):

```bash
docker-compose down -v
```

## Desenvolvimento

Para desenvolvimento local sem Docker:

1. Certifique-se de ter PostgreSQL instalado e rodando na porta 5432
2. Configure as variáveis de ambiente apropriadas
3. Execute a aplicação usando Maven:
   ```bash
   mvn spring-boot:run
   ```

# Bank System API

API REST para gerenciamento de clientes, contas bancárias e transações financeiras, desenvolvida com Java e Spring Boot.

O projeto foi criado com foco em boas práticas de desenvolvimento backend, arquitetura em camadas, tratamento de exceções e regras de negócio aplicadas ao contexto bancário.

---

# Tecnologias Utilizadas

* Java 21
* Spring Boot
* Spring Data JPA
* Hibernate
* PostgreSQL
* Maven
* Lombok
* Bean Validation
* Jakarta Validation
* Spring Transaction Management

---

# Funcionalidades

## Clientes

* Criar cliente
* Buscar cliente por ID
* Listar clientes
* Atualizar dados do cliente

## Contas

* Criar conta
* Buscar conta por ID
* Listar contas
* Consultar saldo
* Alterar limite
* Bloquear conta
* Ativar conta
* Encerrar conta

## Transações

* Depósito
* Saque
* Transferência entre contas
* Consulta de extrato

---

# Arquitetura e Boas Práticas Implementadas

## DTO Pattern

A aplicação utiliza DTOs para separar a camada de API da camada de persistência.

### DTOs Utilizados

#### Cliente

* ClienteDTO
* ClienteResponseDTO

#### Conta

* ContaDTO
* ContaResponseDTO
* SaldoResponseDTO

#### Transação

* TransacaoResponseDTO

---

## Service Layer Pattern

Toda a lógica de negócio está concentrada na camada Service.

Os Controllers possuem apenas a responsabilidade de receber e retornar requisições.

Exemplo:

```text
Controller
    ↓
Service
    ↓
Repository
    ↓
Banco de Dados
```

---

## Repository Pattern

O acesso aos dados é realizado através do Spring Data JPA.

Exemplos:

* ClienteRepository
* ContaRepository
* TransacaoRepository

---

## Tratamento Global de Exceções

O projeto utiliza um GlobalExceptionHandler para padronizar as respostas de erro da API.

### Exceções Customizadas

* ClienteNaoEncontradoException
* ClienteCadastradoException
* ContaNaoEncontradaException
* ContaBloqueadaException
* ContaJaAtivaException
* SaldoInsuficienteException
* EncerrarContaException
* CamposIncorretosAtualizacaoException

---

## Uso de Transações

Operações críticas utilizam a anotação:

```java
@Transactional
```

Garantindo consistência dos dados em casos de falha.

Exemplos:

* Saque
* Depósito
* Transferência
* Encerramento de conta
* Alteração de limite

---

## Reutilização de Código

O projeto possui métodos auxiliares para evitar duplicação de lógica.

### Exemplos

```java
buscarContaEntity()
```

```java
salvarEConverter()
```

```java
validarValor()
```

```java
validarSaldoDisponivel()
```

```java
validarContaAtiva()
```

---

# Regras de Negócio

## Conta

Ao criar uma conta:

* Saldo inicial = R$ 0,00
* Limite inicial = R$ 0,00
* Status inicial = ATIVA

---

## Saque

* Conta deve estar ativa
* Valor deve ser maior que zero
* É permitido utilizar o limite da conta
* Deve existir saldo disponível

---

## Depósito

* Conta deve estar ativa
* Valor deve ser maior que zero

---

## Transferência

* Conta origem deve estar ativa
* Conta destino deve estar ativa
* Valor deve ser maior que zero
* Conta origem deve possuir saldo disponível
* Não é permitido transferir para a própria conta

---

## Alteração de Limite

* Conta deve estar ativa
* Limite deve ser maior que zero

---

## Bloqueio de Conta

* Não é possível bloquear uma conta já bloqueada
* Não é possível bloquear uma conta encerrada

---

## Ativação de Conta

* Não é possível ativar uma conta já ativa
* Não é possível ativar uma conta encerrada

---

## Encerramento de Conta

* Não é possível encerrar conta com saldo
* Não é possível encerrar conta já encerrada

---

# Estrutura do Projeto

```text
src/main/java
│
├── Controller
│
├── Service
│
├── Repository
│
├── Entity
│
├── DTO
│
├── Exception
│
├── Config
│
└── BankSystemApplication
```

---

# Exemplos de Requisição

## Criar Cliente

POST /clientes

```json
{
  "nome": "Matheus Vinicius",
  "cpf": "12345678900",
  "email": "matheus@email.com",
  "celular": "47999999999"
}
```

---

## Criar Conta

POST /contas

```json
{
  "clienteId": 1,
  "tipoConta": "CORRENTE"
}
```

---

## Depositar

POST /contas/{id}/deposito

```json
{
  "valor": 500.00
}
```

---

## Sacar

POST /contas/{id}/saque

```json
{
  "valor": 100.00
}
```

---

## Transferir

POST /contas/transferencia

```json
{
  "contaOrigemId": 1,
  "contaDestinoId": 2,
  "valor": 50.00
}
```

---

## Consultar Saldo

GET /contas/{id}/saldo

### Resposta

```json
{
  "saldo": 450.00
}
```

---

## Consultar Extrato

GET /contas/{id}/extrato

### Resposta

```json
[
  {
    "id": 1,
    "tipo": "DEPOSITO",
    "valor": 500.00,
    "descricao": "Depósito realizado",
    "data": "2025-08-01T10:30:00"
  }
]
```

---

# Como Executar

## Clonar o Projeto

```bash
git clone https://github.com/seu-usuario/bank-system.git
```

---

## Configurar Banco de Dados

application.properties

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/bank_system
spring.datasource.username=postgres
spring.datasource.password=senha

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

---

## Executar a Aplicação

Via Maven:

```bash
mvn spring-boot:run
```

Ou executando a classe:

```java
BankSystemApplication
```

---

# Melhorias Futuras

* Autenticação JWT
* Controle de usuários e permissões
* Swagger/OpenAPI
* Docker
* Testes Unitários (JUnit + Mockito)
* Testes de Integração
* Logs estruturados
* Paginação
* Auditoria de operações
* CI/CD com GitHub Actions

---

# Autor

Matheus Vinicius

Projeto desenvolvido para estudos de Java, Spring Boot, APIs REST, arquitetura em camadas e boas práticas de desenvolvimento backend.

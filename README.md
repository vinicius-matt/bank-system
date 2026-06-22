# 💳 Nimbus Bank System

> API bancária em **Spring Boot** com um frontend de **banco digital** em **React**. Projeto de estudo com foco em boas práticas de back-end: segurança com JWT, autorização por papel e por dono, integridade financeira e separação de responsabilidades.

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-DB-4169E1?logo=postgresql&logoColor=white)

> 🔗 **Demo:** https://nimbus-bank.onrender.com

## APIs

> 🔗 Swagger: https://bank-system-kiad.onrender.com/swagger-ui/index.html
---

## 📸 Telas

**Login**

![Login](https://github.com/user-attachments/assets/507409f4-2ee8-4754-a5da-4ffb1734012f)

**Dashboard**

![Dashboard](https://github.com/user-attachments/assets/ebc08189-49e3-4bdd-ad1c-559ffb2c5592)

---

## Funcionalidades

**Segurança**
- Login e cadastro com **JWT** (access token curto + **refresh token** com rotação).
- Senhas em **BCrypt** e logout que revoga o refresh token.
- Autorização por **papel** (`ADMIN` / `USER`) e por **dono** (cada usuário só acessa as próprias contas).
- Rate limiting no login.

**Operações bancárias**
- Contas corrente e poupança, com limite de cheque especial.
- Depósito, saque e transferência (com checagem de saldo + limite).
- **Pix por chave** (email, CPF, celular ou aleatória).
- Bloquear / ativar / encerrar conta e alterar limite — **somente ADMIN**.
- **Extrato** com exportação em **PDF e CSV**.
- **Notificações** a cada operação (remetente e destinatário).

**Qualidade**
- **Lock otimista** (`@Version`) e operações em **transações** (`@Transactional`).
- Validação de entrada (Bean Validation) e tratamento global de exceções.
- Paginação e documentação da API com **Swagger/OpenAPI**.

---

## Stacks

**Back-end:** Java 21 · Spring Boot 4 · Spring Security · Spring Data JPA · PostgreSQL · JWT (jjwt) · OpenPDF · JUnit 5 + Mockito

**Front-end:** React 18 · Vite · React Router · Axios · Recharts

---

## Estrutura

```
bank-system/
├─ src/main/java/com/Bank/NimbusBank/
│  ├─ Controller/   # endpoints REST
│  ├─ Service/      # regras de negócio
│  ├─ Repository/   # acesso a dados (JPA)
│  ├─ Entity/       # entidades
│  ├─ dto/          # objetos de transporte
│  ├─ security/     # JWT + Spring Security
│  ├─ Exception/    # exceções + handler global
│  ├─ model/        # enums
│  └─ config/       # CORS, Swagger, seed
├─ frontend/        # React + Vite
└─ pom.xml
```

---

## Como rodar

### Com Docker (recomendado)

Sobe banco + API + front com um comando:

```bash
docker compose up --build
```

Front em `http://localhost:3000` · API em `http://localhost:8080`.

### Manualmente

**Pré-requisitos:** Java 21+, Node 18+, PostgreSQL com um banco `bank_system`.

```bash
# back-end (porta 8080)
./mvnw spring-boot:run

# front-end (porta 3000)
cd frontend && npm install && npm run dev
```

Na primeira execução são criadas duas contas de demonstração:

| Perfil | Email | Senha |
|--------|-------|-------|
| Cliente | `user@bank.com` | `user123` |
| Administrador | `admin@bank.com` | `admin123` |

Documentação da API: `http://localhost:8080/swagger-ui.html`

---

## Como o JWT funciona

1. O login devolve um **access token** (curto) e um **refresh token** (longo).
2. O front envia `Authorization: Bearer <token>` em cada requisição.
3. Em um `401`, o Axios chama `/auth/refresh`, renova o token e refaz a requisição — de forma transparente.
4. O backend é **stateless** (`SessionCreationPolicy.STATELESS`).

---

## Deploy

Porta, CORS, segredo do JWT e banco são controlados por **variáveis de ambiente**:

| Variável | Onde | Descrição |
|----------|------|-----------|
| `DB_URL` / `DB_USERNAME` / `DB_PASSWORD` | back-end | Conexão com o PostgreSQL |
| `JWT_SECRET` | back-end | Chave Base64 (≥ 256 bits) |
| `ALLOWED_ORIGINS` | back-end | URL do front liberada no CORS |
| `PORT` | back-end | Porta (injetada pela plataforma) |
| `VITE_API_URL` | front-end | URL pública da API (build-time) |

O repositório inclui `Dockerfile`, `docker-compose.yml` e `render.yaml` prontos para deploy
(ex.: **Render**, plano gratuito).

> ⚠️ No plano grátis do Render a API "dorme" após inatividade — a **primeira** requisição pode levar ~30–50s.

---

## Roadmap

- [x] Docker Compose (Postgres + back-end + front-end)
- [ ] Migrations com Flyway (hoje `ddl-auto=update`)
- [ ] Testes de integração (Testcontainers)
- [ ] CI com GitHub Actions
- [ ] Escala fixa de `BigDecimal` e auditoria das operações

---

## Autor

**Matheus Vinícius** — projeto de estudo de Java, Spring Boot, segurança e front-end.

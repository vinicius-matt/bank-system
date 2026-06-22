# 💳 Nimbus Bank System

> API bancária completa em **Spring Boot** com um frontend de **banco digital** em **React** (Nimbus Bank). Projeto de estudo focado em boas práticas de back-end: segurança com JWT, autorização por papel e por dono, integridade financeira e separação de responsabilidades.

![Java](https://img.shields.io/badge/Java-21-ED8B00?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4-6DB33F?logo=springboot&logoColor=white)
![Spring Security](https://img.shields.io/badge/Spring%20Security-JWT-6DB33F?logo=springsecurity&logoColor=white)
![React](https://img.shields.io/badge/React-18-61DAFB?logo=react&logoColor=black)
![Vite](https://img.shields.io/badge/Vite-5-646CFF?logo=vite&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-DB-4169E1?logo=postgresql&logoColor=white)

---

## 📸 Telas

> Adicione aqui prints da aplicação (coloque as imagens em `docs/screenshots/`).

| Login | Dashboard |
|-------|-----------|
| <!-- ![Login](docs/screenshots/login.png) --> _print aqui_ | <!-- ![Dashboard](docs/screenshots/dashboard.png) --> _print aqui_ |

| Detalhe da conta / Extrato | Pix |
|----------------------------|-----|
| <!-- ![Conta](docs/screenshots/conta.png) --> _print aqui_ | <!-- ![Pix](docs/screenshots/pix.png) --> _print aqui_ |

---

## ✨ Funcionalidades

**Autenticação e segurança**
- Cadastro e login com **JWT** (access token curto + **refresh token** de longa duração, com rotação).
- Senhas com hash **BCrypt**; rotas protegidas; logout que revoga o refresh token.
- **Autorização em dois níveis:** por papel (`ADMIN` / `USER`) e por **dono** (um usuário só acessa as próprias contas).
- Proteção simples contra força bruta no login (rate limiting).

**Operações bancárias**
- Contas corrente e poupança, com **limite (cheque especial)**.
- Depósito, saque e **transferência** entre contas (com checagem de saldo + limite).
- **Pix por chave** (email, CPF, celular ou aleatória): cadastro de chaves e transferência via chave.
- Bloquear / ativar / encerrar conta — **restrito a administradores**.
- **Extrato** com exportação em **PDF e CSV**.
- **Notificações** geradas em cada operação (quem envia e quem recebe são avisados).

**Qualidade**
- **Lock otimista** (`@Version`) para evitar corrupção de saldo em operações simultâneas.
- Operações financeiras em **transações** (`@Transactional`) — atomicidade.
- Validação de entrada (Bean Validation) e **tratamento global de exceções** com status HTTP corretos.
- Paginação nas listagens e documentação da API com **Swagger/OpenAPI**.

---

## 🧱 Stack

**Back-end:** Java 21 · Spring Boot 4 · Spring Security · Spring Data JPA (Hibernate) · PostgreSQL · JWT (jjwt) · OpenPDF · Lombok · springdoc-openapi · JUnit 5 + Mockito

**Front-end:** React 18 · Vite · React Router · Axios · Recharts · CSS próprio (tema dark de banco digital)

---

## 🗂️ Estrutura do projeto

```
bank-system/
├─ src/main/java/com/Bank/bank_system/
│  ├─ Controller/     # endpoints REST (Conta, Extrato, Cliente, Auth, Pix, Notificacao)
│  ├─ Service/        # regras de negócio
│  ├─ Repository/     # acesso a dados (Spring Data JPA)
│  ├─ Entity/         # entidades JPA
│  ├─ dto/            # objetos de transporte (request/response)
│  ├─ security/       # JWT, filtro, configuração do Spring Security
│  ├─ Exception/      # exceções de negócio + handler global
│  ├─ model/          # enums (TipoConta, StatusConta, Role, ...)
│  └─ config/         # CORS, Swagger, seed inicial
├─ src/test/          # testes unitários (JUnit + Mockito)
├─ frontend/          # aplicação React + Vite (Nimbus Bank)
└─ pom.xml
```

---

## 🚀 Como rodar

### Pré-requisitos
- Java 21+ e Maven
- Node.js 18+
- PostgreSQL com um banco chamado `bank_system`

### 1. Back-end (porta 8080)

Configure o banco em `src/main/resources/application.properties` (ou via variáveis de ambiente `DB_URL`, `DB_USERNAME`, `DB_PASSWORD`). Depois:

```bash
./mvnw spring-boot:run
```

Na primeira execução são criadas duas contas de demonstração:

| Perfil | Email | Senha |
|--------|-------|-------|
| Cliente | `user@bank.com` | `user123` |
| Administrador | `admin@bank.com` | `admin123` |

Documentação da API: `http://localhost:8080/swagger-ui.html`

### 2. Front-end (porta 3000)

```bash
cd frontend
cp .env.example .env       # ajuste VITE_API_URL se necessário
npm install
npm run dev
```

Acesse `http://localhost:3000`.

---

## 🔐 Como o JWT funciona aqui

1. O login devolve um **access token** (curto) e um **refresh token** (longo).
2. O front guarda os tokens e envia `Authorization: Bearer <token>` em cada requisição.
3. Ao receber `401`, o Axios chama `/auth/refresh` automaticamente, renova o token (rotacionando o refresh) e refaz a requisição — sem o usuário perceber.
4. O backend é **stateless** (`SessionCreationPolicy.STATELESS`): nenhuma sessão em memória, escala horizontalmente.

---

## 🛣️ Próximos passos (roadmap)

- [ ] Migrations com **Flyway** (hoje usa `ddl-auto=update`)
- [ ] **Testes de integração** ponta a ponta (Testcontainers)
- [ ] **Docker Compose** (Postgres + back-end + front-end)
- [ ] CI com GitHub Actions
- [ ] Escala fixa de `BigDecimal` (`precision/scale`) e auditoria das operações

---

## 👤 Autor

**Matheus Vinícius**
Projeto de estudo para praticar Java, Spring Boot, segurança e front-end.

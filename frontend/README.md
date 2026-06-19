# Nimbus Bank — Frontend

Front-end moderno (estilo banco digital) para o **bank-system** (Spring Boot + PostgreSQL),
com autenticação **JWT** integrada ponta a ponta.

Stack: **React 18 + Vite + React Router + Axios + Recharts**.

## Pré-requisitos

- Node.js 18+
- O backend Spring Boot rodando em `http://localhost:8080`
- PostgreSQL ativo (banco `bank_system`)

## Como rodar

```bash
cd frontend
cp .env.example .env        # ajuste VITE_API_URL se necessário
npm install
npm run dev                 # abre em http://localhost:3000
```

Build de produção:

```bash
npm run build       # gera dist/
npm run preview     # serve o build localmente
```

> A porta do dev server é **3000** (em `vite.config.js`) para casar com o CORS
> liberado no backend. O Spring Security também aceita `5173` (porta padrão do Vite),
> caso prefira.

## Login de demonstração

Na primeira execução o backend cria um usuário admin automaticamente
(`DataSeeder`):

```
Cliente comum (com perfil de titular):
  email: user@bank.com
  senha: user123

Administrador (operador, sem perfil de titular):
  email: admin@bank.com
  senha: admin123
```

Você também pode criar uma conta nova pela tela **Criar conta**.

## Como o JWT funciona aqui

1. `POST /auth/login` ou `POST /auth/register` retorna um **access token** (JWT,
   curto — 15 min) e um **refresh token** (7 dias).
2. Ambos ficam em `localStorage` (`AuthContext`).
3. Um interceptor do Axios (`src/api/client.js`) injeta o header
   `Authorization: Bearer <accessToken>` em **todas** as requisições.
4. Ao receber `401`, o interceptor chama `POST /auth/refresh` automaticamente,
   renova o access token (com rotação do refresh) e refaz a requisição original.
   Só derruba a sessão se o refresh também falhar.
5. `POST /auth/logout` revoga o refresh token no servidor.
6. Rotas internas são protegidas por `<ProtectedRoute>`; no boot o app valida
   o token chamando `GET /auth/me`.

## Modelo de acesso (ownership 1:1)

- Cada cadastro cria um **login + perfil de cliente** vinculados (1:1).
- Um usuário **USER** só vê e movimenta as **próprias contas**.
- Operações administrativas (gestão de clientes em `/clientes`) exigem papel
  **ADMIN** — a aba "Clientes" só aparece para administradores.
- O usuário `admin@bank.com` é ADMIN e enxerga todas as contas/clientes.

## Novidades desta versão

- **Cadastro** agora coleta CPF e celular (necessários para o perfil de cliente).
- **Criar conta** não pede mais o ID do cliente — a conta é aberta no nome do
  usuário logado.
- **Transferência** aceita o ID de qualquer conta de destino (própria ou de
  terceiros), com atalhos para suas próprias contas.
- **Extrato exportável** em PDF e CSV (botões na tela de detalhe da conta).
- Listagens paginadas (`Page<>` do Spring são desembrulhadas no `services.js`).

## Estrutura

```
src/
  api/          client (axios + interceptors) e services (endpoints)
  auth/         ProtectedRoute (guard de rotas)
  context/      AuthContext (JWT) e ToastContext (notificações)
  components/   AppLayout, Modal, Loader, Icons, Common, AuthBrand
  pages/        Login, Register, Dashboard, Contas, ContaDetalhe, Transferir, Clientes
  styles/       global / layout / auth (tema dark "banco digital")
  utils/        formatação (moeda BRL, datas, etc.)
```

## Funcionalidades

- Login / cadastro com JWT e guarda de sessão
- Dashboard com patrimônio consolidado, gráfico de evolução e atividade recente
- Listagem de contas com filtros (ativas / bloqueadas / encerradas) e criação
- Detalhe da conta: extrato, depósito, saque, transferência, ajuste de limite
  (cheque especial), bloquear / ativar e encerrar
- Transferência por ID de conta (própria ou de terceiros) com mensagem
- **Pix**: cadastro de chaves (email, CPF, celular, aleatória) e transferência por chave
- **Notificações** em tempo (quase) real: sino no topo com contador de não lidas,
  geradas em depósito, saque, transferência e Pix (origem e destino)
- Exportação de extrato em PDF e CSV
- Gestão de clientes restrita a ADMIN
- Refresh token automático e logout com revogação
- Tema dark responsivo, toasts, modais e estados de carregamento

## Endpoints novos (Pix e Notificações)

```
POST   /pix/chaves                 cadastra uma chave (EMAIL|CPF|CELULAR|ALEATORIA)
GET    /pix/chaves/minhas          lista as chaves do usuário logado
GET    /pix/chaves/conta/{id}      lista as chaves de uma conta
DELETE /pix/chaves/{id}            remove uma chave
POST   /pix/transferir             transfere usando { origemId, chaveDestino, valor, mensagem }

GET    /notificacoes               lista as notificações do usuário
GET    /notificacoes/nao-lidas     { total } de não lidas
PATCH  /notificacoes/{id}/lida     marca uma como lida
PATCH  /notificacoes/lidas         marca todas como lidas
```

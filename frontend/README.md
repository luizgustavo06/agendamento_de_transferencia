# Frontend - Sistema de Agendamento de Transferências

Aplicação em **Angular** para agendamento de transferências financeiras e visualização de extrato com atualização em tempo real e cálculo estimado de taxas.

---

## 1. Visão Geral

Interface web moderna que se comunica com a API Spring Boot em `http://localhost:8080/api/transferencias`, oferecendo:
* **Formulário de Agendamento:** com validações estritas de contas (10 dígitos), valor positivo, bloqueio de datas anteriores a hoje e cálculo prévio da taxa em tempo real.
* **Extrato de Transferências:** tabela reativa exibindo histórico completo de agendamentos e métricas consolidadas.
* **Sistema de Feedback e Alertas:** exibição de mensagens amigáveis para sucessos (200/201) e tratamentos visuais específicos para erros da API (400, 404, 422, 500 ou queda de conexão).

---

## 2. Tecnologias Utilizadas

* **Framework:** Angular (Standalone Components)
* **Linguagem:** TypeScript
* **Formulários:** Reactive Forms (`ReactiveFormsModule`)
* **Comunicação HTTP:** `HttpClient` com RxJS
* **Estilização:** CSS moderno com design tokens e responsividade

---

## 3. Como Executar

### Pré-requisitos
* Node.js (v18+) e npm instalados.
* A API Backend (Spring Boot) em execução na porta `8080`.

### Passos de Instalação e Execução
1. Instale as dependências:
   ```bash
   npm install
   ```

2. Inicie o servidor de desenvolvimento:
   ```bash
   npm start
   ```

3. Acesse a aplicação no navegador:
   **`http://localhost:4200`**

---

## 4. Estrutura do Projeto

* `src/app/componentes/agendamento/`: Formulário de agendamento e estimativa de taxas em tempo real.
* `src/app/componentes/extrato/`: Tabela e métricas de extrato reativo.
* `src/app/componentes/alerta/`: Componente visual de notificações e alertas de erro/sucesso.
* `src/app/servicos/`: `TransferenciaServico` responsável pelas requisições HTTP e eventos de atualização.
* `src/app/modelos/`: Tipagens TypeScript para requisições, respostas e erros.

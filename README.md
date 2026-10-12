# Sistema de Agendamento de Transferências Financeiras

Aplicação completa desenvolvida para avaliação técnica, composta por uma API RESTful em **Spring Boot (Java 11)** e uma interface moderna em **Angular**. O sistema permite agendar transferências financeiras, calcular taxas automaticamente de acordo com a antecedência da data de transferência e consultar o extrato consolidado de agendamentos.

---

## 1. Visão Geral

O sistema permite que o usuário realize o agendamento de transferências financeiras com as seguintes informações:
* **Conta de Origem:** padrão de 10 dígitos numéricos (`XXXXXXXXXX`).
* **Conta de Destino:** padrão de 10 dígitos numéricos (`XXXXXXXXXX`), obrigatoriamente distinta da conta de origem.
* **Valor da Transferência:** valor monetário positivo em Reais (R$).
* **Data da Transferência:** data em que o valor será efetivamente transferido (não pode ser anterior à data atual).
* **Data de Agendamento:** data atual do registro da operação.
* **Taxa de Transferência:** calculada dinamicamente pelo sistema com base na quantidade de dias de antecedência:

| Dias de Antecedência (De / Até) | Valor Fixo (R$) | Taxa Percentual (%) |
| :---: | :---: | :---: |
| **0 dia (mesmo dia)** | R$ 3,00 | 2,5% |
| **1 a 10 dias** | R$ 12,00 | 0,0% |
| **11 a 20 dias** | R$ 0,00 | 8,2% |
| **21 a 30 dias** | R$ 0,00 | 6,9% |
| **31 a 40 dias** | R$ 0,00 | 4,7% |
| **41 a 50 dias** | R$ 0,00 | 1,7% |
| **Acima de 50 dias ou data no passado** | *Não aplicável* | *Lança erro e bloqueia transferência* |

O sistema também disponibiliza uma tabela de extrato com todas as transferências cadastradas, atualizada em tempo real.

---

## 2. Estrutura do Repositório

O projeto é estruturado em um repositório único contendo o backend Spring Boot e a aplicação frontend Angular:

```text
sistema-agendamento-transferencias/
├── src/                          # Código-fonte da API Spring Boot (Java 11)
│   ├── main/java/...             # Controladores, Serviços, Entidades e Regras de Taxa
│   └── test/java/...             # Testes unitários e de integração (JUnit 5 / Mockito)
├── pom.xml                       # Configurações do Maven
├── mvnw / mvnw.cmd               # Maven Wrapper embutido
├── frontend/                     # Aplicação Frontend Angular
│   ├── src/app/...               # Componentes, Modelos e Serviços Angular
│   ├── package.json              # Dependências e scripts do Node.js
│   └── angular.json              # Configurações do framework Angular
├── iniciar.bat                   # Script de inicialização rápida (Windows)
├── iniciar.sh                    # Script de inicialização rápida (Linux/macOS)
├── .gitignore                    # Regras unificadas de exclusão (target/, node_modules/, etc.)
└── README.md                     # Documentação completa do projeto
```

---

## 3. Tecnologias e Ferramentas

### Backend
* **Linguagem:** Java 11
* **Framework:** Spring Boot 2.7.18
* **Persistência:** Spring Data JPA / Hibernate
* **Banco de Dados:** H2 Database (em memória)
* **Validação:** Bean Validation (`spring-boot-starter-validation`)
* **Gerenciador de Dependências:** Maven 3.9 (com Maven Wrapper embutido)
* **Testes:** JUnit 5, Mockito, Spring Boot Test

### Frontend
* **Framework:** Angular (Standalone Components) — *adotado conforme diretriz do teste ("caso não tenha conhecimentos de VueJs, pode ser feito o front com angular")*
* **Linguagem:** TypeScript
* **Ícones:** Google Material Symbols (Biblioteca Oficial do Google)
* **Estilização:** CSS moderno responsivo com design tokens
* **Máscaras e Validações:** Máscara monetária em tempo real (R$) e validações de contas bancárias (10 dígitos)
* **Gerenciador de Pacotes:** Node.js (v24.x) e npm (v11.x)

---

## 4. Decisões Arquiteturais e Padrões de Projeto

### Clean Code e Nomenclatura em Português
* Nomenclatura 100% alinhada à linguagem ubíqua do domínio financeiro (`Transferencia`, `TransferenciaServico`, `TransferenciaControle`, `CalculoTaxaServico`), facilitando a legibilidade e manutenibilidade.
* Código autoexplicativo, enxuto e sem comentários redundantes.

### Padrão Strategy para Cálculo de Taxas
* Em vez de blocos condicionais extensos (`if/else`), foi adotado o padrão **Strategy** através da interface `CalculadoraTaxa` e estratégias isoladas por faixa (`CalculadoraTaxaMesmoDia`, `CalculadoraTaxaAteDezDias`, etc.).
* Atende diretamente ao **Princípio Aberto/Fechado (Open-Closed Principle)** do SOLID: novas faixas ou promoções de taxas podem ser adicionadas criando uma nova estratégia, sem alterar as regras já existentes e validadas.

### Isolamento com DTOs
* Entidades JPA não são expostas na camada web.
* `TransferenciaRequisicaoDTO`: valida campos obrigatórios, padrões de conta (regex `^\d{10}$`) e valores positivos antes de alcançar a regra de negócio.
* `TransferenciaRespostaDTO`: encapsula o retorno estruturado para os clientes da API.

### Tratamento Global de Exceções (`@RestControllerAdvice`)
A classe `ManipuladorExcecoesGlobal` intercepta falhas e retorna payloads estruturados (`MensagemErroDTO`), protegendo a aplicação de expor stacktraces ao cliente:
* **HTTP 400 (Bad Request):** Erros de validação do Bean Validation, com detalhes campo a campo.
* **HTTP 404 (Not Found):** Recursos não localizados.
* **HTTP 422 (Unprocessable Entity):** Violações de regra de negócio (ex.: agendamento com mais de 50 dias ou data no passado).
* **HTTP 500 (Internal Server Error):** Erros inesperados genéricos com mensagem segura.

### Banco de Dados em Memória (H2)
* Permite execução e avaliação imediata sem necessidade de instalação ou configuração de servidores de banco de dados externos.
* Console web ativo para inspeção visual dos dados gravados.

---

## 5. Instruções de Execução

### Pré-requisitos
* **Java 11** instalado e configurado no `PATH`.
* **Node.js** (v18+) e **npm** instalados.

---

### Inicialização Rápida (Recomendado)

Para comodidade do avaliador, o repositório disponibiliza scripts automáticos na raiz que iniciam tanto o backend quanto o frontend simultaneamente:

* **No Windows:**
  Dê um duplo clique em `iniciar.bat` (ou execute via terminal `.\iniciar.bat`). O script:
  * valida se Java e Node.js estão instalados e se as portas 8080 e 4200 estão livres;
  * executa `npm install` automaticamente na primeira execução;
  * abre o Windows Terminal com duas guias (Backend + H2 e Frontend);
  * aguarda os serviços responderem e abre `http://localhost:4200` no navegador.

  Para abrir em janelas separadas do CMD em vez de guias: `.\iniciar.bat janelas`.
* **No Linux / macOS:**
  Execute `./iniciar.sh` (conceda permissão de execução com `chmod +x iniciar.sh` caso necessário).

---

### Executando o Backend Manualmente (Spring Boot)

1. Abra o terminal na pasta raiz do repositório clonado:


2. Execute os testes automatizados para validar a compilação:
   * **Linux/macOS:**
     ```bash
     ./mvnw test
     ```
   * **Windows:**
     ```bash
     .\mvnw.cmd test
     ```

3. Inicie o servidor da aplicação:
   * **Linux/macOS:**
     ```bash
     ./mvnw spring-boot:run
     ```
   * **Windows:**
     ```bash
     .\mvnw.cmd spring-boot:run
     ```

O backend estará ativo em: **`http://localhost:8080`**

#### Acesso ao Console do H2
Para visualizar as tabelas e registros diretamente no navegador:
* **URL:** `http://localhost:8080/h2-console`
* **Driver Class:** `org.h2.Driver`
* **JDBC URL:** `jdbc:h2:mem:transferencias_db`
* **User Name:** `sa`
* **Password:** *(deixe em branco)*

---

### Executando o Frontend (Angular)

1. Abra outro terminal e acesse a pasta do frontend:
   ```bash
   cd frontend
   ```

2. Instale as dependências:
   ```bash
   npm install
   ```

3. Inicie a aplicação Angular:
   ```bash
   npm start
   ```
   *(ou `ng serve`)*

Acesse a interface no navegador em: **`http://localhost:4200`**

---

## 6. Exemplos de Chamadas à API (cURL)

### Cenário 1: Agendamento com Sucesso (HTTP 201 Created)
Agendamento com 2 dias de antecedência (faixa de 1 a 10 dias: taxa fixa de R$ 12,00).

```bash
curl -X POST http://localhost:8080/api/transferencias \
  -H "Content-Type: application/json" \
  -d '{
    "contaOrigem": "1234567890",
    "contaDestino": "9876543210",
    "valor": 1000.00,
    "dataTransferencia": "2026-10-12"
  }'
```

**Resposta (HTTP 201):**
```json
{
  "id": 1,
  "contaOrigem": "1234567890",
  "contaDestino": "9876543210",
  "valor": 1000.00,
  "taxa": 12.00,
  "valorTotal": 1012.00,
  "dataAgendamento": "2026-10-10",
  "dataTransferencia": "2026-10-12"
}
```

---

### Cenário 2: Consulta do Extrato de Agendamentos (HTTP 200 OK)

```bash
curl -X GET http://localhost:8080/api/transferencias
```

**Resposta (HTTP 200):**
```json
[
  {
    "id": 1,
    "contaOrigem": "1234567890",
    "contaDestino": "9876543210",
    "valor": 1000.00,
    "taxa": 12.00,
    "valorTotal": 1012.00,
    "dataAgendamento": "2026-10-10",
    "dataTransferencia": "2026-10-12"
  }
]
```

---

### Cenário 3: Erro de Validação de Campos (HTTP 400 Bad Request)
Conta fora do padrão de 10 dígitos e valor negativo.

```bash
curl -X POST http://localhost:8080/api/transferencias \
  -H "Content-Type: application/json" \
  -d '{
    "contaOrigem": "123",
    "contaDestino": "456",
    "valor": -50.00,
    "dataTransferencia": "2026-10-12"
  }'
```

**Resposta (HTTP 400):**
```json
{
  "dataHora": "2026-10-10T16:00:00",
  "status": 400,
  "erro": "Bad Request",
  "mensagem": "Erro de validação nos campos da requisição.",
  "caminho": "/api/transferencias",
  "validacoes": [
    {
      "campo": "contaOrigem",
      "mensagem": "A conta de origem deve conter exatamente 10 dígitos numéricos."
    },
    {
      "campo": "contaDestino",
      "mensagem": "A conta de destino deve conter exatamente 10 dígitos numéricos."
    },
    {
      "campo": "valor",
      "mensagem": "O valor da transferência deve ser positivo."
    }
  ]
}
```

---

### Cenário 4: Violação de Regra de Negócio (HTTP 422 Unprocessable Entity)
Tentativa de agendamento com mais de 50 dias de antecedência (sem taxa aplicável).

```bash
curl -X POST http://localhost:8080/api/transferencias \
  -H "Content-Type: application/json" \
  -d '{
    "contaOrigem": "1234567890",
    "contaDestino": "9876543210",
    "valor": 500.00,
    "dataTransferencia": "2026-12-30"
  }'
```

**Resposta (HTTP 422):**
```json
{
  "dataHora": "2026-10-10T16:00:00",
  "status": 422,
  "erro": "Unprocessable Entity",
  "mensagem": "Não há taxa aplicável para transferências com mais de 50 dias de antecedência (81 dias informados).",
  "caminho": "/api/transferencias"
}
```

---

### Cenário 5: Recurso Não Encontrado (HTTP 404 Not Found)
Busca por identificador inexistente.

```bash
curl -X GET http://localhost:8080/api/transferencias/999
```

**Resposta (HTTP 404):**
```json
{
  "dataHora": "2026-10-10T16:00:00",
  "status": 404,
  "erro": "Not Found",
  "mensagem": "Transferência com id 999 não encontrada.",
  "caminho": "/api/transferencias/999"
}
```

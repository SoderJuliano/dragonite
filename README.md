# Dragonite: API para FreeHubCV
Dragonite é uma API RESTful construída em Java 21 para alimentar o FreeHubCV, uma plataforma web para criação e edição de currículos.

## Pré-requisitos

- Java 21
- Maven 3

## Instalação

1. Clone este repositório:
   ```bash
   git clone <url-do-repositorio>
   cd dragonite
   ```
2. Compile o projeto e gere o fatjar (jar executável):
   ```bash
   mvn clean package
   ```
   O arquivo será gerado em `target/dragonite-<versao>.jar`.

## Executando a API

- Para rodar diretamente pelo Maven (modo desenvolvimento):
  ```bash
  mvn spring-boot:run
  ```
- Para rodar o fatjar gerado (modo produção ou desenvolvimento):
  ```bash
  java -jar target/dragonite-<versao>.jar
  ```
  Substitua `<versao>` pela versão gerada no build.

## Configuração

- A API inicia por padrão na porta 5200.
- Para alterar a porta, edite o arquivo `src/main/resources/application.yaml`.
- Em produção, utilize variáveis de ambiente para sobrescrever configurações.

## Modelos de IA utilizados

O projeto utiliza diferentes tipos de modelos de IA conforme o endpoint:

### Modelos locais via Ollama
- **llama3** - Modelo principal para geração de texto
- **tinyllama** - Modelo mais leve e rápido
- **gemma3-4b** - Modelo alternativo Google Gemma

### Modelos via API externa (AIML API)
- **mistralai/Mistral-7B-Instruct-v0.2** - Para geração/melhoria de currículos e texto

### Como baixar e rodar os modelos locais

1. Instale o Ollama:
   - Linux:
     ```bash
     curl -fsSL https://ollama.com/install.sh | sh
     ```
   - Windows/Mac: Siga as instruções em https://ollama.com/download

2. Baixe os modelos necessários:
   ```bash
   # Modelo principal (obrigatório)
   ollama run llama3
   
   # Modelo leve (opcional)
   ollama run tinyllama
   
   # Modelo Gemma (opcional)  
   ollama run gemma2:9b
   ```

**Nota:** O modelo `gemma3-4b` usado no código pode requerer o comando `ollama run gemma2:9b` dependendo da versão do Ollama.

### Configuração da API externa

Para os endpoints `/improve-text` e `/generate-cv`, configure chaves da AIML API:
- Obtenha chaves em https://aimlapi.com/
- Configure as variáveis de ambiente `aimlapi.com_KEY0` até `aimlapi.com_KEY13`

## Como o projeto usa os modelos

### Modelos locais (Ollama)
- Requisições HTTP para `http://localhost:11434/api/generate`
- **llama3**: endpoints `/llama3` e `/llama3-stream`  
- **tinyllama**: endpoint `/llamatiny`
- **gemma3-4b**: endpoint `/gemma3`

### Modelos externos (AIML API)
- **mistralai/Mistral-7B-Instruct-v0.2**: endpoints `/improve-text` e `/generate-cv`
- Requisições HTTPS para `https://api.aimlapi.com/v1/chat/completions`

## Comandos úteis

- Compilar e gerar fatjar:
  ```bash
  mvn clean package
  ```
- Rodar API via Maven:
  ```bash
  mvn spring-boot:run
  ```
- Rodar API via fatjar:
  ```bash
  java -jar target/dragonite-<versao>.jar
  ```
- Rodar modelos de IA localmente:
  ```bash
  # Modelo principal
  ollama run llama3
  
  # Modelo leve  
  ollama run tinyllama
  
  # Modelo Gemma
  ollama run gemma2:9b
  ```

## Uso dos endpoints de IA

### Modelos locais (Ollama)

#### /llama3
Gera texto usando o modelo Llama3 local:
```http
POST /llama3
Content-Type: application/json

{
  "newPrompt": "Por que o céu é azul?",
  "language": "PORTUGUESE",
  "isAgent": false
}
```

#### /llama3-stream
Retorna resposta do Llama3 em streaming (SSE/EventSource).

#### /llamatiny
Gera texto usando o modelo TinyLlama (mais leve, menos preciso).

#### /gemma3
Gera texto usando o modelo Gemma3-4B.

#### /gemini
Usa CLI do Google Gemini (requer configuração adicional).

### Modelos via API externa (AIML)

#### /improve-text
Melhora textos usando Mistral-7B via AIML API:
```http
POST /improve-text
Content-Type: application/json

{
  "newPrompt": "texto para melhorar",
  "language": "PORTUGUESE", 
  "isAgent": false
}
```

#### /generate-cv
Gera currículos completos usando Mistral-7B via AIML API:
```http
POST /generate-cv
Content-Type: application/json

{
  "newPrompt": "João Silva, desenvolvedor Java com 5 anos de experiência",
  "language": "PORTUGUESE",
  "isAgent": true
}
```

## Uso alternativo: AIML API

Se preferir, utilize a AIML API (https://aimlapi.com/) com uma chave de API. Configure a chave no arquivo de segredos do projeto e ajuste o backend para usar a API externa.

## Documentação da API

Acesse a documentação Swagger em: [http://localhost:5200/swagger-ui/index.html](http://localhost:5200/swagger-ui/index.html)

## Funcionalidades

- Autenticação JWT
- Login e registro de usuários
- CRUD de usuários
- Geração e edição de currículos personalizados

## Tecnologias

- Java 21
- Spring Boot
- Spring Security
- JWT
- Swagger
- Maven

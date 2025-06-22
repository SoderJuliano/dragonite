# Dragonite: API para FreeHubCV
Dragonite é uma API RESTful construída em Java 21 para alimentar o FreeHubCV, uma plataforma web para criação e edição de currículos.

## Pré-requisitos:

Java 21
Maven 3

## Instalação:

Clone este repositório.
Execute mvn clean install para baixar as dependências e construir o projeto.
Configuração:

## Ambiente de Desenvolvimento:
A API será iniciada na porta 5200 por padrão.
Configure a porta desejada no arquivo application.properties.
Ambiente de Produção:
A API será configurada através de um webhook, sem um host fixo.
As configurações de produção devem ser definidas através de variáveis de ambiente.
Documentação da API:

A documentação da API está disponível através da interface Swagger, acessível em http://localhost:5200/swagger-ui/index.html (em ambiente de desenvolvimento).
Execução:

Execute mvn spring-boot:run para iniciar a API.

Instruções para Configuração e Uso do Endpoint /llama3
Este guia explica como configurar e utilizar o endpoint /llama3 no projeto. O endpoint /llama3 depende da instalação e execução do modelo Llama3 localmente, além da configuração correta do CORS no backend.

### 1. Instalação do Llama3
   Para utilizar o endpoint /llama3, é necessário instalar e rodar o modelo Llama3 localmente. Siga os passos abaixo:
   Passo 1: Instale o Ollama
   Ollama é uma ferramenta que facilita a execução de modelos de linguagem como o Llama3. Siga as instruções de instalação para o seu sistema operacional:
   Linux: Instruções de Instalação para Linux
   Windows/Mac: Consulte a página de download do Ollama.
   Passo 2: Baixe e Execute o Modelo Llama3
   Após instalar o Ollama, execute o seguinte comando no terminal para baixar e rodar o modelo Llama3:

`ollama run llama3`

### 2. Uso do Endpoint /llama3
O endpoint /llama3 foi projetado para gerar texto utilizando o modelo Llama3. Ele recebe um objeto IAPropmptRequest no corpo da requisição e retorna o texto gerado.
Exemplo de Requisição:
``
POST /llama3
Content-Type: application/json

{
"newPrompt": "Por que o céu é azul?",
"language": "PORTUGUESE",
"isAgent": false
}
``
### 3. Uso Alternativo: AIML API
Caso você não queira rodar o Llama3 localmente, é possível utilizar a AIML API com uma chave de API. A AIML API oferece suporte a vários modelos de linguagem, incluindo o Llama3.
Links Úteis:
Documentação da AIML API
Obtenha uma Chave de API
Como Usar:
Obtenha uma chave de API no site da AIML API.
Configure a chave no arquivo de segredos do projeto.
Modifique o endpoint /llama3 para utilizar a AIML API em vez do Llama3 local.

## Funcionalidades:

Autenticação:
Implementa autenticação JWT para proteger os endpoints da API.
Suporta login e registro de usuários.
Gerenciamento de Usuários:
Permite a criação, leitura, atualização e exclusão de usuários.
Geração e Edição de Currículos:
Permite a geração e edição de currículos personalizados através do FreeHubCV.

## Tecnologias:

Java 21
Spring Boot
Spring Security
JWT
Swagger
Maven

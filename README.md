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

# Sistema de Gestão de Biblioteca Distribuído

## Descrição do Projeto


Este projeto é um sistema distribuído para a gestão de bibliotecas, desenvolvido com uma arquitetura de microserviços. O sistema é composto por dez microserviços organizados em módulos **Command** e **Query**, garantindo uma separação clara de responsabilidades e elevada escalabilidade.
Este projeto implementa um sistema de gestão de bibliotecas baseado numa arquitetura de microserviços distribuídos. O sistema facilita a gestão de autenticação, readers, books (incluindo autores e géneros) e lendings, utilizando o Message Broker RabbitMQ para comunicação entre serviços. A arquitetura foi desenhada para ser escalável, modular e de fácil manutenção.

A comunicação entre os microserviços é realizada de forma assíncrona através do **RabbitMQ**, assegurando fiabilidade e eficiência no processamento de mensagens. As interações do cliente com o sistema são realizadas por chamadas HTTP, permitindo a consulta e gestão dos recursos de forma simples e estruturada.

O sistema permite gerir autenticação, readers, books e lendings, proporcionando uma solução completa e robusta para a gestão de bibliotecas.

---

## Arquitetura do Sistema

O sistema é composto por dez microserviços distribuídos, cada um com duas instâncias. Cada serviço é dividido em dois módulos principais:

- **Command**: Responsável por operações de escrita e processamento de mensagens.
- **Query**: Responsável por operações de leitura e consulta de dados.

### Microserviços e Instâncias

#### 1. Auth Service Command
- **Função**: Gere operações de escrita relacionadas à autenticação, como criação de utilizadores e atribuição de roles.
- **Instâncias**:
    - AuthServiceCommand1: Porta **8080**
    - AuthServiceCommand2: Porta **8081**

#### 2. Auth Service Query
- **Função**: Realiza operações de leitura relacionadas à autenticação, como consulta de utilizadores e permissões.
- **Instâncias**:
    - AuthServiceQuery1: Porta **8082**
    - AuthServiceQuery2: Porta **8083**

#### 3. Book Service Command
- **Função**: Gere operações de escrita para livros, como adição, remoção e atualização de dados de livros, autores e géneros.
- **Instâncias**:
    - BookServiceCommand1: Porta **8084**
    - BookServiceCommand2: Porta **8085**

#### 4. Book Service Query
- **Função**: Realiza operações de leitura, como pesquisa de livros por título, autor ou género.
- **Instâncias**:
    - BookServiceQuery1: Porta **8086**
    - BookServiceQuery2: Porta **8087**

#### 5. Lending Service Command
- **Função**: Gerencia operações de escrita no processo de empréstimos, como registo de empréstimos e devoluções.
- **Instâncias**:
    - LendingServiceCommand1: Porta **8088**
    - LendingServiceCommand2: Porta **8089**

#### 6. Lending Service Query
- **Função**: Focado em operações de leitura de dados relacionados aos empréstimos, como estado dos empréstimos e histórico.
- **Instâncias**:
    - LendingServiceQuery1: Porta **8090**
    - LendingServiceQuery2: Porta **8091**

#### 7. Reader Service Command
- **Função**: Responsável pela escrita de dados relacionados aos leitores, como registo de novos leitores ou atualizações de perfis.
- **Instâncias**:
    - ReaderServiceCommand1: Porta **8092**
    - ReaderServiceCommand2: Porta **8093**

#### 8. Reader Service Query
- **Função**: Dedicado à consulta de dados dos leitores, incluindo perfis e histórico de leituras.
- **Instâncias**:
    - ReaderServiceQuery1: Porta **8094**
    - ReaderServiceQuery2: Porta **8095**

#### 9. Acquisition Service Command
- **Função**: Responsável pelas sugestoes de aquisição de livros.
- **Instâncias**:
    - AcquisitionServiceCommand1: Porta **8096**
    - AcquisitionServiceCommand2: Porta **8097**

#### 10. Acquisition Service Query
- **Função**: Dedicado à consulta de dados das aquisições.
- **Instâncias**:
    - AcquisitionServiceQuery1: Porta **8098**
    - AcquisitionServiceQuery2: Porta **8099**

---

## Tecnologias Utilizadas

- **Java 21**
- **Spring Boot** para desenvolvimento dos microserviços.
- **Spring Security** com OAuth2 e JWT para autenticação e autorização.
- **RabbitMQ** para comunicação assíncrona entre microserviços.
- **Hibernate** & **JPA** para persistência de dados.
- **H2 Database** para armazenamento local.
- **Swagger UI** para documentação da API.

---

## Documentação da API

Cada microserviço disponibiliza a sua documentação através do **Swagger UI**. Abaixo, encontram-se os links de cada instância:

- **Auth Service**:
    - AuthServiceCommand1: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
    - AuthServiceCommand2: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
    - AuthServiceQuery1: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
    - AuthServiceQuery2: [http://localhost:8083/swagger-ui.html](http://localhost:8083/swagger-ui.html)

- **Book Service**:
    - BookServiceCommand1: [http://localhost:8084/swagger-ui.html](http://localhost:8084/swagger-ui.html)
    - BookServiceCommand2: [http://localhost:8085/swagger-ui.html](http://localhost:8085/swagger-ui.html)
    - BookServiceQuery1: [http://localhost:8086/swagger-ui.html](http://localhost:8086/swagger-ui.html)
    - BookServiceQuery2: [http://localhost:8087/swagger-ui.html](http://localhost:8087/swagger-ui.html)

- **Lending Service**:
    - LendingServiceCommand1: [http://localhost:8088/swagger-ui.html](http://localhost:8088/swagger-ui.html)
    - LendingServiceCommand2: [http://localhost:8089/swagger-ui.html](http://localhost:8089/swagger-ui.html)
    - LendingServiceQuery1: [http://localhost:8090/swagger-ui.html](http://localhost:8090/swagger-ui.html)
    - LendingServiceQuery2: [http://localhost:8091/swagger-ui.html](http://localhost:8091/swagger-ui.html)

- **Reader Service**:
    - ReaderServiceCommand1: [http://localhost:8092/swagger-ui.html](http://localhost:8092/swagger-ui.html)
    - ReaderServiceCommand2: [http://localhost:8093/swagger-ui.html](http://localhost:8093/swagger-ui.html)
    - ReaderServiceQuery1: [http://localhost:8094/swagger-ui.html](http://localhost:8094/swagger-ui.html)
    - ReaderServiceQuery2: [http://localhost:8095/swagger-ui.html](http://localhost:8095/swagger-ui.html)

- **Acquisition Service**:
    - AcquisitionServiceCommand1: [http://localhost:8096/swagger-ui.html](http://localhost:8092/swagger-ui.html)
    - AcquisitionServiceCommand2: [http://localhost:8097/swagger-ui.html](http://localhost:8093/swagger-ui.html)
    - AcquisitionServiceQuery1: [http://localhost:8098/swagger-ui.html](http://localhost:8094/swagger-ui.html)
    - AcquisitionServiceQuery2: [http://localhost:8099/swagger-ui.html](http://localhost:8095/swagger-ui.html)

-Gestão de Livros: Utilizadores autenticados podem adicionar ou remover livros através do Book Service.

-Gestão de Readers: O Reader Service gere as informações dos readers e os seus perfis.

-Gestão de Lendings: Os readers podem pedir emprestado livros através do Lending Service.

                

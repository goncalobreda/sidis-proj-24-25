#  Sistema de Gestão de Biblioteca Distribuído

	# Descrição do Projeto

Este projeto implementa um sistema de gestão de bibliotecas baseado numa arquitetura de microserviços distribuídos. O sistema facilita a gestão de autenticação, readers, books (incluindo autores e géneros) e empréstimos, utilizando REST APIs para comunicação entre serviços. A arquitetura foi desenhada para ser escalável, modular e de fácil manutenção. 

	# Tecnologias Utilizadas

- Java 21
  
- Spring Boot para desenvolvimento dos microserviços.
 
- Spring Security com OAuth2 e JWT para autenticação e autorização.
 
- Hibernate & JPA para persistência de dados.
 
  - H2 Database.
  
    - Swagger UI para documentação da API.

            # Arquitetura do Sistema

      O sistema é composto por quatro microserviços principais, cada um responsável por uma parte específica da aplicação. Para cada micro serviço foram criadas 2 instancias e cada uma das instancias têm a sua respetiva base de dados. Isto foi feito a pensar na escalabilidade do projeto tanto como na fiabilidade.

                  # 1. Auth Service

         Responsável pela autenticação e autorização de utilizadores.
  
         Utiliza JWT (JSON Web Tokens) para gestão de sessões.
  
         Implementa gestão de users e roles (como LIBRARIAN e READER).

                # 2. Book Service

         Gere a gestão de livros, autores e géneros.
  
      Funcionalidades:
            Adicionar e remover livros.
            Consultar autores e géneros.
            Pesquisa de livros por título, autor e género.

                  # 3. Reader Service

        Responsável pela gestão de readers.

    Funcionalidades:
                    Registo de novos readers.
                    Gestão de perfis de readers.
                    Consulta do histórico de leitura.

                      # 4. Lending Service

        Gere o processo de empréstimos de livros.

      Funcionalidades:
                Registo de empréstimos entre livros e leitores.
                Gestão de datas de devolução.
                Consulta do histórico de empréstimos.


                # Funcionalidades Principais

-Autenticação e autorização: O utilizador faz login através do Auth Service, recebendo um JWT para autenticação nos outros serviços.

-Gestão de livros: Adicionar, remover e pesquisar livros por título, autor ou género através do Book Service.

-Gestão de readers: Registo e consulta de perfis e histórico de leitura através do Reader Service.

-Gestão de lendings: Criação de lendings, consulta e gestão de devoluções através do Lending Service.

                # Fluxo de Funcionamento

-Autenticação: O user faz login através do Auth Service, recebendo um token JWT.

-Gestão de Livros: Utilizadores autenticados podem adicionar ou remover livros através do Book Service.

-Gestão de Leitores: O Reader Service gere as informações dos readers e os seus perfis.

-Gestão de Lendings: Os readers podem pedir emprestado livros através do Lending Service.

                #Documentação da API

Cada microserviço tem a sua documentação acessível através do Swagger UI:

Auth Service1: http://localhost:8080/swagger-ui.html

Auth Service2: http://localhost:8081/swagger-ui.html

Book Service1: http://localhost:8082/swagger-ui.html

Book Service2: http://localhost:8083/swagger-ui.html

Lending Service1: http://localhost:8084/swagger-ui.html

Lending Service2: http://localhost:8085/swagger-ui.html

Reader Service1: http://localhost:8086/swagger-ui.html

Reader Service2: http://localhost:8087/swagger-ui.html


                #Documentção, diagramas


### Vista Lógica de Nível 1

![VL-N1](\docs\VL-N1.png)

### Vista Lógica de Nível 2

![VL-N2](\docs\VL-N2.png)

### Vista Física de Nível 1

![VF-N1](\docs\VF-N1.png)

### Vista Física de Nível 2

![VF-N2](\docs\VF-N2.png)

### Vista Processos de Nível 2 - create a lending

![VP-N2 create lending](\docs\VP-N2 create lending.png)

### Vista Processos de Nível 2 - create a book

![VP-N2 create book](\docs\VP-N2 create book.png)

### Vista Processos de Nível 2 - register/create a reader

![VP-N2 create reader](\docs\VP-N2 register reader.png)










  

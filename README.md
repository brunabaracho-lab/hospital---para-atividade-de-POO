# Sistema de cadastro de pacientes — JavaFX e ORMLite

## Apresentação

Aplicação de computador para cadastrar pacientes e consultar os registros armazenados em um banco de dados SQLite. A interface é desenvolvida em JavaFX e pode ser editada visualmente no Scene Builder. O projeto utiliza ORMLite para a persistência dos dados e separa as responsabilidades seguindo a organização MVC (Model, View e Controller).

## Entidade escolhida: Paciente

A entidade `Paciente` possui os seguintes atributos:

- `id`: identificador único gerado automaticamente pelo banco de dados;
- `nome`: nome completo do paciente;
- `cpf`: CPF do paciente;
- `birthday`: data de nascimento, apresentada na interface no formato brasileiro `dd/MM/aaaa`;
- `idade`: idade informada manualmente no formulário;
- `sexo`: opção selecionada no formulário;
- `endereco`: endereço do paciente.

## Funcionalidades implementadas

- **Cadastro (Create):** grava um novo paciente no banco de dados SQLite;
- **Consulta (Retrieve):** apresenta os pacientes cadastrados em uma tabela (`TableView`);
- Atualização da tabela após um cadastro ou exclusão;
- Validação dos campos obrigatórios e da idade;
- Limpeza do formulário;
- Recarregamento da lista de pacientes;
- **Exclusão (Delete — bônus):** remove do banco o paciente selecionado, após uma confirmação do usuário. O botão fica desabilitado enquanto nenhum paciente estiver selecionado.

A atividade exige obrigatoriamente Create e Retrieve. A exclusão foi acrescentada como melhoria ; a atualização (Update) não foi implementada.

## Organização do projeto

| Arquivo | Responsabilidade |
|---|---|
| `Paciente.java` | Modelo da entidade e mapeamento ORMLite. |
| `PacienteView.fxml` | Interface gráfica, editável no Scene Builder. |
| `PacienteController.java` | Controla os eventos, valida os campos e atualiza a tabela. |
| `PacienteRepository.java` | Executa as operações de acesso aos dados. |
| `Database.java` | Gerencia a conexão SQLite e a migração da tabela antiga. |
| `Main.java` | Inicializa a aplicação JavaFX. |
| `hospital.db` | Banco de dados SQLite utilizado pela aplicação. |

## Requisitos para executar

- JDK compatível com a versão do JavaFX configurada;
- JavaFX configurado no BlueJ;
- Scene Builder instalado para visualizar e editar o arquivo FXML;
- Bibliotecas ORMLite JDBC e ORMLite Core;
- Driver JDBC do SQLite (`sqlite-jdbc`).

Os arquivos `.jar` das bibliotecas não estão incluídos neste projeto. Será necessário configurar essas dependências.

## Como abrir e executar no BlueJ

1. Extraia a pasta do projeto para um local de sua preferência.
2. Abra o BlueJ e selecione **Project (Projeto) > Open Project (Abrir projeto)**.
3. Escolha a pasta extraída que contém o arquivo `package.bluej`.
4. Confira se as bibliotecas ORMLite e SQLite JDBC estão disponíveis para o projeto e se a configuração JavaFX utilizada no BlueJ está ativa.
5. Abra `Main.java` e execute o método `main`, ou use a opção de execução da classe `Main` no BlueJ.
6. A janela do sistema será aberta com o formulário e a tabela de pacientes.


## Testes recomendados

1. Execute a aplicação e confira se a tabela apresenta os registros existentes.

   ![Tela principal do sistema](screenshots/tela-principal.png) Foto 1.
   
2. Preencha os campos do formulário e cadastre um paciente.

![Tela principal do sistema](screenshots/paciente-cadastro.png) Foto 2.
 
3. Confira se o paciente aparece na tabela com um identificador gerado automaticamente.
4. Feche e abra a aplicação novamente e verifique se o registro continua na tabela.
5. Experimente deixar um campo obrigatório vazio e informar uma idade inválida para conferir as mensagens de validação.

![Tela principal do sistema](screenshots/paciente-cadastro2.png) Foto 3.

5. Exclua os dados fornercidos anteriormente.

![Tela principal do sistema](screenshots/exclusao-paciente.png) Foto 4.

![Tela principal do sistema](screenshots/exclusao-paciente2.png) Foto 5.


## Autoria

Projeto acadêmico desenvolvido para a atividade prática de Java/Programação Orientada a Objetos.

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.util.StringConverter;

/** Controller da tela JavaFX de cadastro e consulta de pacientes. */
public class PacienteController {

    @FXML private TextField nomeField;
    @FXML private TextField cpfField;
    @FXML private DatePicker birthdayPicker;
    @FXML private TextField idadeField;
    @FXML private ComboBox<String> sexoComboBox;
    @FXML private TextField enderecoField;

    @FXML private TableView<Paciente> pacientesTable;
    @FXML private Button excluirButton;
    @FXML private TableColumn<Paciente, Integer> idColumn;
    @FXML private TableColumn<Paciente, String> nomeColumn;
    @FXML private TableColumn<Paciente, String> cpfColumn;
    @FXML private TableColumn<Paciente, String> birthdayColumn;
    @FXML private TableColumn<Paciente, Integer> idadeColumn;
    @FXML private TableColumn<Paciente, String> sexoColumn;
    @FXML private TableColumn<Paciente, String> enderecoColumn;

    private Database database;
    private PacienteRepository repository;

    @FXML
    private void initialize() {
        configurarTabela();
        configurarDataNascimento();
        configurarSelecaoDaTabela();
        sexoComboBox.setItems(FXCollections.observableArrayList(
            "Feminino", "Masculino", "Outro", "Prefiro não informar"));

        try {
            database = new Database("hospital.db");
            repository = new PacienteRepository(database);
            atualizarTabela();
        } catch (Exception error) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro ao abrir o banco de dados",
                "Não foi possível preparar ou consultar o banco de dados.\n"
                + error.getMessage());
        }
    }

    /** Configura a data no padrão brasileiro (dia/mês/ano). */
    private void configurarDataNascimento() {
        final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy", new Locale("pt", "BR"));
        birthdayPicker.setConverter(new StringConverter<LocalDate>() {
            @Override
            public String toString(LocalDate data) {
                return data == null ? "" : formato.format(data);
            }

            @Override
            public LocalDate fromString(String texto) {
                if (texto == null || texto.trim().isEmpty()) {
                    return null;
                }
                try {
                    return LocalDate.parse(texto.trim(), formato);
                } catch (DateTimeParseException erro) {
                    return null;
                }
            }
        });
        birthdayPicker.setPromptText("dd/mm/aaaa");
    }

    /** Habilita a exclusao somente quando houver um paciente selecionado. */
    private void configurarSelecaoDaTabela() {
        excluirButton.setDisable(true);
        pacientesTable.getSelectionModel().selectedItemProperty().addListener(
            (observavel, anterior, selecionado) -> excluirButton.setDisable(selecionado == null)
        );
    }

    private void configurarTabela() {
        idColumn.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("id"));
        nomeColumn.setCellValueFactory(new PropertyValueFactory<Paciente, String>("nome"));
        cpfColumn.setCellValueFactory(new PropertyValueFactory<Paciente, String>("cpf"));
        idadeColumn.setCellValueFactory(new PropertyValueFactory<Paciente, Integer>("idade"));
        sexoColumn.setCellValueFactory(new PropertyValueFactory<Paciente, String>("sexo"));
        enderecoColumn.setCellValueFactory(new PropertyValueFactory<Paciente, String>("endereco"));
        birthdayColumn.setCellValueFactory(cell -> {
            Date data = cell.getValue().getBirthday();
            String texto = data == null ? "" : new SimpleDateFormat("dd/MM/yyyy").format(data);
            return new ReadOnlyStringWrapper(texto);
        });
    }

    @FXML
    private void handleCadastrar() {
        if (repository == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Banco de dados indisponível",
                "A conexão com o banco de dados não foi inicializada.");
            return;
        }

        String nome = nomeField.getText() == null ? "" : nomeField.getText().trim();
        String cpf = cpfField.getText() == null ? "" : cpfField.getText().trim();
        String idadeTexto = idadeField.getText() == null ? "" : idadeField.getText().trim();
        String endereco = enderecoField.getText() == null ? "" : enderecoField.getText().trim();
        Date dataNascimento;

        if (nome.isEmpty() || cpf.isEmpty() || idadeTexto.isEmpty()
                || endereco.isEmpty() || birthdayPicker.getValue() == null
                || sexoComboBox.getValue() == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos obrigatórios",
                "Preencha todos os campos antes de cadastrar.");
            return;
        }

        int idade;
        try {
            idade = Integer.parseInt(idadeTexto);
            if (idade < 0) {
                throw new NumberFormatException("idade negativa");
            }
        } catch (NumberFormatException error) {
            mostrarAlerta(Alert.AlertType.WARNING, "Idade inválida",
                "Informe a idade usando apenas números inteiros (zero ou maior).");
            idadeField.requestFocus();
            return;
        }

        dataNascimento = java.sql.Date.valueOf(birthdayPicker.getValue());
        Paciente paciente = new Paciente(nome, cpf, dataNascimento, idade,
            sexoComboBox.getValue(), endereco);

        try {
            repository.create(paciente);
            atualizarTabela();
            limparFormulario();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Cadastro realizado com sucesso",
                "Paciente cadastrado com sucesso! Identificador gerado: " + paciente.getId());
        } catch (Exception error) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro no cadastro",
                "Não foi possível salvar o paciente.\n" + error.getMessage());
        }
    }

    @FXML
    private void handleLimpar() {
        limparFormulario();
    }

    @FXML
    private void handleAtualizar() {
        atualizarTabela();
    }

    /** Exclui do banco o paciente selecionado, somente apos confirmacao. */
    @FXML
    private void handleExcluir() {
        if (repository == null) {
            mostrarAlerta(Alert.AlertType.ERROR, "Banco de dados indisponível",
                "A conexao com o banco de dados nao foi inicializada.");
            return;
        }

        Paciente selecionado = pacientesTable.getSelectionModel().getSelectedItem();
        if (selecionado == null) {
            mostrarAlerta(Alert.AlertType.WARNING, "Nenhum paciente selecionado",
                "Selecione um paciente na tabela antes de solicitar a exclusão.");
            return;
        }

        Alert confirmacao = new Alert(Alert.AlertType.CONFIRMATION);
        ButtonType botaoExcluir = new ButtonType("Excluir", ButtonBar.ButtonData.OK_DONE);
        ButtonType botaoCancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        confirmacao.setTitle("Confirmar exclusão");
        confirmacao.setHeaderText("Deseja realmente excluir este paciente?");
        confirmacao.setContentText("Paciente: " + selecionado.getNome()
            + "\nID: " + selecionado.getId()
            + "\n\nEsta ação removerá o registro do banco de dados.");
        confirmacao.getButtonTypes().setAll(botaoExcluir, botaoCancelar);

        if (confirmacao.showAndWait().orElse(botaoCancelar) != botaoExcluir) {
            return;
        }

        try {
            repository.delete(selecionado);
            atualizarTabela();
            mostrarAlerta(Alert.AlertType.INFORMATION, "Exclusão realizada",
                "O paciente foi excluído com sucesso.");
        } catch (Exception error) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro na exclusão",
                "Não foi possível excluir o paciente.\n" + error.getMessage());
        }
    }

    private void atualizarTabela() {
        if (repository == null) {
            return;
        }
        try {
            List<Paciente> pacientes = repository.loadAll();
            pacientesTable.setItems(FXCollections.observableArrayList(pacientes));
            pacientesTable.refresh();
        } catch (Exception error) {
            mostrarAlerta(Alert.AlertType.ERROR, "Erro na consulta de pacientes",
                "Não foi possível carregar os pacientes.\n" + error.getMessage());
        }
    }

    private void limparFormulario() {
        nomeField.clear();
        cpfField.clear();
        birthdayPicker.setValue(null);
        idadeField.clear();
        sexoComboBox.getSelectionModel().clearSelection();
        enderecoField.clear();
        nomeField.requestFocus();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensagem) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(mensagem);
        alerta.showAndWait();
    }

    /** Chamado pela janela principal quando a aplicacao for fechada. */
    public void shutdown() {
        if (repository != null) {
            repository.close();
        } else if (database != null) {
            database.close();
        }
    }
}

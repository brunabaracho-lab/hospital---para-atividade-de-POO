import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Set;

import com.j256.ormlite.jdbc.JdbcConnectionSource;

/** Gerencia a conexao SQLite e prepara a estrutura da tabela de pacientes. */
public class Database {
    private final String databaseName;
    private JdbcConnectionSource connection;

    public Database(String databaseName) {
        this.databaseName = databaseName;
    }

    public synchronized JdbcConnectionSource getConnection() throws SQLException {
        if (databaseName == null || databaseName.trim().isEmpty()) {
            throw new SQLException("O nome/caminho do banco de dados nao foi informado.");
        }

        if (connection == null) {
            migrateLegacyPatientTable();
            connection = new JdbcConnectionSource("jdbc:sqlite:" + databaseName);
        }
        return connection;
    }

    /**
     * A versao anterior do projeto usava idade como generatedId e nao possuia
     * coluna id. Esta migracao recria a tabela com id como chave primaria,
     * preservando os dados que eventualmente existirem.
     */
    private void migrateLegacyPatientTable() throws SQLException {
        String url = "jdbc:sqlite:" + databaseName;

        try (Connection jdbc = DriverManager.getConnection(url)) {
            Set<String> columns = getPatientColumns(jdbc);
            if (columns.isEmpty() || columns.contains("id")) {
                return;
            }

            boolean oldAutoCommit = jdbc.getAutoCommit();
            jdbc.setAutoCommit(false);
            try (Statement statement = jdbc.createStatement()) {
                statement.execute("ALTER TABLE \"Paciente\" RENAME TO \"Paciente_legacy_migration\"");
                statement.execute("CREATE TABLE \"Paciente\" ("
                    + "\"id\" INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "\"idade\" INTEGER, "
                    + "\"nome\" VARCHAR, "
                    + "\"CPF\" VARCHAR, "
                    + "\"birthday\" TIMESTAMP, "
                    + "\"endereco\" VARCHAR, "
                    + "\"sexo\" VARCHAR"
                    + ")");

                String idade = selectColumnOrNull(columns, "idade");
                String nome = selectColumnOrNull(columns, "nome");
                String cpf = selectColumnOrNull(columns, "CPF");
                String birthday = selectColumnOrNull(columns, "birthday");
                String endereco = selectColumnOrNull(columns, "endereco");
                String sexo = selectColumnOrNull(columns, "sexo");

                statement.executeUpdate(
                    "INSERT INTO \"Paciente\" (\"idade\", \"nome\", \"CPF\", \"birthday\", \"endereco\", \"sexo\") "
                    + "SELECT " + idade + ", " + nome + ", " + cpf + ", "
                    + birthday + ", " + endereco + ", " + sexo
                    + " FROM \"Paciente_legacy_migration\"");

                statement.execute("DROP TABLE \"Paciente_legacy_migration\"");
                jdbc.commit();
            } catch (SQLException error) {
                jdbc.rollback();
                throw error;
            } finally {
                jdbc.setAutoCommit(oldAutoCommit);
            }
        }
    }

    private Set<String> getPatientColumns(Connection jdbc) throws SQLException {
        Set<String> columns = new HashSet<String>();
        try (Statement statement = jdbc.createStatement();
             ResultSet result = statement.executeQuery("PRAGMA table_info(\"Paciente\")")) {
            while (result.next()) {
                columns.add(result.getString("name"));
            }
        }
        return columns;
    }

    private String selectColumnOrNull(Set<String> columns, String column) {
        if (columns.contains(column)) {
            return "\"" + column + "\"";
        }
        return "NULL";
    }

    public synchronized void close() {
        if (connection != null) {
            try {
                connection.close();
            } catch (Exception error) {
                System.err.println("Erro ao fechar o banco: " + error.getMessage());
            } finally {
                connection = null;
            }
        }
    }
}

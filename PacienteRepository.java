import java.sql.SQLException;
import java.util.List;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.table.TableUtils;

/** Camada de acesso aos dados (DAO/Repository) da entidade Paciente. */
public class PacienteRepository {
    private final Database database;
    private final Dao<Paciente, Integer> dao;

    public PacienteRepository(Database database) throws SQLException {
        if (database == null) {
            throw new IllegalArgumentException("A instancia de Database nao pode ser nula.");
        }
        this.database = database;
        this.dao = DaoManager.createDao(database.getConnection(), Paciente.class);
        TableUtils.createTableIfNotExists(database.getConnection(), Paciente.class);
    }

    /** Cadastra um paciente e devolve o proprio objeto com o ID gerado. */
    public Paciente create(Paciente paciente) throws SQLException {
        if (paciente == null) {
            throw new IllegalArgumentException("O paciente nao pode ser nulo.");
        }
        int linhas = dao.create(paciente);
        if (linhas != 1) {
            throw new SQLException("O paciente nao foi salvo no banco de dados.");
        }
        return paciente;
    }

    /** Recupera todos os pacientes cadastrados. */
    public List<Paciente> loadAll() throws SQLException {
        return dao.queryForAll();
    }

    /** Recupera um paciente pelo ID. */
    public Paciente loadFromId(int id) throws SQLException {
        return dao.queryForId(id);
    }

    /** Metodo mantido para permitir a melhoria opcional (Update). */
    public void update(Paciente paciente) throws SQLException {
        dao.update(paciente);
    }

    /** Exclui um paciente pelo objeto e confirma que um registro foi removido. */
    public void delete(Paciente paciente) throws SQLException {
        if (paciente == null) {
            throw new IllegalArgumentException("Selecione um paciente para excluir.");
        }
        int linhas = dao.delete(paciente);
        if (linhas != 1) {
            throw new SQLException("O paciente nao foi encontrado ou nao pode ser excluido.");
        }
    }

    public void close() {
        database.close();
    }
}

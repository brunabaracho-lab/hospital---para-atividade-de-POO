import java.text.SimpleDateFormat;
import java.util.Date;

import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

/**
 * Representa um paciente armazenado no banco de dados do hospital.
 * O campo id e gerado pelo banco; a idade e informada manualmente.
 */
@DatabaseTable(tableName = "Paciente")
public class Paciente {

    @DatabaseField(generatedId = true, columnName = "id")
    private int id;

    @DatabaseField(columnName = "idade")
    private int idade;

    @DatabaseField(columnName = "nome")
    private String nome;

    @DatabaseField(columnName = "CPF")
    private String cpf;

    @DatabaseField(dataType = DataType.DATE, columnName = "birthday")
    private Date birthday;

    @DatabaseField(columnName = "endereco")
    private String endereco;

    @DatabaseField(columnName = "sexo")
    private String sexo;

    /** Construtor vazio exigido pelo ORMLite. */
    public Paciente() {
    }

    public Paciente(String nome, String cpf, Date birthday, int idade,
                    String sexo, String endereco) {
        this.nome = nome;
        this.cpf = cpf;
        this.birthday = birthday;
        this.idade = idade;
        this.sexo = sexo;
        this.endereco = endereco;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdade() {
        return idade;
    }

    public void setIdade(int idade) {
        this.idade = idade;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public Date getBirthday() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday = birthday;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getSexo() {
        return sexo;
    }

    public void setSexo(String sexo) {
        this.sexo = sexo;
    }

    public String printBirthday() {
        if (birthday == null) {
            return "";
        }
        return new SimpleDateFormat("dd/MM/yyyy").format(birthday);
    }
}

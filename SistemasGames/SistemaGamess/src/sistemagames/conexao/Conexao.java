package sistemagames.conexao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Centraliza a conexão JDBC com o MySQL. Pode ser configurada por variáveis de ambiente. */
public final class Conexao {
    private static final String URL = System.getenv().getOrDefault(
            "SISTEMAGAMES_DB_URL", "jdbc:mysql://localhost:3306/sistemagames?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=America/Sao_Paulo&characterEncoding=UTF-8");
    private static final String USUARIO = System.getenv().getOrDefault("SISTEMAGAMES_DB_USER", "root");
    private static final String SENHA = System.getenv().getOrDefault("SISTEMAGAMES_DB_PASSWORD", "");

    private Conexao() { }

    public static Connection conectar() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver MySQL não encontrado. Adicione lib/mysql-connector-j-9.5.0.jar ao projeto.", e);
        }
        return DriverManager.getConnection(URL, USUARIO, SENHA);
    }
}

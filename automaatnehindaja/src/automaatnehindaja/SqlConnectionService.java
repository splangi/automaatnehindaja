package automaatnehindaja;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import org.apache.log4j.Logger;

import config.Config;
import config.ConfigException;

public class SqlConnectionService {
	Connection c;
	private static Logger logger = Logger.getLogger(SqlConnectionService.class);

	public SqlConnectionService() {		
		try {
			Config config = new Config(SqlConnectionService.class.getClassLoader().getResource("").getPath() + "../../config/databaseConfig.cfg");
			String url = config.getString("url");
			String port = config.getString("port");
			String database = config.getString("database");
			String login = config.getString("login");
			String passwd = config.getString("password");
			Class.forName("com.mysql.jdbc.Driver");
			c = DriverManager.getConnection(
					"jdbc:mysql://"+ url + ":" + port + "/" + database, login,
					passwd);
		} catch (ClassNotFoundException e) {
			logger.error("ClassNotFoundException", e);
		}catch (SQLException e) {
			logger.error("SQLException", e);
		} catch (ConfigException e) {
			// TODO Auto-generated catch block
			logger.error("ConfigException", e);
		}
	}

	public Connection getConnection() {
		return c;
	}
	
	
}

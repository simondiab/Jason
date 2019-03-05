package jason;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jason.model.ProcessedEvent;
import jason.model.RawEvent;

public class JSONDbUtils {
	private static final Logger logger = LoggerFactory.getLogger(JSONDbUtils.class);
	
	static Connection getConnection() throws SQLException {
		return DriverManager.getConnection("jdbc:hsqldb:file:testdb/testdb", "SA", "");
	}
	
	static void initDB() throws ClassNotFoundException {
		logger.info("Start initDB()");
		Class.forName("org.hsqldb.jdbc.JDBCDriver");
		try {
			Connection connection = JSONDbUtils.getConnection(); Statement statement = connection.createStatement();
	        //RawEvent table
			statement.execute("CREATE TEXT TABLE IF NOT EXISTS raw_event ("
	        		+ "id INTEGER IDENTITY PRIMARY KEY, "
	        		+ "json_event_id VARCHAR(50) NOT NULL, "
	        		+ "state VARCHAR(50) NOT NULL,"
	        		+ "timestamp BIGINT,"
	                + "type VARCHAR(50),"
	                + "host VARCHAR(50))");
	        statement.execute("SET TABLE raw_event SOURCE 'rawEventFile;fs=|'");
	        
	        //ProcessedEvent table
	        statement.execute("CREATE TEXT TABLE IF NOT EXISTS processed_event ("
	        		+ "id INTEGER IDENTITY PRIMARY KEY, "
	        		+ "json_event_id VARCHAR(50) NOT NULL, "
	        		+ "duration VARCHAR(50) NOT NULL,"
	                + "type VARCHAR(50),"
	                + "host VARCHAR(50),"
	                + "alert BOOLEAN DEFAULT FALSE NOT NULL)");
	        statement.execute("SET TABLE processed_event SOURCE 'processedEventFile;fs=|'");
	        connection.commit();
	        
	       
	    } catch (SQLException e) {
			e.printStackTrace();
		}
		logger.info("End initDB()");
	}


	@SuppressWarnings("unused")
	static void insertProcessedEvent(ProcessedEvent processedEvent) {
		try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
			int result = 0;
			String insert = "INSERT INTO processed_event (json_event_id, duration, type, host, alert) VALUES ("
	        		+ "'" + processedEvent.getId() + "', " 
	        		+ "'" + processedEvent.getDuration() + "', "
	        		+ "'" + processedEvent.getType() + "', "
	        		+ "'" + processedEvent.getHost() + "', "
	        		+ "'" + processedEvent.isAlert() +"') ";
	        result = statement.executeUpdate(insert);
	        connection.commit();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}
	
	@SuppressWarnings("unused")
	static void insertRawEvent(RawEvent rawEvent) {
		try (Connection connection = getConnection(); Statement statement = connection.createStatement();) {
			int result = 0;
			String insert = "INSERT INTO raw_event (json_event_id, state, timestamp, type, host) VALUES ("
	        		+ "'" + rawEvent.getId() + "', " 
	        		+ "'" + rawEvent.getState() + "', " 
	        		+ "'" + rawEvent.getTimestamp() + "', " 
	        		+ "'" + rawEvent.getType() + "', "
	        		+ "'" + rawEvent.getHost() + "') ";
	        result = statement.executeUpdate(insert);
	        connection.commit();
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	}

	public static RawEvent getStartEvent(String id) {
		String sql = "SELECT JSON_EVENT_ID, STATE, TIMESTAMP, TYPE, HOST "
				+ "from raw_event "
				+ "where json_event_id = ?";
		try (Connection conn = getConnection(); PreparedStatement ps = conn.prepareStatement(sql);) {
			RawEvent rawEvent = new RawEvent();
			ps.setString(1, id);
			
	        ResultSet rs = ps.executeQuery();
	        if (rs.next()) {
	        	rawEvent.setId(rs.getString("JSON_EVENT_ID"));
	        	rawEvent.setState(rs.getString("STATE"));
	        	rawEvent.setTimestamp(rs.getLong("TIMESTAMP"));
	        	rawEvent.setType(rs.getString("TYPE"));
	        	rawEvent.setHost(rs.getString("HOST"));
	        	return rawEvent;
	        }
	        
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		return null;
	}

}

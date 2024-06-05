package koneksi;

import java.sql.*;

public final class Connect {
	
	private final String USERNAME = "root"; 
	private final String PASSWORD = "";
	private final String DATABASE = "njuice"; 
	private final String HOST = "localhost:3306"; 
	private final String CONECTION = String.format("jdbc:mysql://%s/%s?enabledTLSProtocols=TLSv1.2", HOST, DATABASE);
	
	private Connection con;
	private Statement st;
	private static Connect connect;

	public ResultSet rs;
	public PreparedStatement ps;
	public ResultSetMetaData rsm;

    private Connect() {
    	try {  
    		 Class.forName("com.mysql.cj.jdbc.Driver");
             con = DriverManager.getConnection(CONECTION,USERNAME, PASSWORD);  
             st = con.createStatement(); 
             System.out.println("server connected !s");
        } catch(Exception e) {
        	e.printStackTrace();
        	System.out.println("Failed to connect the database, the system is terminated!");
        	System.exit(0);
        }  
    }

    public ResultSet selectQuery(String query)
    {
    	try {
    		rs = st.executeQuery(query);
			System.out.println(rs);
			rsm = rs.getMetaData();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	
    	return rs;
    }
    
    public static synchronized Connect getConnection() {
		return connect = (connect == null) ? new Connect() : connect;
    }

    public ResultSet executeQuery(String query) {
        ResultSet rs = null;
    	try {
            rs = st.executeQuery(query);
        } catch(Exception e) {
        	e.printStackTrace();
        }
        return rs;
    }

    public void executeUpdate(String query) {
    	try {
			st.executeUpdate(query);
			System.out.println(st);
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    
    public PreparedStatement prepareStatement(String query) {
		PreparedStatement ps = null;
		try {
			ps = con.prepareStatement(query);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return ps;
	}
    
    public void commit() {
		try {
			con.setAutoCommit(false);
			con.commit();
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }
}
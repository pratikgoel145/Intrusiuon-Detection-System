import java.sql.*;  

public class OracleCon{   
	public static void execute(String query){  
		try{  
			//step1 load the driver class  
			Class.forName("oracle.jdbc.driver.OracleDriver");  

			//step2 create  the connection object 
			Connection con=DriverManager.getConnection(  
					"jdbc:oracle:thin:@localhost:1521:xe","system","system");
			
			//step3 create the statement object  
			Statement stmt=con.createStatement();  
//			System.out.println("Before execution");
//			ResultSet rs1=stmt.executeQuery("select * from STUDENT ");
//			while(rs1.next())  {
//				System.out.println(rs1.getString(1)+"  "+rs1.getDouble(2));  
//			}
		
			//step4 execute query  
			//query = query.substring(0, query.length()-2);
			stmt.executeQuery(query); //no semicolon 
			System.out.println("Query Executed!");
			
//			ResultSet rs2=stmt.executeQuery("select * from STUDENT, SAMPLE ");		
//			System.out.println("\nAfter execution");
//			while(rs2.next())  {
//				System.out.println(rs2.getString("NAME")+"  "+rs2.getDouble("NUM")+" "+rs2.getInt("A")+" "+rs2.getInt("B"));  
//			}
			//step5 close the connection object  
			con.close();  

		}catch(Exception e){ System.out.println(e);}

	}
	
	public static void main(String args[]){
		execute("select count(*) from CUSTOMER");
	}
}  
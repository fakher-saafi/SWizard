import com.lyance.srvwiz.service.postgresService;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**  *  * Java program to connect to MySQL Server database running on localhost,  * using JDBC type 4 driver.  *  * @author http://java67.blogspot.com  */
public class postgresTest {
    public static void main(String args[]) throws SQLException {
        postgresService postgresService=new postgresService();
        Connection con = null;

        String url = "jdbc:postgresql://localhost:5433/app";
        String username = "postgres";
        String password = "0000";

        if (postgresService.testConnection(url,username,password)){
           // System.out.println(postgresService.getTables());
           // System.out.println(postgresService.getTablePk("company"));
           // System.out.println(postgresService.getTableColumns("company"));

            System.out.println(postgresService.FindById("company","1"));
            //System.out.println(postgresService.FindOneBy("company","name","Paul"));

        }


    }
}

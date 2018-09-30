import com.lyance.srvwiz.service.mysqlService;

import java.sql.Connection;
import java.sql.SQLException;

/**  *  * Java program to connect to MySQL Server database running on localhost,  * using JDBC type 4 driver.  *  * @author http://java67.blogspot.com  */
public class mysqlTest {
    public static void main(String args[]) throws SQLException {
        mysqlService ms=new mysqlService();
        Connection con = null;

        System.out.println(ms.isTypeString("json"));

    }
}

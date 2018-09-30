package com.lyance.srvwiz.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

@Service
@Transactional
public class mysqlService {
    private String path;
    private String username;
    private String password;
    private  Connection con = null;
    private  Statement stmt = null;
    private  ResultSet rs = null;
    private  List<String> tables=null;
    private  Map<String,String> colomns=null;
    private ObjectMapper mapper = new ObjectMapper(); // jackson's objectmapper
    private final List<String> MysqlStringTypes=new ArrayList<>(Arrays.asList("char","date","text","binary","blob","enum","set","time","year","json"));

    public mysqlService() {
        tables=new ArrayList<String>();
        colomns=new HashMap<String,String>();

    }
    public mysqlService(String path,String username, String password) {
        //testConnection();

        colomns=new HashMap<String,String>();


    }

    public Connection getCon() {
        return con;
    }

    public Statement getStmt() {
        return stmt;
    }

    public ResultSet getRs() {
        return rs;
    }

    public List<String> getTables() {
        String query ="show tables";

        tables=new ArrayList<String>();
        try {
            stmt = con.prepareStatement(query);

            //Resultset returned by query

            rs = stmt.executeQuery(query);

            while(rs.next()){ String tablename = rs.getString(1);
                System.out.println("table : " + tablename);
                tables.add(tablename);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tables;
    }

    public String getTablePk(String table) {
        String query ="SHOW KEYS FROM "+table+" WHERE Key_name = 'PRIMARY'";
        String tablepk="";

        try {
            stmt = con.prepareStatement(query);

            //Resultset returned by query

            rs = stmt.executeQuery(query);

            while(rs.next()){ tablepk = rs.getString(5);
                System.out.println("tablePk : " + tablepk);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return tablepk;
    }

    public Map<String, String> getColomns() {
        return colomns;
    }


    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "mysqldbService{" +
            "con=" + con +
            ", stmt=" + stmt +
            ", rs=" + rs +
            ", tables=" + tables +
            ", colomns=" + colomns +
            '}';
    }

    public boolean testConnection(String path,String username,String password){
        boolean test=false;

        try {

            con = DriverManager.getConnection(path, username, password);
            if (con != null) { System.out .println("Successfully connected to MySQL database ");
                test=true;
            }
        } catch (SQLException ex) {
            System.out .println("pathhhhhhh "+path);
            //System.out .println("An error occurred while connecting "+webservice.getDatabaseProduct().toString()+" database");
            ex.printStackTrace();

        }
        return test;
    }
    public boolean testConnection(){
        boolean test=false;

        try {

            con = DriverManager.getConnection(this.path, this.username, this.password);
            if (con != null) { System.out .println("Successfully connected to MySQL database ");
                test=true;
            }
        } catch (SQLException ex) {
            System.out .println("pathhhhhhh "+path);
            //System.out .println("An error occurred while connecting "+webservice.getDatabaseProduct().toString()+" database");
            ex.printStackTrace();

        }
        return test;
    }
    public Map<String,String> getTableColumns(String table){
        String query="SHOW COLUMNS from "+table;
        try {
            stmt = con.prepareStatement(query);

            //Resultset returned by query

            rs = stmt.executeQuery(query);

            while(rs.next()){
                String name = rs.getString(1);
                String type = rs.getString(2);
                //  System.out.println("name : " + name+" type : " + type);

                colomns.put(name,type);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colomns;
    }


    public List<Map> findAll(String table) throws SQLException{
        String query="SELECT * from "+table;


            stmt = con.prepareStatement(query);


            //Resultset returned by query


            rs = stmt.executeQuery(query);
        try {
            return convertToMap(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;

    }

    public  List<Map> convertToMap(ResultSet resultSet)
        throws Exception {
        List<Map> list=new ArrayList<>();
        while (resultSet.next()) {
            Map hashMap = new HashMap();
            int total_rows = resultSet.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {

                hashMap.put(resultSet.getMetaData().getColumnLabel(i + 1)
                    .toLowerCase(), resultSet.getObject(i + 1));

            }
            list.add(hashMap);
        }
        return list;
    }




    public List<Map> FindOneBy(String table,String column,String columnValue)throws SQLException{
        String query="SELECT * FROM `"+table+"` WHERE "+column+"='"+columnValue+"'";


            stmt = con.prepareStatement(query);

            //Resultset returned by query


            rs = stmt.executeQuery(query);
        try {
            return convertToMap(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }

    public Map FindById(String table,String ID)throws SQLException{
        String query="SELECT * FROM `"+table+"` WHERE "+getTablePk(table)+"='"+ID+"'";

        System.out.println(query);
            stmt = con.prepareStatement(query);

            //Resultset returned by query


            rs = stmt.executeQuery(query);
            while (rs.next()) {
            Map hashMap = new HashMap();
            int total_rows = rs.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {

                hashMap.put(rs.getMetaData().getColumnLabel(i + 1)
                    .toLowerCase(), rs.getObject(i + 1));



            }
                return hashMap;
            }



        return null;
    }
    public boolean isTypeString(String type){
        for (String mst:MysqlStringTypes) {
        if (type.contains(mst)){
            return true;
        }
        }
        return false;
    }

    public void Add(Map map,String table)throws SQLException{
        Map<String,String> tableStruct =getTableColumns(table);
        String column="";
        String value="";
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();

            if(tableStruct.containsKey(entry.getKey())&& isTypeString(tableStruct.get(entry.getKey()))){
                column+=entry.getKey();
                value+="'"+entry.getValue()+"'";
            }else {
                column+=entry.getKey();
                value+=entry.getValue();
            }

            //  System.out.println("Key = " + key + ", Value = " + value);
            if (entries.hasNext()) {
                column+=",";
                value+=",";
            }
        }


        String query="INSERT INTO "+table+" ("+column+") VALUES ("+value+")";
        System.out.println(query);

        stmt = con.prepareStatement(query);
        stmt.executeUpdate(query);

        con.close();


    }

    public void Update(Map map,String table)throws SQLException{
        Map<String,String> tableStruct =getTableColumns(table);
        String query="UPDATE "+table+" Set ";
        Iterator entries = map.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry entry = (Map.Entry) entries.next();

            if(tableStruct.containsKey(entry.getKey())&& isTypeString(tableStruct.get(entry.getKey()))){
                query+=entry.getKey()+"="+"'"+entry.getValue()+"'";
            }else {
                query+=entry.getKey()+"="+entry.getValue();
            }

            //  System.out.println("Key = " + key + ", Value = " + value);
            if (entries.hasNext()) {
                query+=",";
            }
        }


         query+=" where "+getTablePk(table)+"="+map.get(getTablePk(table));
         System.out.println(query);

        stmt = con.prepareStatement(query);
        stmt.executeUpdate(query);

        con.close();


    }

    public void Delete(String table,String id)throws SQLException{

        String  query="DELETE FROM "+table+" WHERE "+getTablePk(table)+"="+id;
        System.out.println(query);

        stmt = con.prepareStatement(query);
        stmt.executeUpdate(query);

        con.close();

    }

}

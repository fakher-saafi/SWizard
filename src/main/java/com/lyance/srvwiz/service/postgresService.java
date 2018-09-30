package com.lyance.srvwiz.service;


import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.util.*;

@Service
@Transactional
public class postgresService {
    private String path;
    private String username;
    private String password;
    private  Connection con = null;
    private  Statement stmt = null;
    private  ResultSet rs = null;
    private  List<String> tables=null;
    private  Map<String,String> colomns=null;
    private final List<String> PostgresStringTypes=new ArrayList<>(Arrays.asList("char","date","text","binary","BYTEA","time","year","json"));

    public postgresService() {
        tables=new ArrayList<String>();
        colomns=new HashMap<String,String>();

    }
    public postgresService(String path, String username, String password) {
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
        String query ="SELECT table_name FROM information_schema.tables WHERE table_type='BASE TABLE' AND table_schema='public'";

        tables=new ArrayList<String>();
        try {
            stmt = con.createStatement();

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
        String query ="SELECT c.column_name\n" +
            "FROM information_schema.key_column_usage AS c\n" +
            "LEFT JOIN information_schema.table_constraints AS t\n" +
            "ON t.constraint_name = c.constraint_name\n" +
            "WHERE t.table_name = '"+table+"' AND t.constraint_type = 'PRIMARY KEY';";

        String tablepk="";

        try {
            stmt = con.createStatement();

            //Resultset returned by query

            rs = stmt.executeQuery(query);

            while(rs.next()){ tablepk = rs.getString(1);
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
            if (con != null) { System.out .println("Successfully connected to Postgres database ");
                test=true;
            }
        } catch (SQLException ex) {
            System.out .println("pathhhhhhh "+path);
            System.out .println("An error occurred while connecting to database");
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
        String query="SELECT COLUMN_NAME,DATA_TYPE FROM information_schema.COLUMNS WHERE TABLE_NAME = '"+table+"'";
        try {
            stmt = con.createStatement();

            //Resultset returned by query

            rs = stmt.executeQuery(query);

            while(rs.next()){
                String name = rs.getString(1);
                String type = rs.getString(2);
                System.out.println("name : " + name+" type : " + type);

                colomns.put(name,type);

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colomns;
    }


    public List<Map> findAll(String table) throws SQLException {
        String query="SELECT * from "+table;
        System.out.println(query);

            stmt = con.createStatement();


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




    public List<Map> FindOneBy(String table,String column,String columnValue){
        String query="SELECT * FROM "+table+" WHERE "+column+"='"+columnValue+"'";
        System.out.println(query);
        try {
            stmt = con.createStatement();

            //Resultset returned by query


            rs = stmt.executeQuery(query);
            return convertToMap(rs);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return null;
    }

    public Map FindById(String table,String ID)throws SQLException{
        String query="SELECT * FROM "+table+" WHERE "+getTablePk(table)+"='"+ID+"'";

        System.out.println(query);
            stmt = con.createStatement();

            //Resultset returned by query


            rs = stmt.executeQuery(query);
            while (rs.next()) {
            Map hashMap = new HashMap();
            int total_rows = rs.getMetaData().getColumnCount();
            for (int i = 0; i < total_rows; i++) {

                hashMap.put(rs.getMetaData().getColumnLabel(i + 1)
                    .toLowerCase(), rs.getObject(i + 1));



            }
                System.out.println(hashMap);
                return hashMap;
            }



        return null;
    }
    public boolean isTypeString(String type){
        for (String pst:PostgresStringTypes) {
            if (type.contains(pst)){
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

        stmt = con.createStatement();
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

        stmt = con.createStatement();
        stmt.executeUpdate(query);

        con.close();


    }

    public void Delete(String table,String id)throws SQLException{

        String  query="DELETE FROM "+table+" WHERE "+getTablePk(table)+"="+id;
        System.out.println(query);

        stmt = con.createStatement();
        stmt.executeUpdate(query);

        con.close();

    }

}

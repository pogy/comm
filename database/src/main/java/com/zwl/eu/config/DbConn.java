package com.zwl.eu.config;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DbConn {

    /**数据库驱动*/
    private static final String DRIVERNAME = "com.mysql.jdbc.Driver";
 
    private static final String USER = "root";
    
    private static final String PASSWORD = "7ujm8ik,";
 
    /**数据库连接地址*/
    private static final String URL = "jdbc:mysql://127.0.0.1:3306/" + DataBaseConfigure.MODULENAME + "?characterEncoding=utf8";
    
    private static Connection CONN = null;

	/**初始化连接*/
    public static Connection getConn() throws ClassNotFoundException, SQLException {
    	if(CONN == null){
    		Class.forName(DRIVERNAME);
        	CONN = DriverManager.getConnection(URL,USER,PASSWORD);
    	}
    	return CONN;
    }
}

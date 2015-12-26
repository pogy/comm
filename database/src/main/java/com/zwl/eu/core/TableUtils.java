package com.zwl.eu.core;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.Maps;
import com.zwl.eu.config.DataBaseConfigure;
import com.zwl.eu.config.DbConn;

public class TableUtils {
    
    public void start() throws ClassNotFoundException, SQLException, IOException {
        String prefix = "show full fields from ";
        List<String> columns = null;
        List<String> types = null;
        List<String> comments = null;
        PreparedStatement pstate = null;
        List<String> tables = getTables();
        Map<String, String> tableComments = getTableComment();
        for ( String table : tables ) {
        	if("order".equals(table)){table = "vipkid.order";}
            columns = new ArrayList<String>();
            types = new ArrayList<String>();
            comments = new ArrayList<String>();
            pstate = DbConn.getConn().prepareStatement(prefix + table);
            ResultSet results = pstate.executeQuery();
            while ( results.next() ) {
                columns.add(results.getString("FIELD"));
                types.add(results.getString("TYPE"));
                comments.add(results.getString("COMMENT"));
            }
            
            Map<String,Object> map = processTable(table);
            String tableComment = tableComments.get(table);
            try{
            	EntityUtil eu = new EntityUtil();
	            eu.buildEntityBean(columns, types, comments, tableComment,map.get(DataBaseConfigure.BEAN_NAME).toString());
	            eu.buildMapper(map.get(DataBaseConfigure.BEAN_NAME).toString(),map.get(DataBaseConfigure.MAPPER_NAME).toString());
	            eu.buildMapperXml(columns, types, comments,table,map.get(DataBaseConfigure.BEAN_NAME).toString(),map.get(DataBaseConfigure.MAPPER_NAME).toString());
            }catch(Exception e){
            	System.out.print(e.getMessage());
            }
            
        }
        DbConn.getConn().close();
    }
	
    
	/**根据表名，获取beanName，MapperName*/
    public Map<String,Object> processTable(String table) {
    	Map<String, Object> resultMap = Maps.newHashMap();
        StringBuffer sb = new StringBuffer(table.length());
        String tableNew = table.toLowerCase();
        String[] tables = tableNew.split("_");
        String temp = null;
        for ( int i = 0; i < tables.length ; i++ ) {
            temp = tables[i].trim();
            sb.append(temp.substring(0, 1).toUpperCase()).append(temp.substring(1));
        }
        resultMap.put(DataBaseConfigure.BEAN_NAME, sb.toString());
        resultMap.put(DataBaseConfigure.MAPPER_NAME, sb.toString() + "Mapper");
        return resultMap;
    }
    
    /**
     *  获取所有的表
     *
     * @return
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    public List<String> getTables() throws SQLException, ClassNotFoundException {
        List<String> tables = new ArrayList<String>();
        PreparedStatement pstate = DbConn.getConn().prepareStatement("show tables");
        ResultSet results = pstate.executeQuery();
        while ( results.next() ) {
            String tableName = results.getString(1);
            tables.add(tableName);
        }
        return tables;
    }
    
    
    /**
     *  获取所有的数据库表注释
     *
     * @return
     * @throws SQLException 
     * @throws ClassNotFoundException 
     */
    private Map<String, String> getTableComment() throws SQLException, ClassNotFoundException { 
        Map<String, String> maps = new HashMap<String, String>();
        PreparedStatement pstate = DbConn.getConn().prepareStatement("show table status");
        ResultSet results = pstate.executeQuery();
        while ( results.next() ) {
            String tableName = results.getString("NAME");
            String comment = results.getString("COMMENT");
            maps.put(tableName, comment);
        }
        return maps;
    }
    
 
}

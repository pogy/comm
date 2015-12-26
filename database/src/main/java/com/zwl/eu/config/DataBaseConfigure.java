package com.zwl.eu.config;

import java.io.IOException;
import java.sql.SQLException;

import com.zwl.eu.core.TableUtils;

public class DataBaseConfigure {
	
	/**数据库名称/模块名称*/
    public static final String MODULENAME = "vipkid"; 
    
    /**pojo 生成路径*/
    public static final String BEAN_PATH = "d:/eu/entity_bean"; 
 
    /**映射接口 生成路径*/
    public static final String MAPPER_PATH = "d:/eu/entity_mapper";
 
    /**mapper.xml 生成路径*/
    public static final String XML_PATH = "d:/eu/entity_mapper/xml";
 
    /**pojo所在包定义*/
    public static final String BEAN_PACKAGE = "com.eu." + DataBaseConfigure.MODULENAME + ".entity";
 
    /**mapper 所在包定义*/
    public static final String MAPPER_PACKAGE = "com.eu." + DataBaseConfigure.MODULENAME + ".mapper";
    
    public static final String BEAN_NAME = "beanName";
    
    public static final String MAPPER_NAME = "mapperName";
    
    public static void main( String[] args ) {
        try {
        	
            new TableUtils().start();
            System.out.println("---------------执行完毕-------------------------");
            // 自动打开生成文件的目录
            Runtime.getRuntime().exec("cmd /c start explorer D:\\eu\\");
        } catch ( ClassNotFoundException e ) {
            e.printStackTrace();
        } catch ( SQLException e ) {
            e.printStackTrace();
        } catch ( IOException e ) {
            e.printStackTrace();
        }
    }
    
}

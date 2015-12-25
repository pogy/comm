package com.vipkid.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;

public class JsonUtils {

	//从给定位置读取Json文件
    public static String readJson(String path){
        //从给定位置获取文件
        File file = new File(path);
        BufferedReader reader = null;
        //返回值,使用StringBuffer
        StringBuffer data = new StringBuffer();
        
        try {
            reader = new BufferedReader(new FileReader(file));
            //每次读取文件的缓存
            String temp = null;
            while((temp = reader.readLine()) != null){
                data.append(temp);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            //关闭文件流
            if (reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return data.toString();
    }
    //给定路径与Json文件，存储到硬盘
    public static void writeJson(String path, Object json, String fileName){
        BufferedWriter writer = null;
        File file = new File(path);
        if(!file.exists()){
        	try {
        		file.mkdir();
			} catch (Exception e) {
				e.printStackTrace();
			}
        	
        }
        file = new File(path + fileName + ".json");
        //如果文件不存在，则新建一个
        if(!file.exists()){
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //写入
        try {
            writer = new BufferedWriter(new FileWriter(file));
            writer.write(json.toString());
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            try {
                if(writer != null){
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    /**
     * 根据文件名前缀，过滤文件
     * @param dir		查找目录
     * @param fileName	文件名前缀
     * @return
     */
    public static String[] getFileNames(String dir, String fileName){
    	 File f = new File(dir);  
    	 if(f.exists()){
    		 MyFilter filter = new MyFilter(fileName);  
             String[] files = f.list(filter);  
             return files;
    	 }else{
    		 return null;
    	 }
    }
    
    static class MyFilter implements FilenameFilter{  
        private String type;  
        public MyFilter(String type){  
            this.type = type;
        }  
        public boolean accept(File dir,String name){  
            return name.startsWith(name);  
        }
		public String getType() {
			return type;
		}
		public void setType(String type) {
			this.type = type;
		}
        
    }

}

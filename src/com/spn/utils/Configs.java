package com.spn.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {
	
	Properties props;
	
	public static final String BFT_CONFIGS="config.properties";
	public static final String CFT_CONFIGS="cft.config.properties";
	
	
	
	public Configs(String filename) throws IOException{
		
		this.props = new Properties();
               // File f = new File(PROPS_LOCATION);
		//InputStream is= new FileInputStream(f);
        
		InputStream is = getClass().getClassLoader().getResourceAsStream(filename);
		
		if(is != null){
			props.load(is);
		}else{
			throw new FileNotFoundException("The file: "+filename+" was not found!");
		}
	}
	
	public String getProperty(String p){
            System.out.println("Configs.. Reading Property: " + p + " : " + props.getProperty(p));
		return this.props.getProperty(p);
	}
	
	public void dumProperties(){
		
		for (Object k : this.props.keySet()){
			String key = (String)k;;
			System.out.println("Property "+key+" as value: "+this.props.getProperty(key));
		}
	}
}

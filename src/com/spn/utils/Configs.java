package com.spn.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {
	
	Properties props;
	
	private static final String PROPS_LOCATION="config.properties";
	
	
	
	public Configs() throws IOException{
		
		this.props = new Properties();
               // File f = new File(PROPS_LOCATION);
		//InputStream is= new FileInputStream(f);
        
		InputStream is = getClass().getClassLoader().getResourceAsStream(PROPS_LOCATION);
		
		if(is != null){
			props.load(is);
		}else{
			throw new FileNotFoundException("The file: "+PROPS_LOCATION+" was not found!");
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

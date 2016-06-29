package com.spn.utils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Configs {
	
	Properties props;
	
	private static final String PROPS_LOCATION="config.conf";
	
	
	
	public Configs() throws IOException{
		
		this.props = new Properties();
		
		InputStream is = getClass().getClassLoader().getResourceAsStream(PROPS_LOCATION);
		
		if(is != null){
			props.load(is);
		}else{
			throw new FileNotFoundException("The file: "+PROPS_LOCATION+" was not found!");
		}
	}
	
	public String getProperty(String p){
		return this.props.getProperty(p);
	}
	
	public void dumProperties(){
		
		for (Object k : this.props.keySet()){
			String key = (String)k;
			System.out.println("Property "+key+" as value: "+this.props.getProperty(key));
		}
	}
}

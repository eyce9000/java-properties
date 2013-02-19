package com.grl.props;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Properties;

public class PropertyMapper {
	public <T> String describeProperties(Class<T> type){
		String description = "";
		Field[] fields = type.getDeclaredFields();
		for(Field field :fields){
			Property annotation = field.getAnnotation(Property.class);
			description += annotation.name();
			if(annotation.required())
				description += " *";
			if(!annotation.description().isEmpty())
				description += "\n\t"+annotation.description();
			description +="\n";
		}
		description += "\n* required Property";
		return description;
	}
	
	public <T> T loadPropertyFile(File file, Class<T> type) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException{
		T object = type.newInstance();
		loadPropertyFileIntoInstance(file,object,type);
		return object;
	}
	
	public <T> void loadPropertyFileIntoInstance(File file, T object, Class<T> type) throws FileNotFoundException, IOException{

		Properties props = new Properties();
		props.load(new FileReader(file));
		
		
		Field[] fields = object.getClass().getDeclaredFields();
		
		for(Field field :fields){
			
			boolean wasPublic = field.isAccessible();
			field.setAccessible(true);
			Property annotation = field.getAnnotation(Property.class);
			if(annotation!=null){
				String key = annotation.name();
				try {
					String value;
					if(annotation.required())
						value = loadOrThrow(props,key);
					else
						value = load(props,key);
					
					if(key!=null && value!=null){
						if(field.getType().isInstance("")){
							field.set(object, value.toString());
						}
					}
				} catch(Exception ex){
					ex.printStackTrace();
				}
			}
			field.setAccessible(wasPublic);
		}
	}
	private String load(Properties props, String key){
		return props.getProperty(key);
	}
	private String loadOrThrow(Properties props, String key){
		String value = load(props,key);
		if(value!=null)
			return value;
		else
			throw new IllegalArgumentException("Missing property "+key);
	}
}

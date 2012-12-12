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
			description += "Property "+annotation.name();
			if(annotation.required())
				description += " is required";
			description +="\n";
		}
		return description;
	}
	
	public <T> T loadPropertyFile(File file, Class<T> type) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException{
		Properties props = new Properties();
		props.load(new FileReader(file));
		
		T object = type.newInstance();
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
		return object;
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

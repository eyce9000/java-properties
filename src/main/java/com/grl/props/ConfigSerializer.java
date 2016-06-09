package com.grl.props;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Map.Entry;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.AnnotationIntrospectorPair;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.xml.XmlFactory;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.fasterxml.jackson.module.jaxb.JaxbAnnotationIntrospector;

public class ConfigSerializer<T> {
	TypeReference<HashMap<String,String>> def = new TypeReference<HashMap<String,String>>(){};
	
	public static enum Format{PROPERTIES,YAML,XML,JSON};
	
	private Class<? extends T> clazz;
	private Format format;
	private ObjectMapper mapper;
	
	public ConfigSerializer(Class<? extends T> clazz, Format format){
		this.clazz = clazz;
		this.format = format;
		
		mapper = new ObjectMapper();
		
		switch(format){
		case PROPERTIES:
			break;
		
		case JSON:
			break;
		case YAML:{
			mapper = new ObjectMapper(new YAMLFactory());
			
			break;
		}
		case XML:{
			mapper = new XmlMapper();
			break;
		}
		}
		
		mapper.setAnnotationIntrospector(new AnnotationIntrospectorPair(
				new JacksonAnnotationIntrospector(),
				new JaxbAnnotationIntrospector(mapper.getTypeFactory())
		));
	}
	
	/*
	 * *******************************************************************
	 * R E A D E R S
	 * *******************************************************************
	 */
	
	public T deserialize(File file) throws IllegalArgumentException, FileNotFoundException, IOException{
		return deserialize(new FileReader(file));
	}
	
	public T deserialize(InputStream stream) throws IllegalArgumentException, IOException{
		return deserialize(new InputStreamReader(stream));
	}
	
	public T deserialize(Reader reader) throws IllegalArgumentException, IOException{
		if(format==Format.PROPERTIES){
			return (T)mapper.convertValue(readProperties(reader), clazz);
		}
		else{
			return mapper.readValue(reader, clazz);
		}
	}
	
	private Map<String,Object> readProperties(Reader reader) throws IOException{
		Properties props = new Properties();
		props.load(reader);
		Map<String,Object> data = new HashMap<String,Object>();
		for(Entry<Object,Object> entry:props.entrySet()){
			data.put((String)entry.getKey(),entry.getValue());
		}
		return data;
	}

	
	/*
	 * *******************************************************************
	 * W R I T E R S
	 * *******************************************************************
	 */
	 public void serialize(T value, File file) throws JsonGenerationException, JsonMappingException, IOException{
		 serialize(value, new FileWriter(file));
	 }
	 
	 public void serialize(T value, OutputStream stream) throws JsonGenerationException, JsonMappingException, IOException{
		 serialize(value, new OutputStreamWriter(stream));
	 }
	 
	 public void serialize(T value, Writer writer) throws JsonGenerationException, JsonMappingException, IOException{
		 if(format==Format.PROPERTIES){
			 writeProperties(value,writer);
		 }
		 else{
			 mapper.writeValue(writer, value);
		 }
	 }
	 
	 private void writeProperties(T value, Writer writer) throws IOException{
		 Properties props = new Properties();
			Map<String,String> data = (Map<String,String>)mapper.convertValue(value,  def);
			for(Entry<String,String> entry:data.entrySet()){
				if(entry.getValue()!=null){
					props.setProperty(entry.getKey(), entry.getValue());
				}
			}
			props.store(writer, "");
	 }
}

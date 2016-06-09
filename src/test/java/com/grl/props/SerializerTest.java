package com.grl.props;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.junit.Before;
import org.junit.Test;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.grl.props.ConfigSerializer.Format;

public class SerializerTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void test() throws FileNotFoundException, IOException {
		SampleObject obj1 = new SampleObject();
		obj1.intValue = 12;
		obj1.longValue = Long.MAX_VALUE;
		obj1.strValue = "String value";
		obj1.dblValue = 1.1;
		obj1.urlValue = new URL("http://www.ibm.com/");

		testFormatSerialization(obj1,Format.PROPERTIES);
		obj1.listValue = Arrays.asList(new String[]{"Test 1","Test 2"});
		testFormatSerialization(obj1,Format.YAML);
		testFormatSerialization(obj1,Format.JSON);
		testFormatSerialization(obj1,Format.XML);
	}
	
	public void testFormatSerialization(SampleObject obj1,Format format) throws JsonGenerationException, JsonMappingException, IOException{
		ConfigSerializer<SampleObject> serializer = new ConfigSerializer<SampleObject>(SampleObject.class, format);
		File file =  new File("test-files/config."+format.toString().toLowerCase());
		
		serializer.serialize(obj1,file);
		SampleObject obj2 = serializer.deserialize(file);
		
		assertThat(obj2.intValue, equalTo(obj1.intValue));
		assertThat(obj2.longValue, equalTo(obj1.longValue));
		assertThat(obj2.strValue, equalTo(obj1.strValue));
		assertThat(obj2.dblValue, equalTo(obj1.dblValue));
		assertThat(obj2.urlValue, equalTo(obj1.urlValue));
	}
	
	@XmlRootElement(name="config")
	public static class SampleObject {
		@XmlElement(name="int.value")
		int intValue = 2;
		@XmlElement(name="long.value")
		long longValue;
		@XmlElement(name="str.value")
		String strValue = "Default value";
		@XmlElement(name="dbl.value")
		double dblValue = 1;
		@XmlElement(name="url.value")
		URL urlValue;
		@XmlElement(name="list.value")
		List<String> listValue;
	}
}

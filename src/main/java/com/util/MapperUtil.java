package com.util;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.fasterxml.jackson.core.FormatFeature;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SequenceWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.pojo.Varia;

public class MapperUtil {
	/**
	 * 把栅格地图单车的结果写入文件
	 * 
	 * @param        <T>
	 * @param path
	 * @param result
	 */
	public static <T> void writeListData(String path, List<T> ls, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(path);
		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class,clas);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			ObjectWriter writer = mapper.writerFor(javaType);
			SequenceWriter sequenceWriter = writer.writeValues(file);
			sequenceWriter.write(ls);
			sequenceWriter.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * 把栅格地图单车的结果写入文件
	 * 
	 * @param        <T>
	 * @param path
	 * @param result
	 */
	public static <T> void writeMapData(String path, Map<String, T> data, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File(path);
		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, clas);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			ObjectWriter writer = mapper.writerFor(javaType);
			SequenceWriter sequenceWriter = writer.writeValues(file);
			sequenceWriter.write(data);
			sequenceWriter.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 把栅格地图单车的结果写入文件
	 * 
	 * @param        <T>
	 * @param path
	 * @param result
	 */
	public static <T> void writeMapListData(String fileName, Map data,Class keyClass, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		Path path=Paths.get(fileName);
		if(!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				System.out.println("创建文件 "+fileName+" 失败");
			}
		}
		File file = path.toFile();
		try {
			JavaType listType=mapper.getTypeFactory().constructCollectionType(List.class, clas);
			JavaType StringType=mapper.getTypeFactory().constructType(keyClass);
			JavaType javaType = mapper.getTypeFactory().constructMapLikeType(HashMap.class, StringType, listType);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			ObjectWriter writer = mapper.writerFor(javaType);
			SequenceWriter sequenceWriter = writer.writeValues(file);
			sequenceWriter.write(data);
			sequenceWriter.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * 把栅格地图单车的结果写入文件
	 * 
	 * @param        <T>
	 * @param path
	 * @param result
	 */
	public static <T> void writeIntMapMapData(String fileName, Map data) {
		ObjectMapper mapper = new ObjectMapper();
		Path path=Paths.get(fileName);
		if(!Files.exists(path)) {
			try {
				Files.createFile(path);
			} catch (IOException e) {
				System.out.println("创建文件 "+fileName+" 失败");
			}
		}
		File file = path.toFile();
		try {
			JavaType mapType=mapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, Object.class);
			JavaType intType=mapper.getTypeFactory().constructType(Integer.class);
			JavaType javaType = mapper.getTypeFactory().constructMapLikeType(HashMap.class, intType, mapType);
			mapper.configure(SerializationFeature.INDENT_OUTPUT, true);
			ObjectWriter writer = mapper.writerFor(javaType);
			SequenceWriter sequenceWriter = writer.writeValues(file);
			sequenceWriter.write(data);
			sequenceWriter.close();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public static Map<Integer, Map<String, Object>> readIntMapMapData(String fileName) {
		ObjectMapper mapper = new ObjectMapper();
		Path path = Paths.get(fileName);
		if (!Files.exists(path)) {
			return null;
		}

		try {
			JavaType mapType=mapper.getTypeFactory().constructMapLikeType(HashMap.class, String.class, Object.class);
			JavaType intType=mapper.getTypeFactory().constructType(Integer.class);
			JavaType javaType = mapper.getTypeFactory().constructMapLikeType(HashMap.class, intType, mapType);
			Map<Integer, Map<String, Object>> maps = mapper.readValue(path.toFile(), javaType);
			return maps;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
		
	}
	
	/**
	 * 把栅格地图单车的结果写入文件
	 * 
	 * @param        <T>
	 * @param path
	 * @param result
	 */
	public static <T> Map<String, List<T>> readMapListData(String file, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return null;
		}

		try {
			JavaType listType=mapper.getTypeFactory().constructCollectionType(List.class, clas);
			JavaType StringType=mapper.getTypeFactory().constructType(String.class);
			JavaType javaType = mapper.getTypeFactory().constructMapLikeType(HashMap.class, StringType, listType);
			
			Map<String, List<T>> ls = mapper.readValue(path.toFile(), javaType);
			return ls;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取生成的栅格地图单车文件
	 * 
	 * @param      <T>
	 * @param file
	 * @return
	 */
	public static <T> Map<String, T> readMapData(String file, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return null;
		}

		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(Map.class, String.class, clas);
			Map<String, T> maps = mapper.readValue(path.toFile(), javaType);
			return maps;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	/**
	 * 读取生成的栅格地图单车文件
	 * 
	 * @param      <T>
	 * @param file
	 * @return
	 */
	public static <T> List<T> readListData(String file, Class<T> clas) {
		ObjectMapper mapper = new ObjectMapper();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return null;
		}

		try {
			JavaType javaType = mapper.getTypeFactory().constructParametricType(List.class, clas);
			List<T> ls = mapper.readValue(path.toFile(), javaType);
			return ls;
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		return null;
	}

	public static void main(String[] args) {
		Map<String, List<Integer>> map=new HashMap<>();
		List<Integer> ls=new ArrayList<>();
		ls.add(123);
		ls.add(446);
		map.put("123", ls);
		
		Map<String, List<Integer>> map2=readMapListData("./mapList.txt",Integer.class);
		
		System.out.println(map2);
	}
}

package com.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.WriteLock;
import java.util.stream.Stream;

import org.apache.http.Header;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pojo.Bike;
import com.pojo.BikeHeader;
import com.pojo.BikePos;
import com.pojo.Point;

@Component
public class FilesUtil {

	public static String DEFAULT_BIKE_FILE = "/Users/daniel/projects/bikeData/";
	
	private final static ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
	private final static ObjectMapper mapper = new ObjectMapper();

	public static void main(String[] args) {
//		Date[] dates=getFileRange();
//		System.out.println(dates[0]);
//		System.out.println(dates[1]);
//		Date d1=DateUtil.pareToHour("2018_11_20 0");
//		Date d2=DateUtil.pareToHour("2018_11_23 15");
//		
//		
//		List<Path> ls=listAllFiles();
//		for(Path p:ls) {
//			System.out.println(p);
//		}

	}
	
	

	public static Date[] getFileRange() {
		Path startPath = Paths.get(DEFAULT_BIKE_FILE);

		Date[] range = new Date[2];

		try {
			Files.walkFileTree(startPath, new SimpleFileVisitor<Path>() {
				Date start = null;
				Date endDate = null;

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					Path temPath = dir.getFileName();
					if (temPath == null || temPath.equals(" ") || temPath.equals(startPath.getFileName())) {
						return FileVisitResult.CONTINUE;
					} else {
						String dirName = temPath.toString();
						Date temp = DateUtil.parseToDay(dirName);
						if (temp == null) {
							return FileVisitResult.TERMINATE;
						}
						if (start == null) {
							start = endDate = temp;
						} else {
							if (start.after(temp)) {
								start = temp;
							}
							if (endDate.before(temp)) {
								endDate = temp;
							}
						}
						range[0] = start;
						range[1] = endDate;
						return FileVisitResult.SKIP_SUBTREE;

					}

				}

			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return range;
	}

	/**
	 * 读取两个时间之间带mapHeader的文件
	 * 
	 * @param st_time
	 * @param end_time
	 * @return
	 */
	public static List<Map<String, Object>> readFilesToBikeMap(Date st_time, Date end_time,boolean check24) {
		List<Path> files = listFilesInDuration(st_time, end_time,check24);
		List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();

		for (Path f : files) {
			result.add(readFileToBikeMap(f.toString()));
		}
		result.sort(new Comparator<Map<String, Object>>() {
			@Override
			public int compare(Map<String, Object> o1, Map<String, Object> o2) {
				BikeHeader header1 = (BikeHeader) o1.get("header");
				BikeHeader header2 = (BikeHeader) o2.get("header");

				return header1.getStartTime().compareTo(header2.getStartTime());
			}
		});
		return result;
	}

	public static List<Path> listAllFiles(boolean check24) {
		Date[] dates = FilesUtil.getFileRange();
		Date enDate = dates[1];
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(enDate);
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		dates[1] = calendar.getTime();

		List<Path> files = listFilesInDuration(dates[0], dates[1],check24);
		files.sort(new Comparator<Path>() {

			@Override
			public int compare(Path o1, Path o2) {
				String p1=o1.getParent().getFileName().toString();
				String s1=o1.getFileName().toString();
				String p2=o2.getParent().getFileName().toString();
				String s2=o2.getFileName().toString();
				if(p1.equals(p2)) {
					int x1=Integer.parseInt(s1.substring(0,s1.length()-4));
					int x2=Integer.parseInt(s2.substring(0,s2.length()-4));
					return Integer.compare(x1, x2);
				}else {
					Date d1=DateUtil.parseToDay(p1);
					Date d2=DateUtil.parseToDay(p2);
					return d1.compareTo(d2);
				}
			}
		});
		return files;
	}

	public static List<BikeHeader> readFileHeaders(Date st_time, Date end_time,boolean check24) {
		List<Path> files = listFilesInDuration(st_time, end_time,check24);
		List<BikeHeader> result = new ArrayList<BikeHeader>();

		for (Path f : files) {
			result.add(readFileHeader(f.toString()));
		}
		result.sort(new Comparator<BikeHeader>() {
			@Override
			public int compare(BikeHeader o1, BikeHeader o2) {

				return o1.getStartTime().compareTo(o2.getStartTime());
			}

		});
		return result;
	}

	/**
	 * 读取单日24个小时的单车文件，带头
	 * @param date
	 * @return
	 */
	public static List<Map<String, Object>> ListFilesInDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		Date start = calendar.getTime();
		calendar.set(Calendar.HOUR_OF_DAY, 23);
		Date enDate = calendar.getTime();

		return readFilesToBikeMap(start, enDate,false);
	}

	// listFilesInDuration("2018_10_30 0", "2018_11_1 0");
	public static List<Path> listFilesInDuration(Date st_time, Date end_time,boolean check24) {
		List<Path> ls = new ArrayList<Path>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(st_time);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date begin_time = cal.getTime();

		cal.setTime(end_time);
		cal.set(Calendar.HOUR_OF_DAY, 0);
		Date stop_time = cal.getTime();

		if (st_time.compareTo(end_time) > 0) {
			return ls;
		}
		if (st_time.compareTo(end_time) == 0) {
			String singlePath = PathUtil.getFileByTime(st_time);
			ls.add(Paths.get(singlePath));
			return ls;
		}
		Path startPath = Paths.get(DEFAULT_BIKE_FILE);

		try {
			Files.walkFileTree(startPath, new FileVisitor<Path>() {
				String tempStr = null;

				@Override
				public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) throws IOException {
					tempStr = dir.getFileName().toString();
					
					if (tempStr.equals(startPath.getFileName().toString())) {
						return FileVisitResult.CONTINUE;
					} else {
						if(check24) {
							File file=new File(dir.toUri());
							if(file.list().length<24) {
								return FileVisitResult.SKIP_SUBTREE;
							}
						}
						
						if (!RegexUtil.matchDate(tempStr)) {
							return FileVisitResult.CONTINUE;
						}
						Date temp = DateUtil.parseToDay(tempStr);

						if (temp.compareTo(begin_time) >= 0 && temp.compareTo(stop_time) <= 0) {
							return FileVisitResult.CONTINUE;
						}
						
						return FileVisitResult.SKIP_SUBTREE;
					}

				}

				@Override
				public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
					String name = file.getFileName().toString();
					String str_date = file.getParent().getFileName().toString();
					str_date = str_date + " " + name.substring(0, name.length() - 4);
					if (!RegexUtil.matchHour(str_date)) {
						return FileVisitResult.CONTINUE;
					}
					Date temp = DateUtil.pareToHour(str_date);
					if (temp.compareTo(st_time) >= 0 && temp.compareTo(end_time) <= 0) {
						ls.add(file);
					}
					return FileVisitResult.CONTINUE;

				}

				@Override
				public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
					return FileVisitResult.TERMINATE;
				}

				@Override
				public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {

					return FileVisitResult.CONTINUE;
				}
			});
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ls;
	}

	public static String checkLastestFile() {

		File file = new File(DEFAULT_BIKE_FILE);
		File[] fls = file.listFiles();

		Date near = null;
		File latest = null;
		String string = null;
		Date temp = null;
		if (fls.length > 0) {
			latest = fls[0];
			near = DateUtil.parseToDay(fls[0].getName());

			for (File f : fls) {
				string = f.getName();
				if (RegexUtil.matchDate(string)) {
					temp = DateUtil.parseToDay(f.getName());
				}

				if (temp == null) {
					continue;
				}
				if (temp.after(near)) {
					near = temp;
					latest = f;
				}
			}
			String name = null;
			int small = 0;
			if (latest.isDirectory()) {
				File[] txtFiles = latest.listFiles();
				for (File f : txtFiles) {
					name = f.getName();
					int t = Integer.parseInt(name.split("\\.")[0]);
					if (t > small) {
						small = t;
						latest = f;
					}
				}
			}
			return latest.getAbsolutePath();
		}
		return null;

	}

	/**
	 * 读取文件，不要头，只取单车数据
	 * 
	 * @param file
	 * @return 返回单车的列表
	 */
	public static Map<String, Object> readFileToBikeList(String file) {
		Map<String, Object> mp = new HashMap<String, Object>();
		List<Bike> bikes = new ArrayList<>();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return mp;
		}
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path);
			String strHeader = reader.readLine();
			BikeHeader header = mapper.readValue(strHeader, BikeHeader.class);

			mp.put("header", header);
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				Bike bike = mapper.readValue(temp, Bike.class);

				bikes.add(bike);
			}
			mp.put("bikes", bikes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mp;
	}

	/**
	 * 读取文件，要头
	 * 
	 * @param file
	 * @return 返回键值为单车ID的map
	 */
	public static Map<String, Object> readFileToBikeMap(String file) {
		Map<String, Object> mp = new HashMap<String, Object>();
		List<BikePos> bikes = new ArrayList<BikePos>();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return mp;
		}
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path);
			String strHeader = reader.readLine();
			BikeHeader header = mapper.readValue(strHeader, BikeHeader.class);

			mp.put("header", header);
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				Bike bike = mapper.readValue(temp, Bike.class);
				BikePos pos = new BikePos(bike.getDistId(), Double.parseDouble(bike.getDistX()),
						Double.parseDouble(bike.getDistY()), 20);

				bikes.add(pos);
			}
			mp.put("bikes", bikes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mp;
	}

	public static BikeHeader readFileHeader(String file) {
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return null;
		}
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path);
			String strHeader = reader.readLine();
			BikeHeader header = mapper.readValue(strHeader, BikeHeader.class);

			return header;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static Map<String, Object> readFileToPoint(String file) {
		Map<String, Object> mp = new HashMap<String, Object>();
		List<Point> bikes = new ArrayList<Point>();

		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return mp;
		}
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path);
			String strHeader = reader.readLine();
			BikeHeader header = mapper.readValue(strHeader, BikeHeader.class);

			mp.put("header", header);
			String temp = null;
			while ((temp = reader.readLine()) != null) {
				Bike bike = mapper.readValue(temp, Bike.class);

				Point p = new Point(
						new double[] { Double.parseDouble(bike.getDistX()), Double.parseDouble(bike.getDistY()) },
						bike.getDistId(), 1);

				bikes.add(p);
			}
			mp.put("bikes", bikes);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mp;
	}

	public static Map<String, Object> readFileInfo(String file) {
		Map<String, Object> mp = new HashMap<String, Object>();
		List<BikePos> list = new ArrayList<BikePos>();
		Path path = Paths.get(file);
		if (!Files.exists(path)) {
			return mp;
		}
		BufferedReader reader = null;
		try {
			reader = Files.newBufferedReader(path);
			String strHeader = reader.readLine();
			BikeHeader header = mapper.readValue(strHeader, BikeHeader.class);

			mp.put("header", header);

			String temp = null;
			while ((temp = reader.readLine()) != null) {
				Bike bike = mapper.readValue(temp, Bike.class);
				BikePos pos = new BikePos(bike.getDistId(), Double.parseDouble(bike.getDistX()),
						Double.parseDouble(bike.getDistY()), 20);
				list.add(pos);
			}
			mp.put("bikes", list);
		} catch (IOException e1) {
			e1.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return mp;
	}

	public static <T> void writeListToFile(String fileName, List<T> ls, Class<T> clas) {
		Path p = Paths.get(fileName);
		BufferedWriter writer = null;
		try {
			if (!Files.exists(p)) {
				Files.createFile(p);

			}

			writer = Files.newBufferedWriter(p, Charset.forName("utf-8"), StandardOpenOption.APPEND);

			for (T t : ls) {
				writer.write(mapper.writeValueAsString(t));
				writer.write('\n');
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static <T> void writeObjectToFile(String fileName, T t, Class<T> clas) {
		Path p = Paths.get(fileName);
		BufferedWriter writer = null;
		try {
			if (!Files.exists(p)) {
				Files.createFile(p);

			}

			writer = Files.newBufferedWriter(p, Charset.forName("utf-8"), StandardOpenOption.APPEND);

			writer.write(mapper.writeValueAsString(t));
			writer.write('\n');
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static <T> List<T> readFromFile(String fileName, Class<T> clas) {
		Path p = Paths.get(fileName);
		List<T> ls = new ArrayList<T>();
		BufferedReader reader = null;
		try {
			if (!Files.exists(p)) {
				return ls;

			}
			reader = Files.newBufferedReader(p);

			String temp = null;
			while ((temp = reader.readLine()) != null) {
				T t = mapper.readValue(temp, clas);
				ls.add(t);
			}
			reader.close();
			return ls;
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
		}
		return null;
	}

	public static String writeBikeToFile(BikeHeader header, Map<String, Bike> bikes) {
		Path todayDirec = createDirec();
		Path bikePath = createNowFile(todayDirec.toString());
		BufferedWriter writer = null;
		try {
			writer = Files.newBufferedWriter(bikePath, Charset.forName("utf-8"), StandardOpenOption.APPEND);
			WriteLock writeLock = lock.writeLock();
			writeLock.lock();
			try {
				writer.write(mapper.writeValueAsString(header));
				writer.write("\n");
				Set<String> names = bikes.keySet();
				Iterator<String> iter = names.iterator();
				while (iter.hasNext()) {
					writer.write(mapper.writeValueAsString(bikes.get(iter.next())));
					writer.write("\n");
				}
			} finally {
				writeLock.unlock();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return bikePath.toString();
	}

	public static Path createNowFile(String direcName) {
		if (!direcName.endsWith("/")) {
			direcName = direcName + "/";
		}
		Calendar cal = Calendar.getInstance();
		Path filePath = null;
		int hour = cal.get(Calendar.HOUR_OF_DAY);
		String dateFile = direcName + hour + ".txt";

		filePath = Paths.get(dateFile);

		if (!Files.exists(filePath)) {
			try {
				Files.createFile(filePath);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filePath;

	}

	public static Path createDirec(String... DirecName) {
		Path datePath = null;
		Calendar cal = Calendar.getInstance();
		if (DirecName.length == 0) {
			int year = cal.get(Calendar.YEAR);
			int month = cal.get(Calendar.MONTH) + 1;
			int day = cal.get(Calendar.DATE);
			String dateFile = DEFAULT_BIKE_FILE + year + "_" + month + "_" + day + "/";
			datePath = Paths.get(dateFile);
		} else {
			datePath = Paths.get(DirecName[0]);
		}
		Path parent = Paths.get(DEFAULT_BIKE_FILE);
		if (!Files.exists(parent)) {
			try {
				Files.createDirectory(parent);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (!Files.exists(datePath)) {
			try {
				Files.createDirectory(datePath);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return datePath;

	}
}

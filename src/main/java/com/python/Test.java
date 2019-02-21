package com.python;
 
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
 
public class Test {
 
	public static void main(String[] args) throws IOException, InterruptedException {
		System.out.println("start python");
		//需传入的参数
		String a = "aaa", b = "bbb", c = "cccc", d = "dddd";
		//设置命令行传入的参数
//		String[] arg = new String[]{"python", "pyfile/test2.py", a, b, c, d};
		String[] arg = new String[]{"python", "/Users/daniel/softs/pythonWorks/analyze/write.py"};
		Process pr = Runtime.getRuntime().exec(arg);
		
		BufferedReader in = new BufferedReader(new InputStreamReader(pr.getErrorStream()));  
        String line;  
        
        while ((line = in.readLine()) != null) { 
            System.out.println(line);  
        }  
        in.close();  
		System.out.println("end");
		pr.waitFor();
	}
}

package com.td;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

/*
 * a simple static http server
*/
public class Main {

	private static String pathToServer;
	
	public static void main(String[] args) throws Exception {
		pathToServer = System.getProperty("user.dir");
		HttpServer server = HttpServer.create(new InetSocketAddress(8000), 0);
		server.createContext("/", new MyHandler());
		server.setExecutor(null); // creates a default executor
		server.start();
	}

	static class MyHandler implements HttpHandler {
		public void handle(HttpExchange t) throws IOException {
			URI uri = t.getRequestURI();
			
			Path pathToFile = Paths.get(pathToServer + uri);
			String extension = Files.probeContentType(pathToFile);
			System.out.println("File type: " + extension + "\n");
			System.out.println("Path: " + pathToFile + "\n");
    	    
			Headers h = t.getResponseHeaders();
			h.add("Content-Type", extension);
			
			File file = new File(pathToFile.toString());
			byte [] bytearray = new byte [(int) file.length()];
			FileInputStream fis = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(bytearray, 0, bytearray.length);
			
			t.sendResponseHeaders(200, file.length());
			OutputStream os = t.getResponseBody();
			os.write(bytearray, 0, bytearray.length);
			os.close();
			
			fis.close();
			bis.close();
	    }
	    
	    static String readFile(String path, Charset encoding) throws IOException 
	    {
	    	byte[] encoded = Files.readAllBytes(Paths.get(path));
	    	return new String(encoded, encoding);
	    }
	}
}
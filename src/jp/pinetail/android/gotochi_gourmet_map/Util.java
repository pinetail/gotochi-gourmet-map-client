package jp.pinetail.android.gotochi_gourmet_map;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.LineNumberReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

public class Util {
	private static final String PAY_APP_PACKAGE_NAME = "com.mogya.oasis.android.best";
	private static final String DEVIDED_FILE_PREFIX = "xa";
	private static final int DEFAULT_BUFFER_SIZE = 8192;
	private static final int FILE_DIVIDED_NUM = 4;
	public static boolean isPaymentApp(Context context) {
		String packageName = context.getPackageName();
		return PAY_APP_PACKAGE_NAME.equals(packageName);
	}
	
	public static void logging(String msg) {
		Log.i("oasis", msg);
	}
	
    public static int getRowNum(String fname) throws IOException {
        LineNumberReader fin = null;
		try {
			fin = new LineNumberReader(new FileReader(fname));
	        String aLine;
	        
	        while(null != (aLine = fin.readLine())) {
	            ;
	        }
	        return fin.getLineNumber();
		} catch (FileNotFoundException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		} finally {
			if (fin instanceof LineNumberReader) {
				fin.close();
			}
		}
		
		return 0;

    }
    
    public static List<String> unzip(File dir, String filepath) {
    	BufferedInputStream bin = null;
    	BufferedOutputStream bout = null;
    	List<String> files = new ArrayList<String>();
    	
    	try {
    		Util.logging(filepath);
    		ZipFile zip_file = new ZipFile(filepath);
    		Enumeration<? extends ZipEntry> entries = zip_file.entries();
    		
    		while (entries.hasMoreElements()) {
    			ZipEntry entry = entries.nextElement();
    			
    			files.add(entry.getName());
    			File ofile = new File(dir, entry.getName());
    			
    			if (entry.isDirectory()) {
    				ofile.mkdir();
    			} else {
    				InputStream in;
    				in = zip_file.getInputStream(entry);
    				bin = new BufferedInputStream(in);
    				
    				// 出力
    				bout = new BufferedOutputStream(new FileOutputStream(ofile));
    				
    				int bytedata = 0;
    				while((bytedata = bin.read()) != -1) {
    					bout.write(bytedata);
    				}
    				bout.flush();
    			}
    		}
    	} catch (ZipException e) {
    		e.printStackTrace();
    	} catch (IOException e) {
    		e.printStackTrace();
    	} finally {
    		try {
    			if (bin != null) {
    				bin.close();
    			}
    			
    			if (bout != null) {
    				bout.close();
    			}
    		} catch (IOException e) {}
    	}
    	
    	return files;    	
    }
    
}

package com.github.devholic.SOMAReport.Utilities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.json.JSONObject;

import com.github.devholic.SOMAReport.Controller.DatabaseController;
import com.github.devholic.SOMAReport.Controller.SearchController;

public class FileFactory {
	
	private final Logger Log = Logger.getLogger(SearchController.class);
	static DatabaseController db = new DatabaseController();
	
	/*************************************************
	 * InputStream을 File로 변환해주는 메소드
	 * 
	 * @param InputStream
	 *            in
	 * @return File file
	 * @throws IOException
	 *************************************************/
	public static File stream2file(InputStream in) throws IOException {
		final File tempFile = File.createTempFile("stream2file", ".tmp");
		tempFile.deleteOnExit();
		try {
			FileOutputStream fo = new FileOutputStream(tempFile);
			IOUtils.copy(in, fo);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return tempFile;
	}
	
	
	/*********************************************
	 * 자동으로 캐시 폴더를 삭제하게 한다.
	 ********************************************/
	public void autoDelete(){
	
		//1시간마다 삭제하도록
		int deleteSecond = 3600;
		final SimpleDateFormat fmt = new SimpleDateFormat("HH:mm:ss");
		final ScheduledThreadPoolExecutor exec = new ScheduledThreadPoolExecutor(1);
		
		exec.scheduleAtFixedRate(new Runnable(){
			
			public void run(){
				try{
					
					Calendar cal = Calendar.getInstance() ;
					System.out.println(fmt.format(cal.getTime())) ;
					
					File cachePath = new File("cache/");
					
					deleteCache(cachePath);
					
				}catch(Exception e){
					Log.error(e.getMessage());
					exec.shutdown();
				}
			}
		},0,deleteSecond,TimeUnit.SECONDS);
		
	}
	
	/**********************************************
	 * 해당 Path의 파일과 디렉토리를 삭제한다.
	 * @param path
	 * @return
	 *********************************************/
	public boolean deleteCache(File path){

		//캐시 디렉토리 삭제
		if(!path.exists()) {
            return false;
        }
		
		//디렉토리일 경우 바로 삭제하고, 파일이 있을 경우에 내부의 
		//파일을 모두 지운후에 디렉토리를 지운다.
		File[] files = path.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
            	deleteCache(file);
            } else {
            	//couchDB에 cached_at을 0으로 초기화시킨다.
            	JSONObject fileDoc = JSONFactory.inputStreamToJson(db.getDoc(file.getName()));
            	
            	if (fileDoc.has("cached_at")) {
            		fileDoc.put("cached_at", 0);
            		db.updateDoc(fileDoc);
            	}
            	
                file.delete();
            }
        }

		return path.delete();
	}
}

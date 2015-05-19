package org.ednovo.gooru.domain.service.resource;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.stereotype.Service;

@Service
public class CSVBuilderServiceImpl implements CSVBuilderService {

	/*@Resource(name = "filePath")
	private Properties filePath;
	*/
	// /instance download so used exact link
	public File generateCSVReport(List<Map<String, Object>> resultSet,
			String fileName) throws ParseException, IOException {

		boolean headerColumns = false;

		// Set output File
		File csvfile = new File(setFilePath(fileName));
		@SuppressWarnings("resource")
		PrintStream stream = new PrintStream(csvfile);

		// print header row

		// print row values
		for (Map<String, Object> map : resultSet) {

			if (!headerColumns) {
				for (Map.Entry<String, Object> entry : map.entrySet()) {
					stream.print(entry.getKey() + "|");
					headerColumns = true;
				}
				// print new line
				stream.println("");
			}
			for (Map.Entry<String, Object> entry : map.entrySet()) {
				stream.print(entry.getValue() + "|");
			}
			// print new line
			stream.println("");
		}

		return csvfile;
	}
	
	public String generateCSVMapReport(List<Map<String,Object>> resultSet,String fileName)throws ParseException, IOException{
		
		// Set output File
		File csvfile = new File(setFilePath(fileName));
		@SuppressWarnings("resource")
		PrintStream stream = new PrintStream(csvfile);
		
		//print row values
		ObjectMapper objectMapper = new ObjectMapper(); 
		
		for (Map<String, Object> map : resultSet) {
			stream.print(objectMapper.writeValueAsString(map) + "|");
			stream.println("");
		}
		
		return getFilePath(fileName);
	}

	/*public Properties getFilePath() {
		return filePath;
	}

	public void setFilePath(Properties filePath) {
		this.filePath = filePath;
	}*/
	
	public String setFilePath(String file){
		
		String fileName = "/home/daniel/";
		
		if(file != null && (!file.isEmpty())){
			fileName += file;
		
		}else{
			fileName +="insights";
		}
		System.out.print("fileName:"+fileName);
		return fileName;
	}

	public String getFilePath(String file){
		
		String fileName = "/home/daniel/";
		
		if(file != null && (!file.isEmpty())){
			fileName += file;
		
		}else{
			fileName +="report";
		}
		return fileName;
	}

}

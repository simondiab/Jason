package jason;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import jason.model.RawEvent;


public class JSONLoader {
	
	private static final Logger logger = LoggerFactory.getLogger(JSONLoader.class);

	public static void main(String[] args) {
		logger.info("JSONLoader started");

//		ReadWithScanner parser = new ReadWithScanner("C:\\Temp\\test.txt");
//	    parser.processLineByLine();
		try {
			String fileName = args[0];
			readTextFile(fileName);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
	static void readTextFile(String fileName) throws IOException, ClassNotFoundException {
		JSONDbUtils.initDB();
		
		Path path = Paths.get(fileName);
		try (Scanner scanner = new Scanner(path, StandardCharsets.UTF_8)) {
			while (scanner.hasNextLine()) {
				String nextLine = scanner.nextLine();
				logger.info(nextLine);
				// process each line in some way
				loadBalancer(nextLine);
			}
		}
	}
	
	static void loadBalancer(String line) throws JsonParseException, JsonMappingException, IOException {
		ObjectMapper mapper = new ObjectMapper();
		RawEvent event = mapper.readValue(line, RawEvent.class);
		JSONProcessor.processJsonEvent(event);
	}

}

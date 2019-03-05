package jason;

import java.io.IOException;

import org.junit.Test;

public class JSONLoaderTest {

	@Test
	public void test() throws IOException, ClassNotFoundException {
		JSONLoader.readTextFile("smallDataSet.txt");
	}

}

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class readConfigs {
	//this readConfigs read in and store the data of the config file
	Double r;
	Double t;
	Double l;
	Double m;
	Double a;
	Double f;

	public readConfigs(String configPath){
		//use bufferedReader to read in and store data
		BufferedReader br = null;
		List<Double> list = new ArrayList<Double>();
		try {
			String configLine;
			br = new BufferedReader(new FileReader(configPath));
			while ((configLine = br.readLine()) != null) {
				String [] s = configLine.split("\\s+");
				list.add(Double.valueOf(s[2]));
			}
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			try {
				if (br != null)
					br.close();
			}
			catch (IOException ex){
				ex.printStackTrace();
			}
		}
		r = list.get(0);
		t = list.get(1);
		l = list.get(2);
		m = list.get(3);
		a = list.get(4);
		f = list.get(5);
	}
}

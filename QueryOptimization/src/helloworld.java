import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class helloworld {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World!");
		System.out.println("Hello Again World!");
		
		BufferedReader br = null;
		try {
			String configLine;
			br = new BufferedReader(new FileReader("./config.txt"));
			while ((configLine = br.readLine()) != null) {
				System.out.println(configLine);
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
		
		List <queryNode> queryList = new ArrayList<queryNode>();
		br = null;
		try {
			String queryLine;
			br = new BufferedReader(new FileReader("./query.txt"));
			while ((queryLine = br.readLine()) != null){
				String [] parsing = queryLine.split("\\s+");
				int i = 0;
				queryNode node = new queryNode();
				for (i = 0; i < parsing.length;i++){
					node.conditions.add(Double.valueOf(parsing[i]));
				}
				queryList.add(node);
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
		for (queryNode node : queryList){
			System.out.println(node.conditions);
			System.out.println("hahaha");
		}
	}

}

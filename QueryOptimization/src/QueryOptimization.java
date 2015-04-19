import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class QueryOptimization {
	
	public static List<List<basicTerm>> powerSet(List<basicTerm> mylist) {
		List<List<basicTerm>> ps = new ArrayList<List<basicTerm>>();
		ps.add(new ArrayList<basicTerm>());
		
		for (basicTerm item : mylist) {
			List<List<basicTerm>> newPs = new ArrayList<List<basicTerm>>();
			for (List<basicTerm> subset : ps) {
				newPs.add(subset);
				
				List<basicTerm> newSubset = new ArrayList<basicTerm>(subset);
				newSubset.add(item);
				newPs.add(newSubset);
			}
			ps = newPs;
		}
		ps.remove(0);
		return ps;
	}
	
	public static boolean intersection(List<basicTerm> a, List<basicTerm> b){
		for (int i = 0; i < a.size(); i++){
			for (int j = 0; j < b.size(); j++)
				if (a.get(i).selectivity == b.get(j).selectivity)
					return true;
		}
		return false;
	}
	
	public static void printPowerSet(List<planNode> mylist){
		for (planNode p : mylist){
			System.out.print("[");
			for (basicTerm term : p.subset)
				System.out.print(term.selectivity + ", ");
			System.out.print("] best cost: " + p.c + "\n" );
		}
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Hello World!");
		System.out.println("Hello Again World!");
		
		readConfigs config = new readConfigs();
		System.out.println(config.r);
		System.out.println(config.t);
		System.out.println(config.l);
		System.out.println(config.m);
		System.out.println(config.a);
		System.out.println(config.f);
		
		BufferedReader br = null;
		
		List <queryNode> queryList = new ArrayList<queryNode>();
		try {
			String queryLine;
			br = new BufferedReader(new FileReader("./query.txt"));
			while ((queryLine = br.readLine()) != null){
				String [] parsing = queryLine.split("\\s+");
				int i = 0;
				queryNode node = new queryNode();
				for (i = 0; i < parsing.length;i++){
					basicTerm term = new basicTerm(Double.valueOf(parsing[i]));
					node.conditions.add(term);
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
			for (basicTerm term : node.conditions)
				System.out.print(term.selectivity + ", ");
			System.out.println("hahaha");
		}
		List<List<basicTerm>> myps = powerSet(queryList.get(0).conditions);
		List<planNode> logicalAnd = new ArrayList<planNode>();
		for (List<basicTerm> subset : myps){
			//for (basicTerm term : subset)
				//System.out.print(term.selectivity + ", ");
			planNode newPlanNode = new planNode(subset,config);
			logicalAnd.add(newPlanNode);
			System.out.println("");
		}
		printPowerSet(logicalAnd);
		/*
		for (int i = 0; i < logicalAnd.size(); i++){
			for (int j = 0; j < logicalAnd.size(); j++){
				if (intersection(logicalAnd.get(i).subset,logicalAnd.get(j).subset) == false){
					
				}
			}
		}
		*/
	}

}

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
		
		String config_path = "config.txt";
		String query_path = "query.txt";
		
		readConfigs config = new readConfigs(config_path);
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
			br = new BufferedReader(new FileReader(query_path));
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
		List<List<basicTerm>> myps = powerSet(queryList.get(2).conditions);
		List<planNode> logicalAnd = new ArrayList<planNode>();
		for (List<basicTerm> subset : myps){
			//for (basicTerm term : subset)
				//System.out.print(term.selectivity + ", ");
			planNode newPlanNode = new planNode(subset,config);
			logicalAnd.add(newPlanNode);
			System.out.println("");
		}
		printPowerSet(logicalAnd);
		
		for (int i = 0; i < logicalAnd.size(); i++){
			for (int j = 0; j < logicalAnd.size(); j++){
				if (intersection(logicalAnd.get(i).subset,logicalAnd.get(j).subset) == false){
					//c-metric of s_prime, Left leaf
					c_metric sPrime_c_metric = new c_metric(logicalAnd.get(j));
					//c_metric of leftmost of s, Right leaf
					List<basicTerm> temp = new ArrayList<basicTerm>();
					temp.add(logicalAnd.get(i).subset.get(0));
					planNode leftmost = new planNode(temp,config);
					c_metric s_c_metric = new c_metric(leftmost);
					//d-metric of s_prime, left leaf
					d_metric sPrime_d_metric = new d_metric(logicalAnd.get(j));
					//set of all terms in s except the leftmost one, right leaf
					temp = new ArrayList<basicTerm>();
					for (int z = 1; z < logicalAnd.get(i).subset.size(); z++)
						temp.add(logicalAnd.get(i).subset.get(z));
					
					//check if d-metric of s_prime is dominated by d-metric of some element in s(except leftmost)
					boolean d_metric_domination = false;
					for (int t = 0; t < temp.size(); t++){
						List<basicTerm> some_term = new ArrayList<basicTerm>();
						some_term.add(temp.get(t));
						planNode node = new planNode(some_term,config);
						d_metric some_d_metric = new d_metric(node);
						if (sPrime_d_metric.fcost < some_d_metric.fcost && sPrime_d_metric.combined_selectivity < some_d_metric.combined_selectivity)
							d_metric_domination = true;
					}
					//1st if condition
					if (sPrime_c_metric.cost_ratio < s_c_metric.cost_ratio && sPrime_c_metric.combined_selectivity < s_c_metric.combined_selectivity){
						//do nothing, as Lemma 4.8 implies
					}
					//2nd else-if
					else if (logicalAnd.get(j).combined_selectivity <= 0.5 && d_metric_domination == true){
						//do nothing, as Lemma 4.9 implies
					}
					else {
						//calculate the cost c for the combined plan(s'&&s) using Equation 1
						Double C = logicalAnd.get(i).c;
						Double c = logicalAnd.get(j).fcost + config.m * Math.min((double)(logicalAnd.get(j).combined_selectivity), 1-logicalAnd.get(j).combined_selectivity) + logicalAnd.get(j).combined_selectivity * C ;
						List<basicTerm> union_plan = new ArrayList<basicTerm>();
						union_plan.addAll(logicalAnd.get(j).subset);
						union_plan.addAll(logicalAnd.get(i).subset);
						planNode union = new planNode(union_plan,config);
						if (c < union.c){
							union.c = c;
							union.L = logicalAnd.get(j);
							union.R = logicalAnd.get(i);
							/*
							for (planNode pn : logicalAnd){
								if (pn.subset.containsAll(union.subset) && union.subset.containsAll(pn.subset)){
									logicalAnd.remove(pn);
									logicalAnd.add(union);
								}
							}
							*/
						}
					}
				}
			}
		}
		System.out.println("--------------------------------------------------------");
		printPowerSet(logicalAnd);
	}

}

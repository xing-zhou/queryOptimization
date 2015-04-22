import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
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
			for (basicTerm term : p.set)
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
		List<List<basicTerm>> myps = powerSet(queryList.get(1).conditions);
		List<planNode> logicalAnd = new ArrayList<planNode>();
		for (List<basicTerm> subset : myps){
			//for (basicTerm term : subset)
				//System.out.print(term.selectivity + ", ");
			planNode newPlanNode = new planNode(subset,config);
			logicalAnd.add(newPlanNode);
			//System.out.println("");
		}
		System.out.println("");
		printPowerSet(logicalAnd);
		
		HashMap<List<basicTerm>,planNode> A = new HashMap<List<basicTerm>,planNode>();
		for (List<basicTerm> subset : myps){
			planNode newPlanNode = new planNode(subset,config);
			A.put(subset, newPlanNode);
		}
		
		
		
		
		for (int i = 0; i < logicalAnd.size(); i++){
			for (int j = 0; j < logicalAnd.size(); j++){
				if (intersection(logicalAnd.get(i).set,logicalAnd.get(j).set) == false){
					//c-metric of s_prime, Left leaf
					c_metric sPrime_c_metric = new c_metric(logicalAnd.get(j));
					//d-metric of s_prime, left leaf
					d_metric sPrime_d_metric = new d_metric(logicalAnd.get(j));
					
					//c_metric of leftmost of s, Right leaf
					c_metric s_c_metric = new c_metric();
					d_metric s_d_metric = new d_metric();
					//if the left leaf of s is null, that means the leftmost of s is s itself, the right part is empty
					if (logicalAnd.get(i).L == null){
						s_c_metric = new c_metric(logicalAnd.get(i));
					}//otherwise, calculate the L of s, which is the leftmost of s, and also calculate the right part R of s
					else {
						s_c_metric = new c_metric(logicalAnd.get(i).L);
						s_d_metric = new d_metric(logicalAnd.get(i).R);
					}

					
					//check if d-metric of s_prime is dominated by d-metric of some element in R part of s
					//collect all the possible right children of s, put them in a temp list, and compare each one of them with s' on d_metric
					boolean d_metric_domination = false;
					List<planNode>temp = new ArrayList<planNode>();
					planNode Right = logicalAnd.get(i).R;
					while (Right != null){
						temp.add(Right);
						if (Right.L != null && Right.R != null){
							temp.add(Right.L);
							Right = Right.R;
						}
						else if (Right.L == null && Right.R == null)
							Right = null;
					}
					for (int t = 0; t < temp.size(); t++){
						d_metric some_d_metric = new d_metric(temp.get(t));
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
						//Equation 1: if (E&&E1){blahblah}, cost = fcost(E) +mq + pC, where p is the overall combined selectivity of E, q = min(p,1-p), C is the cost of plan P2: if(E1) {blahblah}
						//here E is s', E1 is s
						Double c = logicalAnd.get(j).fcost + config.m * Math.min((double)(logicalAnd.get(j).combined_selectivity), 1-logicalAnd.get(j).combined_selectivity) + logicalAnd.get(j).combined_selectivity * C ;
						List<basicTerm> union_plan = new ArrayList<basicTerm>();//union of s' and s
						union_plan.addAll(logicalAnd.get(j).set);
						union_plan.addAll(logicalAnd.get(i).set);
						List<basicTerm> targetKey = new ArrayList<basicTerm>();
						for (List<basicTerm> key: A.keySet()){
							if (key.containsAll(union_plan) && union_plan.containsAll(key))
								targetKey = key;
						}//A.get(targetKey) is so-called A[s' union s]
						if (c < A.get(targetKey).c){
							A.get(targetKey).c = c;
							A.get(targetKey).L = logicalAnd.get(j);
							A.get(targetKey).R = logicalAnd.get(i);
						}
					}
				}
			}
		}
		
		System.out.println("--------------------------------------------------------");
		List<basicTerm> target = new ArrayList<basicTerm>();
		for (List<basicTerm> key : A.keySet()) {
			if (key.size() > target.size())
				target = key;
		}
		for (int i = 0; i < target.size(); i++) {
			System.out.print(target.get(i).selectivity);
		}
		System.out.println("\n" + A.get(target).c);
	}

}

import java.text.DecimalFormat;
import java.util.List;


public class planNode {
	List<basicTerm> subset;//the &-term in this plan
	Double c;//cost of this plan (&-term)
	Boolean b;//boolean for no-branch option
	Double k;//size of the subset
	readConfigs config;//configuration file components
	Double fcost;//fcost(E)
	Double cost_ratio;//(p-1)/fcost(E)
	Double combined_selectivity;
	public planNode(List<basicTerm> newPlan, readConfigs con){
		subset = newPlan;
		k = (double)subset.size();
		config = con;
		combined_selectivity = 1.0;
		for (basicTerm term : subset){
			combined_selectivity *= term.selectivity;
		}
		fcost = compute_fcost();
		cost_ratio = compute_cost_ratio();
		b = false;
		c = computeCost();
		Double no_branch = computeNoBranchCost();
		if (no_branch < c){
			c = no_branch;
			b = true;
		}
		System.out.print("[");
		for (basicTerm t : subset)
			System.out.print(t.selectivity + ", ");
		System.out.println("]\nfinal Cost: " + c);
		System.out.println("k = " + k);
		System.out.println("b = " + b);
	}
	public Double computeCost(){
		Double q = 1.0;
		for (basicTerm term : subset){
			q *= term.selectivity;
		}
		Double p_product = q; //p1*p2*p3...*pk
		if (q > 0.5)
			q = 1.0-q;
		Double totalCost = k*config.r + (k-1)*config.l + config.f*k + config.t + config.m * q + p_product*config.a; //Cost Function from Example 5
		totalCost = Math.round(totalCost * 1000.000)/1000.000;
		System.out.println("totalCost: " + totalCost + " with q = " + q);
		return totalCost;
	}
	
	public Double computeNoBranchCost(){
		Double no_branch_cost = k*config.r + (k-1)*config.l + config.f*k + config.a;
		no_branch_cost = Math.round(no_branch_cost * 1000.000)/1000.000;
		System.out.println("no_branch Cost: " + no_branch_cost);
		return no_branch_cost;
	}
	
	public Double compute_fcost(){
		Double _fcost = config.r * k + (k-1) * config.l + config.f*k + config.t;
		return _fcost;
	}
	
	public Double compute_cost_ratio(){
		Double cost_ratio = (combined_selectivity-1)/fcost;
		return cost_ratio;
	}
}

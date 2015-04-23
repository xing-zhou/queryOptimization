import java.text.DecimalFormat;
import java.util.List;

//fundamental structure of a plan, containing all the information about a plan (&-term)
public class planNode {
	List<basicTerm> set;//the &-term in this plan
	Double c;//cost of this plan (&-term)
	Boolean b;//boolean for no-branch option
	Double k;//size of the subset
	readConfigs config;//configuration file components
	Double fcost;//fcost(E)
	Double cost_ratio;//(p-1)/fcost(E)
	Double combined_selectivity;//combined selectivity of the plan
	planNode L;//left child
	planNode R;//right child
	public planNode(List<basicTerm> subset, readConfigs con){
		k = (double)subset.size();
		config = con;
		combined_selectivity = 1.0;
		for (basicTerm term : subset){
			combined_selectivity *= term.selectivity;
		}//calculate combined_selectivity
		fcost = compute_fcost();//compute the fcost
		cost_ratio = compute_cost_ratio();//compute (p-1)/fcost(E)
		b = false;//initialized to false
		c = computeCost(subset);//total cost of this plan
		Double no_branch = computeNoBranchCost();//compute the no-branch cost of this plan
		if (no_branch < c){
			c = no_branch;
			b = true;
		}//update cost if no-branch has lower cost, and change the boolean flag
		
		set = subset;
		L = null;//initialize L and R to be null
		R = null;
	}
	//compute the total cost of plan in branch
	public Double computeCost(List<basicTerm> s){
		Double q = 1.0;
		for (basicTerm term : s){
			q *= term.selectivity;
		}
		Double p_product = q; //p1*p2*p3...*pk
		if (q > 0.5)
			q = 1.0-q;
		Double totalCost = k*config.r + (k-1)*config.l + config.f*k + config.t + config.m * q + p_product*config.a; //Cost Function from Example 5
		totalCost = Math.round(totalCost * 1000.000)/1000.000;
		return totalCost;
	}
	//compute the total cost of plan in no-branch
	public Double computeNoBranchCost(){
		Double no_branch_cost = k*config.r + (k-1)*config.l + config.f*k + config.a;//cost function from no-branch cost 
		no_branch_cost = Math.round(no_branch_cost * 1000.000)/1000.000;
		
		return no_branch_cost;
	}
	
	//compute fcost of the plan (&-term)
	public Double compute_fcost(){
		Double _fcost = config.r * k + (k-1) * config.l + config.f*k + config.t;
		return _fcost;
	}
	//compute (p-1)/fcost(E)
	public Double compute_cost_ratio(){
		Double cost_ratio = (combined_selectivity-1)/fcost;
		return cost_ratio;
	}
}

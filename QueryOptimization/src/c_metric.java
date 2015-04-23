public class c_metric {
	//the c-metric of a plan (&-term)
	Double cost_ratio;
	Double combined_selectivity;
	public c_metric(planNode E){
		this.cost_ratio = E.cost_ratio;
		this.combined_selectivity = E.combined_selectivity;
	}
	public c_metric(){
		this.cost_ratio = 0.0;
		this.combined_selectivity = 0.0;
	}
}

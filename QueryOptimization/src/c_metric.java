public class c_metric {
	Double cost_ratio;
	Double combined_selectivity;
	public c_metric(planNode E){
		this.cost_ratio = E.cost_ratio;
		this.combined_selectivity = E.combined_selectivity;
	}
}

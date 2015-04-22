
public class d_metric {
	Double fcost;
	Double combined_selectivity;
	public d_metric(planNode E){
		this.fcost = E.fcost;
		this.combined_selectivity = E.combined_selectivity;
	}
	public d_metric(){
		this.fcost = 0.0;
		this.combined_selectivity = 0.0;
	}
}

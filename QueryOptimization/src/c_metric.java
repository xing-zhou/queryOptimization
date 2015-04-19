public class c_metric {
	planNode E;
	Double cost_ratio;
	Double p;
	public c_metric(planNode _E){
		E = _E;
		p = 1.0;
		for (basicTerm term : E.subset){
			p *= term.selectivity;
		}//calculate the overall selectivity of &-term E
		cost_ratio = (p-1)/E.fcost;
	}
}

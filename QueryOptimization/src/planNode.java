import java.util.List;


public class planNode {
	List<basicTerm> subset;
	Double c;
	Boolean b;
	public planNode(List<basicTerm> newPlan){
		subset = newPlan;
		c = 0.0;
		b = false;
	}
	//public Double computeCost(){
		//int k = subset.size();
		
	//}
}

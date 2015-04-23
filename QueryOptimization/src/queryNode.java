import java.util.ArrayList;
import java.util.List;


public class queryNode {
	//each queryNode contains a list of basic terms for each query
	List <basicTerm> conditions;
	public queryNode (){
		conditions = new ArrayList<basicTerm>();
	}
}

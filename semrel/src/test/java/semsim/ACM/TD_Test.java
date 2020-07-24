package semsim.ACM;

import java.util.Set;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class TD_Test {

	public static void main(String[] args) {
		String annotations_folder = "/semsim/ACM/dataFromAntonio/output/";
		String weights_folder = "/semsim/ACM/dataFromAntonio/output/";
		String onto_file = "acmtaxonomy.owl";
		
		String avs_file = "avs.txt";
		String weights_file = "weights_td.txt";
		boolean coeff_option = false;
		
		
		WeightedTaxonomy wt = new WeightedTaxonomy(); 
		wt.loadOWL(onto_file);
		wt.initWeights(weights_folder+weights_file, Constants.SEMSIM_BY_ID);
		
		SemsimEngine se = new SemsimEngine(wt, annotations_folder+avs_file, Constants.SEMSIM_BY_ID, coeff_option);
		
		Set<String> resources = se.getAvs().keySet();
		String count = "";
		for(String res:resources)
			count = count + res+";"+se.getAvs().get(res).size()+"\n";
		
		String count_file = "count.csv";
		Utils.print(annotations_folder+count, count, false);
		
		

	}

}

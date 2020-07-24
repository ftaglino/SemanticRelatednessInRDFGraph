package semsim.ACM;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.taxonomy.Pair;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Semsim_ACM_Test {

	public static void main(String[] args) {
		final String WEIGHTING_MODE = "af"; 
		String ontology_folder = "/semsim/ACM/dataFromAntonio/output2/";
		String annotations_folder = "/semsim/ACM/dataFromAntonio/output2/";
		String weights_folder = "/semsim/ACM/dataFromAntonio/output2/";
		String out_folder = "/semsim/ACM/dataFromAntonio/output2/";
		String onto_file = "acmtaxonomy.owl";
		
		String avs_file = "avs.txt";
		String weights_file = "weights_"+WEIGHTING_MODE+".txt";
		
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = new WeightedTaxonomy(); 
		//wt.loadOWL(ontology_folder+onto_file);
		//System.out.println(wt.allClasses());
		wt.initWeights(weights_folder+weights_file, Constants.SEMSIM_BY_ID);
		
		SemsimEngine se = new SemsimEngine(wt, annotations_folder+avs_file, Constants.SEMSIM_BY_ID, coeff_option);
//		SemsimEngine se = new SemsimEngine(wt, annotations_folder+avs_file, Constants.SEMSIM_BY_ID, weights_folder+weights_file, Constants.SEMSIM_BY_ID);
		
		int dimMatrix = 1103;
		
		String out = "";
		for(int i=0; i<dimMatrix; i++) {
			System.out.println(i);
			Vector<OFVElem> ofv_ext = se.getAvs().get(""+i);
			for(int j=0; j<dimMatrix; j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = 0.0d;
				if(j==i)
					semsim = 1.0d;
				else if((ofv_ext == null) || (ofv_int == null))
					;
				else 
					semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(semsim);
				out = out + semsim + " ";
			}
			//System.out.println(out);
			Utils.println(out_folder+"semsim_"+WEIGHTING_MODE+".txt", out, true);
			out = "";
		}

	}

}

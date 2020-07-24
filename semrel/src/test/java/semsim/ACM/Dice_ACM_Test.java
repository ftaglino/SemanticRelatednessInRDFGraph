package semsim.ACM;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.taxonomy.Pair;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;
import it.cnr.iasi.saks.similarity.otherMethods.Dice;

public class Dice_ACM_Test {

	public static void main(String[] args) {
		String annotations_folder = "target/test-classes/semsim/ACM/dataFromAntonio/";
		String out_folder = "/semsim/ACM/dataFromAntonio/output2/";
		
		String avs_file = "AnnotatedPapers.txt";
		int dimMatrix = 1103;
		
		Dice dice = new Dice();
		Vector<Set<String>> avs = dice.loadAnnotations(annotations_folder+avs_file, dimMatrix);
		
		
		String out = "";
		for(int i=0; i<dimMatrix; i++) {
			System.out.println(i);
			Set<String> ofv_ext = avs.elementAt(i);
			for(int j=0; j<dimMatrix; j++) {
				Set<String> ofv_int = avs.elementAt(j);
				double sim = 0.0d;
				if(j==i)
					sim = 1.0d;
				else if((ofv_ext.size() == 0) || (ofv_int.size() == 0))
					;
				else 
					sim = dice.dice(ofv_ext, ofv_int);
				//System.out.println(semsim);
				out = out + sim + " ";
			}
			//System.out.println(out);
			Utils.println(out_folder+"dice.txt", out, true);
			out = "";
		}

	}

}

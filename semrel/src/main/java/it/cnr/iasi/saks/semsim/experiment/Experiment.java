package it.cnr.iasi.saks.semsim.experiment;

import java.util.Set;
import java.util.Vector;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.likelihood.IntrinsicLikelihood;
import it.cnr.iasi.saks.semsim.likelihood.M4;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Experiment {

	public void run_paperVSpaper() {
		String avsFile = "/semsim/ACM/avs/ACM_avs__withUsedKeywords.txt";
		String annotationMode = Constants.SEMSIM_BY_LABEL;

		String out_file = "/results/semsim/ACM/paperVSpaper/Pt_5.txt";
		boolean coeff_option = false;
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new M4();
		wt.initWeights(likelihood);
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, coeff_option);
		
		Set<String> annotatedResources = se.getAvs().keySet(); 
		for(String res_ext:annotatedResources) {
			Vector<OFVElem> av_ext = se.getAvs().get(res_ext);
			for(String res_int:annotatedResources) {
				Vector<OFVElem> av_int = se.getAvs().get(res_int);
				double semsim = se.semsim(av_ext, av_int, coeff_option);
				String content = res_ext+" "+res_int+" "+semsim;
				Utils.println(out_file, content, true);
			}
		}
		
	}
	
}

package semsim;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.Result;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.likelihood.AF;
import it.cnr.iasi.saks.semsim.likelihood.CorpusBasedLikelihood;
import it.cnr.iasi.saks.semsim.taxonomy.Pair;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Semsim_AXED {
	
	@Ignore
	@Test
	public void lub() {
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		String f = "http://www.cnr-axed.org#Interesse_LOW";
		String s = "http://www.cnr-axed.org#Interesse_HIGH";
		Pair pair = new Pair(f,s);
		String lub = wt.lub(pair).elementAt(0).getURI().toString();
		System.out.println(lub);
	}
	
//	@Ignore
	@Test
	public void semsim_with_AF() {
		String avsFile = "/semsimPlus/axed/avs/avs.txt";
		String rvsFile = "/semsimPlus/axed/rvs/rvs.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_intrinsic.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		//String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = true;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		CorpusBasedLikelihood likelihood = new AF();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		wt.initWeights(likelihood, se.getAvs());

		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"] ["+wt.getOrderById(id)+"]";
			System.out.println(data);
			//Utils.println("/semsim/tourism/weights/generated/P2_weights.txt", data, true);
		}
		
		//System.exit(0);
		
		Set<String> rvs_keys = se.getRvs().keySet();
		for(String rv_key: rvs_keys) {
		//for(int i=0; i<se.getRvs().size(); i++) {
			Vector<OFVElem> ofv_ext = se.getRvs().get(rv_key);
			Vector<Result> unordered_results = new Vector<Result>();
			Vector<Result> ordered_results = new Vector<Result>();
			Set<String> avs_keys = se.getAvs().keySet();
			for(String av_key: avs_keys) {
			//for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(av_key);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				//System.out.println(semsim);
				Result res = new Result(av_key, semsim);
				unordered_results.add(res);
			}
			ordered_results = se.sortingResult(unordered_results);
			System.out.println("rv: "+rv_key + " - " + se.toStringRV(ofv_ext, wt));
			for(Result r: ordered_results) {
				System.out.println("telaio n. "+r.getOfv_name() + ";" + r.getValue());
				System.out.println(se.toStringAV(se.getAvs().get(r.getOfv_name()), wt));
				System.out.println("\n");
			}
		}
	}
}

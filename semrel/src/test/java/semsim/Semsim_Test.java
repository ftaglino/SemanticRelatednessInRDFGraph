package semsim;

import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.Result;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.likelihood.BUT;
import it.cnr.iasi.saks.semsim.likelihood.CorpusBasedLikelihood;
import it.cnr.iasi.saks.semsim.likelihood.IntrinsicLikelihood;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.likelihood.AF;
import it.cnr.iasi.saks.semsim.likelihood.M3;
import it.cnr.iasi.saks.semsim.likelihood.M4;
import it.cnr.iasi.saks.semsim.likelihood.Zhou;
import it.cnr.iasi.saks.semsim.likelihood.TopDown;
import it.cnr.iasi.saks.semsim.taxonomy.Pair;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Semsim_Test {
	
	@Ignore	
	@Test
	public void semsim_init() {
		//String avsFile = "/semsim/ACM/avs/ACM_avs__withUsedKeywords.txt";
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new M4();
		wt.initWeights(likelihood);
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, coeff_option);

		for(int i=0; i<se.getAvs().size(); i++) {
			System.out.println(i+"->"+se.getAvs().get(""+i));			
		}
	}

//	@Ignore
//	@Test
	public void consim() {
		//String avsFile = "/semsim/ACM/avs/ACM_avs__withUsedKeywords.txt";
		//String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String avsFile = "/semsimPlus/axed/avs/avs.txt";
		String annotationMode = Constants.SEMSIM_BY_LABEL;
		boolean coeff_option = false;
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		CorpusBasedLikelihood likelihood = new AF();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, coeff_option);
		wt.initWeights(likelihood, se.getAvs());
		
		String first = "http://www.semanticweb.org/francesco/ontologies/2019/10/untitled-ontology-216#Cilindrata_Alta";
		String second = "http://www.semanticweb.org/francesco/ontologies/2019/10/untitled-ontology-216#Cilindrata_Media";
		double ic_first = se.ic(first);
		System.out.println("ic("+wt.getNameById(first)+")="+ic_first);
		System.out.println(se.getWt().getWeights().get(first));
		Pair pair = new Pair(first, second);
		double consim = se.consim(pair);
		System.out.println("consim("+wt.getNameById(first)+", "+wt.getNameById(second)+")="+consim);
		
	}

	@Ignore
	@Test
	public void semsim_with_M1() {
		//String avsFile = "/semsim/test/avs/simple.txt";
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
		//String weightsFile = "/semsim/test/weights/CompInd_tourism_weights_intrinsic.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		//String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		CorpusBasedLikelihood likelihood = new CF();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		//SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode);
		wt.initWeights(likelihood, se.getAvs());
				
		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/P1_weights.txt", data, true);
		}
		
		//System.exit(0);
		
		for(int i=0; i<se.getRvs().size(); i++) {
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				System.out.println(semsim);
			}
		}
	}

//	@Ignore
//	@Test
	public void semsim_with_AF() {
		String avsFile = "/semsimPlus/encyclopedia/avs/avs.txt";
		String rvsFile = "/semsimPlus/encyclopedia/rvs/rvs_ALL.txt";
//		String avsFile = "/semsimPlus/encyclopedia/avs/avs.txt";
//		String rvsFile = "/semsimPlus/encyclopedia/rvs/rvs.txt";
//		String avsFile = "/semsimPlus/encyclopedia/avs/avsWithoutCoeff.txt";
//		String rvsFile = "/semsimPlus/encyclopedia/rvs/rvsWithoutCoeff_ALL.txt";
//		String avsFile = "/semsimPlus/encyclopedia/avs/avsWithoutCoeff.txt";
//		String rvsFile = "/semsimPlus/encyclopedia/rvs/rvsWithoutCoeff.txt";
		//String avsFile = "/weightedSemsim/avs/CompInd_tourism_avs.txt";
		//String rvsFile = "/weightedSemsim/rvs/CompInd_tourism_rvs.txt";
		//String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		//String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
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
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/P2_weights.txt", data, true);
		}
		
		//System.exit(0);
				
		
		for(int i=0; i<se.getRvs().size(); i++) {
			int temp = i+1;
			System.out.println("USER U"+temp);
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			Vector<Result> unordered_results = new Vector<Result>();
			Vector<Result> ordered_results = new Vector<Result>(); 
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				//System.out.println(semsim);
				int x = (j+1);
				Result res = new Result("P"+x, semsim);
				unordered_results.add(res);
			}
			ordered_results = se.sortingResult(unordered_results);
			for(Result r: ordered_results)
				System.out.println(r.getOfv_name() + ";" + r.getValue());
			System.out.println("\n");
		}
	}
	
//	@Ignore
//	@Test
	public void semsim_with_TopDown() {
		//String avsFile = "/semsimPlus/encyclopedia/avs/avsWithoutCoeff.txt";
		//String rvsFile = "/semsimPlus/encyclopedia/rvs/rvsWithoutCoeff.txt";
		String avsFile = "/semsimPlus/encyclopedia/avs/avs.txt";
		String rvsFile = "/semsimPlus/encyclopedia/rvs/rvs.txt";
		//String avsFile = "/weightedSemsim/avs/CompInd_tourism_avs.txt";
		//String rvsFile = "/weightedSemsim/rvs/CompInd_tourism_rvs.txt";
		//String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		//String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
		String weightsFile = "/semsimPlus/encyclopedia/weights/top_down.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = true;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new TopDown();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		//wt.initWeights(likelihood);
		wt.initWeights(weightsFile, weightsMode); 
		
		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/P2_weights.txt", data, true);
		}
		
		//System.exit(0);
				
		
		for(int i=0; i<se.getRvs().size(); i++) {
			int temp = i+1;
			System.out.println("USER U"+temp);
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				System.out.println(semsim);
			}
			System.out.println("\n");
		}
	}
	
	@Ignore
	@Test
	public void semsim_with_M3() {
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_intrinsic.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		CorpusBasedLikelihood likelihood = new M3();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		wt.initWeights(likelihood, se.getAvs());

		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/P3_weights.txt", data, true);
		}
		
		for(int i=0; i<se.getRvs().size(); i++) {
			System.out.println("i="+i);
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				System.out.println(semsim);
			}
		}
	}

	@Ignore
	@Test
	public void semsim_with_M4() {
		//String avsFile = "/semsim/ACM/avs/ACM_avs__withUsedKeywords.txt";
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
		//String avsFile = "/semsim/tourism/avs/simple.txt";
		//String rvsFile = "/semsim/tourism/rvs/simple.txt";
		String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_intrinsic.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_frequency.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new M4();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		wt.initWeights(likelihood);

		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/P5_weights.txt", data, true);
		}
		
		System.exit(0);

		
		for(int i=0; i<se.getRvs().size(); i++) {
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				System.out.println(semsim);
			}
		}
	}
	
	@Ignore
	@Test
	public void semsim_with_BUT() {
		//String avsFile = "/semsim/ACM/avs/ACM_avs__withUsedKeywords.txt";
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
		//String avsFile = "/semsim/tourism/avs/simple.txt";
		//String rvsFile = "/semsim/tourism/rvs/simple.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_intrinsic.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_frequency.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new BUT();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		wt.initWeights(likelihood);

		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/BUT_weights.txt", data, true);
		}
		
//		System.exit(0);

		
		for(int i=0; i<se.getRvs().size(); i++) {
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				System.out.println(semsim);
			}
		}
	}
	
	@Ignore
	@Test
	public void semsim_with_ZHOU() {
		//String avsFile = "/semsim/ACM/avs/ACM_avs__withUsedKeywords.txt";
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String rvsFile = "/semsim/tourism/rvs/CompInd_tourism_rvs.txt";
		//String avsFile = "/semsim/tourism/avs/simple.txt";
		//String rvsFile = "/semsim/tourism/rvs/simple.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_intrinsic.txt";
		//String weightsFile = "/semsim/tourism/weights/CompInd_tourism_weights_frequency.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		String weightsMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new Zhou();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, rvsFile, coeff_option);
		wt.initWeights(likelihood);
		se.initIC_ZHOU();

		Map<String, Double> weights = wt.getWeights();
		for(String id:weights.keySet()) {
			String data = wt.getNameById(id)+" = ["+weights.get(id)+"]";
			System.out.println(data);
			Utils.println("/semsim/tourism/weights/generated/ZHOU_weights.txt", data, true);
		}
		
//		System.exit(0);

		
		for(int i=0; i<se.getRvs().size(); i++) {
			Vector<OFVElem> ofv_ext = se.getRvs().get(""+i);
			for(int j=0; j<se.getAvs().size(); j++) {
				Vector<OFVElem> ofv_int = se.getAvs().get(""+j);
				double semsim = se.semsim(ofv_ext, ofv_int, coeff_option);
				//System.out.println(i+" "+j+" "+semsim);
				System.out.println(semsim);
			}
		}
	}
}

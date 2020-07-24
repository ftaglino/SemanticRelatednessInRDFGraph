package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Zhou implements IntrinsicLikelihood {
	Map<String, Double> likelihood = new HashMap<String, Double>();
	
	public Map<String, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<String, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	
	public Map<String, Double> likelihood(WeightedTaxonomy wt) {
		Map<String, Double> result = new HashMap<String, Double>();
		return result;
	}
}

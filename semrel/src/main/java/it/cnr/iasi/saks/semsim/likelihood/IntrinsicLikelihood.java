package it.cnr.iasi.saks.semsim.likelihood;

import java.util.Map;

import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public interface IntrinsicLikelihood {
	public Map<String, Double> likelihood(WeightedTaxonomy wt);
}

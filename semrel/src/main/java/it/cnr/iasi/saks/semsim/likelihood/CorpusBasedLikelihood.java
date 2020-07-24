package it.cnr.iasi.saks.semsim.likelihood;

import java.util.Map;
import java.util.Vector;

import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public interface CorpusBasedLikelihood {
	public Map<String, Double> likelihood(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs);
}

package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class BUT implements IntrinsicLikelihood {
	Map<String, Double> likelihood = new HashMap<String, Double>();
	
	public Map<String, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<String, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	
	public Map<String, Double> likelihood(WeightedTaxonomy wt) {
		Map<String, Double> result = new HashMap<String, Double>();
		for(Node c:wt.allClasses()) {
			String c_id = c.getURI().toString();
			this.getLikelihood().put(c_id, 0d);
		}
		double leaf_weight = 1.0d / ((double)wt.leaves().size());
		for(Node leaf:wt.leaves()) {
			String leaf_id = leaf.getURI().toString();
			this.getLikelihood().put(leaf_id, leaf_weight);
			for(Node anc:wt.ancestors(leaf_id)) {
				String anc_id = anc.getURI().toString();
				double old_value = this.getLikelihood().get(anc_id);
				this.getLikelihood().put(anc_id, old_value + leaf_weight);
			}
		}
		result = this.getLikelihood();
		return result;
	}
	
}

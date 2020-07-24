package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class M4 implements IntrinsicLikelihood {
	Map<String, Double> likelihood = new HashMap<String, Double>();
	
	public Map<String, Double> getLikelihood() {
		return likelihood;
	}

	public void setLikelihood(Map<String, Double> likelihood) {
		this.likelihood = likelihood;
	}
	
	private void likelihood(WeightedTaxonomy wt, Node root) {
		Vector<Node> nextToWeight = new Vector<Node>(wt.children(root));
		if(nextToWeight.size() > 0) {
			double value = this.getLikelihood().get(root.getURI().toString()) / ((double)nextToWeight.size());
		
			for(Node n:nextToWeight) {
				String key = n.getURI().toString();
				if(((this.getLikelihood().containsKey(key)) 
						&& (this.getLikelihood().get(key) > value)) 
					|| !(this.getLikelihood().containsKey(key))) {
						this.getLikelihood().put(key, value);
						this.likelihood(wt, n);
				}
			}
		}
	}

	public Map<String, Double> likelihood(WeightedTaxonomy wt) {
		Map<String, Double> result = new HashMap<String, Double>();

		Node root = NodeFactory.createURI(Constants.OWL_THING);
		this.getLikelihood().put(Constants.OWL_THING, 1.0d);
		this.likelihood(wt, root);
				
		result = this.getLikelihood();
		return result;
	}
	
}

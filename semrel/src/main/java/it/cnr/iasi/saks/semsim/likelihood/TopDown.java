package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class TopDown implements IntrinsicLikelihood {
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
		this.getLikelihood().put(Constants.OWL_THING, 1.0d);
		
		Vector<Node> toCompute = new Vector<Node>();
		Node thing = NodeFactory.createURI(Constants.OWL_THING);
		toCompute.add(thing);
		assignWeightsInProbabilisticMode(toCompute, wt);
		
		result = this.getLikelihood();
		return result;
	}
	
	
	// Assign the weights in a probabilistic mode
	// w(c)=w(parent(c))/|children(parent(c))|
	public void assignWeightsInProbabilisticMode(Vector<Node> toCompute, WeightedTaxonomy wt) {
		Vector<Node> nextToCompute = new Vector<Node>(); 
		for(Node n:toCompute) {
			Vector<Node> children = wt.children(n);
			for(Node child:children) {
				double weight = likelihood.get(n.getURI().toString())/children.size();
				//System.out.println(child.getURI().toString()+"\t"+weight);
				this.getLikelihood().put(child.getURI().toString(), weight);
				nextToCompute.add(child);
			}
		}
		if(nextToCompute.size()>0)
			assignWeightsInProbabilisticMode(nextToCompute, wt);
	}
}

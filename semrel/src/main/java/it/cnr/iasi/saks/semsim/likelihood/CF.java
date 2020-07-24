/**
 * This Class implements the Concept Frequency method
 */

package it.cnr.iasi.saks.semsim.likelihood;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semsim.OFVElem;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class CF implements CorpusBasedLikelihood {
	Map<String, Integer> classId_times = new HashMap<String, Integer>();
		
	public Map<String, Integer> getClassId_times() {
		return classId_times;
	}

	public void setClassId_times(Map<String, Integer> classId_times) {
		this.classId_times = classId_times;
	}
	
	public Map<String, Double> likelihood(WeightedTaxonomy wt, Map<String, Vector<OFVElem>> avs) {
		Map<String, Double> result = new HashMap<String, Double>();

		Set<Node> classes = wt.allClasses();
		for(Node n:classes)
			this.getClassId_times().put(n.getURI().toString(), 0);
		
		double total_used_times = 0;
		for(String av_id:avs.keySet()) {
			total_used_times = total_used_times + (double)(avs.get(av_id).size());
			for(OFVElem ofv_elem:avs.get(av_id)) {
				String c = ofv_elem.getConc_id(); 
				// add explicit annotation
				this.getClassId_times().put(c, this.getClassId_times().get(c)+1);
				// add implicit annotation
				Vector<Node> ancestors = wt.ancestors(c);
				for(Node n:ancestors) {
					String id = n.getURI().toString();
					this.getClassId_times().put(id, this.getClassId_times().get(id)+1);
				}
	
			}
		}
		
		for(Node n:classes) {
			result.put(n.getURI().toString(), ((double)(this.getClassId_times().get(n.getURI().toString())))/total_used_times);
		}
		
		return result;
	}	
}

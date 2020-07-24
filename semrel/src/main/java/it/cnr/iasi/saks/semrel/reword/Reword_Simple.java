/*
 * 	 This file is part of SemRel, originally promoted and
 *	 developed at CNR-IASI. For more information visit:
 *	 http://saks.iasi.cnr.it/tools/semrel
 *	     
 *	 This is free software: you can redistribute it and/or modify
 *	 it under the terms of the GNU General Public License as 
 *	 published by the Free Software Foundation, either version 3 of the 
 *	 License, or (at your option) any later version.
 *	 
 *	 This software is distributed in the hope that it will be useful,
 *	 but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	 GNU General Public License for more details.
 * 
 *	 You should have received a copy of the GNU General Public License
 *	 along with this source.  If not, see <http://www.gnu.org/licenses/>.
 */
package it.cnr.iasi.saks.semrel.reword;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class Reword_Simple extends Abstract_Reword {

	public Reword_Simple(KnowledgeBase kb) {
		this.setKb(kb);
	}
	
	protected Map<Node, Double> relatednessSpace(Node n, String in_out) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node u1 = NodeFactory.createURI("*u1");
		filters.addAll(this.getKb().instantiateFilters("u1", Constants.SUBJECT));
		Node p1 = NodeFactory.createVariable("p1");
		filters.addAll(this.getKb().instantiateFilters("p1", Constants.PREDICATE));
		Triple t = null;
		if(in_out.equals(Constants.IN) || in_out.equals(Constants.IN_OUT))
			t = new Triple(u1, p1, n);
		else if(in_out.equals(Constants.OUT) || in_out.equals(Constants.IN_OUT)) 
			t = new Triple(n, p1, u1);
		pattern.setFilters(filters);
		pattern.getTriples().add(t);

		
		Set<Node> preds_in = new HashSet<Node>();
		for(Node p:kb.nodesByPattern(pattern))
			preds_in.add(p);

		for(Node p:preds_in)
			result.put(p, pfitf(n, p, in_out));
		
		return result;
	}
	
	@Override
	public Map<Node, Double> relatednessSpace_in(Node n) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		result = relatednessSpace(n, Constants.IN);
		return result;
	}
	
	@Override
	public Map<Node, Double> relatednessSpace_out(Node n) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		result = relatednessSpace(n, Constants.OUT);
		return result;
	}	
}

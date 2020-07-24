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

package it.cnr.iasi.saks.semrel.ldsd;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * 
 * @author francesco
 *
 */
public class Ldsd_dw implements Ldsd {
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param kb
	 * @param pred_obj {@link Constants.PREDICATE} if n2 has to be considered as a triples' predicate; {@link Constants.OBJECT} if n2 has to be considered as a triples' object.
	 * @return
	 */
	private static Set<Node> cd_nodes(Node n1, Node n2, KnowledgeBase kb, String pred_obj) {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Triple t;
		if(pred_obj.equals(Constants.OBJECT)) {
			Node p = NodeFactory.createVariable("p1");
			filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
			t = new Triple(n1, p, n2);
		}
		else {
			Node o = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			t = new Triple(n1, n2, o);
		}
			
		pattern.setFilters(filters);
		pattern.getTriples().add(t);
		
		Vector<Node> temp = kb.nodesByPattern(pattern);
		
		// from Vector to Set
		for(Node n:temp)
			result.add(n);
		
		return result;
	}
	
	/**
	 * Search for the set of distinct nodes u such that <n, p, u> triples exist in the knowledge base.  
	 * @param n
	 * @param p
	 * @param kb
	 * @return
	 */
	public static Set<Node> cd_p_nodes(Node n, Node p, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result = cd_nodes(n, p, kb, Constants.PREDICATE);
		return result;
	}
	
	/**
	 * Search for the set of distinct predicates p such that <n1, p, n2> triples exists in the knowledge base.  
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static Set<Node> cd_o_nodes(Node n1, Node n2, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result = cd_nodes(n1, n2, kb, Constants.OBJECT);
		return result;
	}
	
	public double ldsd(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		
		double part_1 = 0;
		double part_2 = 0;
		Set<Node> preds_1 = cd_o_nodes(n1, n2, kb);
		for(Node p:preds_1) {
			part_1 = part_1 + ( 
						Ldsd_d.cd(n1, n2, p, kb)/
						(1+Math.log(Ldsd_d.cd_p(n1, p, kb)))
					);
		}
		Set<Node> preds_2 = cd_o_nodes(n2, n1, kb);
		for(Node p:preds_2) {
			part_2 = part_2 + ( 
					Ldsd_d.cd(n2, n1, p, kb)/
						(1+Math.log(Ldsd_d.cd_p(n2, p, kb)))
					);
		}
		
		result = 1.0d/(1.0d + 
					part_1 + 
					part_2
					);
		
		return result;
	}
}

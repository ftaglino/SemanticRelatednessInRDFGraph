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
public class Ldsd_i implements Ldsd {
	private static int c_ix_u(Node n1, Node n2, KnowledgeBase kb, String in_out) {
		int result = 0;
	
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Triple t1 = null;
		Triple t2 = null;
		Node p = NodeFactory.createVariable("p1");
		filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
		Node u= NodeFactory.createURI("*u2");
		if(in_out.equals(Constants.OUT)) {
			filters.addAll(kb.instantiateFilters("u2", Constants.OBJECT));
			t1 = new Triple(n1, p, u);
			t2 = new Triple(n2, p, u);
		}
		else if(in_out.equals(Constants.IN)) {
			filters.addAll(kb.instantiateFilters("u2", Constants.SUBJECT));
			t1 = new Triple(u, p, n1);
			t2 = new Triple(u, p, n2);
		}
		pattern.setFilters(filters);
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		
		result = kb.countNodesByPattern(pattern, Constants.SPARQL_DISTINCT);
		
		return result;
	}
	
	/**
	 * Count the the set of distinct predicates x such that <n1, x, ?u> . <n2, x, ?u>
	 * That is, the total number of indirect and distinct predicates x between n1 and n2, outgoing from both n1 and n2.    
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static int c_io_u(Node n1, Node n2, KnowledgeBase kb) {
		int result = 0;
		result = c_ix_u(n1, n2, kb, Constants.OUT);
		return result;
	}
	
	/**
	 * Count the the set of distinct predicates x such that <?u, x, n1> . <?u, x, n2>
	 * That is, the total number of indirect and distinct predicates between n1 and n2, incoming both n1 and n2.    
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static int c_ii_u(Node n1, Node n2, KnowledgeBase kb) {
		int result = 0;
		result = c_ix_u(n1, n2, kb, Constants.IN);
		return result;
	}
	
	public double ldsd(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		
		double part_1 = c_io_u(n1, n2, kb);
		double part_2 = c_ii_u(n1, n2, kb);
		
		result = 1/(1 + 
				part_1 + 
				part_2
			);		
		return result;
	}
}

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
public class Ldsd_iw implements Ldsd {
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param p
	 * @param kb
	 * @param in_out {@link Constants.IN} or {@link Constants.OUT}
	 * @return
	 */
	private static int c_ix(Node n1, Node n2, Node p, KnowledgeBase kb, String in_out) {
		int result = 0;
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node u1 = NodeFactory.createVariable("u1");
		Triple t1 = null;
		Triple t2 = null;
		if(in_out.equals(Constants.OUT)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			t1 = new Triple(n1,p,u1);
			t2 = new Triple(n2,p,u1);
		}
		else if(in_out.equals(Constants.IN)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			t1 = new Triple(u1,p,n1);
			t2 = new Triple(u1,p,n2);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.setFilters(filters);
		
		if(kb.pathExistence(pattern))
			result = 1;
		
		return result;
	}
	
	/**
	 * Check if at least one occurrence of the pattern <n1, p, ?u1> . <n2, p, ?u1> exists. 
	 * That is, whether a node u1 exists, such that both n1 and n2 are connected to u1 via an outgoing link p.    
	 * @param n1
	 * @param n2
	 * @param p1
	 * @param kb
	 * @return 1 if there is a resource u1 linked to both n1 and n2 via an outgoing (from both n1 and n2) predicate p. Otherwise, it returns 0.
	 */
	public static int c_io(Node n1, Node n2, Node p, KnowledgeBase kb) {
		int result = 0;
		result = c_ix(n1, n2, p, kb, Constants.OUT);
		return result;
	}
	
	
	/**
	 * Check if at least one occurrence of the pattern <?u1, p, n1> . <?u1, p, n2> exists. 
	 * That is, whether a node u1 exists, such that both n1 and n2 are connected to u1 via an incoming link p.    
	 * @param n1
	 * @param n2
	 * @param p1
	 * @param kb
	 * @return 1 if there is a resource u1 linked to both n1 and n2 via an incoming (to both n1 and n2) predicate p. Otherwise, it returns 0.
	 */
	public static int c_ii(Node n1, Node n2, Node p, KnowledgeBase kb) {
		int result = 0;
		result = c_ix(n1, n2, p, kb, Constants.IN);
		return result;
	}
	
	/**
	 * 
	 * @param n
	 * @param p
	 * @param kb
	 * @param in_out {@link Constants.IN} or {@link Constants.OUT} 
	 * @return
	 */
	private static int c_ix_p(Node n, Node p, KnowledgeBase kb, String in_out) {
		int result = 0;
	
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Triple t1 = null;
		Triple t2 = null;
		Node x = NodeFactory.createVariable("u1");
		Node u= NodeFactory.createURI("*u2");
		if(in_out.equals(Constants.OUT)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			filters.addAll(kb.instantiateFilters("u2", Constants.OBJECT));
			t1 = new Triple(n, p, u);
			t2 = new Triple(x, p, u);
		}
		else if(in_out.equals(Constants.IN)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			filters.addAll(kb.instantiateFilters("u2", Constants.SUBJECT));
			t1 = new Triple(u, p, n);
			t2 = new Triple(u, p, x);
		}
		pattern.setFilters(filters);
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		
		result = kb.countNodesByPattern(pattern, Constants.SPARQL_NOT_DISTINCT);
		
		return result;
	}
	
	/**
	 * Count the the set of distinct nodes x such that <n, p, ?u> . <?x, p, ?u>
	 * That is, the total number of nodes x linked indirectly to the node n via the predicate p. Predicate p is outgoing from both n and x.   
	 * @param n
	 * @param p
	 * @param kb
	 * @return
	 */
	public static int c_io_p(Node n, Node p, KnowledgeBase kb) {
		int result = 0;
		result = c_ix_p(n, p, kb, Constants.OUT);
		return result;
	}
	
	/**
	 * Count the the set of distinct nodes x such that <?u, p, n> . <?u, p, ?x>
	 * That is, the total number of nodes x linked indirectly to the node n via the predicate p. Predicate p is incoming for both n and x.    
	 * @param n
	 * @param p
	 * @param kb
	 * @return
	 */
	public static int c_ii_p(Node n, Node p, KnowledgeBase kb) {
		int result = 0;
		result = c_ix_p(n, p, kb, Constants.IN);
		return result;
	}
		
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param kb
	 * @param in_out {@link Constants.IN} or {@link Constants.OUT}
	 * @return
	 */
	private static Set<Node> c_ix_u_nodes(Node n1, Node n2, KnowledgeBase kb, String in_out) {
		Set<Node> result = new HashSet<Node>();
		
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();		
		Triple t1 = null;
		Triple t2 = null;
		Node x = NodeFactory.createVariable("p1");
		filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
		Node u= NodeFactory.createURI("*u2");
		if(in_out.equals(Constants.OUT)) {
			filters.addAll(kb.instantiateFilters("u2", Constants.OBJECT));
			t1 = new Triple(n1, x, u);
			t2 = new Triple(n2, x, u);
		}
		else if(in_out.equals(Constants.IN)) {
			filters.addAll(kb.instantiateFilters("u2", Constants.SUBJECT));
			t1 = new Triple(u, x, n1);
			t2 = new Triple(u, x, n2);
		}
		pattern.setFilters(filters);
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		
		Vector<Node> temp = kb.nodesByPattern(pattern);
		
		//from Vector to Set 
		for(Node n:temp)
			result.add(n);
		
		return result;
	}
	

	/**
	 * Search for the set of distinct predicates p such that <n1, p, ?u> . <n2, p, ?u> paths exist in the knowledge base.
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static Set<Node> c_io_u_nodes(Node n1, Node n2, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result = c_ix_u_nodes(n1, n2, kb, Constants.OUT);
		return result;
	}
	
	/**
	 * Search for the set of distinct predicates p such that <?u, p, n1> . <?u, p, n2> paths exist in the knowledge base.
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static Set<Node> c_ii_u_nodes(Node n1, Node n2, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result = c_ix_u_nodes(n1, n2, kb, Constants.IN);
		return result;
	}
	
	public double ldsd(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		
		double part_1 = 0;
		double part_2 = 0;
		Set<Node> preds_1 = c_ii_u_nodes(n1, n2, kb);
		for(Node p:preds_1) {
			part_1 = part_1 + ( 
						c_ii(n1, n2, p, kb)/
						(1+Math.log(c_ii_p(n1, p, kb)))
					);
		}
		
		Set<Node> preds_2 = c_io_u_nodes(n1, n2, kb);
		for(Node p:preds_2) {
			part_2 = part_2 + ( 
						c_io(n1, n2, p, kb)/
						(1+Math.log(c_io_p(n1, p, kb)))
					);
		}
		
		result = 1/(1 + 
				part_1 + 
				part_2
			);		
		return result;
	}
}

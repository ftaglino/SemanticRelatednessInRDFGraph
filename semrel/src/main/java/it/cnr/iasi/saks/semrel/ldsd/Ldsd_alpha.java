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
public class Ldsd_alpha implements Ldsd {
	
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param p1
	 * @param kb
	 * @param in_out {@link Constants.IN} or {@link Constants.OUT}
	 * @return
	 */
	private static Set<Node> c_ix_prime_nodes(Node n1, Node n2, Node p1, KnowledgeBase kb, String in_out) {
		Set<Node> result = new HashSet<Node>();
		
		
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();		
		Triple t1 = null;
		Triple t2 = null;
		Node u= NodeFactory.createVariable("u1");
		if(in_out.equals(Constants.OUT)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			t1 = new Triple(n1, p1, u);
			t2 = new Triple(n2, p1, u);
		}
		else if(in_out.equals(Constants.IN)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			t1 = new Triple(u, p1, n1);
			t2 = new Triple(u, p1, n2);
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
	 * Search for the set of distinct resources u such that the path <n1, p1, u> . <n2, p1, u> exists in the knowledge base.
	 * @param n1
	 * @param n2
	 * @param p1 
	 * @param kb
	 * @return
	 */
	public static Set<Node> c_io_prime_nodes(Node n1, Node n2, Node p1, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result = c_ix_prime_nodes(n1, n2, p1, kb, Constants.OUT);
		return result;
	}
	
	/**
	 * Search for the set of distinct resources u such that the path <u, p1, n1> . <u, p1, n2> exists in the knowledge base.
	 * @param n1
	 * @param n2
	 * @param p1 
	 * @param kb
	 * @return
	 */	
	public static Set<Node> c_ii_prime_nodes(Node n1, Node n2, Node p1, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result = c_ix_prime_nodes(n1, n2, p1, kb, Constants.IN);
		return result;
	}
	
	
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param p1
	 * @param kb
	 * @param in_out {@link Constants.IN} or {@link Constants.OUT}
	 * @return
	 */
	private static int c_ix_prime(Node n1, Node n2, Node p1, KnowledgeBase kb, String in_out) {
		int result = 0;
		
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();		
		Triple t1 = null;
		Triple t2 = null;
		Node u= NodeFactory.createVariable("u1");
		if(in_out.equals(Constants.OUT)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			t1 = new Triple(n1, p1, u);
			t2 = new Triple(n2, p1, u);
		}
		else if(in_out.equals(Constants.IN)) {
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			t1 = new Triple(u, p1, n1);
			t2 = new Triple(u, p1, n2);
		}
		pattern.setFilters(filters);
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		
		result = kb.countNodesByPattern(pattern, Constants.SPARQL_DISTINCT);
		
		return result;
	}
	

	/**
	 * Count set of distinct resources u such that the path <n1, p1, u> . <n2, p1, u> exists in the knowledge base.
	 * @param n1
	 * @param n2
	 * @param p1 
	 * @param kb
	 * @return
	 */
	public static int c_io_prime(Node n1, Node n2, Node p1, KnowledgeBase kb) {
		int result = 0;
		result = c_ix_prime(n1, n2, p1, kb, Constants.OUT);
		return result;
	}
	
	/**
	 * Count set of distinct resources u such that the path <u, p1, n1> . <u, p1, n2> exists in the knowledge base.
	 * @param n1
	 * @param n2
	 * @param p1 
	 * @param kb
	 * @return
	 */	
	public static int c_ii_prime(Node n1, Node n2, Node p1, KnowledgeBase kb) {
		int result = 0;
		result = c_ix_prime(n1, n2, p1, kb, Constants.IN);
		return result;
	}
	
	public double ldsd(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		
		double part_1 = 0;
		double part_2 = 0;
		double part_3 = 0;
		double part_4 = 0;
		
		Set<Node> preds_1 = Ldsd_dw.cd_o_nodes(n1, n2, kb);
		for(Node p:preds_1) {
			part_1 = part_1 + ( 
						Ldsd_d.cd(n1, n2, p, kb)/
						(1+Math.log(Ldsd_d.cd_p(n1, p, kb)))
					);
		}
		Set<Node> preds_2 = Ldsd_dw.cd_o_nodes(n2, n1, kb);
		for(Node p:preds_2) {
			part_2 = part_2 + ( 
					Ldsd_d.cd(n2, n1, p, kb)/
						(1+Math.log(Ldsd_d.cd_p(n2, p, kb)))
					);
		}
		
		Set<Node> preds_3 = Ldsd_iw.c_ii_u_nodes(n1, n2, kb);
		for(Node p:preds_3) {
			part_3 = part_3 + ( 
						c_ii_prime(n1, n2, p, kb)/
						(1+Math.log(Ldsd_iw.c_ii_p(n1, p, kb)))
					);
		}
		
		Set<Node> preds_4 = Ldsd_iw.c_io_u_nodes(n1, n2, kb);
		for(Node p:preds_4) {
			part_4 = part_4 + ( 
						c_io_prime(n1, n2, p, kb)/
						(1+Math.log(Ldsd_iw.c_io_p(n1, p, kb)))
					);
		}
		
		result = 1/(1 + 
					part_1 + 
					part_2 +
					part_3 +
					part_4
					);		
		return result;
	}
}

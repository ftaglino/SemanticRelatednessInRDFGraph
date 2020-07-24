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
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * 
 * @author francesco
 *
 */
public class Ldsd_gamma implements Ldsd {
	
	/**
	 * Count the paths <?u1, p, ?u2>   
	 * @param p
	 * @param kb
	 * @return
	 */
	public static int c_dp(Node p, KnowledgeBase kb) {
		int result = 0;
		
		Node s1 = NodeFactory.createVariable("u1");
		Set<Filter> s_filters = kb.instantiateFilters("u1", Constants.SUBJECT);
		Node o1 = NodeFactory.createVariable("u2");
		Set<Filter> o_filters = kb.instantiateFilters("u2", Constants.OBJECT);
		
		PathPattern pattern = new PathPattern();
		Triple t = new Triple(s1, p, o1);
		pattern.getTriples().add(t);
		pattern.getFilters().addAll(s_filters);
		pattern.getFilters().addAll(o_filters);
		
		result = kb.countPathsByPattern(pattern);
		
		return result;
	}
	
	private static int c_ixp(Node p, Node n, KnowledgeBase kb, String in_out) {
		int result = 0;
		Node u1 = NodeFactory.createVariable("u1");
		Node u2 = NodeFactory.createVariable("u2");
		PathPattern pattern = new PathPattern();
		Triple t1 = null;
		Triple t2 = null;
		Set<Filter> u1_filters = null;
		Set<Filter> u2_filters = null;
		if(in_out.equals(Constants.IN)) {
			u1_filters = kb.instantiateFilters("u1", Constants.OBJECT);
			u2_filters = kb.instantiateFilters("u2", Constants.OBJECT);
			t1 = new Triple(n, p, u1);
			t2 = new Triple(n, p, u2);
		}
		else if(in_out.equals(Constants.OUT)) {
			u1_filters = kb.instantiateFilters("u1", Constants.SUBJECT);
			u2_filters = kb.instantiateFilters("u2", Constants.SUBJECT);
			t1 = new Triple(u1, p, n);
			t2 = new Triple(u2, p, n);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getFilters().addAll(u1_filters);
		pattern.getFilters().addAll(u2_filters);
		
		result = kb.countPathsByPattern(pattern);
		
		return result;
	}
	
	/**
	 * Count the occurrences of the pattern <n, p, ?u1> . <n, p, ?u2>
	 * @param p
	 * @param n
	 * @param kb
	 * @return
	 */
	public static int c_iip(Node p, Node n, KnowledgeBase kb) {
		int result = 0;
		result = c_ixp(p, n, kb, Constants.IN);
		return result;
	}
	
	/**
	 * Count the occurrences of the pattern <?u1, p, n> . <?u2, p, n>
	 * @param p
	 * @param n
	 * @param kb
	 * @return
	 */
	public static int c_iop(Node p, Node n, KnowledgeBase kb) {
		int result = 0;
		result = c_ixp(p, n, kb, Constants.OUT);
		return result;
	}
	
	private static Set<Path> c_ix_paths(Node n1, Node n2, KnowledgeBase kb, String in_out) {
		Set<Path> result = new HashSet<Path>();
		Node u1 = NodeFactory.createVariable("u1");
		Node p1 = NodeFactory.createVariable("p1");
		PathPattern pattern = new PathPattern();
		Triple t1 = null;
		Triple t2 = null;
		Set<Filter> u1_filters = null;
		Set<Filter> p1_filters = kb.instantiateFilters("p1", Constants.PREDICATE);
		if(in_out.equals(Constants.IN)) {
			u1_filters = kb.instantiateFilters("u1", Constants.SUBJECT);
			t1 = new Triple(u1, p1, n1);
			t2 = new Triple(u1, p1, n2);
		}
		else if(in_out.equals(Constants.OUT)) {
			u1_filters = kb.instantiateFilters("u1", Constants.OBJECT);
			t1 = new Triple(n1, p1, u1);
			t2 = new Triple(n2, p1, u1);
		}
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.getFilters().addAll(u1_filters);
		pattern.getFilters().addAll(p1_filters);
		
		Vector<Path> temp = kb.pathsByPattern(pattern, Constants.ACYCLIC);
		
		for(Path p:temp) 
			result.add(p);
		
		return result;
	}

	/**
	 * Search for the set of paths <?u1, ?p1, n1> . <?u1, ?p1, n2>
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static Set<Path> c_ii_paths(Node n1, Node n2, KnowledgeBase kb) {
		Set<Path> result = new HashSet<Path>();
		result = c_ix_paths(n1, n2, kb, Constants.IN);
		return result;
	}
	
	/**
	 * Search for the set of paths <n1, ?p1, ?u1> . <n2, ?p1, ?u1>
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static Set<Path> c_io_paths(Node n1, Node n2, KnowledgeBase kb) {
		Set<Path> result = new HashSet<Path>();
		result = c_ix_paths(n1, n2, kb, Constants.OUT);
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
						(1+Math.log(c_dp(p, kb)))
					);
		}
		Set<Node> preds_2 = Ldsd_dw.cd_o_nodes(n2, n1, kb);
		for(Node p:preds_2) {
			part_2 = part_2 + ( 
					Ldsd_d.cd(n2, n1, p, kb)/
						(1+Math.log(c_dp(p, kb)))
					);
		}
		
		Set<Path> paths_1 = c_ii_paths(n1, n2, kb);
		for(Path p:paths_1) {
			Node p1 = p.getTriples().get(0).getPredicate();
			Node s1 = p.getTriples().get(0).getSubject();
			part_3 = part_3 + 1 / (1 + c_iip(p1, s1, kb));
		}
		
		Set<Path> paths_2 = c_io_paths(n1, n2, kb);
		for(Path p:paths_2) {
			Node p1 = p.getTriples().get(0).getPredicate();
			Node o1 = p.getTriples().get(0).getObject();
			part_4 = part_4 + 1 / (1 + c_iip(p1, o1, kb));
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

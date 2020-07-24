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
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * 
 * @author francesco
 *
 */
public class Ldsd_d implements Ldsd{
	/**
	 * Check if the path <n1, p, n2> exists in the knowledge base.
	 * @param path
	 * @param kb
	 * @return 1 if the path exist, 0if the path does not exist.
	 */
	public static int cd(Node n1, Node n2, Node p, KnowledgeBase kb) {
		int result = 0;  
		Path path = new Path();
		Triple t = new Triple(n1, p, n2);
		path.getTriples().add(t);
		if(kb.pathExistence(path)) 
			result = 1;
		return result;
	}
	
	/**
	 * 
	 * @param n1
	 * @param n2
	 * @param kb
	 * @param pred_obj {@link Constants.PREDICATE} if n2 has to be considered as a triples' predicate; {@link Constants.OBJECT} if n2 has to be considered as a triples' object. 
	 * @return
	 */
	private static int cd(Node n1, Node n2, KnowledgeBase kb, String pred_obj) {
		int result = 0;
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
	
		result = kb.countPathsByPattern(pattern);
		
		return result;
	}
	
	/**
	 * Count how many distinct triples <n, p, x> exist. 
	 * That is, the total number of distinct instances of the predicate p, from Node n to any node.   
	 * @param n
	 * @param p 
	 * @param kb
	 * @return
	 */
	public static int cd_p(Node n, Node p, KnowledgeBase kb) {
		int result = 0;
		result = cd(n, p, kb, Constants.PREDICATE);
		return result;
	}
	
	/**
	 * Count how many distinct triples <n1, x, n2> exist.
	 * That is, the total number of direct and distinct predicates from n1 to n2.   
	 * @param n1
	 * @param n2
	 * @param kb
	 * @return
	 */
	public static int cd_o(Node n1, Node n2, KnowledgeBase kb) {
		int result = 0;
		result = cd(n1, n2, kb, Constants.OBJECT);
		return result;
	}
	
	public double ldsd(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		result = 1.0d/(1.0d + 
					((double)cd_o(n1, n2, kb)) + 
					((double)cd_o(n2, n1, kb))
					);
		return result;
	}
}

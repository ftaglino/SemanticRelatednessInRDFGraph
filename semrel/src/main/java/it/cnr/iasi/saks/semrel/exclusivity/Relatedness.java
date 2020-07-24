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
package it.cnr.iasi.saks.semrel.exclusivity;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import it.cnr.iasi.saks.semrel.Utils;

/**
 * The implementation of this method refers to the following publication
 * Hulpuus I., Prangnawarat N., Hayes C. 
 * Path-based Semantic Relatedness on Linked Data and its use to Word and Entity Disambiguation.
 * Springer International Publishing, Cham, 442–457. DOI:hp://dx.doi.org/10.1007/978-3-319-25007-6 26 
 *  
 * @author ftaglino
 *
 */
public class Relatedness {	

	public static double exclusivity(Triple t, KnowledgeBase kb) {
		double result = 0;
		
		double np1 = 0;
		{
			PathPattern p = new PathPattern();
			Set<Filter> filters = new HashSet<Filter>();
			Node o1 = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			Triple t1 = new Triple(t.getSubject(), t.getPredicate(), o1);
			p.getTriples().add(t1);
			p.setFilters(filters);
			np1 = kb.countPathsByPattern(p);
		}
		
		double np2 = 0;
		{
			PathPattern p = new PathPattern();
			Set<Filter> filters = new HashSet<Filter>();
			filters = new HashSet<Filter>();
			Node s1 = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			Triple t1 = new Triple(s1, t.getPredicate(), t.getObject());
			p.getTriples().add(t1);
			p.setFilters(filters);
			np2 = kb.countPathsByPattern(p);
		}
		
		result = 1 / (np1 + np2 - 1);
		return result; 
	}
	
	public static double path_weight(Path path, KnowledgeBase kb) {
		double result = 0;
		double temp = 0;
		for(Triple t:path.getTriples())
			temp = temp + (1 / exclusivity(t, kb));
		result = 1 / temp;
		return result;
	}
	
	public static double semrel(Node n1, Node n2, KnowledgeBase kb, int minLength, int maxLength, String mode, int k, double alpha, boolean acyclic) {
		double result = 0;
		Vector<Path> paths = kb.paths(n1, n2, minLength, maxLength, mode, acyclic);

		Map<Path,Double> weightedPaths = new HashMap<Path, Double>();
		for(Path p:paths) {
			weightedPaths.put(p, path_weight(p, kb));
		}

		int n = 0;
		if(k == Constants.MEAN)
			n = (paths.size() / 2) +  (paths.size() % 2);
		else n = k;
		
		List<Entry<Path, Double>> n_greatestPaths = Utils.findGreatest_n(weightedPaths, n);
		 
		for (Entry<Path, Double> entry : n_greatestPaths) 
			result = result + Math.pow(alpha, entry.getKey().size())*entry.getValue().doubleValue();

		return result;
	}
}

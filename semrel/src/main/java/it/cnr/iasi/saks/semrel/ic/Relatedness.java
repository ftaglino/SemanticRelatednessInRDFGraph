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
package it.cnr.iasi.saks.semrel.ic;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * The implementation of this semantic relatedness method refers to the following publication
 * Schuhumacher M., Ponzetto S.P. 
 * Knowledge-based Graph Document Modeling
 * WSDM'14 February 24-28, 2014, New York, New York, USA.
 *  
 * @author francesco
 *
 */
public class Relatedness {
	
	public static double path_cost(Path path, IC method) {
		double result = 0;
		for(Triple t:path.getTriples()) {
			PathPattern pattern = new PathPattern();
			pattern.getTriples().add(t);
			result = result + method.cost(pattern);
		}
		return result;
	} 
	
	public static double path_ic(Path path, IC method) {
		double result = 0;
		for(Triple t:path.getTriples()) {
			PathPattern pattern = new PathPattern();
			pattern.getTriples().add(t);
			result = result + method.ic(pattern);
		}
		return result;
	}
	
	public static double semrel(Node n1, Node n2, KnowledgeBase kb, int minLength, int maxLength, String mode, boolean acyclic, Abstract_IC method) {
		double result = method.getMax_ic()*maxLength;
		double max_distance = method.getMax_ic()*maxLength;
		double temp = max_distance;
		Vector<Path> paths = kb.paths(n1, n2, minLength, maxLength, mode, acyclic);
		for(Path p:paths) {
			double p_cost = path_cost(p, method);
			if(p_cost<temp)
				temp = p_cost;
		}
		result = 1 - temp/max_distance;
		return result;
	}	
	
	public static double semrel_(Node n1, Node n2, KnowledgeBase kb, int minLength, int maxLength, String mode, boolean acyclic, IC method) {
		double result = 0;
		Vector<Path> paths = kb.paths(n1, n2, minLength, maxLength, mode, acyclic);
		for(Path p:paths) {
			double p_cost = path_cost(p, method);
			if(result==0)
				result = p_cost;
			else if(p_cost<result)
				result = p_cost;
		}
		return result;
	}
}

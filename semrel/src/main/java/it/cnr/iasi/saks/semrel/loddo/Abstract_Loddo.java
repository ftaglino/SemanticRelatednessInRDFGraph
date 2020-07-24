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
package it.cnr.iasi.saks.semrel.loddo;

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
 * @author ftaglino
 *
 */
public abstract class Abstract_Loddo implements Loddo {
	
	public Set<Node> description(Node n1, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>(); 
		Node u1 = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
		Node p1 = NodeFactory.createURI("*p1");
		filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
		Triple t = new Triple(u1,p1,n1);
		pattern.setFilters(filters);
		pattern.getTriples().add(t);
	
		for(Node n:kb.nodesByPattern(pattern))
			result.add(n);
		
		PathPattern pattern2 = new PathPattern(); 
		Triple t2 = new Triple(n1,p1,u1);
		pattern2.setFilters(filters);
		pattern2.getTriples().add(t2);
		
		for(Node n:kb.nodesByPattern(pattern2))
			result.add(n);
				
		return result;
	}
	
	public Set<Node> commonDescription(Node n1, Node n2, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		
		result = this.description(n1, kb);
		result.retainAll(description(n2, kb));
				
		return result;
	}
		
}

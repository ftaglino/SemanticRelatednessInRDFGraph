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
public abstract class Abstract_IC implements IC {
	protected KnowledgeBase kb;
	protected double max_ic;
	
	public Abstract_IC(KnowledgeBase kb) {
		this.kb = kb;
		this.setMax_ic(this.max_ic());
	}

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
		
	public double getMax_ic() {
		return max_ic;
	}

	public void setMax_ic(double max_ic) {
		this.max_ic = max_ic;
	}

	public int countPossibleEvents() {
		int result = 0;
		result = kb.countAllTriples();
		return result;
	}
	
	public double likelihood(PathPattern favoriteEventsPattern) {
		double result = 0;
		int favoriteEvents = kb.countPathsByPattern(favoriteEventsPattern);
		int possibleEvents = countPossibleEvents();
		result = ((double)favoriteEvents)/((double)possibleEvents);
		return result;
	}
	
	public double likelihood_conditioned(PathPattern eventsPattern) {
		double result = 0;
		
		Node s1 = NodeFactory.createVariable("u1");
		Set<Filter> s_filters = this.getKb().instantiateFilters("u1", Constants.SUBJECT);
		Node p1 = NodeFactory.createVariable("p1");
		Set<Filter> p_filters = this.getKb().instantiateFilters("p1", Constants.PREDICATE);		

		// prob(s1, p, o)
		double obj_pred_likelihood = 0;
		{
			PathPattern pattern1 = new PathPattern();
			Triple t1 = new Triple(s1, eventsPattern.getTriples().get(0).getPredicate(), eventsPattern.getTriples().get(0).getObject());
			pattern1.getTriples().add(t1);
			pattern1.getFilters().addAll(s_filters);
			obj_pred_likelihood = likelihood(pattern1);
		}
		
		// prob(s1, p1, o)
		double obj_likelihood = 0;
		{
			PathPattern pattern2 = new PathPattern();
			Triple t2 = new Triple(s1, p1, eventsPattern.getTriples().get(0).getObject());
			pattern2.getTriples().add(t2);
			pattern2.getFilters().addAll(s_filters);
			pattern2.getFilters().addAll(p_filters);
			obj_likelihood = likelihood(pattern2);
		}

		result = obj_pred_likelihood / obj_likelihood; 
		
		return result;
	}
	
	
	public double ic_basic(PathPattern favoriteEventsPattern) {
		double result = 0;
		result = -Math.log(likelihood(favoriteEventsPattern));
		return result;
	}	
	
	
	public double cost(PathPattern pattern) {
		double result = 0;
		result = this.getMax_ic() - this.ic(pattern);
		return result;
	}
}

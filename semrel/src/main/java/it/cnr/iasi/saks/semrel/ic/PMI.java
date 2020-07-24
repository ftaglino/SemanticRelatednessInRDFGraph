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
public class PMI extends Abstract_IC {
	private static PMI instance = null;
	
	protected PMI(KnowledgeBase kb) {
		super(kb);
	}
	
	public synchronized static PMI getInstance(KnowledgeBase kb){
    	if (instance == null){
    		instance = new PMI(kb);
    	}
    	return instance;
    }
	
	public double pmi(PathPattern pattern) {
		double result = 0;
		
		Node p = pattern.getTriples().get(0).getPredicate();
		Node o = pattern.getTriples().get(0).getObject();
		
		Node s1 = NodeFactory.createVariable("u1");
		Set<Filter> s1_filters = this.getKb().instantiateFilters("u1", Constants.SUBJECT);
		
		Node p1 = NodeFactory.createVariable("p1");
		Set<Filter> p1_filters = this.getKb().instantiateFilters("p1", Constants.PREDICATE);
		
		Node o1 = NodeFactory.createVariable("u2");
		Set<Filter> o1_filters = this.getKb().instantiateFilters("u2", Constants.OBJECT);

		double pred_obj_likelihood = 0;
		{
			PathPattern pattern1 = new PathPattern();
			Triple t1 = new Triple(s1, p, o);
			pattern1.getTriples().add(t1);
			pattern1.getFilters().addAll(s1_filters);
			pred_obj_likelihood = likelihood(pattern1);
		}
		
		double pred_likelihood = 0;
		{
			PathPattern pattern2 = new PathPattern();
			Triple t2 = new Triple(s1, p, o1);
			pattern2.getTriples().add(t2);
			pattern2.getFilters().addAll(s1_filters);
			pattern2.getFilters().addAll(o1_filters);
			pred_likelihood = likelihood(pattern2);
		}
		
		double obj_likelihood = 0;
		{
			PathPattern pattern3 = new PathPattern();
			Triple t3 = new Triple(s1, p1, o);
			pattern3.getTriples().add(t3);
			pattern3.getFilters().addAll(s1_filters);
			pattern3.getFilters().addAll(p1_filters);
			obj_likelihood = likelihood(pattern3);
		}
		result = Math.log(pred_obj_likelihood / 
							(pred_likelihood * obj_likelihood)
						);
		return result;
	}
	
	public double ic(PathPattern pattern) {
		double result = 0;
		double ic_simple = IC_Simple.getInstance(kb).ic(pattern);
		double pmi = this.pmi(pattern);
		result = ic_simple + pmi;
		return result;
	}
	
	public double max_pmi() {
		double result = 0;
		result = Math.log(countPossibleEvents() / 2);
		return result;
	}
	
	public double max_ic() {
		double result = 0;
		double max_ic_simple = IC_Simple.getInstance(this.getKb()).max_ic();
		double max_pim = this.max_pmi();
		result = max_ic_simple + max_pim; 
		return result;
	}
}

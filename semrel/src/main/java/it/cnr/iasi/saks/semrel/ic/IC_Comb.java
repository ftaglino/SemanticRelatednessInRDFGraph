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
public class IC_Comb extends Abstract_IC {
	private static IC_Comb instance = null;
	
	protected IC_Comb(KnowledgeBase kb) {
		super(kb);
	}
	
	public synchronized static IC_Comb getInstance(KnowledgeBase kb){
    	if (instance == null){
    		instance = new IC_Comb(kb);
    	}
    	return instance;
    }
	
	public double ic(PathPattern pattern) {
		double result = 0;
		if(pattern.getTriples().size()!=1);
		// throws an exception
		else {
			Node s = NodeFactory.createVariable("u1");
			Set<Filter> s_filters = kb.instantiateFilters("u1", Constants.SUBJECT);
			Node o = NodeFactory.createVariable("u2");
			Set<Filter> o_filters = kb.instantiateFilters("u2", Constants.OBJECT);
			
			double pred_ic = 0;
			{
				PathPattern p = new PathPattern();
				Triple t = new Triple(s, pattern.getTriples().get(0).getPredicate(), o);
				p.getTriples().add(t);
				p.getFilters().addAll(pattern.getFilters());
				p.getFilters().addAll(s_filters);
				p.getFilters().addAll(o_filters);
				pred_ic = ic_basic(p);
			}
			
			double obj_pred_ic = 0;
			{
				PathPattern p1 = new PathPattern();
				Node p = NodeFactory.createVariable("p1");
				Set<Filter> p_filters = kb.instantiateFilters("p1", Constants.PREDICATE);
				Triple t1 = new Triple(s, p, pattern.getTriples().get(0).getObject());
				p1.getTriples().add(t1);
				p1.getFilters().addAll(pattern.getFilters());
				p1.getFilters().addAll(s_filters);
				p1.getFilters().addAll(p_filters);
				obj_pred_ic = ic_basic(p1);
			}
			
			result = pred_ic+obj_pred_ic;
		}
		return result;
	}	
		
	public double max_ic() {
		double result = 0;
		result = 2*(-Math.log(1/((double)this.getKb().countAllTriples())));
		return result;
	}
}

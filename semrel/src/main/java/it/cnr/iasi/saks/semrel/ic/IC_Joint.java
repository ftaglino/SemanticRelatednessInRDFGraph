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
public class IC_Joint extends Abstract_IC {
	private static IC_Joint instance = null;
	
	protected IC_Joint(KnowledgeBase kb) {
		super(kb);
	}

	public synchronized static IC_Joint getInstance(KnowledgeBase kb){
    	if (instance == null){
    		instance = new IC_Joint(kb);
    	}
    	return instance;
    }
	
	public double ic(PathPattern pattern) {
		double result = 0;
		if(pattern.getTriples().size()!=1);
		// throws an exception
		else {
			double pred_ic = 0;
			{
				PathPattern p1 = new PathPattern();
				Triple t1;
				Node s = NodeFactory.createVariable("u1");
				Set<Filter> s_filters = this.getKb().instantiateFilters("u1", Constants.SUBJECT);
				Node o = NodeFactory.createVariable("u2");
				Set<Filter> o_filters = this.getKb().instantiateFilters("u2", Constants.OBJECT);
				t1 = new Triple(s, pattern.getTriples().get(0).getPredicate(), o);
				
				p1.getTriples().add(t1);
				p1.getFilters().addAll(s_filters);
				p1.getFilters().addAll(o_filters);
				pred_ic = ic_basic(p1);
			}
			
			double ic_conditioned = ic_conditioned(pattern);
			
			result = pred_ic + ic_conditioned;
		}
		return result;
	}
	
	public double max_ic() {
		double result = 0;
		result = 2*(-Math.log(1/((double)this.getKb().countAllTriples())));
		return result;
	}
	
	public double ic_conditioned(PathPattern pattern) {
		double result = 0;
		result = -Math.log(likelihood_conditioned(pattern));
		return result;
	}
}

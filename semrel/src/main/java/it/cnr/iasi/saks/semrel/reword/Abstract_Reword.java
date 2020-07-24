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
package it.cnr.iasi.saks.semrel.reword;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;

public class Abstract_Reword {
	protected KnowledgeBase kb;
	protected String direction = Constants.IN_OUT; 

	public KnowledgeBase getKb() {
		return kb;
	}

	public void setKb(KnowledgeBase kb) {
		this.kb = kb;
	}
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	private double pf(Node n, Node p, String in_out) {
		double result = 0;
		
		int numTriples_u1_p_n = 0;
		{
			PathPattern pattern1 = new PathPattern();
			Set<Filter> filters1 = new HashSet<Filter>();
			Node u1_1 = NodeFactory.createVariable("u1");
			filters1.addAll(this.getKb().instantiateFilters("u1", Constants.SUBJECT));
			Triple t1 = null;
			if(in_out.equals(Constants.IN))
				t1 = new Triple(u1_1, p, n);
			else if(in_out.equals(Constants.OUT)) 
				t1 = new Triple(n, p, u1_1);
			
			pattern1.getTriples().add(t1);
			pattern1.setFilters(filters1);
			numTriples_u1_p_n = this.getKb().countPathsByPattern(pattern1);
		}
				
		int numTriples_u1_p1_n = 0;
		{
			PathPattern pattern2 = new PathPattern();
			Set<Filter> filters2 = new HashSet<Filter>();
			Node u2_1 = NodeFactory.createVariable("u1");
			filters2.addAll(this.getKb().instantiateFilters("u1", Constants.SUBJECT));
			Node p2_1 = NodeFactory.createVariable("p1");
			filters2.addAll(this.getKb().instantiateFilters("p1", Constants.PREDICATE));
			Triple t2 = new Triple(u2_1, p2_1, n);
			pattern2.getTriples().add(t2);
			pattern2.setFilters(filters2);
			numTriples_u1_p1_n = this.getKb().countPathsByPattern(pattern2);
		}

		int numTriples_n_p1_u1 = 0;
		{
			PathPattern pattern3 = new PathPattern();
			Set<Filter> filters3 = new HashSet<Filter>();
			Node u3_1 = NodeFactory.createVariable("u1");
			filters3.addAll(this.getKb().instantiateFilters("u1", Constants.SUBJECT));
			Node p3_1 = NodeFactory.createVariable("p1");
			filters3.addAll(this.getKb().instantiateFilters("p1", Constants.PREDICATE));
			Triple t3 = new Triple(n, p3_1, u3_1);
			pattern3.getTriples().add(t3);
			pattern3.setFilters(filters3);
			numTriples_n_p1_u1 = this.getKb().countPathsByPattern(pattern3);
		}

		result = 	((double)numTriples_u1_p_n) /
					(((double)numTriples_u1_p1_n) + ((double)numTriples_n_p1_u1));
		
		return result;
	}
	
	public double pf_in(Node n, Node p) {
		double result = 0;
		result = pf(n, p, Constants.IN);
		return result;
	}
	
	public double pf_out(Node n, Node p) {
		double result = 0;
		result = pf(n, p, Constants.OUT);		
		return result;
	}	
		
	
	public double itf(Node p) {
		double result = 0;
		
		int numTriples_u1_p_u2 = 0;
		{
			PathPattern pattern = new PathPattern();
			Set<Filter> filters = new HashSet<Filter>();
			Node u1 = NodeFactory.createVariable("u1");
			filters.addAll(this.getKb().instantiateFilters("u1", Constants.SUBJECT));
			Node u2 = NodeFactory.createVariable("u2");
			filters.addAll(this.getKb().instantiateFilters("u2", Constants.OBJECT));
			Triple t = new Triple(u1, p, u2);
			pattern.getTriples().add(t);
			pattern.setFilters(filters);
			numTriples_u1_p_u2 = this.getKb().countPathsByPattern(pattern);
		}
		
		result = Math.log(((double) this.getKb().countAllTriples()) / ((double) numTriples_u1_p_u2));
		
		return result;
	}
	
	protected double pfitf(Node n, Node p, String in_out) {
		double result = 0;
		double pf = 0.0;
		if(in_out.equals(Constants.IN))
			pf = pf_in(n, p);
		else if(in_out.equals(Constants.OUT))
			pf = pf_out(n, p);

		double itf = itf(p);
		result = pf*itf;
		return result;
	}
	
	public double pfitf_in(Node n, Node p) {
		double result = 0;
		result = pfitf(n, p, Constants.IN);
		return result;
	}
	
	public double pfitf_out(Node n, Node p) {
		double result = 0;
		result = pfitf(n, p, Constants.OUT);
		return result;
	}	
	
	public Map<Node, Double> relatednessSpace_in(Node n) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		result = this.relatednessSpace_in(n);
		return result;
	}
	
	public Map<Node, Double> relatednessSpace_out(Node n) {
		Map<Node, Double> result = new HashMap<Node, Double>();
		result = this.relatednessSpace_out(n);
		return result;
	}
	
	public static double cosine(Map<Node, Double> m1, Map<Node, Double> m2) {
		double result = 0;
		if((m1.size() > 0) && (m2.size() > 0)) {
			Set<Node> keys = new HashSet<Node>();
			keys.addAll(m1.keySet());
			keys.addAll(m2.keySet());
			double dot_product = 0;		
			for(Node n:keys) {
				if((m1.get(n)!=null) && (m2.get(n)!=null))
					dot_product = dot_product + m1.get(n).doubleValue() * m2.get(n).doubleValue();
			}
			double n1_norm = norm(m1);
			double n2_norm = norm(m2);		
			result =	dot_product / (n1_norm*n2_norm);
		}
		return result;
	}
	
	/**
	 * Calculate the norm of one feature vector
	 * 
	 * @param feature of one cluster
	 * @return
	 */
	public static double norm(Map<Node, Double> features) {
		double result = 0.0;
		Set<Node> keys = features.keySet();
		double temp = 0.0;
		for(Node n:keys) {
			temp = temp + Math.pow(features.get(n).doubleValue(), 2);
		}
		result = Math.sqrt(temp);
		
		return result;
	}
}

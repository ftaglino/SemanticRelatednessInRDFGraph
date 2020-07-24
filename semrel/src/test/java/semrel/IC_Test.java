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
package semrel;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.RDFGraphImpl_Filtered;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.ic.IC_Simple;
import it.cnr.iasi.saks.semrel.ic.PMI;
import it.cnr.iasi.saks.semrel.ic.Relatedness;
import it.cnr.iasi.saks.semrel.ic.IC_Comb;
import it.cnr.iasi.saks.semrel.ic.IC_Joint;

/**
 * 
 * @author francesco
 *
 */
public class IC_Test {
	@Ignore	
	@Test
	public void ic() {
		KnowledgeBase kb = RDFGraphImpl.getInstance();
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"board");
		Node o = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		
		PathPattern pattern = new PathPattern();
		Triple t = new Triple(s,p,o);
		pattern.getTriples().add(t);
		
		IC_Simple ic_s = IC_Simple.getInstance(kb);
		System.out.println("1. "+ic_s.getMax_ic());
		IC_Joint ic_j = IC_Joint.getInstance(kb);
		System.out.println("2. "+ic_j.getMax_ic());
		IC_Comb ic_c = IC_Comb.getInstance(kb);
		System.out.println("3. "+ic_c.getMax_ic());
		PMI pmi = PMI.getInstance(kb);
		System.out.println("4. "+pmi.getMax_ic());
		
		double ic_simple = ic_s.ic(pattern);
		double ic_joint = ic_j.ic(pattern);
		double ic_comb = ic_c.ic(pattern);
		double ic_simple_plus_pmi = pmi.ic(pattern);
		
		double cost_simple = ic_s.cost(pattern);
		double cost_joint = ic_j.cost(pattern);
		double cost_comb = ic_c.cost(pattern);
		double cost_simple_plus_pmi = pmi.cost(pattern);
		
		System.out.println("ic_simple="+ic_simple);
		System.out.println("ic_joint="+ic_joint);
		System.out.println("ic_comb="+ic_comb);
		System.out.println("ic_s_plus_pmi="+ic_simple_plus_pmi);
		
		System.out.println("cost_basic="+cost_simple);
		System.out.println("cost_joint="+cost_joint);
		System.out.println("cost_comb="+cost_comb);
		System.out.println("cost_s_plus_pmi="+cost_simple_plus_pmi);
		
		Assert.assertTrue((ic_simple>=0) && (ic_joint>=0) && (ic_comb>=0) && (ic_simple_plus_pmi>=0));
	}
	
	@Ignore
	@Test
	public void paths_ic() {
		KnowledgeBase kb = RDFGraphImpl.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		int length = 2; 
		String mode = Constants.NOT_DIRECTED_PATH;
		Vector<Path> result = kb.paths(n1, n2, length, mode, Constants.ACYCLIC);
		System.out.println("RESULT");
		
		IC_Simple ic_s = IC_Simple.getInstance(kb);
		IC_Joint ic_j = IC_Joint.getInstance(kb);
		IC_Comb ic_c = IC_Comb.getInstance(kb);
		PMI pmi = PMI.getInstance(kb);
		
		System.out.println("N."+"\t"+"ic_simple"+"\t\t"+"ic_joint"+"\t\t"+"ic_comb");
		for(int i=0; i<result.size(); i++) { 
			double res_s = Relatedness.path_cost(result.get(i), ic_s);
			double res_j = Relatedness.path_cost(result.get(i), ic_j);
			double res_c = Relatedness.path_cost(result.get(i), ic_c);
			double res_p = Relatedness.path_cost(result.get(i), pmi);
			System.out.println(i+"\t"+res_s+"\t"+res_j+"\t"+res_c+"\t"+res_p);
		}
				
		Assert.assertTrue(true);
	}
	
	//@Ignore
	@Test
	public void semrel() {
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		int minLength = 1;
		int maxLength = 2; 
		String mode = Constants.NOT_DIRECTED_PATH;
		
		IC_Simple ic_s = IC_Simple.getInstance(kb);
		IC_Joint ic_j = IC_Joint.getInstance(kb);
		IC_Comb ic_c = IC_Comb.getInstance(kb);
		PMI pmi = PMI.getInstance(kb);
		
		double semrel_ic_s = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, Constants.ACYCLIC, ic_s);
		double semrel_ic_j = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, Constants.ACYCLIC, ic_j);
		double semrel_ic_c = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, Constants.ACYCLIC, ic_c);
		double semrel_ic_s_plus_pmi = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, Constants.ACYCLIC, pmi);
		
		System.out.println("RESULT");
		System.out.println(n1.getURI().toString()+", "+n2.getURI().toString());
		System.out.println("ic_simple"+"\t\t"+"ic_joint"+"\t\t"+"ic_comb"+"\t\t\t"+"ic_simple_plus_pmi");
		System.out.println(semrel_ic_s+"\t"+semrel_ic_j+"\t"+semrel_ic_c+"\t"+semrel_ic_s_plus_pmi);
		
		Assert.assertTrue(true);
	}
}

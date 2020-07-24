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

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.reword.Relatedness;
import it.cnr.iasi.saks.semrel.reword.Reword_Complete;
import it.cnr.iasi.saks.semrel.reword.Reword_Mip;
import it.cnr.iasi.saks.semrel.reword.Reword_Simple;

/**
 * 
 * @author francesco
 *
 */
public class Reword_Test {
	
	@Test
	public void semrel() {
		KnowledgeBase kb = RDFGraphImpl.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Rock_music");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Blues");
		int minLength = 1;
		int maxLength = 2;
		String mode = Constants.NOT_DIRECTED_PATH;
		boolean acyclic = true;
			
		Reword_Simple reword_simple = new Reword_Simple(kb);
		reword_simple.setDirection(Constants.IN);
		double semrel_reword_simple_in = Relatedness.semrel(n1, n2, reword_simple);
		reword_simple.setDirection(Constants.OUT);
		double semrel_reword_simple_out = Relatedness.semrel(n1, n2, reword_simple);
		reword_simple.setDirection(Constants.IN_OUT);
		double semrel_reword_simple_in_out = Relatedness.semrel(n1, n2, reword_simple);

		
		Reword_Mip reword_mip = new Reword_Mip(kb);
		reword_mip.setMinLength(minLength);
		reword_mip.setMaxLength(maxLength);
		reword_mip.setMode(mode);
		reword_mip.setAcyclic(acyclic);
		double semrel_reword_mip = Relatedness.semrel(n1, n2, reword_mip);
		
		Reword_Complete reword_complete = new Reword_Complete(kb);
		reword_complete.setMinLength(minLength);
		reword_complete.setMaxLength(maxLength);
		reword_complete.setMode(mode);
		reword_complete.setAcyclic(acyclic);
		double semrel_reword_complete = Relatedness.semrel(n1, n2, reword_complete);
		
		
		System.out.println("RESULT");
		System.out.println(n1.getURI().toString()+", "+n2.getURI().toString());
		System.out.println("reword_simple_in\t\treword_simple_out\t\treword_simple_in_out\t\treword_mip\t\treword_complete");
		System.out.println(semrel_reword_simple_in+"\t\t"+semrel_reword_simple_out+"\t\t"+semrel_reword_simple_in_out+"\t\t"+semrel_reword_mip+"\t\t"+semrel_reword_complete);

		Assert.assertTrue(true);
	}
/*
	@Ignore
	@Test
	public void pf_in() {
		KnowledgeBase kb = KBImpl.getInstance();
		
		Node n = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"industry");
		
		double pf_in = Relatedness.pf_in(n, p, kb);
		pf_in = Relatedness.pf_in(n, p, kb);
		
		System.out.println("pf_in="+pf_in);
	}
	
	@Ignore
	@Test
	public void pf_out() {
		KnowledgeBase kb = KBImpl.getInstance();
		
		Node n = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"publishe");
		
		double pf_out = Relatedness.pf_out(n, p, kb);
		pf_out = Relatedness.pf_out(n, p, kb);
		
		System.out.println("pf_out="+pf_out);
	}
*/
}

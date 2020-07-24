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
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.exclusivity.Relatedness;

/**
 * 
 * @author francesco
 *
 */
public class Exclusivity_Test {
	
	@Test
	public void semrel() {
		KnowledgeBase kb = RDFGraphImpl.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Google");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Andy_Bechtolsheim");
		int minLength = 1;
		int maxLength = 2; 
		String mode = Constants.NOT_DIRECTED_PATH;
		int k = Constants.MEAN;
		boolean acyclic = Constants.ACYCLIC;
		double alpha = 0.25;		
		double semrel_025 = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, k, alpha, acyclic);
		alpha = 0.50;		
		double semrel_050 = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, k, alpha, acyclic);
		alpha = 0.75;		
		double semrel_075 = Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, k, alpha, acyclic);

		
		System.out.println("RESULT");
		System.out.println(n1.getURI().toString()+", "+n2.getURI().toString());
		System.out.println("exclusivity_025\t\t\texclusivity_050\t\t\texclusivity_075");
		System.out.println(semrel_025+"\t\t"+semrel_050+"\t\t"+semrel_075+"\t\t");
	}
}

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
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_alpha;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_beta;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_cw;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_d;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_dw;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_gamma;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_i;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_iw;
import it.cnr.iasi.saks.semrel.ldsd.Relatedness;

/**
 * 
 * @author francesco
 *
 */
public class Ldsd_Test {
	
	@Test
	public void semrel() {
		KnowledgeBase kb = RDFGraphImpl.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Ridley_Scott");

		Ldsd_d ldsd_d = new Ldsd_d();
		Ldsd_dw ldsd_dw = new Ldsd_dw();
		Ldsd_i ldsd_i = new Ldsd_i();
		Ldsd_iw ldsd_iw = new Ldsd_iw();
		Ldsd_cw ldsd_cw = new Ldsd_cw();
		Ldsd_alpha ldsd_alpha = new Ldsd_alpha();
		Ldsd_beta ldsd_beta = new Ldsd_beta();
		Ldsd_gamma ldsd_gamma = new Ldsd_gamma();
		
		double semrel_ldsd_d = Relatedness.semrel(n1, n2, kb, ldsd_d);
		double semrel_ldsd_dw = Relatedness.semrel(n1, n2, kb, ldsd_dw);
		double semrel_ldsd_i = Relatedness.semrel(n1, n2, kb, ldsd_i);
		double semrel_ldsd_iw = Relatedness.semrel(n1, n2, kb, ldsd_iw);
		double semrel_ldsd_cw = Relatedness.semrel(n1, n2, kb, ldsd_cw);
		double semrel_ldsd_alpha = Relatedness.semrel(n1, n2, kb, ldsd_alpha);
		double semrel_ldsd_beta = Relatedness.semrel(n1, n2, kb, ldsd_beta);
		double semrel_ldsd_gamma = Relatedness.semrel(n1, n2, kb, ldsd_gamma);
		
		System.out.println("RESULT");
		System.out.println(n1.getURI().toString()+", "+n2.getURI().toString());
		System.out.println("ldsd_d"+"\t\t"+"ldsd_dw"+"\t\t"+"ldsd_i"+"\t\t"+"ldsd_iw"+"\t\t"+"ldsd_cw"+"\t\t"+"ldsd_alpha"+"\t\t"+"ldsd_beta"+"\t\t"+"ldsd_gamma");
		System.out.println(semrel_ldsd_d+"\t\t"+semrel_ldsd_dw+"\t\t"+semrel_ldsd_i+"\t\t"+semrel_ldsd_iw+"\t\t"+semrel_ldsd_cw+"\t\t"+semrel_ldsd_alpha+"\t\t"+semrel_ldsd_beta+"\t\t"+semrel_ldsd_gamma);		
//		System.out.println("\t\t"+"\t\t"+"\t\t"+"\t\t"+semrel_ldsd_gamma);
	}
}

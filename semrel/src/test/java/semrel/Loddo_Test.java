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
import it.cnr.iasi.saks.semrel.loddo.Lod_Jaccard;
import it.cnr.iasi.saks.semrel.loddo.Lod_Overlap;
import it.cnr.iasi.saks.semrel.loddo.Loddo;
import it.cnr.iasi.saks.semrel.loddo.Relatedness;

/**
 * 
 * @author francesco
 *
 */
public class Loddo_Test {
	
	@Test
	public void semrel() {
		KnowledgeBase kb = RDFGraphImpl.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Google");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Andy_Bechtolsheim");
		
		Loddo lod_jaccard = new Lod_Jaccard();  
		Loddo lod_overlap = new Lod_Overlap();
		
		double semrel_lod_jaccard = Relatedness.semrel(n1, n2, kb, lod_jaccard);
		double semrel_lod_overlap = Relatedness.semrel(n1, n2, kb, lod_overlap);
		
		System.out.println("RESULT");
		System.out.println(n1.getURI().toString()+", "+n2.getURI().toString());
		System.out.println("lod_jaccard"+"\t\t"+"lod_overlap");
		System.out.println(semrel_lod_jaccard+"\t"+semrel_lod_overlap);
	}
}

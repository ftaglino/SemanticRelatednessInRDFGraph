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
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraphImpl_Filtered;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.ft.Relatedness;

/**
 * 
 * @author francesco
 *
 */
public class Ft_Test {
	@Ignore
	@Test
	public void countHyponyms() {
		int result = 0;
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		Node n = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Person");
		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance();
		result = ft.countHyponyms(n, kb);
		System.out.println("countHyponyms("+n.getURI().toString()+")= "+result);
		
		Assert.assertTrue(result > 0);
	}
	
	@Ignore
	@Test
	public void weightOf__dbr_rdftype_dbo() {
		double result = 0;
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Barack_Obama");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Agent");
		
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		Triple t = new Triple(s, p, o);

		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance();
		result = ft.weightOf__dbr_rdftype_dbo(t, kb);

		System.out.println("weightOf__dbr_rdftype_dbo("+t.toString()+")= "+result);
		
		Assert.assertTrue(result >= 0);
	}
	
	@Ignore
	@Test
	public void weightOf__dbr_dbo_dbr() {
		double result = 0;
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Barack_Obama");
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"almaMater");
		Node o = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Harvard_Law_School");
		
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		Triple t = new Triple(s, p, o);

		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance();
		result = ft.weightOf__dbr_dbo_dbr(t, kb);

		System.out.println("weightOf__dbr_dbo_dbr("+t.toString()+")= "+result);
		
		Assert.assertTrue(result >= 0);
	}
	
	@Ignore
	@Test
	public void semrel() {
		double result = 0;
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Barack_Obama");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Roland_Burris");
		
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		
		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance();
		result = ft.semrel(n1, n2, kb);
		
		System.out.println("semrel("+n1.getURI().toString()+", "+n2.getURI().toString()+")= " + result);
	}
	
	
	@Test
	public void path_rel() {
		double result = 0;
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Barack_Obama");
		Node p = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"successor");
		Node o = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Roland_Burris");
		
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		
		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance();
		Path path = new Path();
		Triple t = new Triple(s, p, o);
		path.getTriples().add(t);
		result = ft.path_rel(path, kb);
		
		System.out.println("semrel("+s.getURI().toString()+", "+p.getURI().toString()+", "+o.getURI().toString()+")= " + result);
	}
	
	@Ignore
	@Test
	public void ic_basedOnTyping() {
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Agent");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Person");
		
		KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance();
		
		double ic = ft.ic_basedOnTyping(n1, kb);
		System.out.println("ic("+n1.getURI().toString()+")="+ic);
		
		ic = ft.ic_basedOnTyping(n2, kb);
		System.out.println("ic("+n2.getURI().toString()+")="+ic);
	}
}

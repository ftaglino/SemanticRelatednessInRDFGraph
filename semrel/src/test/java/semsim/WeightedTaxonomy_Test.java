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
package semsim;

import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semsim.SemsimEngine;
import it.cnr.iasi.saks.semsim.likelihood.BUT;
import it.cnr.iasi.saks.semsim.likelihood.CorpusBasedLikelihood;
import it.cnr.iasi.saks.semsim.likelihood.IntrinsicLikelihood;
import it.cnr.iasi.saks.semsim.likelihood.CF;
import it.cnr.iasi.saks.semsim.likelihood.AF;
import it.cnr.iasi.saks.semsim.likelihood.M3;
import it.cnr.iasi.saks.semsim.taxonomy.Pair;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;
import junit.framework.Assert;

/**
 * 
 * @author francesco
 *
 */
public class WeightedTaxonomy_Test {
	
	@Ignore
	@Test
	public void loading() {
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		
		Node s1 = NodeFactory.createVariable("s");
		Node p1 = NodeFactory.createURI(Constants.RDFS_LABEL);
		Node o1 = NodeFactory.createLiteral("Reliability");
		
		Triple t1 = new Triple(s1,p1,o1);
		PathPattern pattern = new PathPattern();
		pattern.getTriples().add(t1);
		Vector<Node> nodes = wt.nodesByPattern(pattern);
		for(Node n:nodes) {
			System.out.println(n.getURI());
		}

		Assert.assertTrue(true);
	}

	@Ignore
	@Test
	public void loading2() {
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		
		Node s1 = NodeFactory.createURI("*s");
		Node p1 = NodeFactory.createURI(Constants.RDFS_LABEL);
		Node o1 = NodeFactory.createLiteral("Database query processing");
		
		Node s2 = s1;
		Node p2 = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Node o2 = NodeFactory.createVariable("u1");
		
		Vector<String> ppa = new Vector<String>();
		ppa.add("");
		ppa.add("*");

		Triple t1 = new Triple(s1,p1,o1);
		Triple t2 = new Triple(s2,p2,o2);
		PathPattern pattern = new PathPattern();
		pattern.getTriples().add(t1);
		pattern.getTriples().add(t2);
		pattern.setPropertyPathAdornments(ppa);

		Vector<Node> nodes = wt.nodesByPattern(pattern);
		for(Node n:nodes) {
			System.out.println(n.getURI());
		}

		Assert.assertTrue(true);
	}

	
	@Ignore
	@Test
	public void node_labelMap() {
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();

		for(String k:wt.getIds()) {
			String label = wt.getLabelById(k);
			String id = wt.getIdByLabel(label);
			System.out.println(id+"-"+label);
			System.out.println(label+"-"+id);
		}
			
		String id = "http://ACM_hierarchy__all#642";
		String label = wt.getLabelById(id);
		System.out.println("label= "+label);
		
		Assert.assertTrue(true);
	}
	
	@Ignore
	@Test
	public void lub() {
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
//		String first = "http://ACM_hierarchy__all#1017";
//		String second = "http://ACM_hierarchy__all#1104";
		String first = "http://semsim.saks.iasi.cnr.it/CompInd_tourism#ArcheologicalSite";
		String second = "http://semsim.saks.iasi.cnr.it/CompInd_tourism#ArtGallery";
		Pair pair = new Pair(first, second);
		Vector<Node> lubs = wt.lub(pair);
		String first_label = wt.getLabelById(first);
		String second_label = wt.getLabelById(second);
		
		String first_name = wt.getNameById(first);
		String second_name = wt.getNameById(second);
		for(Node l:lubs) {
			String lub_label = wt.getLabelById(l.getURI().toString());
			System.out.println("lub("+first_label+", "+second_label+")="+lub_label);
			String lub_name = wt.getNameById(l.getURI().toString());
			System.out.println("lub("+first_name+", "+second_name+")="+lub_name);
		}
	}
	
	@Ignore
	@Test
	public void weighting() {
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		CorpusBasedLikelihood likelihood = new M3();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, coeff_option);
		wt.initWeights(likelihood, se.getAvs());
		
		System.out.println(wt.getWeights().size());
		for(Node n:wt.allClasses()) {
			String id = n.getURI().toString();
			double weight = wt.getWeights().get(id);
			System.out.println("weight("+id+")= "+weight);		
		}

	}
	
	@Ignore
	@Test
	public void id_times() {
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		CF likelihood = new CF();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, coeff_option);
		wt.initWeights(likelihood, se.getAvs());
		
		System.out.println(wt.getWeights().size());
		for(Node n:wt.allClasses()) {
			String id = n.getURI().toString();
			int times = likelihood.getClassId_times().get(id);
			System.out.println("times("+id+")= "+times);		
		}

	}
	
	
	@Test
	public void weighting_BUT() {
		String avsFile = "/semsim/tourism/avs/CompInd_tourism_avs.txt";
		String annotationMode = Constants.SEMSIM_BY_NAME;
		boolean coeff_option = false;
		
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		IntrinsicLikelihood likelihood = new BUT();
		SemsimEngine se = new SemsimEngine(wt, avsFile, annotationMode, coeff_option);
		wt.initWeights(likelihood);
		
		System.out.println(wt.getWeights().size());
		for(Node n:wt.allClasses()) {
			String id = n.getURI().toString();
			String name = wt.getNameById(id);
			double weight = wt.getWeights().get(id);
			System.out.println("weight("+name+")= "+weight);		
		}

	}
}



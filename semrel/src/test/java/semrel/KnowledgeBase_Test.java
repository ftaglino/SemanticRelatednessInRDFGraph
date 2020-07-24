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

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;

/**
 * 
 * @author francesco
 *
 */
public class KnowledgeBase_Test {

	@Ignore
	@Test
	public void nodesByPattern() {
		System.out.println("nodesByPattern");
	
		RDFGraphImpl kb = RDFGraphImpl.getInstance();

		Node s = NodeFactory.createURI("*s");
		Node o = NodeFactory.createVariable("o");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t = new Triple(s,p,o);
		PathPattern pattern = new PathPattern();
		pattern.getTriples().add(t);
		Vector<Node> nodes = kb.nodesByPattern(pattern);
		for(Node n:nodes) {
			System.out.println(n.getURI());
		}
		Assert.assertTrue(true);
	}
	
	@Ignore
	@Test
	public void countPathsByPattern() {
		System.out.println("countPathsByPattern");
		
		RDFGraphImpl kb = RDFGraphImpl.getInstance();
		
		Node s1 = NodeFactory.createVariable("s");
		Node o1 = NodeFactory.createVariable("o");
		Node p1 = NodeFactory.createURI(Constants.RDF_TYPE);
		
		Node s2 = NodeFactory.createVariable("o");
		Node o2 = NodeFactory.createURI(Constants.OWL_CLASS);
		Node p2 = NodeFactory.createURI(Constants.RDF_TYPE);
		
		PathPattern pattern = new PathPattern(); 
		Triple t = new Triple(s1, p1, o1);
		pattern.getTriples().addElement(t);
		t = new Triple(s2, p2, o2);
		pattern.getTriples().addElement(t);
		int n = kb.countPathsByPattern(pattern);
		System.out.println("n="+n);
		
		pattern = new PathPattern();
		t = new Triple(s1, p1, o1);
		pattern.getTriples().addElement(t);
		t = new Triple(s2, p2, o2);
		pattern.getTriples().addElement(t);
		n = kb.countPathsByPattern(pattern);
		System.out.println("n="+n);
		
		Assert.assertTrue(n>0);
	}
	
	@Ignore
	@Test
	public void countAllTriples() {
		System.out.println("countAllTriples");

		RDFGraphImpl kb = RDFGraphImpl.getInstance();
		double n = kb.countAllTriples();
		System.out.println("n="+n);
		Assert.assertTrue(n>0);
	}

	@Ignore
	@Test
	public void pathPatternEquality() {
		Node s1 = NodeFactory.createVariable("s");
		Node o1 = NodeFactory.createVariable("o");
		Node p1 = NodeFactory.createURI(Constants.RDF_TYPE);
		PathPattern pattern1 = new PathPattern(); 
		Triple t1 = new Triple(s1, p1, o1);
		Filter f1 = new Filter();
		f1.setValue(Filter.generateFilterIn_nodesInDBO("a"));
		pattern1.getTriples().add(t1);
		pattern1.getFilters().add(f1);
		
		Node s2 = NodeFactory.createVariable("s");
		Node o2 = NodeFactory.createVariable("o");
		Node p2 = NodeFactory.createURI(Constants.RDF_TYPE);
		PathPattern pattern2 = new PathPattern(); 
		Triple t2 = new Triple(s2, p2, o2);
		Filter f2 = new Filter();
		f2.setValue(Filter.generateFilterIn_nodesInDBO("a"));
		pattern2.getTriples().add(t2);
		pattern2.getFilters().add(f2);
		
		Assert.assertTrue(pattern1.equals(pattern2));
	}
	
	@Ignore
	@Test
	public void pathsByPattern() {
		Node s1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Barack_Obama");
		Node p1 = NodeFactory.createVariable("p1");
		Node o1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Illinois");
		Triple t1 = new Triple(s1,p1,o1);
		PathPattern pattern = new PathPattern();
		pattern.getTriples().add(t1);
		RDFGraphImpl kb = RDFGraphImpl.getInstance();
		Vector<Path> result = kb.pathsByPattern(pattern, Constants.ACYCLIC);
		for(int i=0; i<result.size(); i++)
			System.out.println("result("+i+1+")="+result.get(i).toString());
	}
	
//	@Ignore
	@Test
	public void paths() {
		RDFGraphImpl kb = RDFGraphImpl.getInstance();
		
		Node n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Apple_Inc.");
		Node n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Steve_Jobs");
		int minLength = 1;
		int maxLength = 2;
		String mode = Constants.NOT_DIRECTED_PATH;
		Vector<Path> result = kb.paths(n1, n2, minLength, maxLength, mode, Constants.ACYCLIC);
		System.out.println("RESULT");
		for(int i=0; i<result.size(); i++)
			System.out.println("result("+i+")="+result.get(i).toString());
		
		Assert.assertTrue(true);
	}
	
	@Ignore
	@Test
	public void nodeFreq() {
		RDFGraphImpl kb = RDFGraphImpl.getInstance();
		PathPattern pattern = new PathPattern();
		Node s = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+"Barack_Obama");
		// create predicate variable and instantiate predicate's filters 
		Node p = NodeFactory.createVariable("p1");
		Set<Filter> p_filters = kb.instantiateFilters("p1", Constants.PREDICATE);
		// create predicate variable and instantiate object's filters 
		Node o = NodeFactory.createVariable("u1");
		Set<Filter> o_filters = kb.instantiateFilters("u1", Constants.OBJECT);

		
		Triple t = new Triple(s,p,o);
						
		pattern.getTriples().add(t);
		pattern.getFilters().addAll(p_filters);
		pattern.getFilters().addAll(o_filters);
		
		System.out.println("isValid="+pattern.isValid());
				
		int freq = kb. countPathsByPattern(pattern);
		System.out.println("freq="+freq);
		
		Assert.assertTrue(freq>0);
	}
	
	@Ignore
	@Test
	public void updateCache() {
		RDFGraphImpl kb = RDFGraphImpl.getInstance();
		{
		PathPattern pattern = new PathPattern();
		Node s1 = NodeFactory.createVariable("u1");
		Set<Filter> filters = new HashSet<Filter>();
		filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
		Node p1 = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o1 = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Agent");
		Triple t = new Triple(s1,p1,o1);
		pattern.setFilters(filters);
		pattern.getTriples().add(t);
		
		kb.getCache().update(pattern, 2);
		}
		
		kb.getCache().printCache();
		
		PathPattern pattern2 = new PathPattern();
		Node s2 = NodeFactory.createVariable("u1");
		Set<Filter> filters2 = new HashSet<Filter>();
		filters2.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
		Node p2 = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o2 = NodeFactory.createURI(Constants.DBPEDIA_DBO_NS+"Agent");
		Triple t2 = new Triple(s2,p2,o2);
		pattern2.setFilters(filters2);
		pattern2.getTriples().add(t2);
		
		System.out.println(pattern2.toString());
		
		int x = kb.getCache().getNumPathsByPattern(pattern2);
		
		System.out.println("x="+x);
		
	} 
}

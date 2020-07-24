package it.cnr.iasi.saks.semrel;

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

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.sparql.SPARQLConnector;
import it.cnr.iasi.saks.semrel.sparql.SPARQLQueryCollector;
/**
 * 
 * @author francesco
 *
 */
public abstract class RDFGraph implements KnowledgeBase {
	
	private Set<Filter> p_filters = new HashSet<Filter>();
	private Set<Filter> so_filters = new HashSet<Filter>();

	private KBCache cache = new KBCache();
	private SPARQLConnector sc;
	private Object knowledgeSourceRef;
	private String graph;
		

	public KBCache getCache() {
		return cache;
	}

	public void setCache(KBCache cache) {
		this.cache = cache;
	}
	
	public Set<Filter> getP_filters() {
		return p_filters;
	}

	public void setP_filters(Set<Filter> p_filters) {
		this.p_filters = p_filters;
	}

	public Set<Filter> getSo_filters() {
		return so_filters;
	}

	public void setSo_filters(Set<Filter> so_filters) {
		this.so_filters = so_filters;
	}

	public String getGraph() {
		return graph;
	}

	public void setGraph(String graph) {
		this.graph = graph;
	}

	public Object getKnowledgeResourceRef() {
		return knowledgeSourceRef;
	}

	public void setKnowledgeResourceRef(Object knowledgeSourceRef) {
		this.knowledgeSourceRef = knowledgeSourceRef;
	}

	public SPARQLConnector getSc() {
		return sc;
	}

	public void setSc(SPARQLConnector sc) {
		this.sc = sc;
	}

	/**
	 * Count the nodes filling a given pattern. 
	 * @param pattern A PathPattern representing the search criteria.
	 * @param distinct {@link Constants.SPARQL_DISTINCT} or {@link Constants.SPARQL_NOT_DISTINCT} 
	 * @return
	 */
	public int countNodesByPattern(PathPattern pattern, String distinct) {
		int result = 0;
		result = this.getCache().getNumNodesByPattern(pattern);
		// if the answer is not in the cache then ... 
		if(result == -1) {
			// ... get it from the SPARQL endpoint ...
			
			result = SPARQLQueryCollector.countNodesByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern, distinct);
			// ... and store the result in the cache
			this.getCache().getNumNodesByPattern().put(pattern, result);
		}
		return result; 
	}
	
	/**
	 * Get the nodes corresponding to a given pattern.
	 * For the allowed values in the pattern, see the method countTriplesByPattern. 
	 * If the required information is not in the cache, it is asked to the SPARQL endpoint. 
	 * @param pattern
	 * @return
	 */
	public Vector<Node> nodesByPattern(PathPattern pattern) {
		Vector<Node> result = new Vector<Node>();
		//result = this.getCache().getNodesByPattern().get(pattern);
		// if the answer is not in the cache then ...
		//if(result == null) {
			// ... get it from the SPARQL endpoint ...
			result = SPARQLQueryCollector.nodesByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
			// ... and store the result in the cache
			//this.getCache().getNodesByPattern().put(pattern, result);
		//}
		return result;
	}
	

	/**
	 * Get the number of paths matching a given pattern.
	 * This is a special case of the generic one implemented by the countGraphExpressionsByPattern method
	 * A pattern is a Triple. Each node in the triple can be:
	 * (1) a non empty String URI, meaning that the triples' element is fixed. 
	 * (2) an empty String URI, meaning that the triple's element is not fixed but is not the requested info. 
	 * (3) a variable, meaning that the triple's element is the requested info.
	 * If the required information is not in the cache, it is asked to the SPARQL endpoint.
	 * @param pattern
	 * @return
	 */
	public int countPathsByPattern(PathPattern pattern) {
		int result = 0;
		result = this.getCache().getNumPathsByPattern(pattern);
		// if the answer is not in the cache then ... 
		if(result == -1) {
			// ... get it from the SPARQL endpoint ...
			result = SPARQLQueryCollector.countPathsByPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
			// ... and store the result in the cache
			//this.getCache().getNumPathsByPattern().put(pattern, result);
			this.getCache().update(pattern, result);
		}
		return result; 
	}
	
	public int countTriplesWithPredicateRDF_TYPE() {
		int result = 0;
		PathPattern p0 = new PathPattern();
		Node s = NodeFactory.createVariable("u1");
		Set<Filter> s_filters = this.instantiateFilters("u1", Constants.SUBJECT);
		Node o = NodeFactory.createVariable("u2");
		Set<Filter> o_filters = this.instantiateFilters("u2", Constants.OBJECT);
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t0 = new Triple(s, p, o);
		p0.getTriples().add(t0);
		p0.getFilters().addAll(s_filters);
		p0.getFilters().addAll(o_filters);
		result = this.getCache().getNumPathsByPattern(p0);
		if(!(result>0)) {
			Node s1, o1, p1;
			Node s2, o2, p2; 
			
			// 1 count all triples with predicate rdf:type and object as an OWL Class:
			//		<u1, rdf:type, u2> .
			//		<u2, rdf:type, owl:Class>
			s1 = NodeFactory.createVariable("u1");
			p1 = NodeFactory.createURI(Constants.RDF_TYPE);
			o1 = NodeFactory.createVariable("u2");
			
			s2 = NodeFactory.createVariable("u2");
			p2 = NodeFactory.createURI(Constants.RDF_TYPE);
			o2 = NodeFactory.createURI(Constants.OWL_CLASS);
	
			PathPattern pattern = new PathPattern(); 
			Triple t = new Triple(s1, p1, o1);
			pattern.getTriples().addElement(t);
			t = new Triple(s2, p2, o2);
			pattern.getTriples().addElement(t);
			
			int countAllTriplesWithPredicate_RDFType_and_ObjectInDBO = 
					this.countPathsByPattern(pattern);
	
			// 2 count all triples with predicate rdf:type and object owl:ObjectProperty: 
			//     <u1, rdf:type, owl:ObjectProperty> .
			//		filters on u1
			Set<Filter> filters = new HashSet<Filter>();
			s1 = NodeFactory.createVariable("u1");
			filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
			p1 = NodeFactory.createURI(Constants.RDF_TYPE);
			o1 = NodeFactory.createURI(Constants.OWL_OBJECT_PROPERTY);
			pattern = new PathPattern();
			t = new Triple(s1, p1, o1);
			pattern.getTriples().add(t);
			pattern.setFilters(filters);
	
			int countAllTriplesWithPredicate_RDFType_and_ObjectEqualsToObjectProperty = 
					this.countPathsByPattern(pattern);
			
			result = countAllTriplesWithPredicate_RDFType_and_ObjectInDBO +
					countAllTriplesWithPredicate_RDFType_and_ObjectEqualsToObjectProperty;
			//this.getCache().getNumPathsByPattern().put(p0, result);
			this.getCache().update(p0, result);
		}
		return result;
	}
	
	public int countAllTriples() {
		int result = 0;
		result = this.getCache().getAllTriplesNum();
		if(!(result>0)) {
			Node s1, o1, p1;
			Node s2, o2, p2; 
			PathPattern pattern = new PathPattern();
			
			int countAllTriplesWithPredicate_RDFType = this.countTriplesWithPredicateRDF_TYPE(); 
			
			// 1. count all triples with predicate as an Object Property in DBO
			//		<u1, p1, o1> . 
			//		<p1, rdf:type, owl:ObjectProperty>
			//		NO FILTERS ARE APPLIED
			s1 = NodeFactory.createVariable("u1");
			o1 = NodeFactory.createVariable("u2");
			p1 = NodeFactory.createVariable("p1");
			
			s2 = NodeFactory.createVariable("p1");
			o2 = NodeFactory.createURI(Constants.OWL_OBJECT_PROPERTY);
			p2 = NodeFactory.createURI(Constants.RDF_TYPE);
						
			pattern = new PathPattern(); 
			Triple t = new Triple(s1, p1, o1);
			pattern.getTriples().addElement(t);
			t = new Triple(s2, p2, o2);
			pattern.getTriples().addElement(t);
			
			// 1.1 count all triples with predicate in out/dbo.txt
			String fileName = getClass().getResource("/predicates/out/dbo.txt").getFile();
			int countAllTriplesWithPredicateIn_DBO_OUT = 
					this.countAllTriplesWithPredicateInFile(fileName, Constants.NOT_FILTERING);

			int countAllTriplesWithPredicateAsAnObjectPropertyInDBO = 	
					this.countPathsByPattern(pattern) - 
					countAllTriplesWithPredicateIn_DBO_OUT;

			// 2. count all triples with predicate in in/rdfs.txt
			fileName = getClass().getResource("/predicates/in/rdfs.txt").getFile();
			int countAllTriplesWithPredicateIn_RDFS_IN = 
					this.countAllTriplesWithPredicateInFile(fileName, Constants.FILTERING);
			
			// 3. count all triples with predicate in in/owl.txt
			fileName = getClass().getResource("/predicates/in/owl.txt").getFile();
			int countAllTriplesWithPredicateIn_OWL_IN = 
					this.countAllTriplesWithPredicateInFile(fileName, Constants.FILTERING);
			
			result = countAllTriplesWithPredicate_RDFType +
					countAllTriplesWithPredicateAsAnObjectPropertyInDBO +
					countAllTriplesWithPredicateIn_RDFS_IN +
					countAllTriplesWithPredicateIn_OWL_IN;
		}
		return result;
	}
	
	private int countAllTriplesWithPredicateInFile(String file, boolean filtering) {
		int result = 0;
		BufferedReader br = null;
        try {     
        	br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
            	if(!(line.trim().startsWith("#"))) {
	            	Node s1, o1, p1;
	            	Set<Filter> filters = new HashSet<Filter>();
	        		s1 = NodeFactory.createVariable("u1");
	        		o1 = NodeFactory.createVariable("u2");
	        		p1 = NodeFactory.createURI(line.trim());
	        		PathPattern pattern = new PathPattern();
	            	Triple t = new Triple(s1,p1,o1);
	            	pattern.getTriples().add(t);
	            	if(filtering) {
		            	filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
		            	filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
		            	pattern.setFilters(filters);
	            	}
	            	int partial = this.countPathsByPattern(pattern);
	            	result = result + partial;
            	}
            }

	    } catch (IOException e) {
	        e.printStackTrace();
	    } finally {
	        try {
	            if (br != null) {
	                br.close();
	            }
	        } catch (IOException ex) {
	            ex.printStackTrace();
	        }
	    }
        
        return result;		
	}
	
	public Vector<Path> pathsByPattern(PathPattern pattern, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		Vector<Path> temp = SPARQLQueryCollector.pathsByPathPattern(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
		if(acyclic) 
			for(Path p:temp)
				if(p.isAcyclic())
					result.add(p);
				else;
		else result = temp;
		return result;
	}
	
	public boolean pathExistence(PathPattern pattern) {
		boolean result = false;
		result = SPARQLQueryCollector.pathsExistence(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), pattern);
		return result;
	}
	
	public boolean pathExistence(Path path) {
		boolean result = false;
		result = SPARQLQueryCollector.pathExistence(this.getSc(), this.getKnowledgeResourceRef(), this.getGraph(), path);
		return result;
	}

	public Vector<Path> paths(Node n1, Node n2, int minLength, int maxLength, String mode, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		Set<Node> pathExtremes = new HashSet<Node>();
		pathExtremes.add(n1);
		pathExtremes.add(n2);
		if(this.getCache().getPaths(pathExtremes) != null)
			result = this.getCache().getPaths(pathExtremes);
		else {
			for(int l=minLength; l<=maxLength; l++)
				result.addAll(this.paths(n1, n2, l, mode, acyclic));
			this.getCache().getPaths().put(pathExtremes, result);
		}
		return result;
	}

	public int clearNodesByPattern() {
		int result = 0;
		result = this.getCache().clearNodesByPattern();
		return result;
	}
	
	public int clearNumNodesByPattern() {
		int result = 0;
		result = this.getCache().clearNumNodesByPattern();
		return result;
	}
	
	public int clearNumPathsByPattern() {
		int result = 0;
		result = this.getCache().clearNumPathsByPattern();
		return result;
	}

	public int clearPaths() {
		int result = 0;
		result = this.getCache().clearPaths();
		return result;
	}
	
	public void clearCache() {
		this.setCache(new KBCache());
	}
	
	/**
	 * Search and retrieve paths connecting n1 and n2.
	 * @param n1 {@link Node} a node at the extremity of the searched paths
	 * @param n2 {@link Node} a node at the extremity of the searched paths
	 * @param length The length of the searched paths that must be 1, 2 or 3.
	 * @param mode {@link Constants.DIRECTED_PATH} or {@link Constants.NO_DIRECTED_PATH} for assuming the graph as directed or not, respectively.
	 * @param acyclic if the retrieved p aths must be acyclic {@link Constraints.ACYCLIC} or not {@link Constraints.ACYCLIC} 
	 * @return {@link Vector} of {@link Path} representing the paths connecting he two nodes.
	 */
	public Vector<Path> paths(Node n1, Node n2, int length, String mode, boolean acyclic) {
		Vector<Path> result = new Vector<Path>();
		//Vector<Path> temp = SPARQLQueryCollector.getPaths(this.getEndpoint(), this.getGraph(), n1, n2, maxLength, mode, filters);
		Vector<Path> temp = new Vector<Path>(); 
		Set<Filter> filters = new HashSet<Filter>();
		// If the searched paths must be straight from n1 to n2, 
		if(mode.equals(Constants.DIRECTED_PATH)) {
			// If the length of the searched paths is equal to 1  
			if(length==1) {
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				PathPattern pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
			}
			// If the length of the searched paths is equal to 2
			else if(length==2) {
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				PathPattern pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
			}
			// If the length of the searched paths is equal to 3			
			else if(length==3) {
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				Node s3 = o2;
				Node p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				Node o3 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				Triple t3 = new Triple(s3,p3,o3);
				PathPattern pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
			}
		}

		// if the edges (predicates) are considered not directed (they can be traversed in any direction)
		if(mode.equals(Constants.NOT_DIRECTED_PATH)) {
			// If the length of the searched paths is equal to 1  
			if(length==1) {
//				1.1. path pattern = n1--(p1)->n2 (0)
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				PathPattern pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.setFilters(filters);
				result = this.pathsByPattern(pattern, acyclic);
//				1.2. path pattern = u1<-(p1)--u2 (1)
				s1 = n2;
				o1 = n1;
				t1 = new Triple(s1,p1,o1);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
			}
			// If the length of the searched paths is equal to 2
			else if(length==2) {
				// 2.1. path pattern = n1--(p1)->u1--(p2)->n2 (0-0)
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				PathPattern pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				result = this.pathsByPattern(pattern, acyclic);

				// 2.2. path pattern = n1--(p1)->u1<-(p2)--n2 (0-1)
				filters = new HashSet<Filter>();
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				s2 = n2;
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = o1;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 2.3. path pattern = n1<-(p1)--u1--(p2)->n2 (1-0)
				filters = new HashSet<Filter>();
				s1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = n1;
				s2 = s1;
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				result = this.pathsByPattern(pattern, acyclic);
				// 2.4. path pattern = n1<-(p1)--u1<-(p2)--n2 (1-1)
				filters = new HashSet<Filter>();
				s1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = n1;
				s2 = n2;
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = s1;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));

			}
			// If the length of the searched paths is equal to 3			
			if(length>=3) {
				// 3.1. path pattern = n1--(p1)->u1--(p2)->u2--(p3)->n2 (0-0-0)
				filters = new HashSet<Filter>();
				Node s1 = n1;
				Node p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				Node o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				Node s2 = o1;
				Node p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				Node o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				Node s3 = o2;
				Node p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				Node o3 = n2;
				Triple t1 = new Triple(s1,p1,o1);
				Triple t2 = new Triple(s2,p2,o2);
				Triple t3 = new Triple(s3,p3,o3);
				PathPattern pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.2. path pattern = n1--(p1)->u1--(p2)->u2<-(p3)--n2 (0-0-1)
				filters = new HashSet<Filter>();
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				s2 = o1;
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = o2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.3 path pattern = n1--(p1)->u1<-(p2)--u2--(p3)->n2 (0-1-0)
				filters = new HashSet<Filter>();
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				s2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = o1;
				s3 = s2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.4 path pattern = n1--(p1)->u1<-(p2)--u2<-(p3)--n2 (0-1-1)
				filters = new HashSet<Filter>();
				s1 = n1;
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
				s2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = o1;
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = s2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.5 path pattern = n1<-(p1)--u1--(p2)->u2--(p3)->n2 (1-0-0)
				filters = new HashSet<Filter>();
				s1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = n1;
				s2 = s1;
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				s3 = o2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.6 path pattern = n1<-(p1)--u1--(p2)->u2<-(p3)--n2 (1-0-1)
				filters = new HashSet<Filter>();
				s1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = n1;
				s2 = s1;
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.OBJECT));
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = o2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.7 path pattern = n1<-(p1)--u1<-(p2)--u2--(p3)->n2 (1-1-0)
				filters = new HashSet<Filter>();
				s1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = n1;
				s2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = s1;
				s3 = s2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = n2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
				// 3.8 path pattern = n1<-(p1)--u1<-(p2)--u2<-(p3)--n2 (1-1-1)
				filters = new HashSet<Filter>();
				s1 = NodeFactory.createVariable("u1");
				filters.addAll(this.instantiateFilters("u1", Constants.SUBJECT));
				p1 = NodeFactory.createVariable("p1");
				filters.addAll(this.instantiateFilters("p1", Constants.PREDICATE));
				o1 = n1;
				s2 = NodeFactory.createVariable("u2");
				filters.addAll(this.instantiateFilters("u2", Constants.SUBJECT));
				p2 = NodeFactory.createVariable("p2");
				filters.addAll(this.instantiateFilters("p2", Constants.PREDICATE));
				o2 = s1;
				s3 = n2;
				p3 = NodeFactory.createVariable("p3");
				filters.addAll(this.instantiateFilters("p3", Constants.PREDICATE));
				o3 = s2;
				t1 = new Triple(s1,p1,o1);
				t2 = new Triple(s2,p2,o2);
				t3 = new Triple(s3,p3,o3);
				pattern = new PathPattern();
				pattern.getTriples().add(t1);
				pattern.getTriples().add(t2);
				pattern.getTriples().add(t3);
				pattern.setFilters(filters);
				temp.addAll(this.pathsByPattern(pattern, acyclic));
			}
			
		}
		if(acyclic)
			for(Path p:temp)
				if(p.isAcyclic())
					result.add(p);
				else;
		else result = temp;
		return result;
	}
		
	public Set<Filter> instantiateFilters(String varName, String nodePosition) {
		Set<Filter> result = new HashSet<Filter>();
		if(nodePosition.equals(Constants.SUBJECT) || nodePosition.equals(Constants.OBJECT)) {
			for(Filter f:this.getSo_filters()) {
				Filter instance_f = new Filter(f.getValue().replaceAll("%%var%%", varName));
				result.add(instance_f);
			}
		}
		else if(nodePosition.equals(Constants.PREDICATE)) {
			for(Filter f:this.getP_filters()) {
				Filter instance_f = new Filter(f.getValue().replaceAll("%%var%%", varName));
				result.add(instance_f);
			}
			Filter instance_f = new Filter(Filter.generateFilterOut_nodesInDBO(varName));
			result.add(instance_f);
		}		
		return result;
	}
}

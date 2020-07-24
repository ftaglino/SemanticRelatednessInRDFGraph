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
package it.cnr.iasi.saks.semrel.ft;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.RDFGraphImpl_Filtered;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.ic.IC_Simple;

/**
 * 
 * @author francesco
 *
 */
public class Relatedness {
	private static Relatedness instance = null;
	private KnowledgeBase kb;
	protected static final Logger logger = LoggerFactory.getLogger(it.cnr.iasi.saks.semrel.ft.Relatedness.class);
	
	public synchronized static Relatedness getInstance(){
    	if (instance == null){
    		instance = new Relatedness();
    	}
    	return instance;
    }
	
	public double semrel(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		int minLength = 1;
		int maxLength = 2;
		String mode = Constants.NOT_DIRECTED_PATH;
		boolean acyclic = Constants.ACYCLIC;
		Vector<Path> paths = kb.paths(n1, n2, minLength, maxLength, mode, acyclic);
		if(paths.size() != 0) {
			paths = cleanPaths(n1, n2, paths, kb);
			double temp = 0;
    		int pathIndex = 1;
			for(Path path:paths) {
				this.logger.error(pathIndex+". "+path.toString());
				pathIndex ++;
				temp = temp + path_rel(path, kb);
			}
			result = temp/paths.size();
		}
		logger.error("#semrel: {}", result);
		return result;
	}
	
	public double path_rel(Path path, KnowledgeBase kb) {
//		System.out.println(path);
		double result = 0;
		double temp = 0;
		for(Triple t:path.getTriples()) {
			switch(tripleType(t)) {
				case Constants.DBR_RDFTYPE_DBO: {
					temp = temp + weightOf__dbr_rdftype_dbo(t, kb);
					break;
				}
				case Constants.DBR_DBO_DBR: {
					temp = temp + weightOf__dbr_dbo_dbr(t, kb);
					break;
				}
				case Constants.DBR_RDFSSEEALSO_DBR: {
					temp = temp + weightOf__dbr_rdfsSeeAlso_dbr(t, kb);
					break;
				}
				case Constants.DBR_RDFTYPE_OWLTHING: {
					temp = temp + 0;
					logger.error("#Triple: {}", t.toString());
					break;
				}
				default: {
					logger.error("#Triple: {}", t.toString());					
				}
			}
		}
		result = temp/path.size();
		logger.error("#Path rel: {}", result);
		return result;
	}
	
	public int tripleType(Triple t) {
		int result = 0;
		String s_type = nodeType(t.getSubject());
		String p_type = nodeType(t.getPredicate());
		String o_type = nodeType(t.getObject());
		
		if(s_type.equals(Constants.DBPEDIA_DBR_NS) 
				&& p_type.equals(Constants.RDF_TYPE)
				&& o_type.equals(Constants.DBPEDIA_DBO_NS))
			result = Constants.DBR_RDFTYPE_DBO;
		
		else if(s_type.equals(Constants.DBPEDIA_DBR_NS) 
				&& p_type.equals(Constants.DBPEDIA_DBO_NS)
				&& o_type.equals(Constants.DBPEDIA_DBR_NS))
			result = Constants.DBR_DBO_DBR; 
		
		else if(s_type.equals(Constants.DBPEDIA_DBR_NS) 
				&& p_type.equals(Constants.RDFS_SEEALSO)
				&& o_type.equals(Constants.DBPEDIA_DBR_NS))
			result = Constants.DBR_RDFSSEEALSO_DBR;
		
		else if(s_type.equals(Constants.DBPEDIA_DBR_NS) 
				&& p_type.equals(Constants.RDF_TYPE)
				&& o_type.equals(Constants.OWL_THING))
			result = Constants.DBR_RDFTYPE_OWLTHING; 

		return result;
	}
	
	public String nodeType(Node n) {
		String result = "";
		if(isDBR_resource(n))
			result = Constants.DBPEDIA_DBR_NS;
		else if(isDBO_resource(n))
			result = Constants.DBPEDIA_DBO_NS;
		else if(isRDFType(n))
			result = Constants.RDF_TYPE;
		else if(isRDFSeeAlso(n))
			result = Constants.RDFS_SEEALSO;
		return result;
	}
	
	public boolean isDBR_resource(Node n) {
		boolean result = false;
		if(n.getURI().toString().startsWith(Constants.DBPEDIA_DBR_NS))
			return true;
		return result;
	}
	
	public boolean isDBO_resource(Node n) {
		boolean result = false;
		if(n.getURI().toString().startsWith(Constants.DBPEDIA_DBO_NS))
			return true;
		return result;
	}
	
	public boolean isRDFType(Node n) {
		boolean result = false;
		if(n.getURI().toString().equals(Constants.RDF_TYPE))
			return true;
		return result;
	}
	
	public boolean isRDFSeeAlso(Node n) {
		boolean result = false;
		if(n.getURI().toString().equals(Constants.RDFS_SEEALSO))
			return true;
		return result;
	}
	 
	public double weightOf__dbr_rdftype_dbo(Triple t, KnowledgeBase kb) {
		double result = 0;
		Set<Node> types = typesOf(t.getSubject(), kb);
		Node s = mst(types, t.getObject(), kb);
		double ic_s = ic_normalized(s, kb);
		double ic_o = ic_normalized(t.getObject(), kb);
				
		result = (ic_o * (ic_s - ic_o  + 1)  + ic_s * (ic_s - ic_o  + 1)) *
				1 / (2*Math.pow(ic_max(kb), 2));
 		return result;
	}
	
	public double weightOf__dbr_dbo_dbr(Triple t, KnowledgeBase kb) {
		double result = 0;
		Node p_domain = domainOf(t.getPredicate(), kb);
		Node p_range = rangeOf(t.getPredicate(), kb);
		
		Set<Node> s_types = typesOf(t.getSubject(), kb);
		Node s = mst(s_types, p_domain, kb);
		Set<Node> o_types = typesOf(t.getObject(), kb);
		Node o = mst(o_types, p_range, kb);
		
		double ic_dom = ic_normalized(p_domain, kb);
		double ic_s = ic_normalized(s, kb);
		double ic_ran = ic_normalized(p_range, kb);
		double ic_o = ic_normalized(o, kb);
		
		result = ((ic_s * (ic_s - ic_dom + 1)) + (ic_o * (ic_o - ic_ran + 1))) *
				1 / (2*Math.pow(ic_max(kb), 2));
				
		return result;
	}
	
	public double weightOf__dbr_rdfsSeeAlso_dbr(Triple t, KnowledgeBase kb) {
		double result = 0;
		Set<Node> s_types = typesOf(t.getSubject(), kb);
		Set<Node> msts_s = msts(s_types, kb);
		double ic_s = 0d;
		for(Node n:msts_s) {
			double temp = ic_normalized(n, kb);
			if(temp > ic_s) ic_s = temp; 
		}
			
		Set<Node> o_types = typesOf(t.getObject(), kb);
		Set<Node> msts_o = msts(o_types, kb);
		double ic_o = 0d;
		for(Node n:msts_o) {
			double temp = ic_normalized(n, kb);
			if(temp > ic_o) ic_o = temp; 
		}
		
		result = Math.pow(
				((ic_s + 1) * (ic_o + 1))
				* 1 / Math.pow(ic_max(kb), 2),
				2);
		
		return result;

	}
	
	public Node domainOf(Node n, KnowledgeBase kb) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.RDFS_DOMAIN);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		
		Vector<Node> temp = kb.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result = temp.get(0);
		
		return result;
	}
	
	public Node rangeOf(Node n, KnowledgeBase kb) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.RDFS_RANGE);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		
		Vector<Node> temp = kb.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result = temp.get(0);
		
		return result;
	}
	
	public int countHyponyms(Node n, KnowledgeBase kb) {
		int result = 0;
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>(); 
		Node u = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
		Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
		Vector<String> ppa = new Vector<String>();
		ppa.add("*");
		Triple t = new Triple(u, p, n);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		pattern.setPropertyPathAdornments(ppa);
		result = kb.countNodesByPattern(pattern, Constants.SPARQL_DISTINCT);
		return result;
	}
	
	public int countDBOClasses(KnowledgeBase kb) {
		int result = 0;
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>(); 
		Node s = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.OWL_CLASS);
		Triple t = new Triple(s, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		result = kb.countNodesByPattern(pattern, Constants.SPARQL_DISTINCT);
		return result;
	}
	
	public double ic(Node n, KnowledgeBase kb) {
		double result = 0;
		if(n.getURI().toString().equals(Constants.OWL_THING))
			result = owlThing_ic(kb);
		else
			result = 1 - (
					Math.log(countHyponyms(n, kb)) / 
					Math.log(countDBOClasses(kb))
					);
		return result;		
	}
	
	/**
	 * IC is computed on the basis of the following likelihood (|<n,?p1,?o1>| + |<?o2,?p2,n>|) / |<?s3,?p3,?o3>| or |<?s1,n,?o1>| / |<?s2,?p2,?o2>|, depending whether n (nodeRole) is a SUBJECT/OBJECT or a PREDICATE   
	 * @param n
	 * @param nodeRole
	 * @param kb
	 * @return
	 */
	public double ic__(Node n, String nodeRole, KnowledgeBase kb) {
		double result = 0;
		
		IC_Simple ic_simple = IC_Simple.getInstance(kb);
		PathPattern pattern = new PathPattern();
		Triple t;
		Set<Filter> filters = new HashSet<Filter>();
		if(nodeRole.equals(Constants.SUBJECT) || nodeRole.equals(Constants.OBJECT)) {
			double temp = 0;
			Node u = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			Node p = NodeFactory.createVariable("p1");
			filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
			t = new Triple(n, p, u);
			pattern.getTriples().add(t);
			temp = ic_simple.ic_basic(pattern);
			
			pattern = new PathPattern();
			filters = new HashSet<Filter>();
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
			t = new Triple(u, p, n);
			pattern.getTriples().add(t);
			temp = temp + ic_simple.ic_basic(pattern);
			
			result = temp;
		}
		else if(nodeRole.equals(Constants.PREDICATE)) {
			Node u1 = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			Node u2 = NodeFactory.createVariable("u2");
			filters.addAll(kb.instantiateFilters("u2", Constants.OBJECT));
			t = new Triple(u1, n, u2);
			pattern.getTriples().add(t);
			result = ic_simple.ic_basic(pattern);
		}
			
		return result;		
	}
	
	//public double owlThing_ic(KnowledgeBase kb) {
	
	public double owlThing_ic(KnowledgeBase kb) {
		double result = 0.0;
		return result;
	}
	
	public double countFacts(KnowledgeBase kb) {
		double result = 0;
		
		PathPattern pattern = new PathPattern();
		Triple t;
		Set<Filter> filters = new HashSet<Filter>();
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.OWL_THING);
		t = new Triple(s, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		result = kb.countNodesByPattern(pattern, Constants.SPARQL_DISTINCT);
		
		return result;
	}
	
	public double ic_max(KnowledgeBase kb) {
		double result = 0;
		result = -Math.log(1/((double)countFacts(kb)));
		return result;
	}
	
	/**
	 * This IC method is valid only when n is SUBJECT or OBJECT, not in the case of PREDICATE. 
	 * However, it is computed on the basis of the following likelihood (|<n,?p1,?o1>| + |<?o2,?p2,n>|) / |<?s3,?p3,?o3>|  
	 * @param n
	 * @param kb
	 * @return
	 */
	public double ic_(Node n, KnowledgeBase kb) {
		double result = owlThing_ic(kb);
			
		if(!(n.getURI().toString().equals(Constants.OWL_THING))) {
			double temp = 0;
			
			IC_Simple ic_simple = IC_Simple.getInstance(kb);
			PathPattern pattern = new PathPattern();
			Triple t;
			Set<Filter> filters = new HashSet<Filter>();
			Node u = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
			Node p = NodeFactory.createVariable("p1");
			filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
			t = new Triple(n, p, u);
			pattern.getTriples().add(t);
			pattern.setFilters(filters);
			//temp = ic_simple.ic_basic(pattern);
			temp = kb.countPathsByPattern(pattern);
			
			pattern = new PathPattern();
			filters = new HashSet<Filter>();
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			filters.addAll(kb.instantiateFilters("p1", Constants.PREDICATE));
			t = new Triple(u, p, n);
			pattern.getTriples().add(t);
			pattern.setFilters(filters);
			temp = temp + kb.countPathsByPattern(pattern);
			
			double likelihood = temp/((double)kb.countAllTriples());
			result = -Math.log(likelihood);
		}	
		return result;		
	}
	
	/**
	 * This IC method is valid only when n is SUBJECT or OBJECT, not in the case of PREDICATE.
	 * IC is computed on the basis of the following likelihood |<?u1,rdf:type,?u2> . <?u2,rdfs:subClassOf*,?n>| / |<?s2,?p2,?o2>| 
	 * @param n
	 * @param kb
	 * @return
	 */
	public double ic_basedOnTyping(Node n, KnowledgeBase kb) {
		double result = 0;
		
		if((n.getURI().toString().equals(Constants.OWL_THING))) {
			result = 0;
		}
		else {
			double temp = 0;
			
			IC_Simple ic_simple = IC_Simple.getInstance(kb);
			PathPattern pattern = new PathPattern();
			Triple t1, t2;
			Set<Filter> filters = new HashSet<Filter>();
			Node u1 = NodeFactory.createVariable("u1");
			filters.addAll(kb.instantiateFilters("u1", Constants.SUBJECT));
			Node p1 = NodeFactory.createURI(Constants.RDF_TYPE);
			Node u2 = NodeFactory.createURI("*u2");
			filters.addAll(kb.instantiateFilters("u2", Constants.OBJECT));
			t1 = new Triple(u1, p1, u2);
			Node p2 = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
			t2 = new Triple(u2, p2, n);
			Vector<String> ppa = new Vector<String>();
			ppa.add("");
			ppa.add("*");
			pattern.getTriples().add(t1);
			pattern.getTriples().add(t2);
			pattern.setFilters(filters);
			pattern.setPropertyPathAdornments(ppa);
			//temp = ic_simple.ic_basic(pattern);
			temp = kb.countNodesByPattern(pattern,Constants.SPARQL_DISTINCT);
						
			double countFacts = ((double)this.countFacts(kb)); 
			//double likelihood = temp/((double)kb.countAllTriples());
			double likelihood = temp/countFacts;
			result = -Math.log(likelihood);
		}	
		return result;		
	}
	
	public double ic_normalized(Node n, KnowledgeBase kb) {
		double result = 0;
		result = ic_basedOnTyping(n, kb);
				;/// -Math.log(1/((double)kb.countAllTriples()));				
		return result;
	}
	
	
	public static Set<Node> typesOf(Node n, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(kb.instantiateFilters("u1", Constants.OBJECT));
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);

		Vector<Node> temp = kb.nodesByPattern(pattern);
				
		result.addAll(temp);
		
		return result;
	}
	
	public static boolean pathExists(PathPattern pattern, KnowledgeBase kb) {
		boolean result = false;
		
		return result;
	}
	
	public static Node mst(Set<Node> types, Node ancestorNode, KnowledgeBase kb) {
		Node result = ancestorNode;
		
		for(Node type:types) {
			Path path = new Path();
			Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
			Triple t = new Triple(type, p, result);
			Vector<String> ppa = new Vector<String>();
			ppa.add("*");
			path.getTriples().add(t);
			path.setPropertyPathAdornments(ppa);
			if(kb.pathExistence(path))
				result = type;
		}

		return result;
	}
	
	public static Set<Node> msts(Set<Node> types, KnowledgeBase kb) {
		Set<Node> result = new HashSet<Node>();
		result.add(NodeFactory.createURI(Constants.OWL_THING));
		
		Vector<String> ppa = new Vector<String>();
		ppa.add("*");
		for(Node type:types) {
			Node p = NodeFactory.createURI(Constants.RDFS_SUBCLASSOF);
			for(Node res:result) {
				Path path = new Path();
				Triple t = new Triple(type, p, res);
				path.getTriples().add(t);
				path.setPropertyPathAdornments(ppa);
				if(kb.pathExistence(path)) {
					result.add(type);
					result.remove(res);
				}
				else {
					Path path2 = new Path();
					Triple t2 = new Triple(res, p, type);
					path2.getTriples().add(t2);
					path2.setPropertyPathAdornments(ppa);
					if(!(kb.pathExistence(path2))) {
						result.add(type);	
					}
				}
			}
		}

		return result;
	}
	
	public static Vector<Path> cleanPaths(Node n1, Node n2, Vector<Path> paths, KnowledgeBase kb) {
		Vector<Path> result = new Vector<Path>();
			
		Node mst = NodeFactory.createURI(Constants.OWL_THING);
		for(Path path:paths) {
			if(path.size() == 2) {
				if(path.getTriples().get(0).getPredicate().getURI().toString().equals(Constants.RDF_TYPE) 
					&& path.getTriples().get(1).getPredicate().getURI().toString().equals(Constants.RDF_TYPE)) {
					Set<Node> types = new HashSet<Node>();
					types.add(mst);
					types.add(path.getTriples().get(0).getObject());
					mst = mst(types, NodeFactory.createURI(Constants.OWL_THING), kb);
				}
				else result.add(path);
			}
			else result.add(path);
		}
		if(!(mst.equals(NodeFactory.createURI(Constants.OWL_THING)))) {
			Path path = new Path();
			Triple t1 = new Triple(n1, NodeFactory.createURI(Constants.RDF_TYPE), mst);
			Triple t2 = new Triple(n2, NodeFactory.createURI(Constants.RDF_TYPE), mst);
			path.getTriples().add(t1);
			path.getTriples().add(t2);
			result.add(path);
		}
		
		return result;
	}
}

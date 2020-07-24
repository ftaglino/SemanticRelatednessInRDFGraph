package it.cnr.iasi.saks.semsim.ontology;

import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.ontology.OntModel;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Filter;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Ontology extends WeightedTaxonomy {
	
	private static Ontology instance = null;
	
	public Ontology() {
		super();
	}
	
	public synchronized static Ontology getInstance(){
    	if (instance == null){
    		instance = new Ontology();
    	}
    	return instance;
    }
	
	public Set<Node> allObjectProperties() {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern();
		Node s = NodeFactory.createVariable("u1");
		Node p = NodeFactory.createURI(Constants.RDF_TYPE);
		Node o = NodeFactory.createURI(Constants.OWL_OBJECT_PROPERTY);
		Triple t = new Triple(s, p, o); 
		pattern.getTriples().add(t);
		Vector<Node> temp = this.nodesByPattern(pattern);
		result.addAll(temp); 
		
		return result;
	}
	
	public Node rangeOf(Node n) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.RDFS_RANGE);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		
		Vector<Node> temp = this.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result = temp.get(0);
		
		return result;
	}

	public Node domainOf(Node n) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.RDFS_DOMAIN);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		
		Vector<Node> temp = this.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result = temp.get(0);
		
		return result;
	}
	
	public Set<Node> equivalentClass(Node n) {
		Set<Node> result = new HashSet<Node>();
		PathPattern pattern = new PathPattern();
		Set<Filter> filters = new HashSet<Filter>();
		Node p = NodeFactory.createURI(Constants.OWL_EQUIVALENT_CLASS);
		Node o = NodeFactory.createVariable("u1");
		filters.addAll(this.instantiateFilters("u1", Constants.OBJECT));
		Triple t = new Triple(n, p, o);
		pattern.getTriples().add(t);
		pattern.setFilters(filters);
		
		Vector<Node> temp = this.nodesByPattern(pattern);
		
		if(temp.size()>0)
			result.addAll(temp);
		
		return result;
	}	
}

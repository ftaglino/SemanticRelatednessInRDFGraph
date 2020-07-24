package modiag;

import java.io.InputStream;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

import com.fasterxml.jackson.core.json.WriterBasedJsonGenerator;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semrel.sparql.SPARQLOntModelConnector;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class Items {

	public static void main(String[] args) {
		String out_file = "/modiag/output/taxonomy_items.json";
		String content = "";
		boolean append = true;
		//SCRIVE SU FILE
		content = "{" +
				"\n \"concepts\":[";
		Utils.println(out_file, content, append);
		
		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		String ontoFile = "modiag/modiag_adni_ontology__v07_FT.owl";
		loadOWL(ontoModel, ontoFile);
		
		//String root = "http://www.modiag.it#AttentionAndCalculationItem";
		//String root = "http://www.modiag.it#SAItemByEvaluatedFeature";
		//String root = "http://www.modiag.it#SAItemByTest";
		String root = "http://www.modiag.it#StandardizedAssessmentItem";
		
		Vector<Element> standardizedAssessmentItem = askForDescendants(ontoModel, root);
		
		for(Element e:standardizedAssessmentItem) {
			content = writeJsonItems(e.getId(), e.getLabel(), e.getDescription());
			//SCRIVE SU FILE
			Utils.println(out_file, content, append);
		}
			
		//SCRIVE SU FILE
		content = "]" +
				"\n \"specializazion_pairs\":[";
		Utils.println(out_file, content, append);
		
		//System.out.println(standardizedAssessmentItem.size());
		
		for(Element e:standardizedAssessmentItem) {
			String parent = e.getId();
			Vector<String> children = askForChildren(ontoModel, parent);
			for(String c:children) {
				String child = c;
				content = writeJsonSpecialization(parent, child);
				Utils.println(out_file, content, append);
			}
		}
		//SCRIVE SU FILE
		content = "]\n}";
		Utils.println(out_file, content, append);
	}
	
	public static void loadOWL(OntModel ontoModel, String ontoFile) {
	    try 
	    {
	        InputStream in = FileManager.get().open(ontoFile);
	        try 
	        {
	            ontoModel.read(in, null);
	            //this.setXmlnsPrefix(ontoModel.getNsPrefixURI("base"));
	        } 
	        catch (Exception e) 
	        {
	            e.printStackTrace();
	        }
	        //LOGGER.info("Ontology " + ontoFile + " loaded.");
	    } 
	    catch (JenaException je) 
	    {
	        System.err.println("ERROR" + je.getMessage());
	        je.printStackTrace();
	        System.exit(0);
	    }
	}
	
	public static Vector<Element> askForDescendants(OntModel ontoModel, String id) {
		Vector<Element> result = new Vector<Element>();
		SPARQLOntModelConnector sc = new SPARQLOntModelConnector(); 
		String queryString = "SELECT ?res_id WHERE "
				+ "{ "
				+ 	"?res_id "+ "<"+Constants.RDFS_SUBCLASSOF+">+" +" <" + id + "> "
				+ "}";
		
		System.out.println(queryString);
		
		Vector<QuerySolution> query_results = sc.execQuery(queryString, ontoModel);
		
		for(int i=0; i<query_results.size(); i++) {
			Node n = query_results.elementAt(i).get("res_id").asNode();
			String res_id = n.getURI().toString();
			String res_label = askForLabel(ontoModel, res_id);
			if(res_label.equalsIgnoreCase(""))
				res_label = n.getLocalName();
			String res_comment = askForComment(ontoModel, res_id);
			Element e = new Element (res_id, res_label, res_comment);
			result.add(e);
		}
		
		return result;
	}
	
	public static Vector<String> askForChildren(OntModel ontoModel, String id) {
		Vector<String> result = new Vector<String>();
		SPARQLOntModelConnector sc = new SPARQLOntModelConnector(); 
		String queryString = "SELECT ?res_id WHERE "
				+ "{ "
				+ 	"?res_id "+ "<"+Constants.RDFS_SUBCLASSOF+">" +" <" + id + "> "
				+ "}";
		
		System.out.println(queryString);
		
		Vector<QuerySolution> query_results = sc.execQuery(queryString, ontoModel);
		
		for(int i=0; i<query_results.size(); i++) {
			String res_id = query_results.elementAt(i).get("res_id").asNode().getURI().toString();
			result.add(res_id);
		}
		
		return result;
	}
	
	public static String askForLabel(OntModel ontoModel, String id) {
		String result = "";
		SPARQLOntModelConnector sc = new SPARQLOntModelConnector(); 
		String queryString = "SELECT ?label WHERE "
				+ "{ "
				+ 	"<"+id+"> <"+Constants.RDFS_LABEL+">" +" ?label"
				+ "}";
				
		Vector<QuerySolution> query_results = sc.execQuery(queryString, ontoModel);
		
		for(int i=0; i<query_results.size(); i++) {
			result = query_results.elementAt(i).get("label").asLiteral().toString();
		}
		
		return result;
	}

	public static String askForComment(OntModel ontoModel, String id) {
		String result = "";
		SPARQLOntModelConnector sc = new SPARQLOntModelConnector(); 
		String queryString = "SELECT ?comment WHERE "
				+ "{ "
				+ 	"<"+id+"> <"+Constants.RDFS_COMMENT+">" +" ?comment"
				+ "}";
				
		Vector<QuerySolution> query_results = sc.execQuery(queryString, ontoModel);
		
		for(int i=0; i<query_results.size(); i++) {
			result = query_results.elementAt(i).get("comment").asLiteral().toString();
		}
		
		return result;
	}
	
	public static String writeJsonItems(String id, String label, String descr) {
		if(label.equalsIgnoreCase("")) label = id;
		String line = "{"
		+ "\"id\":\""+id+"\""
		+ ", "
		+ "\"label\":\""+label+"\""
		+ ", "
		+ "\"description\":\""+descr+"\"},";
		return line;
		//System.out.println(line);
	}
	
	public static String writeJsonSpecialization(String parent, String child) {
		String line = "{"
		+ "\"parent\":\""+parent+"\""
		+ ", "
		+ "\"child\":\""+child+"\"},";
		return line;
		//System.out.println(line);
		
	}
}

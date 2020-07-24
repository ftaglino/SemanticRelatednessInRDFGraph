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

public class Subject {

	public static void main(String[] args) {
		String out_file = "/modiag/output/subjectInfo.json";
		String content = "";
		boolean append = true;
		//SCRIVE SU FILE
		content = "{" +
				"\n \"subjectInfo\":[";
		Utils.println(out_file, content, append);
		
		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
		String ontoFile = "modiag/modiag_adni_ontology__v07_FT.owl";
		loadOWL(ontoModel, ontoFile);
		
		String root = "http://www.modiag.it#subjectInfo";
		
		Vector<SubjectInfo> info = askForDescendants(ontoModel, root);
		
		for(SubjectInfo e:info) {
			content = writeJsonSubjectInfo(e.getId(), e.getLabel(), e.getDescription(), e.getValuesMeaning());
			//SCRIVE SU FILE
			Utils.println(out_file, content, append);
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
	
	public static Vector<SubjectInfo> askForDescendants(OntModel ontoModel, String id) {
		Vector<SubjectInfo> result = new Vector<SubjectInfo>();
		SPARQLOntModelConnector sc = new SPARQLOntModelConnector(); 
		String queryString = "SELECT ?res_id WHERE "
				+ "{ "
				+ 	"?res_id "+ "<"+Constants.RDFS_SUBPROPERTYOF+">+" +" <" + id + "> "
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
			String res_meaning = askForMeaning(ontoModel, res_id);
			SubjectInfo e = new SubjectInfo (res_id, res_label, res_comment, res_meaning);
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

	public static String askForMeaning(OntModel ontoModel, String id) {
		String vm = "http://www.modiag.it#valuesMeaning"; 
		String result = "";
		SPARQLOntModelConnector sc = new SPARQLOntModelConnector(); 
		String queryString = "SELECT ?meaning WHERE "
				+ "{ "
				+ 	"<"+id+"> <"+vm+">" +" ?meaning"
				+ "}";
				
		Vector<QuerySolution> query_results = sc.execQuery(queryString, ontoModel);
		
		for(int i=0; i<query_results.size(); i++) {
			result = query_results.elementAt(i).get("meaning").asLiteral().toString();
		}
		
		return result;
	}
	
	public static String writeJsonSubjectInfo(String id, String label, String descr, String meaning) {
		if(label.equalsIgnoreCase("")) label = id;
		String line = "{"
		+ "\"id\":\""+id+"\""
		+ ", "
		+ "\"label\":\""+label+"\""
		+ ", "
		+ "\"description\":\""+descr+"\""
		+ ", "
		+ "\"valuesMeaning\":\""+meaning+"\"},";
		return line;
		//System.out.println(line);
	}
	
}

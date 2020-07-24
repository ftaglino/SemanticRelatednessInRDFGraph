package semsim.ACM;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.StringTokenizer;

import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.shared.JenaException;
import org.apache.jena.util.FileManager;

public class FixTaxonomy {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String ontoPrefix = "http://acm_onto/";
		String ontoFile_IN = "dataForTaxFix/acm_ontology_OLD.owl";
		//String ontoFile_OUT = "semrel/dataForTaxFix/acm_ontology.owl";
		String ontoFile_OUT = "c:/Users/francesco/Dropbox/FT-ADN/DATI-paper/acm_ontology_NEW.owl";
		String dataFile = "dataForTaxFix/ontology_concepts.txt";
		FixTaxonomy ft = new FixTaxonomy(); 
		OntModel ontoModel = ft.loadOWL(ontoFile_IN);

		
		BufferedReader br = null;
        try {     
        	
        	dataFile = "C:/Users/francesco/Dropbox/FT-ADN/DATI-paper/ontology_concepts.txt";
        	File f = new File(dataFile);

            br = new BufferedReader(new FileReader(f));
        	//br = new BufferedReader(new FileReader(ft.getClass().getResource(dataFile).getFile()));
        	
            String line;
            System.out.println("START");
            while ((line = br.readLine()) != null) {
            	StringTokenizer st = new StringTokenizer(line, "%%%%%%"); 
            	String id = st.nextToken().trim();
            	System.out.println(id);
            	String label_new = st.nextToken().trim();
            	String conceptURI = ontoPrefix+id;
            	if(!id.equalsIgnoreCase("0")) {
	            	OntClass c = ontoModel.getOntClass(conceptURI);
	            	String label_old = c.getLabel(null);
	            	c.removeLabel(label_old, null);
	            	c.addLabel(label_new, null);
            	}
            }
            ft.writeOntModelOnFile(ontoModel, ontoFile_OUT);
        }
        catch(Exception ex) {
        	ex.printStackTrace();
        }

		
	}
	
	public OntModel loadOWL(String ontoFile) {
		OntModel ontoModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM, null);
	    try 
	    {
	    	//ontoFile = "C:/Users/francesco/Dropbox/FT-ADN/DATI-paper/acm_ontology.owl";
	        InputStream in = FileManager.get().open(ontoFile);
	 
	        try 
	        {
	            ontoModel.read(in, null);
	        //  this.setXmlnsPrefix(ontoModel.getNsPrefixURI("base"));
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
	    
	    return ontoModel;
	}
	
	private void writeOntModelOnFile(OntModel m, String outFile) {
		try {
			FileOutputStream fos = new FileOutputStream(outFile);
			m.writeAll(fos, "RDF/XML");
			fos.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	
}

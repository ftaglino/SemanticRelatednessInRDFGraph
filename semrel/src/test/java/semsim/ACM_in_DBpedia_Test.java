package semsim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.junit.Ignore;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.PathPattern;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class ACM_in_DBpedia_Test {
	@Ignore
	@Test
	public void ACMinDBpedia() {
		RDFGraphImpl dbpedia = RDFGraphImpl.getInstance(); 
		WeightedTaxonomy wt = WeightedTaxonomy.getInstance();
		Set<Node> classes = wt.allClasses();
		Node p = NodeFactory.createVariable("p1");
		Node u = NodeFactory.createVariable("u1");
		String file_out_inDBpedia = "/semsim/ACM/ACMinDBpedia.txt";
		String file_out_outDBpedia = "/semsim/ACM/ACMoutDBpedia.txt";
		for(Node n:classes) {
			String id = n.getURI().toString();
			String label = wt.getLabelById(id);
			String adj_label = label.replace(' ', '_');
			Node c = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+adj_label); 

			PathPattern pattern1 = new PathPattern();
			Triple t1 = new Triple(c, p, u);
			pattern1.getTriples().add(t1);
			
			PathPattern pattern2 = new PathPattern();
			Triple t2 = new Triple(u, p, c);
			pattern2.getTriples().add(t2);

			if(dbpedia.pathExistence(pattern1) || dbpedia.pathExistence(pattern2)) {
				Utils.println(file_out_inDBpedia, label, true);
//				System.out.println(label);
			}
			else
				Utils.println(file_out_outDBpedia, label, true);
		}
	}
	
	
	@Test
	public void ACMinDBpedia_pathDiscovery() {
		RDFGraphImpl dbpedia = RDFGraphImpl.getInstance();
		String input_file = "/semsim/ACM/ACMinDBpedia.txt";
		String out_file_paths = "/semsim/ACM/ACMinDBpedia_paths.txt";
		String out_file_countPaths = "/semsim/ACM/ACMinDBpedia_countPaths.txt";
		Utils utils = new Utils();
		Vector<String> ids = utils.loadIds(input_file);
        
        int minLength = 1;
        int maxLength = 2;
        String mode = Constants.NOT_DIRECTED_PATH;
        boolean acyclic = true;
        for(int index_ext= 6; index_ext<ids.size(); index_ext++) {
        	Node n1 = NodeFactory.createURI(ids.get(index_ext));
        	for(int index_int= index_ext+1; index_int<ids.size(); index_int++) {
        		Node n2 = NodeFactory.createURI(ids.get(index_int));
        		Vector<Path> paths = dbpedia.paths(n1, n2, minLength, maxLength, mode, acyclic);
        		System.out.println(index_ext+", "+index_int+"["+paths.size()+"]");
        		if(paths.size()>0) {
	        		Utils.println(out_file_paths, ids.get(index_ext)+", "+ids.get(index_int)+" ["+paths.size()+"]", true);
	        		Utils.println(out_file_countPaths, ids.get(index_ext)+", "+ids.get(index_int)+" ["+paths.size()+"]", true);
	        		for(Path p:paths)
	        			Utils.println(out_file_paths, p.toString(), true);
        		}
            }	
        }
		
	}
}

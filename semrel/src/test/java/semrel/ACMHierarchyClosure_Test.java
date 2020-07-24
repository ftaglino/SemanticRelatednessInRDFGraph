package semrel;

import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

public class ACMHierarchyClosure_Test {

	@Test
	public void acmHierarchyClosure() {
		WeightedTaxonomy acm = WeightedTaxonomy.getInstance();
		String out_file = "/semsim/ACM/transitiveClosure.txt";
		
		Set<Node> classes = acm.allClasses();
		
        for(Node c:classes) {
        	Vector<Node> ancestors = acm.ancestors(c.getURI().toString());
        	ancestors.remove(NodeFactory.createURI(Constants.OWL_THING));
        	for(Node anc:ancestors) {
        		int dist = acm.distance(c, anc);
        		int c_depth = dist + acm.depth(anc);
        		int anc_depth = acm.depth(anc);
        		//String content = c.getLocalName()+" "+anc.getLocalName()+" "+
        		String content = acm.getLabelById(c.getURI().toString()).replaceAll(" ", "_")+" "+acm.getLabelById(anc.getURI().toString()).replaceAll(" ", "_")+" "+
        						c_depth+" "+anc_depth;
        		Utils.println(out_file, content, true);
        		//System.out.println(content);
        	}
        }
	}
	
}

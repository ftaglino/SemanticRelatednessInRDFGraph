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
package it.cnr.iasi.saks.semrel.reword;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.Utils;

public class Reword_Complete extends Reword_Mip {
	
	public Reword_Complete(KnowledgeBase kb) {
		super(kb);
	}
	
	public double semrel(Node n1, Node n2) {
		double result = 0;
		Vector<Path> paths = this.getKb().paths(n1, n2, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());
		
		Map<Path,Double> paths_informativeness = new HashMap<Path, Double>();
		for(Path p:paths) {
			paths_informativeness.put(p, this.path_informativeness(p));
		}

		List<Entry<Path, Double>> mip = Utils.findGreatest(paths_informativeness);
		
		Reword_Simple reword_simple = new Reword_Simple(this.getKb());  
		
		Map<Node, Double> n1_relSpace_in = reword_simple.relatednessSpace_in(n1);
		Map<Node, Double> n1_relSpace_out = reword_simple.relatednessSpace_out(n1);
		
		Map<Node, Double> n2_relSpace_in = reword_simple.relatednessSpace_in(n2);
		Map<Node, Double> n2_relSpace_out = reword_simple.relatednessSpace_out(n2);
		
		// Enrich relatedness spaces with contributions from the mip
		if(mip.size()>0) {
			for(Triple t:mip.get(0).getKey().getTriples()) {
				Node s =  t.getSubject();
				Node p =  t.getPredicate();
				Node o =  t.getObject();
				
				double temp_in = this.pfitf_in(o, p);
				if(n1_relSpace_in.containsKey(p))
					n1_relSpace_in.put(p, n1_relSpace_in.get(p) + temp_in);
				else n1_relSpace_in.put(p, temp_in);
				if(n2_relSpace_in.containsKey(p))
					n2_relSpace_in.put(p, n2_relSpace_in.get(p) + temp_in);
				else n2_relSpace_in.put(p, temp_in);
				
				double temp_out = this.pfitf_out(s, p);
				if(n1_relSpace_out.containsKey(p))
					n1_relSpace_out.put(p, n1_relSpace_out.get(p) + temp_out);
				else n1_relSpace_out.put(p, temp_out);
				if(n2_relSpace_out.containsKey(p))
					n2_relSpace_out.put(p, n2_relSpace_out.get(p) + temp_out);
				else n2_relSpace_out.put(p, temp_out);
			}
		}

		double cosine_in = cosine(n1_relSpace_in, n2_relSpace_in);
		double cosine_out = cosine(n1_relSpace_out, n2_relSpace_out);

		result = (cosine_in + cosine_out)/2;

		return result;
	}
	
}

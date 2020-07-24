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
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.Map.Entry;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;

import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.Utils;

public class Reword_Mip extends Abstract_Reword {
	int minLength = 0;
	int maxLength = 0;
	String mode = "";
	boolean acyclic = true;
	
	public Reword_Mip(KnowledgeBase kb) {
		super();
		this.setKb(kb);
	}

	public int getMinLength() {
		return minLength;
	}

	public void setMinLength(int minLength) {
		this.minLength = minLength;
	}

	public int getMaxLength() {
		return maxLength;
	}

	public void setMaxLength(int maxLength) {
		this.maxLength = maxLength;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public boolean isAcyclic() {
		return acyclic;
	}

	public void setAcyclic(boolean acyclic) {
		this.acyclic = acyclic;
	}

	public double path_informativeness(Path path) {
		double result = 0;
		double temp = 0;
		for(Triple t:path.getTriples())
			temp = 	temp + ( 
							this.pfitf_out(t.getSubject(), t.getPredicate()) +
							this.pfitf_in(t.getObject(), t.getPredicate())
						)/2; 
		
		result = temp / ((double)path.size());
		return result;
	}
	
	/**
	 * Find the most informative path (MIP) connecting nodes n1 and n2, and according to the specified constraints.
	 * @param n1
	 * @param n2
	 * @param minLength
	 * @param maxLength
	 * @param mode
	 * @param acyclic
	 * @return
	 */
	private List<Entry<Path, Double>> mip(Node n1, Node n2) {
		List<Entry<Path, Double>> result = new LinkedList<Entry<Path, Double>>();
		Vector<Path> paths = this.getKb().paths(n1, n2, this.getMinLength(), this.getMaxLength(), this.getMode(), this.isAcyclic());
		
		Map<Path,Double> paths_informativeness = new HashMap<Path, Double>();
		for(Path p:paths) {
			paths_informativeness.put(p, path_informativeness(p));
		}

		result = Utils.findGreatest(paths_informativeness);
		
		return result;
	}
	
	public double mip_informativeness(Node n1, Node n2) {
		double result = 0;
		if(this.mip(n1, n2).size() != 0)
			result = this.mip(n1, n2).get(0).getValue().doubleValue();
		return result;
	}
}

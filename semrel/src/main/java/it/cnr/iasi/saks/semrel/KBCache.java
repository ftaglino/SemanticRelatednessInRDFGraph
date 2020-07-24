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
package it.cnr.iasi.saks.semrel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
/**
 * 
 * @author francesco
 *
 */
public class KBCache {
	protected final Logger logger = LoggerFactory.getLogger(KBCache.class);

	// I would modify maps below, such that keys are represented by the hash codes of the pattern.
	// In this way, two objectives are met:
	//	(1) keys are shorter and the equals method is faster
	//	(2) the distinct option can be added to generate the hashcode 
	
	private int allTriplesNum = 438336517;
	//private int allTriplesNum = 0;
	private Map<PathPattern, Vector<Node>> nodesByPattern = new HashMap<PathPattern, Vector<Node>>();
	// number of nodes filling a given pattern. Only node for which isVariable() == true are considered in the pattern.
	private Map<PathPattern, Integer> numNodesByPattern = new HashMap<PathPattern, Integer>();
	private Map<PathPattern, Integer> numPathsByPattern = new HashMap<PathPattern, Integer>();
	private Map<Set<Node>, Vector<Path>> paths = new HashMap<Set<Node>, Vector<Path>>();

	public int getAllTriplesNum() {
		return this.allTriplesNum;
	}
	public void setAllTriplesNum(int allTriplesNum) {
		this.allTriplesNum = allTriplesNum;
	}
	public Map<PathPattern, Vector<Node>> getNodesByPattern() {
		return this.nodesByPattern;
	}
	public void setNodesByPattern(Map<PathPattern, Vector<Node>> nodesByPattern) {
		this.nodesByPattern = nodesByPattern;
	}
	public void setNumPathsByPattern(Map<PathPattern, Integer> numPathsByPattern) {
		this.numPathsByPattern = numPathsByPattern;
	}
	public Map<PathPattern, Integer> getNumPathsByPattern() {
		return this.numPathsByPattern;
	}
	
	public int getNumPathsByPattern(PathPattern pattern) {
		int result = -1;
		Integer num = numPathsByPattern.get(pattern);
		if(num != null)
			result = num;
		return result;
	}
	
	public Map<PathPattern, Integer> getNumNodesByPattern() {
		return numNodesByPattern;
	}
	
	public void setNumNodesByPattern(Map<PathPattern, Integer> numNodesByPattern) {
		this.numNodesByPattern = numNodesByPattern;
	}
	
	public int getNumNodesByPattern(PathPattern pattern) {
		int result = -1;
		Integer num = this.getNumNodesByPattern().get(pattern);
		if(num != null)
			result = num;
		return result;
	}
	public Map<Set<Node>, Vector<Path>> getPaths() {
		return paths;
	}
	public void setPaths(Map<Set<Node>, Vector<Path>> paths) {
		this.paths = paths;
	}	
	
	public Vector<Path> getPaths(Set<Node> pathExtremes) {
		Vector<Path> result = new Vector<Path>();
		result = this.getPaths().get(pathExtremes);
		if(result == null)
			this.setPaths(new HashMap<Set<Node>, Vector<Path>>());
		return result;
	}

	public void update(PathPattern pattern, int value) {
		this.getNumPathsByPattern().put(pattern, value);
	}
	
	public void printCache() {
		System.out.println("printCache");
		int i = 1;
		System.out.println(this.getNumPathsByPattern().size());
		for(PathPattern p:this.getNumPathsByPattern().keySet()) {
			System.out.println("pattern("+i+")");
			System.out.println(p.toString()+":"+this.getNumPathsByPattern().get(p));
			i++;
		}
		System.out.println("END printCache");
	}
	
	public int clearNodesByPattern() {
		int result = 0;
		result = this.getNodesByPattern().size();
		Map<PathPattern, Vector<Node>> empty = new HashMap<PathPattern, Vector<Node>>();
		this.setNodesByPattern(empty);
		return result;
	}
	
	public int clearNumNodesByPattern() {
		int result = 0;
		result = this.getNumNodesByPattern().size();
		Map<PathPattern, Integer> empty = new HashMap<PathPattern, Integer>();
		this.setNumNodesByPattern(empty);
		return result;
	}
	
	public int clearNumPathsByPattern() {
		int result = 0;
		result = this.getNumPathsByPattern().size();
		Map<PathPattern, Integer> empty = new HashMap<PathPattern, Integer>();
		this.setNumPathsByPattern(empty);
		return result;
	}
	
	public int clearPaths() {
		int result = 0;
		result = this.getPaths().size();
		Map<Set<Node>, Vector<Path>> empty = new HashMap<Set<Node>, Vector<Path>>();
		this.setPaths(empty);
		return result;
	}
}

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

import java.util.Set;
import java.util.Vector;

import org.apache.jena.graph.Node;

/**
 * 
 * @author francesco
 *
 */
public interface KnowledgeBase {
	public int countAllTriples();	
	public int countNodesByPattern(PathPattern pattern, String distinct);
	public Vector<Node> nodesByPattern(PathPattern pattern);
	public int countPathsByPattern(PathPattern pattern);
	public Vector<Path> pathsByPattern(PathPattern pattern, boolean acyclic);
	public boolean pathExistence(PathPattern pattern);
	public boolean pathExistence(Path path);
	public Vector<Path> paths(Node n1, Node n2, int length, String mode, boolean acyclic);
	public Vector<Path> paths(Node n1, Node n2, int minLength, int maxLength, String mode, boolean acyclic);
	public Set<Filter> instantiateFilters(String varName, String nodePosition);
	public void clearCache();
}

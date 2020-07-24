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
package it.cnr.iasi.saks.semrel.loddo;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.KnowledgeBase;

/**
 * 
 * @author francesco
 *
 */
public class Relatedness {
	public static double semrel(Node n1, Node n2, KnowledgeBase kb, Loddo method) {
		double result = 0;
		result = method.overlap(n1, n2, kb);
		return result;
	}
}

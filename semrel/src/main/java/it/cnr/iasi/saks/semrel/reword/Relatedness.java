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


import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.Constants;

/**
 * 
 * @author francesco
 *
 */
public class Relatedness {

	public static double semrel(Node n1, Node n2, Abstract_Reword method) {
		double result = 0;
		
		if (method instanceof Reword_Complete) {
			result = ((Reword_Complete) method).semrel(n1, n2);
		}
		else if(method instanceof Reword_Mip)
			result = ((Reword_Mip) method).mip_informativeness(n1, n2);
		
		else if (method instanceof Reword_Simple) {
			if(method.getDirection().equals(Constants.IN)) {
				double cosine = Abstract_Reword.cosine(method.relatednessSpace_in(n1), method.relatednessSpace_in(n2));
				result = cosine;
			}
			else if(method.getDirection().equals(Constants.OUT)) {
				double cosine = Abstract_Reword.cosine(method.relatednessSpace_out(n1), method.relatednessSpace_out(n2));
				result = cosine;
			}
			else if(method.getDirection().equals(Constants.IN_OUT)) {
				double cosine_in = Abstract_Reword.cosine(method.relatednessSpace_in(n1), method.relatednessSpace_in(n2));
				double cosine_out = Abstract_Reword.cosine(method.relatednessSpace_out(n1), method.relatednessSpace_out(n2));
				
				result = (cosine_in + cosine_out)/2;
			}
		}
			
		return result;
	}
	
}

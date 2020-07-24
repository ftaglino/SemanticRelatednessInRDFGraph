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
package it.cnr.iasi.saks.semrel.ldsd;

import java.util.Set;

import org.apache.jena.graph.Node;

import it.cnr.iasi.saks.semrel.KnowledgeBase;

/**
 * 
 * @author francesco
 *
 */
public class Ldsd_beta implements Ldsd {
	public double ldsd(Node n1, Node n2, KnowledgeBase kb) {
		double result = 0;
		
		double part_1 = 0;
		double part_2 = 0;
		double part_3 = 0;
		double part_4 = 0;
		
		Set<Node> preds_1 = Ldsd_dw.cd_o_nodes(n1, n2, kb);
		for(Node p:preds_1) {
			part_1 = part_1 + ( 
						Ldsd_d.cd(n1, n2, p, kb)/
						(1+Math.log(Ldsd_d.cd_p(n1, p, kb)))
					);
		}
		Set<Node> preds_2 = Ldsd_dw.cd_o_nodes(n2, n1, kb);
		for(Node p:preds_2) {
			part_2 = part_2 + ( 
					Ldsd_d.cd(n2, n1, p, kb)/
						(1+Math.log(Ldsd_d.cd_p(n2, p, kb)))
					);
		}
		
		Set<Node> preds_3 = Ldsd_iw.c_ii_u_nodes(n1, n2, kb);
		for(Node p:preds_3) {
			part_3 = part_3 + ( 
						Ldsd_alpha.c_ii_prime(n1, n2, p, kb)/
						(1 + 
							Math.log(
									(Ldsd_iw.c_ii_p(n1, p, kb) +
									Ldsd_iw.c_ii_p(n2, p, kb)) / 2
						))
					);
		}
		
		Set<Node> preds_4 = Ldsd_iw.c_io_u_nodes(n1, n2, kb);
		for(Node p:preds_4) {
			part_4 = part_4 + ( 
						Ldsd_alpha.c_io_prime(n1, n2, p, kb)/
						(1 + 
							Math.log(
									(Ldsd_iw.c_io_p(n1, p, kb) +
									Ldsd_iw.c_io_p(n2, p, kb)) / 2
						))
					);
		}
		
		result = 1/(1 + 
					part_1 + 
					part_2 +
					part_3 +
					part_4
					);		
		return result;
	}
}

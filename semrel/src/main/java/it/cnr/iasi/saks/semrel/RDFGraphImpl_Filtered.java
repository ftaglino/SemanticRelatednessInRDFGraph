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


/**
 * 
 * @author francesco
 *
 */
public class RDFGraphImpl_Filtered extends RDFGraph_Endpoint {
	private static RDFGraphImpl_Filtered instance = null;

	private RDFGraphImpl_Filtered() {
		super();
		this.load_p_filters();
		this.load_so_filters();
		this.countTriplesWithPredicateRDF_TYPE();
		this.countAllTriples();
	}
		
	public synchronized static RDFGraphImpl_Filtered getInstance(){
    	if (instance == null){
    		instance = new RDFGraphImpl_Filtered();
    	}
    	return instance;
    }

	private void load_p_filters() {
		Filter f1 = new Filter();
		f1.setValue(Filter.generateFilterIn_nodesIn_DBO_or_RDF_or_RDFS("%%var%%"));
		this.getP_filters().add(f1);
	}
	
	private void load_so_filters() {
		Filter f1 = new Filter();
		f1.setValue(Filter.generateFilterIn_nodesIn_DBR_or_DBO("%%var%%"));
		this.getSo_filters().add(f1);
	}
}

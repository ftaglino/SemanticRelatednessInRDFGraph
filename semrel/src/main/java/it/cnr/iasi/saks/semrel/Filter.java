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
public class Filter {
	private String value = "";

	public Filter() {
		super();
	}
	
	public Filter(String value) {
		super();
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}	
	
	public static String generateFilterIn_nodesInDBO(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"\")) ";	//DBO
		return result;
	}
	
	public static String generateFilterIn_nodesInDBR(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBR_NS+"\")) ";	//DBR
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBR_or_DBO(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.DBPEDIA_DBR_NS+"\")) ";				//DBR
		return result;
	}
	
	public static String generateFilterIn_nodesInRDF(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.RDF_NS+"\")) ";	//RDF
		return result;
	}
	
	public static String generateFilterIn_nodesInRDFS(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.RDFS_NS+"\")) ";	//RDFS
		return result;
	}
	
	public static String generateFilterIn_nodesInOWL(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.OWL_NS+"\")) ";	//RDFS
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBO_or_RDF_or_RDFS_or_OWL(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.RDF_NS+"|"	//RDF
				+"^"+Constants.RDFS_NS+"|"	//RDFS
				+"^"+Constants.OWL_NS+"\")) ";				//OWL
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBO_or_RDF_or_RDFS(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.RDF_NS+"|"	//RDF
				+"^"+Constants.RDFS_NS+"\")) ";				//RDFS
		return result;
	}
	
	public static String generateFilterIn_nodesIn_DBO_or_DBR_or_RDF_or_RDFS_or_OWL(String var) {
		String result = ""; 
		result = " FILTER(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_NS+"|"	//DBO
				+"^"+Constants.DBPEDIA_DBR_NS+"|"	//DBR
				+"^"+Constants.RDF_NS+"|"	//RDF
				+"^"+Constants.RDFS_NS+"|"	//RDFS
				+"^"+Constants.OWL_NS+"\")) ";				//OWL
		return result;
	}
	
	public static String generateFilterOut_nodesEquality(String v1, String v2) {
		String result = ""; 
		result = " FILTER(STR(?"+v1+") != STR(?"+v2+")) "; 
		return result;
	}
	
	public static String generateFilterOut_nodesInDBO(String var) {
		String result = ""; 
		result = " FILTER(!(REGEX(STR(?"+var+"), \"^"+Constants.DBPEDIA_DBO_WIKIPAGE+"|"	//wikiPageRedirects and wikiPageDisambiguates
				+"^"+Constants.DBPEDIA_DBO_THUMBNAIL+"\"))) ";				//thumbnail
		return result;
	}
	
	public static String generateFilterOut_isLiteral(String var) {
		String result = ""; 
		result = " FILTER(!(isLiteral(?"+var+")))";
		return result;
	}
	
	@Override
	public boolean equals(Object f) {
		boolean result = false;
		if(f instanceof Filter) {
			if(this.getValue().equals(((Filter)f).getValue()))
				result = true;
		}
		return result;
	}
	
	@Override
	public int hashCode(){
		return this.getValue().hashCode();
	}
}

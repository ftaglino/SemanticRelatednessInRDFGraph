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

import java.util.HashSet;
import java.util.Set;

/**
 * PathPattern is an extension of Path.
 * With respect to the super class, elements in a PathPattern's triple can be also variables, and filters can be applied to variables. 
 * Each node in the triple can be:
 * (1) a non empty String URI, meaning that the triples' element is fixed. 
 * (2) an empty String URI, meaning that the triple's element is not fixed but is not the requested info. 
 * (3) a variable, meaning that the triple's element is the requested info.
 * 
 * @author francesco
 *
 */
public class PathPattern extends Path {
	private Set<Filter> filters = new HashSet<Filter>();
	
	public PathPattern() {
		super();
	}
	
	public Set<Filter> getFilters() {
		return filters;
	}

	public void setFilters(Set<Filter> filters) {
		this.filters = filters;
	}

	
	public boolean isValid() {		
		boolean result = true; 
/*		
		Set<String> variables  = new HashSet<String>();
		for(Triple t:this.getTriples()) {
			if(t.getSubject().isVariable()) {
				if(!(this.getVars().containsKey(t.getSubject().toString().substring(1))))
					return false;
				else variables.add(t.getSubject().toString().substring(1));
			}
			if(t.getObject().isVariable()) {
				if(!(this.getVars().containsKey(t.getObject().toString().substring(1))))
					return false;
				else variables.add(t.getObject().toString().substring(1));
			}
			if(t.getPredicate().isVariable()) {
				if(!(this.getVars().containsKey(t.getPredicate().toString().substring(1))))
					return false;
				else variables.add(t.getPredicate().toString().substring(1));
			}
		}
		if(!(variables.containsAll(this.getVars().keySet()))) {
			return false;
		}
*/
		return result;
	}
	
	
	public boolean equals(PathPattern pp)  {
		boolean result = false;
		// if triples and propertyPathAdornments are the same
		if(super.equals(((Path)pp)))
			// check if the filters of the two patterns are equal
			if(this.getFilters().size() == pp.getFilters().size())
				if(this.getFilters().containsAll(pp.getFilters()))
					result = true;
		return result;
	}

	
	@Override
	public boolean equals(Object o) {
		if (o instanceof PathPattern) {
			PathPattern p = (PathPattern) o;
			return this.equals(p);
		}	
		return false;			
	}
	
	@Override
	public int hashCode(){
		int result = 0;
		result = this.toString().hashCode();
		return result;
	}
	
	/**
	 * build a string representing all the filters
	 * @return
	 */
	public String filtersToString() {
		String result = "";
		for(Filter f:this.getFilters())
			result = result + " \n . " + f.getValue() + " ";
		return result;
	}
	
	
	
	public String toString() {
		String result = "";
		result = super.toString()  
				+ this.filtersToString(); 
//				+"\n" + this.hashCode();
		return result;
	}
}

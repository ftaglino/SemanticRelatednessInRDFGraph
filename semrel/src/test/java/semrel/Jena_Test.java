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
package semrel;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;
import org.junit.Assert;
import org.junit.Test;

import it.cnr.iasi.saks.semrel.Constants;

/**
 * 
 * @author francesco
 *
 */
public class Jena_Test {
	
	@Test
	public void createResource() {
		Resource r = ResourceFactory.createResource(Constants.SAKS_NS+"Person");
		String uri = r.getURI();
		System.out.println("uri="+uri);
		
		Assert.assertNotNull(r);
	} 
	
	@Test
	public void createProperty() {
		Property p = ResourceFactory.createProperty(Constants.SAKS_NS, "friendOf");
		String uri = p.getURI();
		System.out.println("uri="+uri);
		
		p = ResourceFactory.createProperty(Constants.SAKS_NS, "friendOf");
		uri = p.getURI();
		System.out.println("uri="+uri);
		
		Assert.assertNotNull(p);
	}
	
	@Test
	public void createTriple() {
		Node s = NodeFactory.createURI("s");
		Node p = NodeFactory.createURI("p");
		Node o = NodeFactory.createURI("o");
		
		Triple t = Triple.create(s, p, o);
		System.out.println(t.toString());
		Assert.assertNotNull(t);
	}

}

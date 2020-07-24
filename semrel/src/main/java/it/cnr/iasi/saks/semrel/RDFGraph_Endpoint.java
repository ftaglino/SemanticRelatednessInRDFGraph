package it.cnr.iasi.saks.semrel;

import it.cnr.iasi.saks.semrel.sparql.SPARQLEndpointConnector;

public abstract class RDFGraph_Endpoint extends RDFGraph {
	public RDFGraph_Endpoint() {
		super();
		this.setKnowledgeResourceRef(Constants.SPARQL_DBPEDIA_ENDPOINT);
		this.setGraph(Constants.SPARQL_DBPEDIA_GRAPH);
		this.setSc(new SPARQLEndpointConnector());
	}
}

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

package it.cnr.iasi.saks.semrel.experiment;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.RDFGraphImpl;
import it.cnr.iasi.saks.semrel.RDFGraphImpl_Filtered;
import it.cnr.iasi.saks.semrel.KnowledgeBase;
import it.cnr.iasi.saks.semrel.Path;
import it.cnr.iasi.saks.semrel.Utils;
import it.cnr.iasi.saks.semrel.ic.IC_Comb;
import it.cnr.iasi.saks.semrel.ic.IC_Joint;
import it.cnr.iasi.saks.semrel.ic.IC_Simple;
import it.cnr.iasi.saks.semrel.ic.PMI;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_alpha;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_beta;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_cw;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_d;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_dw;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_gamma;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_i;
import it.cnr.iasi.saks.semrel.ldsd.Ldsd_iw;
import it.cnr.iasi.saks.semrel.loddo.*;
import it.cnr.iasi.saks.semrel.reword.Reword_Complete;
import it.cnr.iasi.saks.semrel.reword.Reword_Mip;
import it.cnr.iasi.saks.semrel.reword.Reword_Simple;
import it.cnr.iasi.saks.semrel.sparql.SPARQLEndpointConnector;

/**
 * 
 * @author francesco
 *
 */
public class Experiment {

	protected final Logger logger = LoggerFactory.getLogger(Experiment.class);
	
	public void run() {
		Map<String, Double> results = new HashMap<String, Double>();
		Vector<String> in_file = new Vector<String>();
		String f0 = "G26/G26_modified";
		String f1 = "KORE/1_KORE_modified_ITCompanies";
		String f2 = "KORE/2_KORE_modified_Hollywood";
		String f3 = "KORE/3_KORE_modified_VideoGames";
		String f4 = "KORE/4_KORE_modified_TelevisionSeries";
		String f5 = "KORE/5_KORE_modified_ChuckNorris";
		String f6 = "R122/R122_MODIFIED";
		String f7 = "WS252-Rel/WS252-Rel_modified";
		String f8 = "RG-65/RG-65_modified";
		String f9 = "MC-30/MC-30";
		
/*		in_file.add(f0);
*/		in_file.add(f1);
/*		in_file.add(f2);
		in_file.add(f3);
		in_file.add(f4);
		in_file.add(f5);
		in_file.add(f6);
		in_file.add(f7);		
		in_file.add(f8);		
		in_file.add(f9);
*/		
		Vector<Vector<String>> input = new Vector<Vector<String>>();
		
		BufferedReader br = null;
        
        String in = "";
        String out = "";
        String out_prox = "";
        
        int minLength = 1;
        int maxLength = 2;
		String mode = Constants.NOT_DIRECTED_PATH;
		boolean acyclic = Constants.ACYCLIC;
        
        for(int i=0; i<in_file.size(); i++) {
        	//KnowledgeBase kb = KBImpl.getInstance();
        	KnowledgeBase kb = RDFGraphImpl_Filtered.getInstance();
        	
        	in = "/semrel/datasets/"+in_file.elementAt(i)+".txt";
        	String filtered = (kb instanceof RDFGraphImpl_Filtered) ? filtered = "filtered" : "not_filtered";  
        	out = "/semrel/results/sr/"+filtered+"/"+in_file.elementAt(i)+"____"+maxLength+".csv";
        	out_prox = "/semrel/results/"+in_file.elementAt(i)+"____PROXIMITY_"+maxLength+".csv";
        	
        	//Utils.println("./ciao.txt", "CIAO", true);
        	
        	int row = 0;
        	int limit_up = 0;
        	int limit_down = 0;
        	row++;
        	
        	Utils.println(out, ";;HJ;;LODDO;;;;REWORD;;;;;;;;;;IC;;;;;;;;EXCLUSIVITY;;;;;;LDSD;;;;;;;;;;;;;;;;PROXIMITY;;", true);
        	row++;
        	Utils.println(out, ";;HJ;;LODJaccard;;LODOverlap;;COMPLETE;;MIP;;SIMPLE;;SIMPLE_IN;;SIMPLE_OUT;;SIMPLE;;JOINT;;COMB;;PMI;;alfa=0.25;;alfa=0.50;;alfa=0.75;;D;;DW;;I;;IW;;CW;;alpha;;beta;;gamma;;", true);
        	
	        try {     
	        	br = new BufferedReader(new FileReader(kb.getClass().getResource(in).getFile()));
	        	
	            String line;
	            while ((line = br.readLine()) != null) {
	            	row++;
	            	Node n1;
	            	Node n2;
	            	String name1;
	            	String name2;
	            	if(in_file.elementAt(i).equalsIgnoreCase(f0)) {limit_up = 3; limit_down = 28;}
	            	else if((in_file.elementAt(i).equalsIgnoreCase(f1))||
		        			(in_file.elementAt(i).equalsIgnoreCase(f2))||
		        			(in_file.elementAt(i).equalsIgnoreCase(f3))||
		        			(in_file.elementAt(i).equalsIgnoreCase(f4))) {
	        			if((row>2)&&(row<23)) {limit_up = 3; limit_down = 22;}
	        			else if((row>23)&&(row<44)) {limit_up = 24; limit_down = 43;}
	        			else if((row>44)&&(row<65)) {limit_up = 45; limit_down = 64;}
	        			else if((row>65)&&(row<86)) {limit_up = 66; limit_down = 85;}
	        			else if((row>86)&&(row<107)) {limit_up = 87; limit_down = 106;}
	        		}
	        		else if(in_file.elementAt(i).equalsIgnoreCase(f5)) {limit_up = 3; limit_down = 22;}
	        		else if(in_file.elementAt(i).equalsIgnoreCase(f6)) {limit_up = 3; limit_down = 124;}
	        		else if(in_file.elementAt(i).equalsIgnoreCase(f7)) {limit_up = 3; limit_down = 168;}
	        		else if(in_file.elementAt(i).equalsIgnoreCase(f8)) {limit_up = 3; limit_down = 67;}
	        		else if(in_file.elementAt(i).equalsIgnoreCase(f9)) {limit_up = 3; limit_down = 32;}
	            	StringTokenizer st = new StringTokenizer(line, ";");
	            	name1 = st.nextToken();
	            	name2 = st.nextToken();
	            	n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name1);
	            	n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name2);

	            	if((in_file.elementAt(i).equalsIgnoreCase(f0))&&row==14) {
	            		name1 = "Diego_Maradona";
		            	name2 = "Pelé";
	            		n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name1);
	            		n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name2);
	            	}
	            	else if((in_file.elementAt(i).equalsIgnoreCase(f3))&&row==91) {
	            		name1 = "Max_Payne";
		            	name2 = "K�rtsy_Hatakka";
		            	n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name1);
	            		n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name2);
	            	}
	            	else if((in_file.elementAt(i).equalsIgnoreCase(f3))&&row==94) {
	            		name1 = "Max_Payne";
		            	name2 = "Ragnar�k";
		            	n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name1);
	            		n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name2);	            		
	            	}
	            	else if((in_file.elementAt(i).equalsIgnoreCase(f2))&&row==92) {
	            		name1 = "Leonardo_DiCaprio";
		            	name2 = "Golden_Globe_Award_for_Best_Actor_�_Motion_Picture_Drama";
		            	n1 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name1);
	            		n2 = NodeFactory.createURI(Constants.DBPEDIA_DBR_NS+name2);

	            	}
	        				
	        		// find the paths connecting nodes n1 and n2
	        		Vector<Path> paths = kb.paths(n1, n2, minLength, maxLength, mode, acyclic);
	        		this.logger.error("paths("+name1+", "+name2+") = "+paths.size());
/*	        		
	        		int pathIndex = 1;
	        		for(Path path:paths) {
	        			this.logger.error(pathIndex+". "+path.toString());
	        			pathIndex ++;
	        		}
*/
	        		double semrel = 0;
	        		// HJ
	        		Utils.print(out, name1+";"+name2+";RANGO(D"+row+"#D$"+limit_up+":D$"+limit_down+"#0);;", true);
/*	        		
	        		// 1. LODDO
	        		// 1.1
	        		Lod_Jaccard lod_jaccard = new Lod_Jaccard();
	        		semrel = it.cnr.iasi.saks.semrel.loddo.Relatedness.semrel(n1, n2, kb, lod_jaccard);
	        		results.put(Constants.METHOD_LOD_JACCARD, semrel);
	        		System.out.println(Constants.METHOD_LOD_JACCARD +" = "+ semrel);
	        		// 1.2	        		
	        		Lod_Overlap lod_overlap = new Lod_Overlap();
	        		semrel = it.cnr.iasi.saks.semrel.loddo.Relatedness.semrel(n1, n2, kb, lod_overlap);
	        		results.put(Constants.METHOD_LOD_OVERLAP, semrel);
	        		System.out.println(Constants.METHOD_LOD_OVERLAP +" = "+ semrel);
	        		
	        		// 2. Reword
	        		// 2.1
	        		Reword_Simple reword_simple = new Reword_Simple(kb);
	        		reword_simple.setDirection(Constants.IN);
	        		semrel = it.cnr.iasi.saks.semrel.reword.Relatedness.semrel(n1, n2, reword_simple);
	        		results.put(Constants.METHOD_REWORD_SIMPLE_IN, semrel);
	        		System.out.println(Constants.METHOD_REWORD_SIMPLE_IN +" = "+ semrel);
	        		// 2.2
	        		reword_simple.setDirection(Constants.OUT);
	        		semrel = it.cnr.iasi.saks.semrel.reword.Relatedness.semrel(n1, n2, reword_simple);
	        		results.put(Constants.METHOD_REWORD_SIMPLE_OUT, semrel);
	        		System.out.println(Constants.METHOD_REWORD_SIMPLE_OUT +" = "+ semrel);
	        		// 2.3	        		
	        		reword_simple.setDirection(Constants.IN_OUT);
	        		semrel = it.cnr.iasi.saks.semrel.reword.Relatedness.semrel(n1, n2, reword_simple);
	        		results.put(Constants.METHOD_REWORD_SIMPLE_IN_OUT, semrel);
	        		System.out.println(Constants.METHOD_REWORD_SIMPLE_IN_OUT +" = "+ semrel);
	        		// 2.4
	        		Reword_Mip reword_mip = new Reword_Mip(kb);
	        		reword_mip.setMinLength(minLength);
	        		reword_mip.setMaxLength(maxLength);
	        		reword_mip.setMode(mode);
	        		reword_mip.setAcyclic(acyclic);
	        		semrel = it.cnr.iasi.saks.semrel.reword.Relatedness.semrel(n1, n2, reword_mip);
	        		results.put(Constants.METHOD_REWORD_MIP, semrel);
	        		System.out.println(Constants.METHOD_REWORD_MIP +" = "+ semrel);
	        		// 2.5
	        		Reword_Complete reword_complete = new Reword_Complete(kb);
	        		reword_complete.setMinLength(minLength);
	        		reword_complete.setMaxLength(maxLength);
	        		reword_complete.setMode(mode);
	        		reword_complete.setAcyclic(acyclic);
	        		semrel = it.cnr.iasi.saks.semrel.reword.Relatedness.semrel(n1, n2, reword_complete);
	        		results.put(Constants.METHOD_REWORD_COMPLETE, semrel);
	        		System.out.println(Constants.METHOD_REWORD_COMPLETE +" = "+ semrel);

	        		// 3. Proximity
	        		// 3.1
//	            	Vector<String> pair = new Vector<String>();
//	            	pair.add(nodeUri_1);
//	            	pair.add(nodeUri_2);
//	            	input.add(pair);
//	        		PrintOnFile.print(out, "RANGO(X"+row+"#X$"+limit_up+":X$"+limit_down+"#0);;", true);
	            	 
	        		// 4. IC
	        		// 4.1
	        		IC_Simple ic_simple = IC_Simple.getInstance(kb);
	        		semrel = it.cnr.iasi.saks.semrel.ic.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, acyclic, ic_simple);
	        		results.put(Constants.METHOD_IC_SIMPLE, semrel);
	        		System.out.println(Constants.METHOD_IC_SIMPLE +" = "+ semrel);
	        		// 4.2
	        		IC_Joint ic_joint = IC_Joint.getInstance(kb);
	        		semrel = it.cnr.iasi.saks.semrel.ic.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, acyclic, ic_joint);
	        		results.put(Constants.METHOD_IC_JOINT, semrel);
	        		System.out.println(Constants.METHOD_IC_JOINT +" = "+ semrel);
	        		// 4.3
	        		IC_Comb ic_comb = IC_Comb.getInstance(kb);
	        		semrel = it.cnr.iasi.saks.semrel.ic.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, acyclic, ic_comb);
	        		results.put(Constants.METHOD_IC_COMB, semrel);
	        		System.out.println(Constants.METHOD_IC_COMB +" = "+ semrel);
	        		// 4.4
	        		PMI pmi = PMI.getInstance(kb);
	        		semrel = it.cnr.iasi.saks.semrel.ic.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, acyclic, pmi);
	        		results.put(Constants.METHOD_IC_PLUS_PMI, semrel);
	        		System.out.println(Constants.METHOD_IC_PLUS_PMI +" = "+ semrel);
	      
	        		// 5. Exclusivity
	        		int k = Constants.MEAN;
	        		// 5.1
	        		double alpha = 0.25;
	        		semrel = it.cnr.iasi.saks.semrel.exclusivity.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, k, alpha, acyclic);
	        		results.put(Constants.METHOD_EXCLUSIVITY_025, semrel);
	        		System.out.println(Constants.METHOD_EXCLUSIVITY_025 +" = "+ semrel);
	        		// 5.2
	        		alpha = 0.50;
	        		semrel = it.cnr.iasi.saks.semrel.exclusivity.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, k, alpha, acyclic);
	        		results.put(Constants.METHOD_EXCLUSIVITY_050, semrel);
	        		System.out.println(Constants.METHOD_EXCLUSIVITY_050 +" = "+ semrel);
	        		// 5.3
	        		alpha = 0.75;
	        		semrel = it.cnr.iasi.saks.semrel.exclusivity.Relatedness.semrel(n1, n2, kb, minLength, maxLength, mode, k, alpha, acyclic);
	        		results.put(Constants.METHOD_EXCLUSIVITY_075, semrel);
	        		System.out.println(Constants.METHOD_EXCLUSIVITY_075 +" = "+ semrel);
	        		
	        		// 6. LDSD
	        		// 6.1
	        		Ldsd_d ldsd_d = new Ldsd_d();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_d);
	        		results.put(Constants.METHOD_LDSD_D, semrel);
	        		System.out.println(Constants.METHOD_LDSD_D +" = "+ semrel);
	        		// 6.2
	        		Ldsd_dw ldsd_dw = new Ldsd_dw();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_dw);
	        		results.put(Constants.METHOD_LDSD_DW, semrel);
	        		System.out.println(Constants.METHOD_LDSD_DW +" = "+ semrel);
	        		// 6.3
	        		Ldsd_i ldsd_i = new Ldsd_i();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_i);
	        		results.put(Constants.METHOD_LDSD_I, semrel);
	        		System.out.println(Constants.METHOD_LDSD_I +" = "+ semrel);
	        		// 6.4
	        		Ldsd_iw ldsd_iw = new Ldsd_iw();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_iw);
	        		results.put(Constants.METHOD_LDSD_IW, semrel);
	        		System.out.println(Constants.METHOD_LDSD_IW +" = "+ semrel);
	        		// 6.5
	        		Ldsd_cw ldsd_cw = new Ldsd_cw();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_cw);
	        		results.put(Constants.METHOD_LDSD_CW, semrel);
	        		System.out.println(Constants.METHOD_LDSD_CW +" = "+ semrel);
	        		// 6.6
	        		Ldsd_alpha ldsd_alpha = new Ldsd_alpha();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_alpha);
	        		results.put(Constants.METHOD_LDSD_ALPHA, semrel);
	        		System.out.println(Constants.METHOD_LDSD_ALPHA +" = "+ semrel);
	        		// 6.7
	        		Ldsd_beta ldsd_beta = new Ldsd_beta();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_beta);
	        		results.put(Constants.METHOD_LDSD_BETA, semrel);
	        		System.out.println(Constants.METHOD_LDSD_BETA +" = "+ semrel);
	        		// 6.8
	        		Ldsd_gamma ldsd_gamma = new Ldsd_gamma();
	        		semrel = it.cnr.iasi.saks.semrel.ldsd.Relatedness.semrel(n1, n2, kb, ldsd_gamma);
	        		results.put(Constants.METHOD_LDSD_GAMMA, semrel);
	        		System.out.println(Constants.METHOD_LDSD_GAMMA +" = "+ semrel);
*/	        		// 7. FT
	        		// 7.1
	        		it.cnr.iasi.saks.semrel.ft.Relatedness ft = it.cnr.iasi.saks.semrel.ft.Relatedness.getInstance(); 
	        		semrel = ft.semrel(n1, n2, kb);
	        		results.put(Constants.METHOD_FT, semrel);
	        		System.out.println(Constants.METHOD_FT +" = "+ semrel);

	            	Utils.print(out, "RANGO(F"+row+"#F$"+limit_up+":F$"+limit_down+"#0);"+results.get(Constants.METHOD_LOD_JACCARD)+";", true);
	            	Utils.print(out, "RANGO(H"+row+"#H$"+limit_up+":H$"+limit_down+"#0);"+results.get(Constants.METHOD_LOD_OVERLAP)+";", true);
	            	Utils.print(out, "RANGO(J"+row+"#J$"+limit_up+":J$"+limit_down+"#0);"+results.get(Constants.METHOD_REWORD_COMPLETE)+";", true);
	            	Utils.print(out, "RANGO(L"+row+"#L$"+limit_up+":L$"+limit_down+"#0);"+results.get(Constants.METHOD_REWORD_MIP)+";", true);
	            	Utils.print(out, "RANGO(N"+row+"#N$"+limit_up+":N$"+limit_down+"#0);"+results.get(Constants.METHOD_REWORD_SIMPLE_IN_OUT)+";", true);
	            	Utils.print(out, "RANGO(P"+row+"#P$"+limit_up+":P$"+limit_down+"#0);"+results.get(Constants.METHOD_REWORD_SIMPLE_IN)+";", true);
	            	Utils.print(out, "RANGO(R"+row+"#R$"+limit_up+":R$"+limit_down+"#0);"+results.get(Constants.METHOD_REWORD_SIMPLE_OUT)+";", true);
	            	Utils.print(out, "RANGO(T"+row+"#T$"+limit_up+":T$"+limit_down+"#0);"+results.get(Constants.METHOD_IC_SIMPLE)+";", true);
	            	Utils.print(out, "RANGO(V"+row+"#V$"+limit_up+":V$"+limit_down+"#0);"+results.get(Constants.METHOD_IC_JOINT)+";", true);
	            	Utils.print(out, "RANGO(X"+row+"#X$"+limit_up+":X$"+limit_down+"#0);"+results.get(Constants.METHOD_IC_COMB)+";", true);
	            	Utils.print(out, "RANGO(Z"+row+"#Z$"+limit_up+":Z$"+limit_down+"#0);"+results.get(Constants.METHOD_IC_PLUS_PMI)+";", true);
	            	Utils.print(out, "RANGO(AB"+row+"#AB$"+limit_up+":AB$"+limit_down+"#0);"+results.get(Constants.METHOD_EXCLUSIVITY_025)+";", true);
	            	Utils.print(out, "RANGO(AD"+row+"#AD$"+limit_up+":AD$"+limit_down+"#0);"+results.get(Constants.METHOD_EXCLUSIVITY_050)+";", true);
	            	Utils.print(out, "RANGO(AF"+row+"#AF$"+limit_up+":AF$"+limit_down+"#0);"+results.get(Constants.METHOD_EXCLUSIVITY_075)+";", true);
	            	Utils.print(out, "RANGO(AH"+row+"#AH$"+limit_up+":AH$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_D)+";", true);
	            	Utils.print(out, "RANGO(AJ"+row+"#AJ$"+limit_up+":AJ$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_DW)+";", true);
	            	Utils.print(out, "RANGO(AL"+row+"#AL$"+limit_up+":AL$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_I)+";", true);
	            	Utils.print(out, "RANGO(AN"+row+"#AN$"+limit_up+":AN$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_IW)+";", true);
	            	Utils.print(out, "RANGO(AP"+row+"#AP$"+limit_up+":AP$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_CW)+";", true);
	            	Utils.print(out, "RANGO(AR"+row+"#AR$"+limit_up+":AR$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_ALPHA)+";", true);
	            	Utils.print(out, "RANGO(AT"+row+"#AT$"+limit_up+":AT$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_BETA)+";", true);
	            	Utils.print(out, "RANGO(AV"+row+"#AV$"+limit_up+":AV$"+limit_down+"#0);"+results.get(Constants.METHOD_LDSD_GAMMA)+";", true);
	            	Utils.println(out, "RANGO(AX"+row+"#AX$"+limit_up+":AX$"+limit_down+"#0);"+results.get(Constants.METHOD_FT)+";", true);
	        		
	        		if(row==(limit_down)) {
	        			row++;
	        			String correlazione = "";
	        			correlazione = "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#C"+limit_up+":C"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#D"+limit_up+":D"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#E"+limit_up+":E"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#F"+limit_up+":F"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#G"+limit_up+":G"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#H"+limit_up+":H"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#I"+limit_up+":I"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#J"+limit_up+":J"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#K"+limit_up+":K"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#L"+limit_up+":L"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#M"+limit_up+":M"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#N"+limit_up+":N"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#O"+limit_up+":O"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#P"+limit_up+":P"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#Q"+limit_up+":Q"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#R"+limit_up+":R"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#S"+limit_up+":S"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#T"+limit_up+":T"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#U"+limit_up+":U"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#V"+limit_up+":V"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#W"+limit_up+":W"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#X"+limit_up+":X"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#Y"+limit_up+":Y"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#Z"+limit_up+":Z"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AA"+limit_up+":AA"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AB"+limit_up+":AB"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AC"+limit_up+":AC"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AD"+limit_up+":AD"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AE"+limit_up+":AE"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AF"+limit_up+":AF"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AG"+limit_up+":AG"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AH"+limit_up+":AH"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AI"+limit_up+":AI"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AJ"+limit_up+":AJ"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AK"+limit_up+":AK"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AL"+limit_up+":AL"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AM"+limit_up+":AM"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AN"+limit_up+":AN"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AO"+limit_up+":AO"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AP"+limit_up+":AP"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AQ"+limit_up+":AQ"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AR"+limit_up+":AR"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AS"+limit_up+":AS"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AT"+limit_up+":AT"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(C"+limit_up+":C"+limit_down+"#AU"+limit_up+":AU"+limit_down+");";
	        			correlazione = correlazione + "CORRELAZIONE(D"+limit_up+":D"+limit_down+"#AV"+limit_up+":AV"+limit_down+");";

		        		Utils.println(out, ";;"+correlazione, true);		        		
	        		}
	        	        		
	            }	            
	        } catch (IOException e) {
	            e.printStackTrace();
	        } finally {
	            try {
	                if (br != null) {
	                    br.close();
	                }
	            } catch (IOException ex) {
	                ex.printStackTrace();
	            }
	        }
	        // when a file is worked out, the cache is emptied
	        //kb.clearCache();

        }
	}

}

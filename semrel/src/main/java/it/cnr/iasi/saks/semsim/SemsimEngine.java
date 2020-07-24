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
package it.cnr.iasi.saks.semsim;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.ontology.OntClass;

import cern.colt.matrix.DoubleFactory2D;
import cern.colt.matrix.DoubleMatrix2D;
import it.cnr.iasi.saks.semrel.Constants;
import it.cnr.iasi.saks.semrel.ft.Relatedness;
import it.cnr.iasi.saks.semsim.taxonomy.Pair;
import it.cnr.iasi.saks.semsim.taxonomy.WeightedTaxonomy;

/**
 * 
 * @author francesco
 *
 */
public class SemsimEngine {

	private Map<String, Vector<OFVElem>> avs = new HashMap<String, Vector<OFVElem>>();
	private Map<String, Vector<OFVElem>> rvs = new HashMap<String, Vector<OFVElem>>();
	private Map<String, Double> ics = new HashMap<String, Double>(); 
	private WeightedTaxonomy wt;
	private DoubleMatrix2D consim_matrix;
	// maps classes'ids to consim matrix indexes
	private Map<String, Integer> id_index = new HashMap<String, Integer>();
	private String annotationMode;
	private String avsFile;
	private String rvsFile;
	
	public SemsimEngine(WeightedTaxonomy wt, String avsFile, String annotationMode, String ic_file, String ic_mode, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
		this.loadICs(ic_file, ic_mode);
	}
	
	public SemsimEngine(WeightedTaxonomy wt, boolean coeff_option) {
		this.wt = wt;
		this.init_idIndexMap();
		this.init_consimMatrix();
	}
	
	public SemsimEngine(WeightedTaxonomy wt, String avsFile, String annotationMode, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
	}
	
	public SemsimEngine(WeightedTaxonomy wt, String avsFile, String annotationMode, String rvsFile, boolean coeff_option) {
		this.init(wt, avsFile, annotationMode, coeff_option);
		this.setRvsFile(rvsFile);
		this.loadRequests(rvsFile, annotationMode, coeff_option);
	}

	private void init(WeightedTaxonomy wt, String avsFile, String annotationMode, boolean coeff_option) {
		this.wt = wt;
		this.setAnnotationMode(annotationMode);
		this.setAvsFile(avsFile);
		this.loadAnnotatedResources(avsFile, annotationMode, coeff_option);
		this.init_idIndexMap();
		this.init_consimMatrix();
	}
	
	public WeightedTaxonomy getWt() {
		return wt;
	}

	public void setWt(WeightedTaxonomy wt) {
		this.wt = wt;
	}

	public Map<String, Vector<OFVElem>> getAvs() {
		return avs;
	}

	public void setRvs(Map<String, Vector<OFVElem>> rvs) {
		this.rvs = rvs;
	}

	public Map<String, Vector<OFVElem>> getRvs() {
		return rvs;
	}

	public void setAvs(Map<String, Vector<OFVElem>> avs) {
		this.avs = avs;
	}
	
	public Map<String, Double> getIcs() {
		return ics;
	}

	public void setIcs(Map<String, Double> ics) {
		this.ics = ics;
	}

	public DoubleMatrix2D getConsim_matrix() {
		return consim_matrix;
	}

	public void setConsim_matrix(DoubleMatrix2D consim_matrix) {
		this.consim_matrix = consim_matrix;
	}

	public Map<String, Integer> getId_index() {
		return id_index;
	}

	public void setId_index(Map<String, Integer> id_index) {
		this.id_index = id_index;
	}

	public String getAnnotationMode() {
		return annotationMode;
	}

	public void setAnnotationMode(String annotationMode) {
		this.annotationMode = annotationMode;
	}

	public String getAvsFile() {
		return avsFile;
	}

	public void setAvsFile(String avsFile) {
		this.avsFile = avsFile;
	}

	public String getRvsFile() {
		return rvsFile;
	}

	public void setRvsFile(String rvsFile) {
		this.rvsFile = rvsFile;
	}
	
	private void loadOFV(String input_file, String annotationMode, String ofvType, boolean coeff_option) {
		System.out.println(input_file);
        try {
            BufferedReader b = new BufferedReader(new FileReader(this.getClass().getResource(input_file).getFile()));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
				String s = readLine;
				String res_num = s.substring(0, s.indexOf(Constants.SEMSIM_EQUAL)).trim();
				s = s.substring(s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_START)+1, s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_END));
				StringTokenizer st = new StringTokenizer(s,Constants.SEMSIM_ANNOTATION_SEPARATOR);
				Vector<OFVElem> ofv = new Vector<OFVElem>();
				while (st.hasMoreTokens()) {
					String id = ""; 
					String temp = st.nextToken().trim();
					String token = "";
					String coeff = "";
					if(coeff_option) {
						String ofv_elem_parts[] = temp.split(":");
						token = ofv_elem_parts[0].trim();
						coeff = ofv_elem_parts[1].trim();
					}
					else {
						token = temp;
						coeff = Constants.COEFF_NULL;
					}
					if(annotationMode.equals(Constants.SEMSIM_BY_LABEL))
						id = this.getWt().getIdByLabel(token);
					else if(annotationMode.equals(Constants.SEMSIM_BY_ID))
						id = token;
					else if(annotationMode.equals(Constants.SEMSIM_BY_NAME))
						id = this.getWt().getIdByName(token);
					
					OFVElem ofv_elem = new OFVElem(id, coeff);
					ofv.add(ofv_elem);
				}
				if(ofvType.equals(Constants.SEMSIM_AV))
					this.getAvs().put(res_num, ofv);
				else
					this.getRvs().put(res_num, ofv);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	public void loadAnnotatedResources(String input_file, String annotationMode, boolean coeff_option) {
		this.loadOFV(input_file, annotationMode, Constants.SEMSIM_AV, coeff_option);
	}
	
	public void loadRequests(String input_file, String annotationMode, boolean coeff_option) {
		this.loadOFV(input_file, annotationMode, Constants.SEMSIM_RV, coeff_option);
	}
	
	private void init_consimMatrix() {
		this.setConsim_matrix(DoubleFactory2D.sparse.make(this.getWt().size(), this.getWt().size()));
		this.getConsim_matrix().assign(Constants.CONSIM_MATRIX_INIT);
	}
	
	public double consim(Pair pair) {
		double result = 0.0d;
		
		double factor = computeFactor(pair);
		
		int index_first = this.getId_index().get(pair.getFirst());
		int index_second = this.getId_index().get(pair.getSecond());
		
		boolean checkSiblings = checkConsimBetweenOrderedSiblings(pair);
	
/*		if(elem>=0) {
			result = elem;
			System.out.print("inCache"+" ["+result+"]");
		}
		else */{
			
			if(checkSiblings) {
				result = consimBetweenOrderedSiblings(pair);
				this.getConsim_matrix().set(index_first, index_second, result);
			}
			else {
				String lub = this.bestLubId(this.getWt().lub(pair));
							
				double lub_ic = this.ic(lub);
				double first_ic = this.ic(pair.getFirst());
				double second_ic = this.ic(pair.getSecond());
				
				result = 2 * lub_ic / (first_ic + second_ic);
				this.getConsim_matrix().set(index_first, index_second, result);
				//System.out.println(lub.substring(lub.indexOf("#")+1)+" ["+result+"]");
			}
		}
		
		return result;
	}
	
	public double semsim_WithSpecialConcepts_FOR_AXED(Vector<OFVElem> orig_rv, Vector<OFVElem> av, boolean coeff_option) {
		double result = 0;
				
		Set<String> specialConcepts_label = new HashSet<String>();
		specialConcepts_label.add("InStock_Giacenza");
		specialConcepts_label.add("CostoAcquisto");
		specialConcepts_label.add("Interesse");
		
		double percentage_dealer = 0.6d;
		double percentage_client = 1.0d - percentage_dealer;
		double[] percs = {percentage_dealer, percentage_client};
		
		double[] semsim_with_percs = {0.0d, 0.0d};
		
		Vector<Vector<OFVElem>> requests = new Vector<Vector<OFVElem>>();
		Vector<OFVElem> rv_dealer = new Vector<OFVElem>();
		Vector<OFVElem> rv_client = new Vector<OFVElem>();
				
		for(OFVElem elem:orig_rv) {
			boolean special = false;
			for(String sc:specialConcepts_label) {
				if(this.getWt().getLabelById(elem.getConc_id()).startsWith(sc)) {
					special = true;
				}
			}
			if(special)
				rv_dealer.add(elem);
			else 
				rv_client.add(elem);

		}
				
		requests.add(rv_dealer);
		requests.add(rv_client);
		
		
		for(int z=0; z<2; z++) {
		Vector<OFVElem> rv = requests.get(z); 
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i).getConc_id(), rv.get(j).getConc_id());
//					System.out.println(i + "\t" + j + "\t" + pair);
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
//				for (int k = rvSize; k < (rvSize - diff); k++) {
//					semsimMatrix[i][k] = 1;
//					semsimValues[i][k] = 0;
//				}
			}
			
			for (int j = 0; j < avSize; j++) {
				for (int i = rvSize; i < avSize; i++) {
					semsimMatrix[j][i] = 1;
					semsimValues[j][i] = 0;
				}

			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		int assign[][] = ha.computeAssignments(semsimMatrix);

//		for(int k=0; k<assign.length; k++)
//			System.out.println(assign[k][0] + "\t" + assign[k][1]);
		
		double semsim = 0;
		
		if(coeff_option){
			for (int i = 0; i < assign.length; i++) {
				String c_ofv_elem = "";
				String c_rv_elem = "";
				if(semsimValues[assign[i][0]][assign[i][1]]!=0) {
					if(i<avs.size()) {
						c_ofv_elem = av.elementAt(assign[i][0]).getCoeff();
					}
					if(i<rv.size()) {
						c_rv_elem = rv.elementAt(assign[i][1]).getCoeff();
					}
				}

				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]]*getCoeffContribution(c_ofv_elem,c_rv_elem);
			}

		}
		else{
			for (int i = 0; i < assign.length; i++) {
				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
			}
		}
		
/*		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
*/		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = rv.size();
		//double fact = max;
		semsim_with_percs[z] = semsim * percs[z] / fact;
		}
		result = semsim_with_percs[0] + semsim_with_percs[1];  
		return result;
	}
	
	
	public double semsim(Vector<OFVElem> rv, Vector<OFVElem> av, boolean coeff_option) {
		double result = 0;
				
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i).getConc_id(), rv.get(j).getConc_id());
//					System.out.println(i + "\t" + j + "\t" + pair);
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
//				for (int k = rvSize; k < (rvSize - diff); k++) {
//					semsimMatrix[i][k] = 1;
//					semsimValues[i][k] = 0;
//				}
			}
			
			for (int j = 0; j < avSize; j++) {
				for (int i = rvSize; i < avSize; i++) {
					semsimMatrix[j][i] = 1;
					semsimValues[j][i] = 0;
				}

			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		int assign[][] = ha.computeAssignments(semsimMatrix);

//		for(int k=0; k<assign.length; k++)
//			System.out.println(assign[k][0] + "\t" + assign[k][1]);
		
		double semsim = 0;
		
		if(coeff_option){
			for (int i = 0; i < assign.length; i++) {
				String c_ofv_elem = "";
				String c_rv_elem = "";
				if(semsimValues[assign[i][0]][assign[i][1]]!=0) {
					if(i<avs.size()) {
						c_ofv_elem = av.elementAt(assign[i][0]).getCoeff();
					}
					if(i<rv.size()) {
						c_rv_elem = rv.elementAt(assign[i][1]).getCoeff();
					}
				}

				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]]*getCoeffContribution(c_ofv_elem,c_rv_elem);
			}

		}
		else{
			for (int i = 0; i < assign.length; i++) {
				semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
			}
		}
		
/*		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
*/		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = rv.size();
		//double fact = max;
		result = semsim / fact;
		return result;
	}
	
	public double _semsim(Set<String> avSet, Set<String> rvSet) {
		double result = 0;
		
		Vector<String> av = new Vector<String>(avSet);
		Vector<String> rv = new Vector<String>(rvSet);
		
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i), rv.get(j));
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i), rv.get(j));
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
				for (int k = rvSize; k < (rvSize - diff); k++) {
					semsimMatrix[i][k] = 1;
					semsimValues[i][k] = 0;
				}
			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		int assign[][] = ha.computeAssignments(semsimMatrix);

		double semsim = 0;
		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = max;
		result = semsim / fact;
		return result;
	}
	
	
	public double __semsim(Set<OFVElem> avSet, Set<OFVElem> rvSet, boolean coeff_option) {
		double result = 0;
		
		Vector<OFVElem> av = new Vector<OFVElem>(avSet);
		Vector<OFVElem> rv = new Vector<OFVElem>(rvSet);
		
		int rvSize = rv.size();
		int avSize = av.size();
		int diff = avSize - rvSize;
		double[][] semsimMatrix = null;
		double[][] semsimValues = null;

		if (diff <= 0) {
			double[][] m = new double[avSize - diff][rvSize];
			double[][] m2 = new double[avSize - diff][rvSize];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|<|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimValues[i][j] = this.consim(pair);
//					System.out.println(pair+" "+
//					semsimValues[i][j]);
//					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
			}
			for (int i = avSize; i < rvSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					semsimMatrix[i][j] = 1;
					semsimValues[i][j] = 0;
				}

			}
		} else {
			double[][] m = new double[avSize][rvSize + diff];
			double[][] m2 = new double[avSize][rvSize + diff];
			semsimMatrix = m;
			semsimValues = m2;
			// Fill the semsim matrix with |ofv|>=|rv|
			for (int i = 0; i < avSize; i++) {
				for (int j = 0; j < rvSize; j++) {
					Pair pair = new Pair(av.get(i).getConc_id(), rv.get(j).getConc_id());
					semsimValues[i][j] = this.consim(pair);
					semsimMatrix[i][j] = 1 - semsimValues[i][j];
				}
				for (int k = rvSize; k < (rvSize - diff); k++) {
					semsimMatrix[i][k] = 1;
					semsimValues[i][k] = 0;
				}
			}
		}

		HungarianAlgorithm ha = new HungarianAlgorithm();
		for(int i=0; i<semsimMatrix.length; i++) {
			for(int j=0; j<semsimMatrix.length; j++) {
//				System.out.print(semsimMatrix[i][j]+"\t");
			}
//			System.out.print("\n");
		}
		int assign[][] = ha.computeAssignments(semsimMatrix);

		double semsim = 0;
		
		
	if(coeff_option){
		for (int i = 0; i < assign.length; i++) {
			String c_ofv_elem = "";
			String c_rv_elem = "";
			if(semsimValues[assign[i][0]][assign[i][1]]!=0) {
				if(i<avs.size()) {
					c_ofv_elem = av.elementAt(assign[i][0]).getCoeff();
				}
				if(i<rv.size()) {
					c_rv_elem = rv.elementAt(assign[i][1]).getCoeff();
				}
			}

			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]]*getCoeffContribution(c_ofv_elem,c_rv_elem);
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}

	}
	else{
		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
	}

		
		
		
		
		
/*		
		for (int i = 0; i < assign.length; i++) {
			semsim = semsim + semsimValues[assign[i][0]][assign[i][1]];
		}
*/		
		//result = semsim / assign.length;
		double min = Math.min(avSize, rvSize);
		double max = Math.max(avSize, rvSize);
		double mean = (min + max)/2;
		double m_3 = max - (max - min)/3;
		double m_4 = max - (max - min)/4;
		double m_root_2 = max - Math.sqrt(max - min);
		double m_root_3 = max - Math.pow(max - min, 1.0d/3.0d);
		double geom_mean = Math.sqrt(max * min);
		
		double fact = max;
		result = semsim / fact;
		return result;
	}
	
	private void init_idIndexMap() {
		int index = 0;
		Iterator iter = this.getWt().getIds().iterator();
		while(iter.hasNext()) {
			this.getId_index().put(((String)(iter.next())), index);
			index++;
		}
	}
	
	/**
	 * @param id
	 * @return
	 */
	public double ic(String id) {
		double result = 0;
		if(this.getIcs().get(id) == null) {
			result = -Math.log(this.getWt().getWeights().get(id));
			this.getIcs().put(id, result);
		}
		else 
			result = this.getIcs().get(id);
/*		
		Relatedness ft = new Relatedness();
		Node n = NodeFactory.createURI(id);
		result = ft.ic(n, this.wt);
*/		
		return result;
	}
	
	public String bestLubId(Vector<Node> classes) {
		String result = Constants.OWL_THING;
		result = bestLub(classes).getURI().toString();
		return result;
	}
	
	public Node bestLub(Vector<Node> classes) {
		Node result = NodeFactory.createURI(Constants.OWL_THING);
		for(Node n:classes) {
			double temp_ic = this.ic(n.getURI().toString());
			double result_ic = this.ic(result.getURI().toString());
			if(temp_ic > result_ic)
				result = n;
		}
		return result;
	}
	
	public void initIC_SECO() {
		Relatedness ft = new Relatedness();
		for(Node c:this.getWt().allClasses())
			this.getIcs().put(c.getURI().toString(), ft.ic(c, this.wt));
	}
	
	public void initIC_ZHOU() {
		Relatedness ft = new Relatedness();
		double k = 0.5d;
		double seco = 0d;
		double zhou = 0d;
		int max_depth = this.getWt().max_depth();
		double max_depth_log = Math.log(max_depth);
		for(Node c:this.getWt().allClasses()) {
			seco = ft.ic(c, this.wt);
			int depth_c = this.getWt().depth(c);
			zhou = (Math.log(depth_c))/max_depth_log;
			this.getIcs().put(c.getURI().toString(), (k*seco + (1-k)*zhou));
			System.out.println("zhou("+this.getWt().getNameById(c.getURI().toString())+")="+(k*seco + (1-k)*zhou));
		}
	}
	
	private void loadICs(String input_file, String mode) {
		this.getIcs().put(Constants.OWL_THING, 0.0d);
        try {
            BufferedReader b = new BufferedReader(new FileReader(this.getClass().getResource(input_file).getFile()));
            String readLine = "";
            while ((readLine = b.readLine()) != null) {
				String s = readLine;
				
				String class_ref = s.substring(0, s.indexOf(Constants.SEMSIM_EQUAL)).trim();
				String id = "";
				if(mode.equals(Constants.SEMSIM_BY_LABEL))
					id = this.wt.getIdByLabel(class_ref);
				else if(mode.equals(Constants.SEMSIM_BY_ID))
					id = class_ref;
				else if(mode.equals(Constants.SEMSIM_BY_NAME))
					id = this.wt.getIdByName(class_ref);

				s = s.substring(s.indexOf(Constants.SEMSIM_WEIGHT_DELIM_START)+1, s.indexOf(Constants.SEMSIM_ANNOTATION_DELIM_END)).trim();
				double weight = Double.valueOf(s);
				this.getIcs().put(id, weight);
            }
            b.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
	}
	
	private double getCoeffContribution(String coeff1, String coeff2) {
		if(coeff1.equalsIgnoreCase(coeff2)) {
			return 1.0d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.COEFF_HIGH)) {
			if(coeff2.equalsIgnoreCase(Constants.COEFF_MEDIUM))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.COEFF_LOW))
				return 0.5d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.COEFF_MEDIUM)) {
			if(coeff2.equalsIgnoreCase(Constants.COEFF_HIGH))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.COEFF_LOW))
				return 0.8d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.COEFF_LOW)) {
			if(coeff2.equalsIgnoreCase(Constants.COEFF_HIGH))
				return 0.5d;
			else if(coeff2.equalsIgnoreCase(Constants.COEFF_MEDIUM))
				return 0.8d;
		}
		return 0.0d;
	}

	public Vector<Result> sortingResult(Vector<Result> r) {
		Vector<Result> result = new Vector<Result>();
		result.add(r.elementAt(0));
		for(int i=1; i<r.size(); i++) {
			//			System.out.println("i="+i);
			Result elem = (Result)r.get(i);
			boolean inserted = false;
			int resultCurrentSize = result.size(); 
			for(int j=0; (j<resultCurrentSize)&&(inserted==false); j++) {
				//				System.out.println("j="+j);
				Result elem_r = (Result)result.get(j);
				if(elem.getValue()>elem_r.getValue()) {
					//					System.out.println(elem.getValue()+" "+elem_r.getValue());
					result.add(j, elem);
					inserted = true;
				}
			}
			if(inserted==false)
				result.add(elem);
		}
		return result;
	}
	
	private boolean checkConsimBetweenOrderedSiblings(Pair pair) {
		boolean result = false;
		if(this.getWt().ifSibling(pair)) {
			if(this.getWt().getOrderById(pair.getFirst())>0 && this.getWt().getOrderById(pair.getSecond())>0)
				result = true;
		}
		return result;
	}

	private double _checkConsimBetweenOrderedSiblings(Pair pair) {
		double result = 0.0d;
		String first = pair.getFirst();
		String second = pair.getSecond();
		String start1 = "";
		String end1 = "";
		String start2 = "";
		String end2 = "";
		if(first.endsWith(Constants.ENDS_IN_HIGH)) {
			start1 = first.substring(0, first.indexOf(Constants.ENDS_IN_HIGH));
			end1 = Constants.ENDS_IN_HIGH;
		}
		else  
			if(first.endsWith(Constants.ENDS_IN_MEDIUM)) {
				start1 = first.substring(0, first.indexOf(Constants.ENDS_IN_MEDIUM));
				end1 = Constants.ENDS_IN_MEDIUM;
			}
			else  
				if(first.endsWith(Constants.ENDS_IN_LOW)) {
					start1 = first.substring(0, first.indexOf(Constants.ENDS_IN_LOW));
					end1 = Constants.ENDS_IN_LOW;
				}
		if(second.endsWith(Constants.ENDS_IN_HIGH)) {
			start2 = second.substring(0, second.indexOf(Constants.ENDS_IN_HIGH));
			end2 = Constants.ENDS_IN_HIGH;
		}
		else  
			if(second.endsWith(Constants.ENDS_IN_MEDIUM)) {
				start2 = second.substring(0, second.indexOf(Constants.ENDS_IN_MEDIUM));
				end2 = Constants.ENDS_IN_MEDIUM;
			}
			else  
				if(second.endsWith(Constants.ENDS_IN_LOW)) {
					start2 = second.substring(0, second.indexOf(Constants.ENDS_IN_LOW));
					end2 = Constants.ENDS_IN_LOW;
				}
		
		if(start1.equalsIgnoreCase(start2)) {
			if(!(start1.equalsIgnoreCase(""))) {
				if(!(end1.equalsIgnoreCase("") || end2.equalsIgnoreCase(""))) {
					result = _consimBetweenOrderedSiblings(end1, end2);
				}
			}
		}
		return result;
	}
	
	private double consimBetweenOrderedSiblings(Pair pair) {
		double result = 0.0d;
		String lub = "";
		double children_count = 0.0d;
		int orderingFirst = 0; 
		int orderingSecond = 0;
		
		if(pair.getFirst().equalsIgnoreCase(pair.getSecond()))
			result = 1.0d;
		else {
			orderingFirst = this.getWt().getOrderById(pair.getFirst());
			orderingSecond = this.getWt().getOrderById(pair.getSecond());
			
			//lub = this.bestLubId(this.getWt().lub(pair));
			lub = this.getWt().lub(pair).elementAt(0).getURI().toString();
			children_count = this.getWt().children(NodeFactory.createURI(lub)).size();
			
			result = (1/children_count)*(children_count-Math.abs(orderingFirst-orderingSecond));  
		}
		System.out.println(pair + "["+result+"] ["+children_count+"] "+lub + " of="+orderingFirst + "os="+orderingSecond);
		return result;
	}
	
	private double _consimBetweenOrderedSiblings(String coeff1, String coeff2) {
		if(coeff1.equalsIgnoreCase(coeff2)) {
			return 1.0d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.ENDS_IN_HIGH)) {
			if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_MEDIUM))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_LOW))
				return 0.5d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.ENDS_IN_MEDIUM)) {
			if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_HIGH))
				return 0.8d;
			else if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_LOW))
				return 0.8d;
		}
		else if(coeff1.equalsIgnoreCase(Constants.ENDS_IN_LOW)) {
			if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_HIGH))
				return 0.5d;
			else if(coeff2.equalsIgnoreCase(Constants.ENDS_IN_MEDIUM))
				return 0.8d;
		}
		return 0.0d;
	}
	
	public String toStringRV(Vector<OFVElem> ofv, WeightedTaxonomy wt) {
		String result = "[";
		for(OFVElem elem:ofv) {
			if (elem.getCoeff().equals(""))
				result = result + wt.getLabelById(elem.getConc_id()) +"; ";
			else
				result = result + wt.getLabelById(elem.getConc_id())+"("+elem.getCoeff()+"); ";
		}
		result = result.substring(0, result.length()-2)+"]";
		return result;
	}
	
	public String toStringAV(Vector<OFVElem> ofv, WeightedTaxonomy wt) {
		String result = "[";
		for(OFVElem elem:ofv) {
			result = result + wt.getLabelById(elem.getConc_id()) +"; ";
		}
		result = result.substring(0, result.length()-2)+"]";
		return result;
	}
	
	public double computeFactor(Pair pair) {
		double result = 0.0d;
		
		Set<String> specialConcepts_label = new HashSet<String>();
		specialConcepts_label.add("InStock_Giacenza");
		
		for(String sc:specialConcepts_label) {
			String id = this.getWt().getIdByName(sc);
			if(pair.getFirst().startsWith(id) && pair.getSecond().startsWith(id));
				result = 2.0d;
		}
		
		return result;
	}
}


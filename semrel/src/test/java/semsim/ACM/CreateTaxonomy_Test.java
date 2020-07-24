package semsim.ACM;

import it.cnr.iasi.saks.semsim.taxonomy.ACMDataImport;

public class CreateTaxonomy_Test {

	public static void main(String[] args) {
		String prefix = "http://acm_test/";
		String out_file = "acmtaxonomy.owl";
		
		String in_folder = "target/test-classes/semsim/ACM/dataFromAntonio/";
		String out_folder = "/semsim/ACM/dataFromAntonio/output2/";
		String onto_out_folder ="target/test-classes/semsim/ACM/dataFromAntonio/output2/";
		String annotations_folder = "/semsim/ACM/dataFromAntonio/";
		
		ACMDataImport acm = new ACMDataImport(onto_out_folder+out_file, prefix);
		acm.initTaxonomy(2114);
		String in_file = in_folder+"id_Path.txt";
		acm.addLabel(in_file);
		in_file = in_folder+"Lista-Iperonimi-Iponimi.txt";
		acm.addSubClasses(in_file);
		
		in_file = in_folder + "ConceptsWeights.txt";
		String out_cf = out_folder+"weights_cf.txt";
		String out_af = out_folder+"weights_af.txt";
		String out_td = out_folder+"weights_td.txt";
		String out_iic = out_folder+"weights_iic.txt";
		String out_rnd = out_folder+"weights_rnd.txt";
		
		acm.generateWeightFiles(in_file, out_cf, out_af, out_td, out_iic, out_rnd);
		
		in_file = "ACM-Annotated_Papers_Leaves.txt";
		String out_avs = "avs.txt";
		acm.generateAVsFile(in_folder+in_file, out_folder+out_avs);
	}

}

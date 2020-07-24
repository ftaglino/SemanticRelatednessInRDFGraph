package semsim;

import org.junit.Test;

import it.cnr.iasi.saks.semsim.experiment.Experiment;

public class Experiment_Test {
	
	@Test
	public void experiment() {
		Experiment experiment = new Experiment(); 
		experiment.run_paperVSpaper();
	}
}

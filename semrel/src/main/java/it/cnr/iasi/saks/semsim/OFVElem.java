package it.cnr.iasi.saks.semsim;

public class OFVElem {
	String conc_id = "";
	String coeff = "";
	
	public String getConc_id() {
		return conc_id;
	}

	public void setConc_id(String conc_id) {
		this.conc_id = conc_id;
	}

	public String getCoeff() {
		return coeff;
	}

	public void setCoeff(String coeff) {
		this.coeff = coeff;
	}

	public OFVElem(String conc_id, String coeff) {
		super();
		this.conc_id = conc_id;
		this.coeff = coeff;
	}

	public String toString() {
		String result = "";
		result = this.getConc_id() + "(" + this.getCoeff() + ")";
		return result;
	}
}
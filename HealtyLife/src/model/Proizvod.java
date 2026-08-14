package model;

public class Proizvod {
    
	private String barKod;
	private String naziv;
	private String sastojci;
	private int posan;
	private int halal;
	private int energijaKJ;
	private int energijaKcal;
	private double masti;
	private double zasiceneMasti;
	private double ugljeniHidrati;
	private double seceri;
	private double vlakna;
	private double proteini;
	private double so;
	private String preporucenaDnevnaDoza;
	
    public Proizvod() {
    }

	public String getBarKod() {
		return barKod;
	}
	
	public String getNaziv() {
		return naziv;
	}

	public String getSastojci() {
		return sastojci;
	}

	public int getPosan() {
		return posan;
	}

	public int getHalal() {
		return halal;
	}

	public int getEnergijaKJ() {
		return energijaKJ;
	}

	public int getEnergijaKcal() {
		return energijaKcal;
	}

	public double getMasti() {
		return masti;
	}

	public double getZasiceneMasti() {
		return zasiceneMasti;
	}

	public double getUgljeniHidrati() {
		return ugljeniHidrati;
	}

	public double getSeceri() {
		return seceri;
	}

	public double getVlakna() {
		return vlakna;
	}

	public double getProteini() {
		return proteini;
	}

	public double getSo() {
		return so;
	}

	public String getPreporucenaDnevnaDoza() {
		return preporucenaDnevnaDoza;
	}
	

	public void setBarKod(String barKod) {
		this.barKod = barKod;
	}

	public void setNaziv(String naziv) {
		this.naziv = naziv;
	}

	public void setSastojci(String sastojci) {
		this.sastojci = sastojci;
	}

	public void setPosan(int posan) {
		this.posan = posan;
	}

	public void setHalal(int halal) {
		this.halal = halal;
	}

	public void setEnergijaKJ(int energijaKJ) {
		this.energijaKJ = energijaKJ;
	}

	public void setEnergijaKcal(int energijaKcal) {
		this.energijaKcal = energijaKcal;
	}

	public void setMasti(double masti) {
		this.masti = masti;
	}

	public void setZasiceneMasti(double zasiceneMasti) {
		this.zasiceneMasti = zasiceneMasti;
	}

	public void setUgljeniHidrati(double ugljeniHidrati) {
		this.ugljeniHidrati = ugljeniHidrati;
	}

	public void setSeceri(double seceri) {
		this.seceri = seceri;
	}

	public void setVlakna(double vlakna) {
		this.vlakna = vlakna;
	}

	public void setProteini(double proteini) {
		this.proteini = proteini;
	}

	public void setSo(double so) {
		this.so = so;
	}

	public void setPreporucenaDnevnaDoza(String preporucenaDnevnaDoza) {
		this.preporucenaDnevnaDoza = preporucenaDnevnaDoza;
	}

	public Proizvod(String barKod, String naziv, String sastojci, int posan, int halal, int energijaKJ,
			int energijaKcal, double masti, double zasiceneMasti, double ugljeniHidrati, double seceri, double vlakna,
			double proteini, double so, String preporucenaDnevnaDoza) {
		super();
		this.barKod = barKod;
		this.naziv = naziv;
		this.sastojci = sastojci;
		this.posan = posan;
		this.halal = halal;
		this.energijaKJ = energijaKJ;
		this.energijaKcal = energijaKcal;
		this.masti = masti;
		this.zasiceneMasti = zasiceneMasti;
		this.ugljeniHidrati = ugljeniHidrati;
		this.seceri = seceri;
		this.vlakna = vlakna;
		this.proteini = proteini;
		this.so = so;
		this.preporucenaDnevnaDoza = preporucenaDnevnaDoza;
	}

	public boolean isPosan() {
    	if (posan==1) {
			return true;
		} else {
			return false;
		}
    }
    
    public boolean isHalal() {
    	if (halal==1) {
			return true;
		} else {
			return false;
		}
    }
    
}

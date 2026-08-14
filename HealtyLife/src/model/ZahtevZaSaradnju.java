package model;

public class ZahtevZaSaradnju {
	
    private int idZahteva;
    private int idKorisnika;
    private int idNutricioniste;
    private String status;
    private String tip;
    private String opis;
    private String odgovor;

    /**
     * Default constructor
     */
    public ZahtevZaSaradnju() {
    }
    
    
    
    public int getIdZahteva() {
		return idZahteva;
	}



	public void setIdZahteva(int idZahteva) {
		this.idZahteva = idZahteva;
	}



	public int getIdKorisnika() {
		return idKorisnika;
	}



	public void setIdKorisnika(int idKorisnika) {
		this.idKorisnika = idKorisnika;
	}



	public int getIdNutricioniste() {
		return idNutricioniste;
	}



	public void setIdNutricioniste(int idNutricioniste) {
		this.idNutricioniste = idNutricioniste;
	}



	public String getStatus() {
		return status;
	}



	public void setStatus(String status) {
		this.status = status;
	}



	public String getTip() {
		return tip;
	}



	public void setTip(String tip) {
		this.tip = tip;
	}



	public String getOpis() {
		return opis;
	}



	public void setOpis(String opis) {
		this.opis = opis;
	}



	public String getOdgovor() {
		return odgovor;
	}



	public void setOdgovor(String odgovor) {
		this.odgovor = odgovor;
	}



	public ZahtevZaSaradnju(int idZahteva, int idKorisnika, int idNutricioniste, String status, String tip, String opis,
			String odgovor) {
		super();
		this.idZahteva = idZahteva;
		this.idKorisnika = idKorisnika;
		this.idNutricioniste = idNutricioniste;
		this.status = status;
		this.tip = tip;
		this.opis = opis;
		this.odgovor = odgovor;
	}



	public void promeniStatus(String s) {
        // TODO implement here
    }
}

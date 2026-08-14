package model;

import java.time.LocalDate;

public class RacunDTO {
	private int idRacuna;
	private int idZahteva;
	private String imeNutricioniste;
	private String prezimeNutricioniste;
	private String imeKorisnika;
	private String prezimeKorisnika;
	private String tip;
	private int iznos;
	private double provizija;
	private String status;
	private String datum;
	
	public RacunDTO() {
		super();
	}

	public RacunDTO(int idRacuna, int idZahteva, String imeNutricioniste, String prezimeNutricioniste,
			String imeKorisnika, String prezimeKorisnika, String tip, int iznos, int provizija, String status,
			String datum) {
		super();
		this.idRacuna = idRacuna;
		this.idZahteva = idZahteva;
		this.imeNutricioniste = imeNutricioniste;
		this.prezimeNutricioniste = prezimeNutricioniste;
		this.imeKorisnika = imeKorisnika;
		this.prezimeKorisnika = prezimeKorisnika;
		this.tip = tip;
		this.iznos = iznos;
		this.provizija = provizija;
		this.status = status;
		this.datum = datum;
	}

	public int getIdRacuna() {
		return idRacuna;
	}

	public void setIdRacuna(int idRacuna) {
		this.idRacuna = idRacuna;
	}

	public int getIdZahteva() {
		return idZahteva;
	}

	public void setIdZahteva(int idZahteva) {
		this.idZahteva = idZahteva;
	}

	public String getImeNutricioniste() {
		return imeNutricioniste;
	}

	public void setImeNutricioniste(String imeNutricioniste) {
		this.imeNutricioniste = imeNutricioniste;
	}

	public String getPrezimeNutricioniste() {
		return prezimeNutricioniste;
	}

	public void setPrezimeNutricioniste(String prezimeNutricioniste) {
		this.prezimeNutricioniste = prezimeNutricioniste;
	}

	public String getImeKorisnika() {
		return imeKorisnika;
	}

	public void setImeKorisnika(String imeKorisnika) {
		this.imeKorisnika = imeKorisnika;
	}

	public String getPrezimeKorisnika() {
		return prezimeKorisnika;
	}

	public void setPrezimeKorisnika(String prezimeKorisnika) {
		this.prezimeKorisnika = prezimeKorisnika;
	}

	public String getTip() {
		return tip;
	}

	public void setTip(String tip) {
		this.tip = tip;
	}

	public int getIznos() {
		return iznos;
	}

	public void setIznos(int iznos) {
		this.iznos = iznos;
	}

	public double getProvizija() {
		return provizija;
	}

	public void setProvizija(Double provizija) {
		this.provizija = provizija;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDatum() {
		return datum;
	}

	public void setDatum(String datum) {
		this.datum = datum;
	}
	
	
	
	
}

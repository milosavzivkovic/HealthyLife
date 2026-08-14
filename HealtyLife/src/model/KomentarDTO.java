package model;

public class KomentarDTO {

	private String ime;
	private String prezime;
	private int ocena;
	private String komentar;
	private String preporucenaDoza;
	public KomentarDTO(String ime, String prezime, int ocena, String komentar, String preporucenaDoza) {
		super();
		this.ime = ime;
		this.prezime = prezime;
		this.ocena = ocena;
		this.komentar = komentar;
		this.preporucenaDoza = preporucenaDoza;
	}
	public KomentarDTO() {
		super();
	}
	public String getIme() {
		return ime;
	}
	public void setIme(String ime) {
		this.ime = ime;
	}
	public String getPrezime() {
		return prezime;
	}
	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}
	public int getOcena() {
		return ocena;
	}
	public void setOcena(int ocena) {
		this.ocena = ocena;
	}
	public String getKomentar() {
		return komentar;
	}
	public void setKomentar(String komentar) {
		this.komentar = komentar;
	}
	public String getPreporucenaDoza() {
		return preporucenaDoza;
	}
	public void setPreporucenaDoza(String preporucenaDoza) {
		this.preporucenaDoza = preporucenaDoza;
	}
	
	
	
}

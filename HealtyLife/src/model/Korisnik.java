package model;

public class Korisnik {
    private int idKorisnika;
    private String ime;
    private String prezime;
    private String email;
    private String lozinka;

    /**
     * Default constructor
     */
    public Korisnik() {
    }
    
    public Korisnik(int idKorisnika, String ime, String prezime, String email, String lozinka) {
		super();
		this.idKorisnika = idKorisnika;
		this.ime = ime;
		this.prezime = prezime;
		this.email = email;
		this.lozinka = lozinka;
	}
    

	@Override
	public String toString() {
		return "Korisnik [idKorisnika=" + idKorisnika + ", ime=" + ime + ", prezime=" + prezime + ", email=" + email
				+ ", lozinka=" + lozinka + "]";
	}

	public void setIdKorisnika(int idKorisnika) {
		this.idKorisnika = idKorisnika;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLozinka(String lozinka) {
		this.lozinka = lozinka;
	}

	public int getIdKorisnika() {
		return idKorisnika;
	}

	public String getIme() {
		return ime;
	}

	public String getPrezime() {
		return prezime;
	}

	public String getEmail() {
		return email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public void posaljiZahtev() {
        // TODO implement here
    }
}

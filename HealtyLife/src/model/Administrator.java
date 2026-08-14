package model;

public class Administrator {

    private int idAdministratora;
    private String email;
    private String lozinka;
    private String ime;
    private String prezime;
    
	public Administrator(int idAdministratora, String email, String lozinika, String ime, String prezime) {
		super();
		this.idAdministratora = idAdministratora;
		this.email = email;
		this.lozinka = lozinika;
		this.ime = ime;
		this.prezime = prezime;
	}
	
	public int getIdAdministratora() {
		return idAdministratora;
	}

	public String getEmail() {
		return email;
	}

	public String getLozinka() {
		return lozinka;
	}

	public String getIme() {
		return ime;
	}

	public String getPrezime() {
		return prezime;
	}
	
	
	public void setIdAdministratora(int idAdministratora) {
		this.idAdministratora = idAdministratora;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setLozinka(String lozinika) {
		this.lozinka = lozinika;
	}

	public void setIme(String ime) {
		this.ime = ime;
	}

	public void setPrezime(String prezime) {
		this.prezime = prezime;
	}

	/**
     * Default constructor
     */
    public Administrator() {
    }
    
    public void administracijaProizvoda() {
        // TODO implement here
    }
    
    public void administracijaNutricionista() {
        // TODO implement here
    }
    
    public void administracijaKorisnika() {
        // TODO implement here
    }
	
}

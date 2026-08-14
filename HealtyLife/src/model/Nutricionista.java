package model;

public class Nutricionista {

    private int idNutricioniste;
    private String ime;
    private String prezime;
    private String licenca;
    private String email;
    private String lozinka;

	 /**
     * Default constructor
     */
    public Nutricionista() {
    }
    
    

    public Nutricionista(int idNutricioniste, String ime, String prezime, String licenca, String email,
			String lozinka) {
		super();
		this.idNutricioniste = idNutricioniste;
		this.ime = ime;
		this.prezime = prezime;
		this.licenca = licenca;
		this.email = email;
		this.lozinka = lozinka;
	}



	public int getIdNutricioniste() {
		return idNutricioniste;
	}



	 public String getIme() {
		 return ime;
	 }



	 public String getPrezime() {
		 return prezime;
	 }



	 public String getLicenca() {
		 return licenca;
	 }



	 public String getEmail() {
		 return email;
	 }



	 public String getLozinka() {
		 return lozinka;
	 }

	 

	public void setIdNutricioniste(int idNutricioniste) {
		this.idNutricioniste = idNutricioniste;
	}



	 public void setIme(String ime) {
		 this.ime = ime;
	 }



	 public void setPrezime(String prezime) {
		 this.prezime = prezime;
	 }



	 public void setLicenca(String licenca) {
		 this.licenca = licenca;
	 }



	 public void setEmail(String email) {
		 this.email = email;
	 }



	 public void setLozinka(String lozinka) {
		 this.lozinka = lozinka;
	 }



	public void odgovoriNaZahtev() {
        // TODO implement here
    }

    public void kreirajPlan() {
        // TODO implement here
    }

    public void odrziKonsultaciju() {
        // TODO implement here
    }

    public void posaljiRacun() {
        // TODO implement here
    }
}

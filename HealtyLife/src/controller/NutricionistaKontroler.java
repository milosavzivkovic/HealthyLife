package controller;

public class NutricionistaKontroler {


    public void proslediPodatkeRegistracija(String ime, String prezime, String licenca, String email, String lozinka) {
        // TODO implement here
    }

    public static boolean validirajPodatke(String ime, String prezime, String licenca, String email, String lozinka) {
    	if (ime.isEmpty() || prezime.isEmpty() || email.isEmpty() || lozinka.isEmpty() || licenca.isEmpty() ||
    			!email.contains("@") || lozinka.length() < 6) {
    		return false;
		} else {
			return true;
		}
    }

    public void proslediPodatke(String email, String lozinka) {
        // TODO implement here
    }

    public static boolean validacijaNutricioniste(String email, String lozinka) {
    	if (email.isEmpty() || lozinka.isEmpty()) {
    		return false;
		} else {
			return true;
		}
    }
    
    /**
     * Default constructor
     */
    public NutricionistaKontroler() {
    }

}

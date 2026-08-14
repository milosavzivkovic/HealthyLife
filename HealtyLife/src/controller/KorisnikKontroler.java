package controller;

public  class KorisnikKontroler {

    public static boolean validirajPodatke(String ime, String prezime, String email, String lozinka) {
        
    	if (ime.isEmpty() || prezime.isEmpty() || email.isEmpty() || lozinka.isEmpty() || 
    			!email.contains("@") || lozinka.length() < 6) {
    		return false;
		} else {
			return true;
		}
    }

    public static void proslediPodatke(String email, String lozinka) {
        // TODO implement here
    }
 
    public static boolean validacijaKorisnika(String email, String lozinka) {
    	if (email.isEmpty() || lozinka.isEmpty()) {
    		return false;
		} else {
			return true;
		}
    }

    public static void proslediPodatkeRegistracija(String ime, String prezime, String email, String lozinka) {
   	
    }
	 /**
     * Default constructor
     */
    public KorisnikKontroler() {
    }

}

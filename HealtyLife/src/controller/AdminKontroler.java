package controller;

public class AdminKontroler {
	
	public void proslediPodatke(String email, String lozinka) {
        // TODO implement here
    }
    public static boolean validacijaAdmina(String email, String lozinka) {
    	if (email.isEmpty() || lozinka.isEmpty()) {
    		return false;
		} else {
			return true;
		}
    }
    
    /**
     * Default constructor
     */
    public AdminKontroler() {
    }
}

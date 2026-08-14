package controller;

public class ProizvodKontroler {
	/**
     * Default constructor
     */
    public ProizvodKontroler() {
    }

    public static boolean validirajProizvod(String naziv, String barKod, String sastojci, String posan, String halal, String energija_kj, String energija_kcal,
    		String masti, String zasiceneMasti, String ugljeniHidrati, String seceri, String vlakna, String proteini, String so, String preporucenaDnevnaDoza) {
    	if (naziv.isEmpty()|| barKod.length()<=12 || barKod.isEmpty() || sastojci.isEmpty() || posan.isEmpty() || halal.isEmpty()
    			|| energija_kj.isEmpty() || energija_kcal.isEmpty() || masti.isEmpty() || zasiceneMasti.isEmpty()
    			|| ugljeniHidrati.isEmpty() || seceri.isEmpty() || vlakna.isEmpty() || proteini.isEmpty() || so.isEmpty() || preporucenaDnevnaDoza.isEmpty()) {
    		return false;
		} else {
			return true;
		}
    }

    /**
     * 
     */
    public void prikazProizvoda() {
        // TODO implement here
    }

    /**
     * @param id
     */
    public void obrisiProizvod(int id) {
        // TODO implement here
    }

    /**
     * @param id 
     * @return
     */
    public boolean validacijaBarKod(String barKod) {
    	if (barKod.isEmpty() || barKod.length()>=12) {
    		return false;
		} else {
			return true;
		}
    }

    /**
     * @param naziv 
     * @param slika 
     * @param stanje 
     * @param barKod
     */
    public void izmeniProizvod(String naziv, String stanje, int barKod) {
        // TODO implement here
    }

    /**
     * @param naziv 
     * @param slika 
     * @param stanje 
     * @param barKod
     */
    public void dodajProizvod(String naziv, String stanje, int barKod) {
        // TODO implement here
    }
}

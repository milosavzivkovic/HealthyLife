package controller;

public class KomentarKontroler {
	
    public void dodajKomentar(int idN, int idP, String komentar) {
        // TODO implement here
    }

    public boolean validirajKomentar(String komentar) {
        // TODO implement here
        return false;
    }

    public void oceniProizvod(int id_nutricioniste, int id_Bar_koda, int ocena, String preporucenaKol) {
        // TODO implement here
    }

    public static boolean validacijaOcene(String komentar, int ocena, String preporucenaKol) {
    	if (komentar.isEmpty() || preporucenaKol.isEmpty() || ocena<=0) {
    		return false;
		} else {
			return true;
		}
    }
    /**
     * Default constructor
     */
    public KomentarKontroler() {
    }

}

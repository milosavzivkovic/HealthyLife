package controller;

public class ZahtevKontroler {

    public void posaljiZahtev(int idZahteva, int idK, int idN, String tip, String opis) {
        // TODO implement here
    }

    public static boolean validirajZahtev(String tip, String opis) {
    	if (tip.isEmpty() || opis.isEmpty()) {
			return false;
		}else return true;
    }

    public void prikaziZahteve() {
        // TODO implement here
    }

    public void odgovoriNaZahtev(int idZahteva, int idK, int idN, String odgovor) {
        // TODO implement here
    }

    public static boolean validirajOdgovor(String odgovor) {
    	if (odgovor.isEmpty()) {
			return false;
		}else return true;
    }

	  /**
     * Default constructor
     */
    public ZahtevKontroler() {
    }
}

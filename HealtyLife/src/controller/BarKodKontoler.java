package controller;

public class BarKodKontoler {
	
    public static boolean validirajBarKod(String barKod) {
        if (barKod.isEmpty() || barKod.length()<=12) {
			return false;
		}else return true;
    }

    public void skenirajBarKod(int id_Bar_koda) {
        // TODO implement here
    }
    
    /**
     * Default constructor
     */
    public BarKodKontoler() {
    }

}

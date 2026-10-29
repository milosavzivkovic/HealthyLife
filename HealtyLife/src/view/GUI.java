package view;

import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.sql.SQLException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import api.OpenFoodFacts;
import dao.DAO;
import model.Korisnik;

/**
 * Glavni prozor aplikacije - API VERZIJA.
 *
 * U ovoj verziji podaci o proizvodima stizu sa Open Food Facts (api.OpenFoodFacts),
 * a nasa baza se koristi samo za naloge korisnika (prijava i registracija).
 * Pretraga radi i bez prijave (kao gost), pa i bez pokrenutog XAMPP-a.
 *
 * Ekrani:
 *   PocetnaPanel   - pocetna strana
 *   PrijavaPaneli  - prijava i registracija korisnika
 *   KorisnikPanel  - pretraga proizvoda po bar-kodu ili nazivu i prikaz proizvoda
 */
public final class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	static final String POCETNA = "POCETNA";
	static final String PRIJAVA_KORISNIK = "PRIJAVA_KORISNIK";
	static final String REGISTRACIJA_KORISNIK = "REGISTRACIJA_KORISNIK";
	static final String KORISNIK = "KORISNIK";

	final DAO dao = new DAO();
	final OpenFoodFacts api = new OpenFoodFacts();

	/** Prijavljeni korisnik; null = gost. */
	Korisnik korisnik;

	private final CardLayout kartice = new CardLayout();
	private final JPanel sadrzaj = new JPanel(kartice);

	private final PrijavaPaneli prijava;
	private final KorisnikPanel korisnikPanel;

	public GUI() {
		setTitle("Healthy Life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Velicina prozora, ali ne veca od ekrana.
		java.awt.Rectangle ekran = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		setSize(Math.min(1300, ekran.width), Math.min(850, ekran.height));
		setMinimumSize(new Dimension(Math.min(1050, ekran.width), Math.min(680, ekran.height)));
		setLocationRelativeTo(null);

		prijava = new PrijavaPaneli(this);
		korisnikPanel = new KorisnikPanel(this);

		sadrzaj.add(new PocetnaPanel(this), POCETNA);
		prijava.dodajEkrane(sadrzaj);
		sadrzaj.add(korisnikPanel, KORISNIK);
		setContentPane(sadrzaj);

		prikazi(POCETNA);
	}

	// ═══════════════════════════════════════════════════════════
	// NAVIGACIJA
	// ═══════════════════════════════════════════════════════════

	void prikazi(String ekran) {
		kartice.show(sadrzaj, ekran);
	}

	void prikaziPocetnu() {
		prikazi(POCETNA);
	}

	/** Otvara ekran za prijavu/registraciju sa praznim poljima. */
	void prikaziPrijavu(String ekran) {
		prijava.resetuj(ekran);
		prikazi(ekran);
		prijava.fokusiraj(ekran);
	}

	void prijavljenKorisnik(Korisnik k) {
		korisnik = k;
		korisnikPanel.otvori();
		prikazi(KORISNIK);
	}

	/** Pretraga bez naloga. */
	void nastaviKaoGost() {
		korisnik = null;
		korisnikPanel.otvori();
		prikazi(KORISNIK);
	}

	void odjava() {
		korisnik = null;
		korisnikPanel.ocisti();
		prikaziPocetnu();
	}

	// ═══════════════════════════════════════════════════════════
	// PORUKE
	// ═══════════════════════════════════════════════════════════

	void greska(Component roditelj, String poruka) {
		JOptionPane.showMessageDialog(roditelj, poruka, "Greška", JOptionPane.ERROR_MESSAGE);
	}

	void upozorenje(Component roditelj, String poruka) {
		JOptionPane.showMessageDialog(roditelj, poruka, "Upozorenje", JOptionPane.WARNING_MESSAGE);
	}

	void info(Component roditelj, String poruka) {
		JOptionPane.showMessageDialog(roditelj, poruka, "Obaveštenje", JOptionPane.INFORMATION_MESSAGE);
	}

	// ═══════════════════════════════════════════════════════════
	// RAD SA BAZOM (samo nalozi korisnika)
	// ═══════════════════════════════════════════════════════════

	@FunctionalInterface
	interface Upit<T> {
		T izvrsi() throws Exception;
	}

	/** Cita iz baze; vraca null ako je doslo do greske (greska je vec prikazana). */
	<T> T izBaze(Component roditelj, String sta, Upit<T> upit) {
		setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		try {
			return upit.izvrsi();
		} catch (Exception e) {
			greska(roditelj, sta + ":\n" + opisGreske(e));
			return null;
		} finally {
			setCursor(Cursor.getDefaultCursor());
		}
	}

	static String opisGreske(Exception e) {
		Throwable t = e;
		while (t != null) {
			String m = t.getMessage() == null ? "" : t.getMessage();
			if (m.contains("Communications link failure") || m.contains("Connection refused")) {
				return "Nije moguće povezati se sa bazom podataka.\nProverite da li je MySQL pokrenut u XAMPP-u.\n\n"
						+ "Pretraga proizvoda radi i bez baze - izaberite \"Pretraži bez prijave\" na početnoj strani.";
			}
			if (t instanceof SQLException s && s.getErrorCode() == 1062) {
				return "Zapis sa ovim podacima već postoji.";
			}
			t = t.getCause();
		}
		return e.getMessage();
	}

	// ═══════════════════════════════════════════════════════════

	public static void main(String[] args) {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}
		UIManager.put("OptionPane.messageFont", Tema.font(Font.PLAIN, 14));
		UIManager.put("OptionPane.buttonFont", Tema.font(Font.PLAIN, 13));
		UIManager.put("ToolTip.font", Tema.font(Font.PLAIN, 12));
		SwingUtilities.invokeLater(() -> new GUI().setVisible(true));
	}
}

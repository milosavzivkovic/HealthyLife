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

import dao.DAO;
import model.Administrator;
import model.Korisnik;
import model.Nutricionista;

/**
 * Glavni prozor aplikacije.
 *
 * Ovde su samo: prebacivanje izmedju ekrana, ko je trenutno prijavljen i
 * zajednicke poruke/greske. Sami ekrani su u posebnim klasama:
 *   PocetnaPanel        - pocetna strana
 *   PrijavaPaneli       - prijava i registracija (admin, korisnik, nutricionista)
 *   AdminPanel          - administracija korisnika, nutricionista, proizvoda, zahteva, zarade
 *   KorisnikPanel       - pretraga proizvoda, skenirani, nutricionisti, inbox, racuni
 *   NutricionistaPanel  - poruke korisnika, pregled i ocenjivanje proizvoda
 */
public final class GUI extends JFrame {

	private static final long serialVersionUID = 1L;

	static final String POCETNA = "POCETNA";
	static final String PRIJAVA_ADMIN = "PRIJAVA_ADMIN";
	static final String PRIJAVA_KORISNIK = "PRIJAVA_KORISNIK";
	static final String REGISTRACIJA_KORISNIK = "REGISTRACIJA_KORISNIK";
	static final String PRIJAVA_NUTRICIONISTA = "PRIJAVA_NUTRICIONISTA";
	static final String REGISTRACIJA_NUTRICIONISTA = "REGISTRACIJA_NUTRICIONISTA";
	static final String KORISNIK = "KORISNIK";
	static final String NUTRICIONISTA = "NUTRICIONISTA";

	final DAO dao = new DAO();

	// Ko je trenutno prijavljen (najvise jedan od ova tri nije null)
	Korisnik korisnik;
	Nutricionista nutricionista;
	Administrator administrator;

	private final CardLayout kartice = new CardLayout();
	private final JPanel sadrzaj = new JPanel(kartice);

	private final PrijavaPaneli prijava;
	private final AdminPanel admin;
	private final KorisnikPanel korisnikPanel;
	private final NutricionistaPanel nutricionistaPanel;

	public GUI() {
		setTitle("Healthy Life");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// Velicina prozora, ali ne veca od ekrana (na laptopu je 850px bilo vise od ekrana).
		java.awt.Rectangle ekran = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment().getMaximumWindowBounds();
		setSize(Math.min(1300, ekran.width), Math.min(850, ekran.height));
		setMinimumSize(new Dimension(Math.min(1050, ekran.width), Math.min(680, ekran.height)));
		setLocationRelativeTo(null);

		prijava = new PrijavaPaneli(this);
		admin = new AdminPanel(this);
		korisnikPanel = new KorisnikPanel(this);
		nutricionistaPanel = new NutricionistaPanel(this);

		// Ekrani se samo prave ovde; podaci iz baze se ucitavaju tek kada se ekran otvori.
		sadrzaj.add(new PocetnaPanel(this), POCETNA);
		prijava.dodajEkrane(sadrzaj);
		admin.dodajEkrane(sadrzaj);
		sadrzaj.add(korisnikPanel, KORISNIK);
		sadrzaj.add(nutricionistaPanel, NUTRICIONISTA);
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

	void prijavljenAdministrator(Administrator a) {
		odjaviSve();
		administrator = a;
		admin.otvori();
	}

	void prijavljenKorisnik(Korisnik k) {
		odjaviSve();
		korisnik = k;
		korisnikPanel.otvori();
		prikazi(KORISNIK);
	}

	void prijavljenNutricionista(Nutricionista n) {
		odjaviSve();
		nutricionista = n;
		nutricionistaPanel.otvori();
		prikazi(NUTRICIONISTA);
	}

	void odjava() {
		odjaviSve();
		prikaziPocetnu();
	}

	private void odjaviSve() {
		korisnik = null;
		nutricionista = null;
		administrator = null;
		korisnikPanel.ocisti();
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

	boolean potvrdi(Component roditelj, String pitanje) {
		return JOptionPane.showConfirmDialog(roditelj, pitanje, "Potvrda",
				JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION;
	}

	// ═══════════════════════════════════════════════════════════
	// RAD SA BAZOM
	// ═══════════════════════════════════════════════════════════
	// Ranije je svaki poziv baze imao svoj try/catch sa porukom. Sada sve ide
	// kroz ove dve metode: kursor "ceka" dok traje upit, a greska se prikazuje
	// razumljivo (npr. kada XAMPP nije pokrenut).

	@FunctionalInterface
	interface Upit<T> {
		T izvrsi() throws Exception;
	}

	@FunctionalInterface
	interface Akcija {
		void izvrsi() throws Exception;
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

	/** Menja podatke u bazi; vraca true ako je uspelo (inace je greska vec prikazana). */
	boolean uBazi(Component roditelj, String sta, Akcija akcija) {
		return izBaze(roditelj, sta, () -> {
			akcija.izvrsi();
			return Boolean.TRUE;
		}) != null;
	}

	static String opisGreske(Exception e) {
		Throwable t = e;
		while (t != null) {
			String m = t.getMessage() == null ? "" : t.getMessage();
			if (m.contains("Communications link failure") || m.contains("Connection refused")) {
				return "Nije moguće povezati se sa bazom podataka.\nProverite da li je MySQL pokrenut u XAMPP-u.";
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

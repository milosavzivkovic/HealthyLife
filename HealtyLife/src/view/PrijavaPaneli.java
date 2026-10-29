package view;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.text.JTextComponent;

import controller.KorisnikKontroler;
import model.Korisnik;

/**
 * Ekrani za prijavu i registraciju korisnika.
 * Ranije je svaki od pet ekrana bio posebno napisan (~80 linija, skoro isti kod);
 * sada ih sve pravi jedna metoda ekran(...).
 */
class PrijavaPaneli {

	private final GUI app;
	private final Map<String, Forma> forme = new HashMap<>();

	/** Polja jednog ekrana, da bi mogla da se isprazne pri ponovnom otvaranju. */
	private static final class Forma {
		final List<JTextComponent> polja = new ArrayList<>();
		final JLabel greska = new JLabel(" ");

		Forma() {
			greska.setFont(Tema.font(Font.PLAIN, 13));
			greska.setForeground(Tema.OPASNOST);
			greska.setAlignmentX(Component.LEFT_ALIGNMENT);
			greska.setPreferredSize(new Dimension(360, 38)); // mesto za poruku u dva reda
		}

		<T extends JTextComponent> T dodaj(T polje) {
			polja.add(polje);
			return polje;
		}

		void greska(String poruka) {
			greska.setText("<html><div style='width:340px'>" + poruka + "</div></html>");
		}

		void resetuj() {
			polja.forEach(p -> p.setText(""));
			greska.setText(" ");
		}
	}

	PrijavaPaneli(GUI app) {
		this.app = app;
	}

	void dodajEkrane(JPanel sadrzaj) {
		sadrzaj.add(prijavaKorisnika(), GUI.PRIJAVA_KORISNIK);
		sadrzaj.add(registracijaKorisnika(), GUI.REGISTRACIJA_KORISNIK);
	}

	void resetuj(String ekran) {
		Forma f = forme.get(ekran);
		if (f != null) f.resetuj();
	}

	void fokusiraj(String ekran) {
		Forma f = forme.get(ekran);
		if (f != null && !f.polja.isEmpty()) f.polja.get(0).requestFocusInWindow();
	}

	private static String lozinka(JPasswordField p) {
		return new String(p.getPassword());
	}

	// ═══════════════════════════════════════════════════════════
	// KORISNIK
	// ═══════════════════════════════════════════════════════════

	private JPanel prijavaKorisnika() {
		Forma f = new Forma();
		JTextField email = f.dodaj(Tema.polje("ime@primer.com"));
		JPasswordField loz = f.dodaj(Tema.poljeLozinke("Lozinka"));

		Dugme prijavi = Dugme.primarno("Prijavi se");
		prijavi.addActionListener(e -> {
			String em = email.getText().trim();
			String lo = lozinka(loz);
			if (!KorisnikKontroler.validacijaKorisnika(em, lo)) {
				f.greska("Unesite email i lozinku.");
				return;
			}
			Korisnik k = app.izBaze(prijavi, "Greška pri prijavi", () -> app.dao.nadjiKorisnika(em, lo));
			if (k == null) return;
			if (k.getIdKorisnika() == 0) {
				f.greska("Pogrešan email ili lozinka.");
				loz.setText("");
				return;
			}
			app.prijavljenKorisnik(k);
		});

		forme.put(GUI.PRIJAVA_KORISNIK, f);
		return ekran("Prijava korisnika", "Dobro došli nazad!", f,
				new String[] { "E-mail", "Lozinka" }, prijavi,
				link("Nemate nalog? Registrujte se →", GUI.REGISTRACIJA_KORISNIK), nazad());
	}

	private JPanel registracijaKorisnika() {
		Forma f = new Forma();
		JTextField ime = f.dodaj(Tema.polje("Petar"));
		JTextField prezime = f.dodaj(Tema.polje("Petrović"));
		JTextField email = f.dodaj(Tema.polje("ime@primer.com"));
		JPasswordField loz = f.dodaj(Tema.poljeLozinke("Najmanje 6 karaktera"));

		Dugme registruj = Dugme.primarno("Napravi nalog");
		registruj.addActionListener(e -> {
			String i = ime.getText().trim();
			String p = prezime.getText().trim();
			String em = email.getText().trim();
			String lo = lozinka(loz);
			if (!KorisnikKontroler.validirajPodatke(i, p, em, lo)) {
				f.greska(PORUKA_NEISPRAVNI_PODACI);
				return;
			}
			Boolean postoji = app.izBaze(registruj, "Greška pri registraciji", () -> app.dao.proveriPostojecegKorisnika(em));
			if (postoji == null) return;
			if (postoji) {
				f.greska("Korisnik sa emailom " + em + " već postoji.");
				return;
			}
			Integer id = app.izBaze(registruj, "Greška pri registraciji", () -> app.dao.dodajKorisnika(i, p, em, lo));
			if (id == null) return;
			app.info(registruj, "Uspešno ste se registrovali!");
			app.prijavljenKorisnik(new Korisnik(id, i, p, em, lo));
		});

		forme.put(GUI.REGISTRACIJA_KORISNIK, f);
		return ekran("Registracija korisnika", "Napravite besplatan nalog", f,
				new String[] { "Ime", "Prezime", "E-mail", "Lozinka" }, registruj,
				link("Već imate nalog? Prijavite se →", GUI.PRIJAVA_KORISNIK), nazad());
	}

	private static final String PORUKA_NEISPRAVNI_PODACI =
			"Sva polja su obavezna, email mora sadržati @, a lozinka mora imati najmanje 6 karaktera.";

	// ═══════════════════════════════════════════════════════════
	// ZAJEDNICKI IZGLED
	// ═══════════════════════════════════════════════════════════

	private Dugme link(String tekst, String ekran) {
		Dugme d = Dugme.link(tekst);
		d.addActionListener(e -> app.prikaziPrijavu(ekran));
		return d;
	}

	private Dugme nazad() {
		Dugme d = Dugme.link("← Nazad na početnu");
		d.setForeground(Tema.TEKST_SPOREDNI);
		d.setFont(Tema.font(Font.PLAIN, 13));
		d.addActionListener(e -> app.prikaziPocetnu());
		return d;
	}

	/**
	 * Bela kartica na zelenoj pozadini: naslov, polja sa labelama, poruka o gresci,
	 * glavno dugme i linkovi. Enter u bilo kom polju = klik na glavno dugme.
	 */
	private JPanel ekran(String naslov, String podnaslov, Forma forma, String[] labele, Dugme potvrdi,
			JComponent... linkovi) {
		Tema.Kartica k = new Tema.Kartica(null);
		k.setLayout(new BoxLayout(k, BoxLayout.Y_AXIS));
		k.setBorder(new EmptyBorder(36, 44, 30, 44));

		JLabel logo = new JLabel("● Healthy Life");
		logo.setFont(Tema.font(Font.BOLD, 14));
		logo.setForeground(Tema.PRIMARNA);
		dodaj(k, logo, 14);
		dodaj(k, Tema.naslov(naslov, 26), 4);
		dodaj(k, Tema.sporedni(podnaslov), 26);

		for (int i = 0; i < forma.polja.size(); i++) {
			JTextComponent polje = forma.polja.get(i);
			polje.setPreferredSize(new Dimension(360, 44));
			polje.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
			if (polje instanceof JTextField tf) tf.addActionListener(e -> potvrdi.doClick());
			dodaj(k, Tema.labelaPolja(labele[i]), 6);
			dodaj(k, polje, 14);
		}

		dodaj(k, forma.greska, 10);

		potvrdi.setFont(Tema.font(Font.BOLD, 15));
		potvrdi.setPreferredSize(new Dimension(360, 46));
		potvrdi.setMaximumSize(new Dimension(Integer.MAX_VALUE, 46));
		dodaj(k, potvrdi, 14);

		for (JComponent l : linkovi) {
			JPanel red = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
			red.setOpaque(false);
			red.add(l);
			red.setMaximumSize(new Dimension(Integer.MAX_VALUE, red.getPreferredSize().height));
			dodaj(k, red, 4);
		}

		JPanel spolja = new Tema.GradijentPanel(new GridBagLayout());
		spolja.add(k);
		return spolja;
	}

	private static void dodaj(JPanel p, JComponent c, int razmakIspod) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (c instanceof JLabel l) l.setMaximumSize(new Dimension(Integer.MAX_VALUE, l.getPreferredSize().height));
		p.add(c);
		if (razmakIspod > 0) p.add(Box.createVerticalStrut(razmakIspod));
	}
}

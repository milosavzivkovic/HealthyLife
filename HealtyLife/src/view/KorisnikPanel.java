package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.time.LocalDate;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import controller.BarKodKontoler;
import controller.ZahtevKontroler;
import model.KomentarDTO;
import model.Nutricionista;
import model.Proizvod;
import model.RacunDTO;
import model.ZahtevZaSaradnju;

/** Ekran prijavljenog korisnika: pretraga proizvoda po bar-kodu i svi njegovi dijalozi. */
class KorisnikPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String PRAZNO = "PRAZNO";
	private static final String PROIZVOD = "PROIZVOD";

	private final GUI app;

	private final JLabel imeKorisnika = Tema.imeUZaglavlju();
	private final JTextField barKodPolje = Tema.polje("Unesite bar-kod sa pakovanja (npr. 8600000000000)");

	private final CardLayout rezultat = new CardLayout();
	private final JPanel rezultatPanel = new JPanel(rezultat);

	// Prikaz proizvoda
	private final JLabel naziv = Tema.naslov("", 26);
	private final JLabel barKodInfo = Tema.sporedni("");
	private final JLabel posnoCip = Tema.cip("", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST);
	private final JLabel halalCip = Tema.cip("", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST);
	private final JLabel kcal = new JLabel();
	private final JPanel nutritivneVrednosti = new JPanel(new GridLayout(0, 1));
	private final JTextArea sastojci = Tema.tekstPrikaz("");
	private final JTextArea doza = Tema.tekstPrikaz("");
	private final JLabel naslovKomentara = Tema.naslov("Komentari nutricionista", 17);
	private final JPanel komentari = new JPanel();
	private JScrollPane skrol;

	KorisnikPanel(GUI app) {
		super(new BorderLayout());
		this.app = app;
		setBackground(Tema.POZADINA);

		Dugme odjava = Dugme.naZaglavlju("Odjavi se");
		odjava.addActionListener(e -> app.odjava());

		JPanel gore = new JPanel(new BorderLayout());
		gore.add(Tema.zaglavlje("Korisnički panel", "Proveri šta jedeš - unesi bar-kod proizvoda", imeKorisnika, odjava),
				BorderLayout.NORTH);
		gore.add(meni(), BorderLayout.SOUTH);

		rezultatPanel.setOpaque(false);
		rezultatPanel.add(praznoStanje(), PRAZNO);
		rezultatPanel.add(prikazProizvoda(), PROIZVOD);

		Tema.SkrolPanel telo = new Tema.SkrolPanel();
		telo.setBorder(new EmptyBorder(22, 28, 28, 28));
		telo.add(levo(pretraga()));
		telo.add(Box.createVerticalStrut(18));
		telo.add(levo(rezultatPanel));

		add(gore, BorderLayout.NORTH);
		add(skrol = Tema.skrol(telo), BorderLayout.CENTER);
	}

	/** Poziva se posle prijave. */
	void otvori() {
		ocisti();
		if (app.korisnik != null) imeKorisnika.setText(app.korisnik.getIme() + " " + app.korisnik.getPrezime());
		SwingUtilities.invokeLater(barKodPolje::requestFocusInWindow);
	}

	/** Brise prikaz prethodnog korisnika (pri odjavi). */
	void ocisti() {
		barKodPolje.setText("");
		rezultat.show(rezultatPanel, PRAZNO);
	}

	private static <T extends JComponent> T levo(T c) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		return c;
	}

	// ═══════════════════════════════════════════════════════════
	// GORNJI DEO: meni i pretraga
	// ═══════════════════════════════════════════════════════════

	private JPanel meni() {
		JPanel m = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		m.setBackground(Tema.POVRSINA);
		m.setBorder(BorderFactory.createCompoundBorder(new MatteBorder(0, 0, 1, 0, Tema.IVICA), new EmptyBorder(10, 20, 10, 20)));
		m.add(stavkaMenija("Skenirani proizvodi", this::prikaziSkenirane));
		m.add(stavkaMenija("Nutricionisti", this::prikaziNutricioniste));
		m.add(stavkaMenija("Inbox", this::prikaziInbox));
		m.add(stavkaMenija("Moji računi", this::prikaziRacune));
		return m;
	}

	private static Dugme stavkaMenija(String tekst, Runnable akcija) {
		Dugme d = Dugme.sekundarno(tekst);
		d.addActionListener(e -> akcija.run());
		return d;
	}

	private JPanel pretraga() {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 10), 22);
		k.add(Tema.labelaPolja("Bar-kod proizvoda"), BorderLayout.NORTH);

		barKodPolje.setFont(Tema.font(Font.PLAIN, 16));
		barKodPolje.setPreferredSize(new Dimension(400, 48));
		barKodPolje.addActionListener(e -> pretrazi());

		Dugme trazi = Dugme.primarno("Pretraži");
		trazi.setFont(Tema.font(Font.BOLD, 15));
		trazi.setPreferredSize(new Dimension(140, 48));
		trazi.addActionListener(e -> pretrazi());

		JPanel red = new JPanel(new BorderLayout(12, 0));
		red.setOpaque(false);
		red.add(barKodPolje, BorderLayout.CENTER);
		red.add(trazi, BorderLayout.EAST);
		k.add(red, BorderLayout.CENTER);
		k.setMaximumSize(new Dimension(Integer.MAX_VALUE, k.getPreferredSize().height));
		return k;
	}

	private void pretrazi() {
		String bk = barKodPolje.getText().trim();
		if (!BarKodKontoler.validirajBarKod(bk)) {
			app.upozorenje(this, "Unesite ispravan bar-kod (najmanje 13 cifara).");
			return;
		}
		String stanje = app.izBaze(this, "Greška pri pretrazi", () -> app.dao.proveriBarKod(bk));
		if (stanje == null) return;
		if (!"aktivan".equals(stanje)) {
			app.info(this, "Ne posedujemo informacije o proizvodu sa bar-kodom " + bk + ".");
			return;
		}
		Proizvod p = app.izBaze(this, "Greška pri pretrazi", () -> app.dao.nadjiProizvod(bk));
		if (p == null) {
			app.info(this, "Proizvod nije pronađen.");
			return;
		}
		app.uBazi(this, "Greška pri čuvanju istorije skeniranja",
				() -> app.dao.unosSkeniranih(app.korisnik.getIdKorisnika(), p.getBarKod(), LocalDate.now()));
		prikaziProizvod(p);
	}

	// ═══════════════════════════════════════════════════════════
	// PRIKAZ PROIZVODA
	// ═══════════════════════════════════════════════════════════

	private JPanel praznoStanje() {
		Tema.Kartica k = Tema.kartica(new BorderLayout(), 30);
		k.add(Tema.prazno("Još niste pretražili proizvod",
				"Unesite bar-kod iznad i pritisnite Enter - prikazaćemo sastav, nutritivne vrednosti i ocene nutricionista."),
				BorderLayout.CENTER);
		return k;
	}

	private JPanel prikazProizvoda() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setOpaque(false);

		// ── Naslovna kartica ──
		Tema.Kartica glava = Tema.kartica(new BorderLayout(20, 0), 24);
		JPanel levoGlava = new JPanel();
		levoGlava.setLayout(new BoxLayout(levoGlava, BoxLayout.Y_AXIS));
		levoGlava.setOpaque(false);
		JPanel cipovi = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cipovi.setOpaque(false);
		cipovi.add(posnoCip);
		cipovi.add(Box.createHorizontalStrut(8));
		cipovi.add(halalCip);
		levoGlava.add(levo(naziv));
		levoGlava.add(Box.createVerticalStrut(4));
		levoGlava.add(levo(barKodInfo));
		levoGlava.add(Box.createVerticalStrut(12));
		levoGlava.add(levo(cipovi));

		kcal.setFont(Tema.font(Font.BOLD, 30));
		kcal.setForeground(Tema.PRIMARNA_TAMNA);
		JLabel kcalOpis = Tema.sporedni("kcal na 100 g/ml");
		JPanel desnoGlava = new JPanel();
		desnoGlava.setLayout(new BoxLayout(desnoGlava, BoxLayout.Y_AXIS));
		desnoGlava.setOpaque(false);
		kcal.setAlignmentX(Component.RIGHT_ALIGNMENT);
		kcalOpis.setAlignmentX(Component.RIGHT_ALIGNMENT);
		desnoGlava.add(kcal);
		desnoGlava.add(kcalOpis);

		glava.add(levoGlava, BorderLayout.CENTER);
		glava.add(desnoGlava, BorderLayout.EAST);

		// ── Nutritivne vrednosti | sastav i doza ──
		Tema.Kartica nutri = Tema.kartica(new BorderLayout(0, 12), 22);
		nutri.add(Tema.naslov("Nutritivne vrednosti (na 100 g/ml)", 17), BorderLayout.NORTH);
		nutritivneVrednosti.setOpaque(false);
		nutri.add(nutritivneVrednosti, BorderLayout.CENTER);

		JPanel desno = new JPanel(new GridLayout(2, 1, 0, 18));
		desno.setOpaque(false);
		desno.add(karticaTeksta("Sastav proizvoda", sastojci));
		desno.add(karticaTeksta("Preporučena dnevna doza", doza));

		JPanel sredina = new JPanel(new GridLayout(1, 2, 18, 0));
		sredina.setOpaque(false);
		sredina.add(nutri);
		sredina.add(desno);

		// ── Komentari ──
		Tema.Kartica kom = Tema.kartica(new BorderLayout(0, 12), 22);
		komentari.setLayout(new BoxLayout(komentari, BoxLayout.Y_AXIS));
		komentari.setOpaque(false);
		kom.add(naslovKomentara, BorderLayout.NORTH);
		kom.add(komentari, BorderLayout.CENTER);

		p.add(levo(glava));
		p.add(Box.createVerticalStrut(18));
		p.add(levo(sredina));
		p.add(Box.createVerticalStrut(18));
		p.add(levo(kom));
		return p;
	}

	private static JPanel karticaTeksta(String naslov, JTextArea tekst) {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 10), 22);
		k.add(Tema.naslov(naslov, 17), BorderLayout.NORTH);
		k.add(tekst, BorderLayout.CENTER);
		return k;
	}

	private void prikaziProizvod(Proizvod p) {
		naziv.setText(p.getNaziv());
		barKodInfo.setText("Bar-kod: " + p.getBarKod());
		Tema.postaviDaNe(posnoCip, p.isPosan(), "Posno", "Nije posno");
		Tema.postaviDaNe(halalCip, p.isHalal(), "Halal", "Nije halal");
		kcal.setText(String.valueOf(p.getEnergijaKcal()));

		nutritivneVrednosti.removeAll();
		redVrednosti("Energija", p.getEnergijaKJ() + " kJ / " + p.getEnergijaKcal() + " kcal", false);
		redVrednosti("Masti", gram(p.getMasti()), false);
		redVrednosti("    od toga zasićene masti", gram(p.getZasiceneMasti()), true);
		redVrednosti("Ugljeni hidrati", gram(p.getUgljeniHidrati()), false);
		redVrednosti("    od toga šećeri", gram(p.getSeceri()), true);
		redVrednosti("Vlakna", gram(p.getVlakna()), false);
		redVrednosti("Proteini", gram(p.getProteini()), false);
		redVrednosti("So", gram(p.getSo()), false);

		sastojci.setText(tekstIliCrtica(p.getSastojci()));
		doza.setText(tekstIliCrtica(p.getPreporucenaDnevnaDoza()));

		ucitajKomentare(p.getBarKod());
		rezultat.show(rezultatPanel, PROIZVOD);
		SwingUtilities.invokeLater(() -> skrol.getVerticalScrollBar().setValue(0));
		revalidate();
		repaint();
	}

	private static String gram(double v) {
		return Tema.broj(v) + " g";
	}

	private static String tekstIliCrtica(String s) {
		return s == null || s.isBlank() ? "-" : s;
	}

	private void redVrednosti(String naziv, String vrednost, boolean podstavka) {
		JPanel red = new JPanel(new BorderLayout());
		red.setOpaque(true);
		red.setBackground(nutritivneVrednosti.getComponentCount() % 2 == 0 ? Tema.POZADINA : Tema.POVRSINA);
		red.setBorder(new EmptyBorder(9, 12, 9, 12));
		JLabel n = new JLabel(naziv);
		n.setFont(Tema.font(Font.PLAIN, 14));
		n.setForeground(podstavka ? Tema.TEKST_SPOREDNI : Tema.TEKST_LABELE);
		JLabel v = new JLabel(vrednost);
		v.setFont(Tema.font(podstavka ? Font.PLAIN : Font.BOLD, 14));
		v.setForeground(Tema.TEKST);
		red.add(n, BorderLayout.WEST);
		red.add(v, BorderLayout.EAST);
		nutritivneVrednosti.add(red);
	}

	private void ucitajKomentare(String barKod) {
		komentari.removeAll();
		ArrayList<KomentarDTO> lista = app.izBaze(this, "Greška pri učitavanju komentara",
				() -> app.dao.vratiKomentareZaProizvod(barKod));
		if (lista == null || lista.isEmpty()) {
			naslovKomentara.setText("Komentari nutricionista");
			JLabel nema = Tema.sporedni(lista == null ? "Komentari trenutno nisu dostupni."
					: "Nijedan nutricionista još nije ocenio ovaj proizvod.");
			komentari.add(levo(nema));
		} else {
			double prosek = lista.stream().mapToInt(KomentarDTO::getOcena).average().orElse(0);
			naslovKomentara.setText("Komentari nutricionista (" + lista.size() + ")  ·  prosečna ocena " + Tema.broj(Math.round(prosek * 10) / 10.0) + " / 5");
			for (KomentarDTO k : lista) {
				komentari.add(levo(karticaKomentara(k)));
				komentari.add(Box.createVerticalStrut(10));
			}
		}
		komentari.revalidate();
	}

	private static JPanel karticaKomentara(KomentarDTO k) {
		Tema.Kartica c = Tema.kartica(new BorderLayout(0, 8), 16);
		c.setBackground(Tema.POZADINA);

		JLabel ko = Tema.naslov(tekstIliCrtica(k.getIme()) + " " + (k.getPrezime() != null ? k.getPrezime() : ""), 14);
		JLabel ocena = new JLabel(Tema.zvezdice(k.getOcena()));
		ocena.setFont(Tema.simboli(16));
		ocena.setForeground(Tema.UPOZORENJE);
		JPanel gore = new JPanel(new BorderLayout());
		gore.setOpaque(false);
		gore.add(ko, BorderLayout.WEST);
		gore.add(ocena, BorderLayout.EAST);

		JPanel telo = new JPanel();
		telo.setLayout(new BoxLayout(telo, BoxLayout.Y_AXIS));
		telo.setOpaque(false);
		telo.add(levo(Tema.tekstPrikaz(k.getKomentar())));
		if (k.getPreporucenaDoza() != null && !k.getPreporucenaDoza().isBlank()) {
			telo.add(Box.createVerticalStrut(8));
			JLabel dl = Tema.labelaPolja("Preporučena količina");
			dl.setFont(Tema.font(Font.BOLD, 12));
			telo.add(levo(dl));
			telo.add(Box.createVerticalStrut(2));
			telo.add(levo(Tema.tekstPrikaz(k.getPreporucenaDoza())));
		}

		c.add(gore, BorderLayout.NORTH);
		c.add(telo, BorderLayout.CENTER);
		return c;
	}

	// ═══════════════════════════════════════════════════════════
	// SKENIRANI PROIZVODI
	// ═══════════════════════════════════════════════════════════

	private void prikaziSkenirane() {
		ArrayList<Proizvod> lista = app.izBaze(this, "Greška pri učitavanju skeniranih proizvoda",
				() -> app.dao.vratiListuSkeniranih(app.korisnik.getIdKorisnika()));
		if (lista == null) return;

		JDialog d = Tema.dijalog(app, "Skenirani proizvodi", "Proizvodi koje ste pretraživali - dupli klik za prikaz", 900, 540);
		DefaultTableModel m = Tema.model("Bar-kod", "Naziv", "kcal", "Masti (g)", "Proteini (g)", "Ugljeni hid. (g)", "So (g)");
		for (Proizvod p : lista) {
			m.addRow(new Object[] { p.getBarKod(), p.getNaziv(), p.getEnergijaKcal(), p.getMasti(), p.getProteini(),
					p.getUgljeniHidrati(), p.getSo() });
		}
		JTable t = new JTable(m);
		Tema.stilizujTabelu(t);
		JTextField trazi = Tema.poljePretrage("Pretraži skenirane...");
		Tema.pretraga(t, trazi);
		Tema.sirineKolona(t, 130, 260, 70, 90, 100, 120, 70);

		Runnable otvori = () -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(d, "Izaberite proizvod u tabeli.");
				return;
			}
			String bk = (String) m.getValueAt(red, 0);
			Proizvod p = app.izBaze(d, "Greška pri učitavanju proizvoda", () -> app.dao.nadjiProizvod(bk));
			if (p == null) return;
			d.dispose();
			barKodPolje.setText(bk);
			prikaziProizvod(p);
		};
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) otvori.run();
			}
		});

		JPanel centar = new JPanel(new BorderLayout());
		centar.setOpaque(false);
		centar.add(Tema.trakaAlata(trazi), BorderLayout.NORTH);
		centar.add(Tema.skrolTabele(t), BorderLayout.CENTER);
		d.add(Tema.omotac(centar, 16, 20, 12, 20), BorderLayout.CENTER);

		Dugme prikazi = Dugme.primarno("Prikaži proizvod");
		prikazi.addActionListener(e -> otvori.run());
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(Tema.brojac(t, "proizvoda"), zatvori, prikazi), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	// ═══════════════════════════════════════════════════════════
	// NUTRICIONISTI I SLANJE PORUKE
	// ═══════════════════════════════════════════════════════════

	private void prikaziNutricioniste() {
		ArrayList<Nutricionista> lista = app.izBaze(this, "Greška pri učitavanju nutricionista", app.dao::vratiListuNutricionista);
		if (lista == null) return;
		if (lista.isEmpty()) {
			app.info(this, "Trenutno nema dostupnih nutricionista.");
			return;
		}

		JDialog d = Tema.dijalog(app, "Nutricionisti", "Izaberite nutricionistu i pošaljite pitanje ili zahtev za plan ishrane", 820, 520);
		DefaultTableModel m = Tema.model("#", "Ime", "Prezime", "Licenca", "Email");
		for (int i = 0; i < lista.size(); i++) {
			Nutricionista n = lista.get(i);
			m.addRow(new Object[] { i, n.getIme(), n.getPrezime(), n.getLicenca(), n.getEmail() });
		}
		JTable t = new JTable(m);
		Tema.stilizujTabelu(t);
		JTextField trazi = Tema.poljePretrage("Pretraži nutricioniste...");
		Tema.pretraga(t, trazi);
		Tema.sakrijKolonu(t, 0);
		Tema.sirineKolona(t, 0, 150, 170, 150, 260);

		Runnable posalji = () -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(d, "Izaberite nutricionistu u tabeli.");
				return;
			}
			slanjePoruke(d, lista.get((int) m.getValueAt(red, 0)));
		};
		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) posalji.run();
			}
		});

		JPanel centar = new JPanel(new BorderLayout());
		centar.setOpaque(false);
		centar.add(Tema.trakaAlata(trazi), BorderLayout.NORTH);
		centar.add(Tema.skrolTabele(t), BorderLayout.CENTER);
		d.add(Tema.omotac(centar, 16, 20, 12, 20), BorderLayout.CENTER);

		Dugme posaljiBtn = Dugme.primarno("Pošalji poruku");
		posaljiBtn.addActionListener(e -> posalji.run());
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(Tema.brojac(t, "nutricionista"), zatvori, posaljiBtn), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	private void slanjePoruke(JDialog roditelj, Nutricionista n) {
		JDialog d = Tema.dijalog(roditelj, "Nova poruka", "Za: " + n.getIme() + " " + n.getPrezime() + "  •  " + n.getEmail(), 540, 480);

		JRadioButton konsultacija = opcija("<html><b>Konsultacija</b><br><span style='color:#64748b'>Besplatno pitanje nutricionisti</span></html>");
		JRadioButton plan = opcija("<html><b>Plan ishrane</b><br><span style='color:#64748b'>Personalizovan plan, uz plaćanje</span></html>");
		konsultacija.setSelected(true);
		ButtonGroup g = new ButtonGroup();
		g.add(konsultacija);
		g.add(plan);
		JPanel opcije = new JPanel(new GridLayout(1, 2, 12, 0));
		opcije.setOpaque(false);
		opcije.add(konsultacija);
		opcije.add(plan);
		opcije.setAlignmentX(Component.LEFT_ALIGNMENT);
		opcije.setMaximumSize(new Dimension(Integer.MAX_VALUE, opcije.getPreferredSize().height));

		JTextArea poruka = Tema.oblast("");
		JPanel forma = new JPanel();
		forma.setLayout(new BoxLayout(forma, BoxLayout.Y_AXIS));
		forma.setOpaque(false);
		forma.add(levo(Tema.labelaPolja("Vrsta zahteva")));
		forma.add(Box.createVerticalStrut(8));
		forma.add(opcije);
		forma.add(Box.createVerticalStrut(16));
		forma.add(levo(Tema.labelaPolja("Poruka")));
		forma.add(Box.createVerticalStrut(6));
		forma.add(Tema.skrolOblasti(poruka, 170));
		d.add(Tema.omotac(forma, 18, 24, 12, 24), BorderLayout.CENTER);

		Dugme posalji = Dugme.primarno("Pošalji");
		posalji.addActionListener(e -> {
			String tekst = poruka.getText().trim();
			String tip = konsultacija.isSelected() ? "Konsultacija" : "Plan ishrane";
			if (!ZahtevKontroler.validirajZahtev(tip, tekst)) {
				app.upozorenje(d, "Unesite poruku.");
				return;
			}
			if (app.uBazi(d, "Greška pri slanju poruke", () -> app.dao.posaljiZahtevNutricionisti(
					app.korisnik.getIdKorisnika(), n.getIdNutricioniste(), tip, tekst))) {
				d.dispose();
				app.info(roditelj, "Poruka (" + tip + ") je poslata nutricionisti " + n.getIme() + " " + n.getPrezime()
						+ ".\nOdgovor ćete videti u Inbox-u.");
			}
		});
		Dugme odustani = Dugme.sekundarno("Odustani");
		odustani.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(null, odustani, posalji), BorderLayout.SOUTH);
		SwingUtilities.invokeLater(poruka::requestFocusInWindow);
		d.setVisible(true);
	}

	private static JRadioButton opcija(String html) {
		JRadioButton rb = new JRadioButton(html);
		rb.setFont(Tema.font(Font.PLAIN, 13));
		rb.setOpaque(true);
		rb.setBackground(Tema.POVRSINA);
		rb.setBorder(BorderFactory.createCompoundBorder(new Tema.ZaobljenaIvica(Tema.IVICA_POLJA, 10, 1), new EmptyBorder(8, 8, 8, 8)));
		rb.setBorderPainted(true);
		rb.setFocusPainted(false);
		return rb;
	}

	// ═══════════════════════════════════════════════════════════
	// MOJI RACUNI
	// ═══════════════════════════════════════════════════════════

	private void prikaziRacune() {
		ArrayList<RacunDTO> racuni = app.izBaze(this, "Greška pri učitavanju računa",
				() -> app.dao.vratiRacuneZaKorisnika(app.korisnik.getIdKorisnika()));
		if (racuni == null) return;

		JDialog d = Tema.dijalog(app, "Moji računi", "Računi za planove ishrane - posle plaćanja plan je vidljiv u Inbox-u", 820, 480);
		DefaultTableModel m = Tema.model("ID", "Nutricionista", "Iznos (RSD)", "Status", "Datum");
		for (RacunDTO r : racuni) {
			m.addRow(new Object[] { r.getIdRacuna(), r.getImeNutricioniste(), r.getIznos(), r.getStatus(), Tema.datum(r.getDatum()) });
		}
		JTable t = new JTable(m);
		Tema.stilizujTabelu(t);
		Tema.sakrijKolonu(t, 0);
		Tema.sirineKolona(t, 0, 260, 140, 150, 170);
		t.getColumnModel().getColumn(3).setCellRenderer(new Tema.StatusCelija());

		JComponent centar = m.getRowCount() == 0
				? Tema.prazno("Nemate račune", "Račun dobijate kada nutricionista odgovori na zahtev za plan ishrane.")
				: Tema.skrolTabele(t);
		d.add(Tema.omotac(centar, 16, 20, 12, 20), BorderLayout.CENTER);

		Dugme plati = Dugme.primarno("Plati izabrani račun");
		plati.addActionListener(e -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(d, "Izaberite račun u tabeli.");
				return;
			}
			if ("placen".equals(m.getValueAt(red, 3))) {
				app.info(d, "Ovaj račun je već plaćen.");
				return;
			}
			int iznos = (int) m.getValueAt(red, 2);
			if (!app.potvrdi(d, "Platiti račun od " + Tema.novac(iznos) + "?")) return;
			int idRacuna = (int) m.getValueAt(red, 0);
			if (app.uBazi(d, "Greška pri plaćanju", () -> app.dao.platiRacun(idRacuna))) {
				m.setValueAt("placen", red, 3);
				app.info(d, "Račun je plaćen! Plan ishrane možete pročitati u Inbox-u.");
			}
		});
		plati.setEnabled(m.getRowCount() > 0);
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(Tema.sporedni("Ukupno računa: " + m.getRowCount()), zatvori, plati), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	// ═══════════════════════════════════════════════════════════
	// INBOX - prepiska sa nutricionistima
	// ═══════════════════════════════════════════════════════════

	private void prikaziInbox() {
		ArrayList<Nutricionista> sagovornici = app.izBaze(this, "Greška pri učitavanju poruka",
				() -> app.dao.vratiSagovornikeKorisnika(app.korisnik.getIdKorisnika()));
		if (sagovornici == null) return;

		JDialog d = Tema.dijalog(app, "Inbox", "Vaše poruke sa nutricionistima", 900, 620);

		DefaultListModel<Nutricionista> listModel = new DefaultListModel<>();
		sagovornici.forEach(listModel::addElement);
		JList<Nutricionista> lista = new JList<>(listModel);
		lista.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
				Nutricionista n = (Nutricionista) v;
				super.getListCellRendererComponent(l, "<html><b>" + n.getIme() + " " + n.getPrezime()
						+ "</b><br><span style='color:#64748b'>" + n.getEmail() + "</span></html>", i, sel, false);
				setBorder(new EmptyBorder(10, 14, 10, 14));
				setBackground(sel ? Tema.PRIMARNA_SVETLA : Tema.POVRSINA);
				setForeground(Tema.TEKST);
				return this;
			}
		});
		lista.setFont(Tema.font(Font.PLAIN, 13));
		JScrollPane listaSkrol = new JScrollPane(lista);
		listaSkrol.setBorder(new MatteBorder(0, 0, 0, 1, Tema.IVICA));
		listaSkrol.setPreferredSize(new Dimension(250, 0));

		JPanel chat = new JPanel();
		chat.setLayout(new BoxLayout(chat, BoxLayout.Y_AXIS));
		chat.setBackground(Tema.CHAT_POZADINA);
		chat.setBorder(new EmptyBorder(10, 0, 10, 0));
		JScrollPane chatSkrol = new JScrollPane(chat);
		chatSkrol.setBorder(null);
		chatSkrol.getVerticalScrollBar().setUnitIncrement(14);
		chatSkrol.getViewport().setBackground(Tema.CHAT_POZADINA);

		if (sagovornici.isEmpty()) {
			chat.add(levo(Tema.prazno("Nemate poruka", "Pošaljite pitanje nutricionisti iz menija \"Nutricionisti\".")));
		} else {
			chat.add(levo(Tema.prazno("Izaberite razgovor", "Kliknite na nutricionistu levo da vidite prepisku.")));
		}

		lista.addListSelectionListener(ev -> {
			if (ev.getValueIsAdjusting() || lista.getSelectedValue() == null) return;
			Nutricionista n = lista.getSelectedValue();
			ArrayList<ZahtevZaSaradnju> zahtevi = app.izBaze(d, "Greška pri učitavanju poruka",
					() -> app.dao.vratiZahteve(app.korisnik.getIdKorisnika(), n.getIdNutricioniste()));
			chat.removeAll();
			if (zahtevi != null) {
				for (ZahtevZaSaradnju z : zahtevi) dodajUPrepisku(chat, z, n);
			}
			chat.revalidate();
			chat.repaint();
			SwingUtilities.invokeLater(() -> {
				JScrollBar sb = chatSkrol.getVerticalScrollBar();
				sb.setValue(sb.getMaximum());
			});
		});

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listaSkrol, chatSkrol);
		split.setDividerLocation(250);
		split.setDividerSize(1);
		split.setBorder(null);
		d.add(split, BorderLayout.CENTER);

		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(null, zatvori), BorderLayout.SOUTH);
		if (!listModel.isEmpty()) lista.setSelectedIndex(0);
		d.setVisible(true);
	}

	private static void dodajUPrepisku(JPanel chat, ZahtevZaSaradnju z, Nutricionista n) {
		String tip = z.getTip() != null && !z.getTip().isEmpty() ? "  •  " + z.getTip() : "";
		chat.add(Tema.chatBalon("Vi" + tip, z.getOpis() != null ? z.getOpis() : "", true));

		String status = z.getStatus() != null ? z.getStatus() : "";
		boolean imaOdgovor = z.getOdgovor() != null && !z.getOdgovor().isBlank();
		if (imaOdgovor) {
			// Konsultacija je besplatna; plan ishrane se vidi tek posle placanja (status dostavljen_odgovor).
			boolean vidljiv = "Konsultacija".equals(z.getTip()) || "dostavljen_odgovor".equals(status);
			if (vidljiv) {
				chat.add(Tema.chatBalon(n.getIme() + " " + n.getPrezime(), z.getOdgovor(), false));
			} else if ("odgovoren".equals(status)) {
				chat.add(Tema.chatNapomena("Plan ishrane je spreman - platite račun u \"Moji računi\" da biste ga videli.",
						Tema.UPOZORENJE, false));
			}
		} else {
			String napomena = switch (status) {
			case "poslat" -> "Čeka se da nutricionista prihvati zahtev.";
			case "prihvacen" -> "Zahtev je prihvaćen - nutricionista priprema odgovor.";
			case "neprihvacen" -> "Nutricionista je odbio ovaj zahtev.";
			default -> null;
			};
			if (napomena != null) {
				chat.add(Tema.chatNapomena(napomena, "neprihvacen".equals(status) ? Tema.OPASNOST : Tema.TEKST_SPOREDNI, true));
			}
		}
		chat.add(Box.createVerticalStrut(8));
	}
}

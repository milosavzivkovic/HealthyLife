package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.table.DefaultTableModel;

import controller.KomentarKontroler;
import controller.ZahtevKontroler;
import model.Korisnik;
import model.Proizvod;
import model.ZahtevZaSaradnju;

/** Ekran prijavljenog nutricioniste: poruke korisnika i ocenjivanje proizvoda. */
class NutricionistaPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final GUI app;
	private final JLabel imeNutricioniste = Tema.imeUZaglavlju();
	private final JLabel dobrodosli = Tema.naslov("", 24);

	NutricionistaPanel(GUI app) {
		super(new BorderLayout());
		this.app = app;
		setBackground(Tema.POZADINA);

		Dugme odjava = Dugme.naZaglavlju("Odjavi se");
		odjava.addActionListener(e -> app.odjava());

		JPanel plocice = new JPanel(new GridLayout(1, 2, 24, 0));
		plocice.setOpaque(false);
		plocice.add(Tema.plocica("Poruke korisnika",
				opis("Prihvatite ili odbijte zahteve, odgovorite na pitanja i pošaljite planove ishrane."),
				Tema.INFO, this::prikaziPoruke));
		plocice.add(Tema.plocica("Pregled proizvoda",
				opis("Pregledajte sve proizvode u bazi i ostavite stručnu ocenu i preporuku."),
				Tema.PRIMARNA, this::prikaziProizvode));
		plocice.setPreferredSize(new Dimension(800, 220));

		JPanel telo = new JPanel(new BorderLayout(0, 22));
		telo.setOpaque(false);
		JPanel naslov = new JPanel();
		naslov.setLayout(new BoxLayout(naslov, BoxLayout.Y_AXIS));
		naslov.setOpaque(false);
		naslov.add(dobrodosli);
		naslov.add(Box.createVerticalStrut(4));
		naslov.add(Tema.sporedni("Šta želite da uradite danas?"));
		telo.add(naslov, BorderLayout.NORTH);
		telo.add(plocice, BorderLayout.CENTER);

		JPanel omot = new JPanel(new BorderLayout());
		omot.setOpaque(false);
		omot.add(telo, BorderLayout.NORTH);

		add(Tema.zaglavlje("Panel nutricioniste", "Saveti i ocene za korisnike Healthy Life", imeNutricioniste, odjava),
				BorderLayout.NORTH);
		add(Tema.omotac(omot, 32, 36, 32, 36), BorderLayout.CENTER);
	}

	void otvori() {
		if (app.nutricionista != null) {
			imeNutricioniste.setText(app.nutricionista.getIme() + " " + app.nutricionista.getPrezime());
			dobrodosli.setText("Dobro došli, " + app.nutricionista.getIme() + "!");
		}
	}

	private static JLabel opis(String tekst) {
		JLabel l = Tema.sporedni("<html><div style='width:300px'>" + tekst + "</div></html>");
		return l;
	}

	private static <T extends JComponent> T levo(T c) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		return c;
	}

	// ═══════════════════════════════════════════════════════════
	// PROIZVODI
	// ═══════════════════════════════════════════════════════════

	private void prikaziProizvode() {
		ArrayList<Proizvod> lista = app.izBaze(this, "Greška pri učitavanju proizvoda", app.dao::vratiListuProizvoda);
		if (lista == null) return;

		JDialog d = Tema.dijalog(app, "Proizvodi", "Dupli klik na red za detalje proizvoda", 1250, 700);
		DefaultTableModel m = Tema.model("Bar-kod", "Naziv", "Posno", "Halal", "kJ", "kcal", "Masti (g)",
				"Zas. masti (g)", "Ugljeni hid. (g)", "Šećeri (g)", "Vlakna (g)", "Proteini (g)", "So (g)");
		for (Proizvod p : lista) {
			m.addRow(new Object[] { p.getBarKod(), p.getNaziv(), p.isPosan() ? "Da" : "Ne", p.isHalal() ? "Da" : "Ne",
					p.getEnergijaKJ(), p.getEnergijaKcal(), p.getMasti(), p.getZasiceneMasti(), p.getUgljeniHidrati(),
					p.getSeceri(), p.getVlakna(), p.getProteini(), p.getSo() });
		}
		JTable t = new JTable(m);
		Tema.stilizujTabelu(t);
		JTextField trazi = Tema.poljePretrage("Pretraži po nazivu ili bar-kodu...");
		Tema.pretraga(t, trazi);
		Tema.sirineKolona(t, 120, 240, 60, 60, 60, 60, 80, 95, 105, 80, 80, 85, 65);

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() != 2) return;
				int red = Tema.izabraniRed(t);
				if (red < 0) return;
				String bk = (String) m.getValueAt(red, 0);
				Proizvod p = app.izBaze(d, "Greška pri učitavanju proizvoda", () -> app.dao.nadjiProizvod(bk));
				if (p != null) detaljiProizvoda(d, p);
			}
		});

		JPanel centar = new JPanel(new BorderLayout());
		centar.setOpaque(false);
		centar.add(Tema.trakaAlata(trazi), BorderLayout.NORTH);
		centar.add(Tema.skrolTabele(t), BorderLayout.CENTER);
		d.add(Tema.omotac(centar, 16, 20, 12, 20), BorderLayout.CENTER);

		Dugme oceni = Dugme.primarno("Oceni izabrani");
		oceni.addActionListener(e -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(d, "Izaberite proizvod u tabeli.");
				return;
			}
			oceniProizvod(d, (String) m.getValueAt(red, 0), (String) m.getValueAt(red, 1));
		});
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(Tema.brojac(t, "proizvoda"), zatvori, oceni), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	private void detaljiProizvoda(JDialog roditelj, Proizvod p) {
		JDialog d = Tema.dijalog(roditelj, p.getNaziv(), "Bar-kod: " + p.getBarKod(), 560, 680);

		JPanel cipovi = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cipovi.setOpaque(false);
		JLabel posno = Tema.cip("", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST);
		JLabel halal = Tema.cip("", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST);
		Tema.postaviDaNe(posno, p.isPosan(), "Posno", "Nije posno");
		Tema.postaviDaNe(halal, p.isHalal(), "Halal", "Nije halal");
		cipovi.add(posno);
		cipovi.add(Box.createHorizontalStrut(8));
		cipovi.add(halal);

		JPanel vrednosti = new JPanel(new GridLayout(0, 2, 0, 0));
		vrednosti.setOpaque(false);
		String[][] redovi = {
				{ "Energija", p.getEnergijaKJ() + " kJ / " + p.getEnergijaKcal() + " kcal" },
				{ "Masti", Tema.broj(p.getMasti()) + " g" },
				{ "Zasićene masti", Tema.broj(p.getZasiceneMasti()) + " g" },
				{ "Ugljeni hidrati", Tema.broj(p.getUgljeniHidrati()) + " g" },
				{ "Šećeri", Tema.broj(p.getSeceri()) + " g" },
				{ "Vlakna", Tema.broj(p.getVlakna()) + " g" },
				{ "Proteini", Tema.broj(p.getProteini()) + " g" },
				{ "So", Tema.broj(p.getSo()) + " g" } };
		for (int i = 0; i < redovi.length; i++) {
			Color pozadina = i % 2 == 0 ? Tema.POZADINA : Tema.POVRSINA;
			JLabel n = new JLabel(redovi[i][0]);
			JLabel v = new JLabel(redovi[i][1], JLabel.RIGHT);
			n.setFont(Tema.font(Font.PLAIN, 14));
			v.setFont(Tema.font(Font.BOLD, 14));
			for (JLabel l : new JLabel[] { n, v }) {
				l.setOpaque(true);
				l.setBackground(pozadina);
				l.setBorder(new EmptyBorder(8, 12, 8, 12));
				vrednosti.add(l);
			}
		}

		JPanel sadrzaj = new JPanel();
		sadrzaj.setLayout(new BoxLayout(sadrzaj, BoxLayout.Y_AXIS));
		sadrzaj.setOpaque(false);
		sadrzaj.add(levo(cipovi));
		sadrzaj.add(Box.createVerticalStrut(16));
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 10), 16);
		k.add(Tema.naslov("Nutritivne vrednosti (na 100 g/ml)", 15), BorderLayout.NORTH);
		k.add(vrednosti, BorderLayout.CENTER);
		sadrzaj.add(levo(k));
		sadrzaj.add(Box.createVerticalStrut(12));
		sadrzaj.add(levo(okvirTeksta("Sastojci", p.getSastojci())));
		sadrzaj.add(Box.createVerticalStrut(12));
		sadrzaj.add(levo(okvirTeksta("Preporučena dnevna doza", p.getPreporucenaDnevnaDoza())));

		Tema.SkrolPanel skrolPanel = new Tema.SkrolPanel();
		skrolPanel.setBorder(new EmptyBorder(18, 22, 18, 22));
		skrolPanel.add(levo(sadrzaj));
		d.add(Tema.skrol(skrolPanel), BorderLayout.CENTER);

		Dugme oceni = Dugme.primarno("Oceni proizvod");
		oceni.addActionListener(e -> {
			d.dispose();
			oceniProizvod(roditelj, p.getBarKod(), p.getNaziv());
		});
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(null, zatvori, oceni), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	private static JPanel okvirTeksta(String naslov, String tekst) {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 8), 16);
		k.add(Tema.naslov(naslov, 15), BorderLayout.NORTH);
		k.add(Tema.tekstPrikaz(tekst), BorderLayout.CENTER);
		return k;
	}

	/** Ocena 1-5 klikom na zvezdicu, strucni komentar i preporucena kolicina. */
	private void oceniProizvod(JDialog roditelj, String barKod, String naziv) {
		JDialog d = Tema.dijalog(roditelj, "Ocena proizvoda", naziv, 520, 600);

		int[] ocena = { 0 };
		JLabel[] zvezde = new JLabel[5];
		JLabel opisOcene = Tema.sporedni("Kliknite na zvezdicu");
		String[] opisi = { "Loše", "Ispod proseka", "Prosečno", "Dobro", "Odlično" };
		JPanel zvezdePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
		zvezdePanel.setOpaque(false);
		Runnable[] oboji = new Runnable[1];
		oboji[0] = () -> {
			for (int i = 0; i < 5; i++) zvezde[i].setForeground(i < ocena[0] ? Tema.UPOZORENJE : Tema.IVICA_POLJA);
		};
		for (int i = 0; i < 5; i++) {
			int vrednost = i + 1;
			JLabel z = new JLabel("★");
			z.setFont(Tema.simboli(34));
			z.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
			z.setToolTipText(vrednost + " - " + opisi[i]);
			z.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					ocena[0] = vrednost;
					opisOcene.setText(vrednost + "/5 - " + opisi[vrednost - 1]);
					oboji[0].run();
				}

				@Override
				public void mouseEntered(MouseEvent e) {
					for (int j = 0; j < 5; j++) zvezde[j].setForeground(j < vrednost ? Tema.UPOZORENJE : Tema.IVICA_POLJA);
				}

				@Override
				public void mouseExited(MouseEvent e) {
					oboji[0].run();
				}
			});
			zvezde[i] = z;
			zvezdePanel.add(z);
		}
		oboji[0].run();
		zvezdePanel.add(Box.createHorizontalStrut(10));
		zvezdePanel.add(opisOcene);
		zvezdePanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, zvezdePanel.getPreferredSize().height));

		JTextArea komentar = Tema.oblast("");
		JTextArea kolicina = Tema.oblast("");

		JPanel forma = new JPanel();
		forma.setLayout(new BoxLayout(forma, BoxLayout.Y_AXIS));
		forma.setOpaque(false);
		forma.add(levo(Tema.sporedni("Bar-kod: " + barKod)));
		forma.add(Box.createVerticalStrut(14));
		forma.add(levo(Tema.labelaPolja("Ocena")));
		forma.add(levo(zvezdePanel));
		forma.add(Box.createVerticalStrut(14));
		forma.add(levo(Tema.labelaPolja("Stručni komentar")));
		forma.add(Box.createVerticalStrut(6));
		forma.add(Tema.skrolOblasti(komentar, 150));
		forma.add(Box.createVerticalStrut(14));
		forma.add(levo(Tema.labelaPolja("Preporučena količina")));
		forma.add(Box.createVerticalStrut(6));
		forma.add(Tema.skrolOblasti(kolicina, 80));
		d.add(Tema.omotac(forma, 18, 24, 12, 24), BorderLayout.CENTER);

		Dugme sacuvaj = Dugme.primarno("Sačuvaj ocenu");
		sacuvaj.addActionListener(e -> {
			String k = komentar.getText().trim();
			String kol = kolicina.getText().trim();
			if (!KomentarKontroler.validacijaOcene(k, ocena[0], kol)) {
				app.upozorenje(d, "Izaberite ocenu i popunite komentar i preporučenu količinu.");
				return;
			}
			if (app.uBazi(d, "Greška pri čuvanju ocene", () -> app.dao.unesiOcenuProizvoda(
					app.nutricionista.getIdNutricioniste(), barKod, k, kol, ocena[0]))) {
				d.dispose();
				app.info(roditelj, "Ocena je sačuvana!\nProizvod: " + naziv + "\nOcena: " + ocena[0] + "/5");
			}
		});
		Dugme otkazi = Dugme.sekundarno("Otkaži");
		otkazi.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(null, otkazi, sacuvaj), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	// ═══════════════════════════════════════════════════════════
	// PORUKE KORISNIKA
	// ═══════════════════════════════════════════════════════════

	private void prikaziPoruke() {
		int idN = app.nutricionista.getIdNutricioniste();
		ArrayList<Korisnik> posiljaoci = app.izBaze(this, "Greška pri učitavanju poruka", () -> app.dao.vratiZahteve(idN));
		if (posiljaoci == null) return;

		JDialog d = Tema.dijalog(app, "Poruke korisnika", "Zahtevi i pitanja koja su vam korisnici poslali", 980, 680);

		DefaultListModel<Korisnik> listModel = new DefaultListModel<>();
		posiljaoci.forEach(listModel::addElement);
		JList<Korisnik> lista = new JList<>(listModel);
		lista.setCellRenderer(new DefaultListCellRenderer() {
			private static final long serialVersionUID = 1L;

			@Override
			public Component getListCellRendererComponent(JList<?> l, Object v, int i, boolean sel, boolean foc) {
				Korisnik k = (Korisnik) v;
				super.getListCellRendererComponent(l, "<html><b>" + k.getIme() + " " + k.getPrezime()
						+ "</b><br><span style='color:#64748b'>" + k.getEmail() + "</span></html>", i, sel, false);
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
		chat.add(levo(posiljaoci.isEmpty()
				? Tema.prazno("Nema poruka", "Još vam nijedan korisnik nije poslao zahtev.")
				: Tema.prazno("Izaberite korisnika", "Kliknite na korisnika levo da vidite njegove zahteve.")));

		// ── Polje za odgovor (vidi se tek kada se klikne "Odgovori") ──
		ZahtevZaSaradnju[] izabrani = { null };
		JLabel odgovorNa = Tema.labelaPolja(" ");
		JTextArea odgovor = Tema.oblast("");
		Dugme posalji = Dugme.primarno("Pošalji odgovor");
		Dugme odustani = Dugme.sekundarno("Odustani");
		JPanel dugmadOdgovora = new JPanel(new GridLayout(2, 1, 0, 8));
		dugmadOdgovora.setOpaque(false);
		dugmadOdgovora.add(posalji);
		dugmadOdgovora.add(odustani);
		JPanel odgovorPanel = new JPanel(new BorderLayout(12, 8));
		odgovorPanel.setBackground(Tema.POVRSINA);
		odgovorPanel.setBorder(new CompoundBorder(new MatteBorder(1, 0, 0, 0, Tema.IVICA), new EmptyBorder(12, 16, 12, 16)));
		odgovorPanel.add(odgovorNa, BorderLayout.NORTH);
		odgovorPanel.add(Tema.skrolOblasti(odgovor, 90), BorderLayout.CENTER);
		JPanel dugmadOmot = new JPanel(new BorderLayout());
		dugmadOmot.setOpaque(false);
		dugmadOmot.add(dugmadOdgovora, BorderLayout.NORTH);
		odgovorPanel.add(dugmadOmot, BorderLayout.EAST);
		odgovorPanel.setVisible(false);

		JPanel desno = new JPanel(new BorderLayout());
		desno.add(chatSkrol, BorderLayout.CENTER);
		desno.add(odgovorPanel, BorderLayout.SOUTH);

		// Ponovo ucitava prepisku izabranog korisnika (posle svake akcije).
		Runnable[] osvezi = new Runnable[1];
		osvezi[0] = () -> {
			Korisnik k = lista.getSelectedValue();
			if (k == null) return;
			izabrani[0] = null;
			odgovor.setText("");
			odgovorPanel.setVisible(false);
			ArrayList<ZahtevZaSaradnju> zahtevi = app.izBaze(d, "Greška pri učitavanju zahteva",
					() -> app.dao.vratiZahteve(k.getIdKorisnika(), idN));
			chat.removeAll();
			if (zahtevi != null) {
				for (ZahtevZaSaradnju z : zahtevi) {
					dodajZahtevUPrepisku(chat, d, k, z, osvezi[0], () -> {
						izabrani[0] = z;
						odgovorNa.setText("Odgovor na: " + z.getTip()
								+ ("Plan ishrane".equals(z.getTip()) ? "  (posle slanja unosite cenu plana)" : ""));
						odgovor.setText("");
						odgovorPanel.setVisible(true);
						d.revalidate();
						odgovor.requestFocusInWindow();
					});
				}
			}
			chat.revalidate();
			chat.repaint();
			SwingUtilities.invokeLater(() -> {
				JScrollBar sb = chatSkrol.getVerticalScrollBar();
				sb.setValue(sb.getMaximum());
			});
		};
		lista.addListSelectionListener(ev -> {
			if (!ev.getValueIsAdjusting()) osvezi[0].run();
		});

		odustani.addActionListener(e -> {
			izabrani[0] = null;
			odgovorPanel.setVisible(false);
			d.revalidate();
		});
		posalji.addActionListener(e -> {
			ZahtevZaSaradnju z = izabrani[0];
			if (z == null) return;
			String tekst = odgovor.getText().trim();
			if (!ZahtevKontroler.validirajOdgovor(tekst)) {
				app.upozorenje(d, "Unesite odgovor.");
				return;
			}
			int iznos = 0;
			if ("Plan ishrane".equals(z.getTip())) {
				Integer unet = unesiIznos(d);
				if (unet == null) return;
				iznos = unet;
			}
			int konacanIznos = iznos;
			if (app.uBazi(d, "Greška pri slanju odgovora",
					() -> app.dao.dodajOdgovorNaZahtev(z.getIdZahteva(), tekst, konacanIznos))) {
				app.info(d, konacanIznos > 0
						? "Odgovor je poslat. Korisnik će ga videti kada plati račun od " + Tema.novac(konacanIznos) + "."
						: "Odgovor je poslat!");
				osvezi[0].run();
			}
		});

		JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, listaSkrol, desno);
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

	/** Cena plana ishrane; null ako je nutricionista odustao. */
	private Integer unesiIznos(JDialog d) {
		JTextField polje = Tema.polje("npr. 3000");
		polje.setPreferredSize(new Dimension(260, 40));
		JPanel p = new JPanel(new BorderLayout(0, 8));
		p.add(Tema.labelaPolja("Cena plana ishrane (RSD):"), BorderLayout.NORTH);
		p.add(polje, BorderLayout.CENTER);
		SwingUtilities.invokeLater(polje::requestFocusInWindow);
		while (true) {
			int r = JOptionPane.showConfirmDialog(d, p, "Cena plana ishrane", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
			if (r != JOptionPane.OK_OPTION) return null;
			try {
				int iznos = Integer.parseInt(polje.getText().trim());
				if (iznos > 0) return iznos;
			} catch (NumberFormatException ignored) {
				// pokazujemo poruku ispod
			}
			app.upozorenje(d, "Unesite ceo broj veći od 0.");
		}
	}

	/** Jedan zahtev u prepisci: poruka korisnika, odgovor ili dugmad za akciju. */
	private void dodajZahtevUPrepisku(JPanel chat, JDialog d, Korisnik k, ZahtevZaSaradnju z, Runnable osvezi,
			Runnable odgovori) {
		String tip = z.getTip() != null && !z.getTip().isEmpty() ? "  •  " + z.getTip() : "";
		chat.add(Tema.chatBalon(k.getIme() + " " + k.getPrezime() + tip, z.getOpis() != null ? z.getOpis() : "", false));

		String status = z.getStatus() != null ? z.getStatus() : "poslat";
		if (z.getOdgovor() != null && !z.getOdgovor().isBlank()) {
			chat.add(Tema.chatBalon("Vi", z.getOdgovor(), true));
			if ("odgovoren".equals(status)) {
				chat.add(Tema.chatNapomena("Odgovoreno - čeka se plaćanje", Tema.UPOZORENJE, true));
			} else if ("dostavljen_odgovor".equals(status)) {
				chat.add(Tema.chatNapomena("Završeno", Tema.PRIMARNA_TAMNA, true));
			}
		} else {
			JPanel akcije = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
			akcije.setOpaque(false);
			akcije.setBorder(new EmptyBorder(0, 10, 0, 0));
			String ime = k.getIme() + " " + k.getPrezime();
			switch (status) {
			case "poslat" -> {
				akcije.add(maloDugme(Dugme.primarno("Prihvati"), () -> {
					if (app.potvrdi(d, "Prihvatiti zahtev korisnika " + ime + "?")
							&& app.uBazi(d, "Greška", () -> app.dao.prihvatiZahtev(z.getIdZahteva()))) osvezi.run();
				}));
				akcije.add(maloDugme(Dugme.opasno("Odbij"), () -> {
					if (app.potvrdi(d, "Odbiti zahtev korisnika " + ime + "?")
							&& app.uBazi(d, "Greška", () -> app.dao.odbijZahtev(z.getIdZahteva()))) osvezi.run();
				}));
			}
			case "prihvacen" -> akcije.add(maloDugme(Dugme.boja("Odgovori", Tema.INFO), odgovori));
			case "neprihvacen" -> {
				// Odbijen zahtev moze ponovo da se prihvati (predomislio se).
				akcije.add(Tema.chatNapomena("Odbijeno", Tema.OPASNOST, false));
				akcije.add(maloDugme(Dugme.sekundarno("Ipak prihvati"), () -> {
					if (app.uBazi(d, "Greška", () -> app.dao.prihvatiZahtev(z.getIdZahteva()))) osvezi.run();
				}));
			}
			default -> {
			}
			}
			akcije.setAlignmentX(Component.LEFT_ALIGNMENT);
			akcije.setMaximumSize(new Dimension(Integer.MAX_VALUE, akcije.getPreferredSize().height));
			chat.add(akcije);
		}
		chat.add(Box.createVerticalStrut(10));
	}

	private static Dugme maloDugme(Dugme d, Runnable akcija) {
		d.setFont(Tema.font(Font.BOLD, 12));
		d.setBorder(new EmptyBorder(6, 14, 6, 14));
		d.addActionListener(e -> akcija.run());
		return d;
	}
}

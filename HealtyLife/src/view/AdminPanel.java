package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;

import controller.ProizvodKontroler;
import model.Korisnik;
import model.Nutricionista;
import model.Proizvod;
import model.RacunDTO;
import model.Statistika;
import model.ZahtevZaSaradnju;

/** Svi ekrani administratora. */
class AdminPanel {

	static final String KONTROLNA_TABLA = "ADMIN";
	static final String KORISNICI = "ADMIN_KORISNICI";
	static final String NUTRICIONISTI = "ADMIN_NUTRICIONISTI";
	static final String PROIZVODI = "ADMIN_PROIZVODI";
	static final String ZAHTEVI = "ADMIN_ZAHTEVI";

	private final GUI app;

	private final JLabel imeAdmina = Tema.imeUZaglavlju();
	private final JLabel dobrodosli = new JLabel();
	private final JLabel brojKorisnika = brojka(Tema.INFO);
	private final JLabel brojNutricionista = brojka(Tema.PRIMARNA);
	private final JLabel brojProizvoda = brojka(Tema.UPOZORENJE);
	private final JLabel brojZahteva = brojka(Tema.LJUBICASTA);
	private final JLabel iznosProvizije = brojka(Tema.PRIMARNA_TAMNA);

	private final DefaultTableModel korisniciModel = Tema.model("ID", "Ime", "Prezime", "Email");
	private final DefaultTableModel nutricionistiModel = Tema.model("ID", "Ime", "Prezime", "Licenca", "Email");
	private final DefaultTableModel proizvodiModel = Tema.model("Bar-kod", "Naziv", "Posno", "Halal",
			"kJ", "kcal", "Masti (g)", "Zas. masti (g)", "Ugljeni hid. (g)", "Šećeri (g)", "Vlakna (g)",
			"Proteini (g)", "So (g)");
	private final DefaultTableModel zahteviModel = Tema.model("ID", "Korisnik", "Nutricionista", "Tip", "Opis",
			"Odgovor", "Status");

	private final JTextField pretragaKorisnika = Tema.poljePretrage("Pretraži po imenu ili emailu...");
	private final JTextField pretragaNutricionista = Tema.poljePretrage("Pretraži po imenu, licenci ili emailu...");
	private final JTextField pretragaProizvoda = Tema.poljePretrage("Pretraži po nazivu ili bar-kodu...");
	private final JTextField pretragaZahteva = Tema.poljePretrage("Pretraži zahteve...");
	private final JComboBox<String> filterStatusa = new JComboBox<>();

	AdminPanel(GUI app) {
		this.app = app;
	}

	void dodajEkrane(JPanel sadrzaj) {
		sadrzaj.add(kontrolnaTabla(), KONTROLNA_TABLA);
		sadrzaj.add(ekranKorisnika(), KORISNICI);
		sadrzaj.add(ekranNutricionista(), NUTRICIONISTI);
		sadrzaj.add(ekranProizvoda(), PROIZVODI);
		sadrzaj.add(ekranZahteva(), ZAHTEVI);
	}

	/** Poziva se posle prijave i pri povratku na kontrolnu tablu. */
	void otvori() {
		if (app.administrator != null) {
			String ime = app.administrator.getIme() + " " + app.administrator.getPrezime();
			imeAdmina.setText(ime);
			dobrodosli.setText("<html>Dobro došli,<br>" + app.administrator.getIme() + "!</html>");
		}
		Statistika s = app.izBaze(app, "Greška pri učitavanju statistike", app.dao::vratiStatistiku);
		if (s != null) {
			brojKorisnika.setText(String.valueOf(s.korisnika()));
			brojNutricionista.setText(String.valueOf(s.nutricionista()));
			brojProizvoda.setText(String.valueOf(s.proizvoda()));
			brojZahteva.setText(String.valueOf(s.zahtevaNaCekanju()));
			iznosProvizije.setText(Tema.novac(s.provizija()));
		}
		app.prikazi(KONTROLNA_TABLA);
	}

	private Dugme nazadNaTablu() {
		Dugme d = Dugme.naZaglavlju("← Kontrolna tabla");
		d.addActionListener(e -> otvori());
		return d;
	}

	// ═══════════════════════════════════════════════════════════
	// KONTROLNA TABLA
	// ═══════════════════════════════════════════════════════════

	private JPanel kontrolnaTabla() {
		Dugme odjava = Dugme.naZaglavlju("Odjavi se");
		odjava.addActionListener(e -> app.odjava());

		JPanel mreza = new JPanel(new GridLayout(2, 3, 20, 20));
		mreza.setOpaque(false);
		mreza.add(karticaDobrodoslice());
		mreza.add(plocica("Korisnici", "registrovanih korisnika", brojKorisnika, Tema.INFO, () -> {
			osveziKorisnike();
			app.prikazi(KORISNICI);
		}));
		mreza.add(plocica("Nutricionisti", "aktivnih nutricionista", brojNutricionista, Tema.PRIMARNA, () -> {
			osveziNutricioniste();
			app.prikazi(NUTRICIONISTI);
		}));
		mreza.add(plocica("Proizvodi", "proizvoda u bazi", brojProizvoda, Tema.UPOZORENJE, () -> {
			osveziProizvode();
			app.prikazi(PROIZVODI);
		}));
		mreza.add(plocica("Zahtevi za saradnju", "zahteva čeka odgovor", brojZahteva, Tema.LJUBICASTA, () -> {
			// Ranije se tabela zahteva punila samo jednom, pri pokretanju aplikacije.
			osveziZahteve();
			app.prikazi(ZAHTEVI);
		}));
		mreza.add(plocica("Evidencija zarade", "ukupna provizija", iznosProvizije, Tema.PRIMARNA_TAMNA,
				this::prikaziEvidencijuZarade));

		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Tema.POZADINA);
		p.add(Tema.zaglavlje("Administratorski panel", "Upravljanje sistemom Healthy Life", imeAdmina, odjava),
				BorderLayout.NORTH);
		mreza.setPreferredSize(new Dimension(900, 440));
		JPanel mrezaGore = new JPanel(new BorderLayout());
		mrezaGore.setOpaque(false);
		mrezaGore.add(mreza, BorderLayout.NORTH);
		p.add(Tema.omotac(mrezaGore, 28, 32, 32, 32), BorderLayout.CENTER);
		return p;
	}

	private JPanel karticaDobrodoslice() {
		JPanel k = new Tema.GradijentPanel(new BorderLayout(0, 10));
		k.setBorder(new EmptyBorder(26, 26, 26, 26));
		dobrodosli.setFont(Tema.font(Font.BOLD, 24));
		dobrodosli.setForeground(Color.WHITE);
		JLabel opis = new JLabel("<html>Izaberite oblast kojom želite da upravljate. Brojke se osvežavaju pri svakom povratku ovde.</html>");
		opis.setFont(Tema.font(Font.PLAIN, 13));
		opis.setForeground(new Color(220, 252, 231));
		k.add(dobrodosli, BorderLayout.NORTH);
		k.add(opis, BorderLayout.CENTER);
		return k;
	}

	private static JLabel brojka(Color boja) {
		JLabel l = new JLabel("-");
		l.setFont(Tema.font(Font.BOLD, 34));
		l.setForeground(boja);
		return l;
	}

	/** Kartica na kontrolnoj tabli sa brojkom i opisom; cela je klikabilna. */
	private static JPanel plocica(String naslov, String opis, JLabel brojka, Color boja, Runnable akcija) {
		JPanel sadrzaj = new JPanel();
		sadrzaj.setLayout(new BoxLayout(sadrzaj, BoxLayout.Y_AXIS));
		sadrzaj.setOpaque(false);
		sadrzaj.add(brojka);
		sadrzaj.add(Tema.sporedni(opis));
		return Tema.plocica(naslov, sadrzaj, boja, akcija);
	}

	/** Zajednicki raspored ekrana sa tabelom: zaglavlje, pretraga, tabela, podnozje sa dugmadima. */
	private JPanel ekranSaTabelom(String naslov, String podnaslov, JTable tabela, JComponent traka, JLabel brojac,
			JComponent... dugmad) {
		JPanel centar = new JPanel(new BorderLayout());
		centar.setOpaque(false);
		centar.add(traka, BorderLayout.NORTH);
		centar.add(Tema.skrolTabele(tabela), BorderLayout.CENTER);

		JPanel p = new JPanel(new BorderLayout());
		p.setBackground(Tema.POZADINA);
		p.add(Tema.zaglavlje(naslov, podnaslov, nazadNaTablu()), BorderLayout.NORTH);
		p.add(Tema.omotac(centar, 20, 28, 16, 28), BorderLayout.CENTER);
		p.add(Tema.podnozje(brojac, dugmad), BorderLayout.SOUTH);
		return p;
	}

	// ═══════════════════════════════════════════════════════════
	// KORISNICI
	// ═══════════════════════════════════════════════════════════

	private JPanel ekranKorisnika() {
		JTable t = new JTable(korisniciModel);
		Tema.stilizujTabelu(t);
		Tema.pretraga(t, pretragaKorisnika);
		Tema.sakrijKolonu(t, 0);
		Tema.sirineKolona(t, 0, 200, 220, 400);
		JLabel brojac = Tema.brojac(t, "korisnika");

		Dugme blokiraj = Dugme.opasno("Blokiraj korisnika");
		blokiraj.addActionListener(e -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(t, "Izaberite korisnika u tabeli.");
				return;
			}
			int id = (int) korisniciModel.getValueAt(red, 0);
			String ime = korisniciModel.getValueAt(red, 1) + " " + korisniciModel.getValueAt(red, 2);
			if (!app.potvrdi(t, "Blokirati korisnika " + ime + "?\n\nBiće obrisani i svi njegovi podaci "
					+ "(skenirani proizvodi, zahtevi i računi).")) return;
			if (app.uBazi(t, "Greška pri blokiranju", () -> app.dao.blokirajKorisnika(id))) {
				app.info(t, "Korisnik " + ime + " je blokiran.");
				osveziKorisnike();
			}
		});

		return ekranSaTabelom("Korisnici", "Pregled i blokiranje korisnika sistema", t,
				Tema.trakaAlata(pretragaKorisnika), brojac, blokiraj);
	}

	private void osveziKorisnike() {
		pretragaKorisnika.setText("");
		ArrayList<Korisnik> lista = app.izBaze(app, "Greška pri učitavanju korisnika", app.dao::vratiListuKorisnika);
		if (lista == null) return;
		korisniciModel.setRowCount(0);
		for (Korisnik k : lista) {
			korisniciModel.addRow(new Object[] { k.getIdKorisnika(), k.getIme(), k.getPrezime(), k.getEmail() });
		}
	}

	// ═══════════════════════════════════════════════════════════
	// NUTRICIONISTI
	// ═══════════════════════════════════════════════════════════

	private JPanel ekranNutricionista() {
		JTable t = new JTable(nutricionistiModel);
		Tema.stilizujTabelu(t);
		Tema.pretraga(t, pretragaNutricionista);
		Tema.sakrijKolonu(t, 0);
		Tema.sirineKolona(t, 0, 170, 190, 170, 320);
		JLabel brojac = Tema.brojac(t, "nutricionista");

		Dugme blokiraj = Dugme.opasno("Blokiraj nutricionistu");
		blokiraj.addActionListener(e -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(t, "Izaberite nutricionistu u tabeli.");
				return;
			}
			int id = (int) nutricionistiModel.getValueAt(red, 0);
			String ime = nutricionistiModel.getValueAt(red, 1) + " " + nutricionistiModel.getValueAt(red, 2);
			if (!app.potvrdi(t, "Blokirati nutricionistu " + ime + "?\n\nBiće obrisani i svi njegovi podaci "
					+ "(komentari, zahtevi i računi).")) return;
			if (app.uBazi(t, "Greška pri blokiranju", () -> app.dao.blokirajNutricionistu(id))) {
				app.info(t, "Nutricionista " + ime + " je blokiran.");
				osveziNutricioniste();
			}
		});

		return ekranSaTabelom("Nutricionisti", "Pregled i blokiranje nutricionista", t,
				Tema.trakaAlata(pretragaNutricionista), brojac, blokiraj);
	}

	private void osveziNutricioniste() {
		pretragaNutricionista.setText("");
		ArrayList<Nutricionista> lista = app.izBaze(app, "Greška pri učitavanju nutricionista",
				app.dao::vratiListuNutricionista);
		if (lista == null) return;
		nutricionistiModel.setRowCount(0);
		for (Nutricionista n : lista) {
			nutricionistiModel.addRow(new Object[] { n.getIdNutricioniste(), n.getIme(), n.getPrezime(), n.getLicenca(),
					n.getEmail() });
		}
	}

	// ═══════════════════════════════════════════════════════════
	// PROIZVODI
	// ═══════════════════════════════════════════════════════════

	private JPanel ekranProizvoda() {
		JTable t = new JTable(proizvodiModel);
		Tema.stilizujTabelu(t);
		Tema.pretraga(t, pretragaProizvoda);
		Tema.sirineKolona(t, 120, 220, 60, 60, 60, 60, 80, 95, 105, 80, 80, 85, 65);
		JLabel brojac = Tema.brojac(t, "proizvoda");

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) izmeniIzabrani(t);
			}
		});

		Dugme dodaj = Dugme.primarno("+ Dodaj proizvod");
		dodaj.addActionListener(e -> formaProizvoda(null));
		Dugme izmeni = Dugme.sekundarno("Izmeni");
		izmeni.addActionListener(e -> izmeniIzabrani(t));
		Dugme obrisi = Dugme.opasno("Obriši");
		obrisi.addActionListener(e -> {
			int red = Tema.izabraniRed(t);
			if (red < 0) {
				app.upozorenje(t, "Izaberite proizvod u tabeli.");
				return;
			}
			String barKod = (String) proizvodiModel.getValueAt(red, 0);
			String naziv = (String) proizvodiModel.getValueAt(red, 1);
			if (!app.potvrdi(t, "Obrisati proizvod \"" + naziv + "\" (bar-kod " + barKod + ")?\n\n"
					+ "Biće obrisani i komentari nutricionista i istorija skeniranja za ovaj proizvod.")) return;
			if (app.uBazi(t, "Greška pri brisanju", () -> app.dao.obrisiProizvod(barKod))) {
				proizvodiModel.removeRow(red);
				app.info(t, "Proizvod \"" + naziv + "\" je obrisan.");
			}
		});

		return ekranSaTabelom("Proizvodi", "Dupli klik na red otvara izmenu proizvoda", t,
				Tema.trakaAlata(pretragaProizvoda), brojac, dodaj, izmeni, obrisi);
	}

	private void osveziProizvode() {
		ArrayList<Proizvod> lista = app.izBaze(app, "Greška pri učitavanju proizvoda", app.dao::vratiListuProizvoda);
		if (lista == null) return;
		proizvodiModel.setRowCount(0);
		for (Proizvod p : lista) {
			proizvodiModel.addRow(new Object[] { p.getBarKod(), p.getNaziv(), p.isPosan() ? "Da" : "Ne",
					p.isHalal() ? "Da" : "Ne", p.getEnergijaKJ(), p.getEnergijaKcal(), p.getMasti(),
					p.getZasiceneMasti(), p.getUgljeniHidrati(), p.getSeceri(), p.getVlakna(), p.getProteini(),
					p.getSo() });
		}
	}

	private void izmeniIzabrani(JTable t) {
		int red = Tema.izabraniRed(t);
		if (red < 0) {
			app.upozorenje(t, "Izaberite proizvod u tabeli.");
			return;
		}
		String barKod = (String) proizvodiModel.getValueAt(red, 0);
		Proizvod p = app.izBaze(t, "Greška pri učitavanju proizvoda", () -> app.dao.nadjiProizvod(barKod));
		if (p != null) formaProizvoda(p);
	}

	/**
	 * Forma za dodavanje (p == null) ili izmenu proizvoda.
	 * Ranije su postojale dve skoro iste forme od po ~100 linija.
	 */
	private void formaProizvoda(Proizvod p) {
		boolean novi = p == null;
		JDialog d = Tema.dijalog(app, novi ? "Novi proizvod" : "Izmena proizvoda",
				novi ? "Unesite podatke o proizvodu" : p.getNaziv(), 680, 760);

		JTextField barKod = Tema.polje("Najmanje 13 cifara", novi ? "" : p.getBarKod());
		barKod.setEditable(novi);
		JTextField naziv = Tema.polje("Naziv proizvoda", novi ? "" : p.getNaziv());
		JCheckBox posan = new JCheckBox("Posno", !novi && p.isPosan());
		JCheckBox halal = new JCheckBox("Halal", !novi && p.isHalal());
		JTextField kj = Tema.polje("0", novi ? "" : String.valueOf(p.getEnergijaKJ()));
		JTextField kcal = Tema.polje("0", novi ? "" : String.valueOf(p.getEnergijaKcal()));
		JTextField masti = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getMasti()));
		JTextField zasMasti = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getZasiceneMasti()));
		JTextField ugljHid = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getUgljeniHidrati()));
		JTextField seceri = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getSeceri()));
		JTextField vlakna = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getVlakna()));
		JTextField proteini = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getProteini()));
		JTextField so = Tema.polje("0", novi ? "" : Tema.zaUnos(p.getSo()));
		JTextArea doza = Tema.oblast(novi ? "" : p.getPreporucenaDnevnaDoza());
		JTextArea sastojci = Tema.oblast(novi ? "" : p.getSastojci());

		for (JCheckBox cb : new JCheckBox[] { posan, halal }) {
			cb.setOpaque(false);
			cb.setFont(Tema.font(Font.BOLD, 14));
			cb.setForeground(Tema.TEKST);
		}
		JPanel oznake = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		oznake.setOpaque(false);
		oznake.add(posan);
		oznake.add(Box.createHorizontalStrut(24));
		oznake.add(halal);

		JPanel forma = new JPanel();
		forma.setLayout(new BoxLayout(forma, BoxLayout.Y_AXIS));
		forma.setBackground(Tema.POZADINA);
		forma.setBorder(new EmptyBorder(20, 28, 16, 28));
		forma.add(red("Bar-kod" + (novi ? " *" : " (ne može se menjati)"), barKod, "Naziv *", naziv));
		forma.add(Box.createVerticalStrut(12));
		forma.add(levo(oznake));
		forma.add(Box.createVerticalStrut(16));
		forma.add(levo(Tema.naslov("Nutritivne vrednosti na 100 g/ml", 15)));
		forma.add(Box.createVerticalStrut(10));
		forma.add(red("Energija (kJ)", kj, "Energija (kcal)", kcal));
		forma.add(Box.createVerticalStrut(10));
		forma.add(red("Masti (g)", masti, "Zasićene masti (g)", zasMasti));
		forma.add(Box.createVerticalStrut(10));
		forma.add(red("Ugljeni hidrati (g)", ugljHid, "Šećeri (g)", seceri));
		forma.add(Box.createVerticalStrut(10));
		forma.add(red("Vlakna (g)", vlakna, "Proteini (g)", proteini));
		forma.add(Box.createVerticalStrut(10));
		forma.add(red("So (g)", so, null, null));
		forma.add(Box.createVerticalStrut(16));
		forma.add(levo(Tema.labelaPolja("Preporučena dnevna doza")));
		forma.add(Box.createVerticalStrut(6));
		forma.add(Tema.skrolOblasti(doza, 70));
		forma.add(Box.createVerticalStrut(12));
		forma.add(levo(Tema.labelaPolja("Sastojci")));
		forma.add(Box.createVerticalStrut(6));
		forma.add(Tema.skrolOblasti(sastojci, 100));

		JScrollPane skrol = new JScrollPane(forma);
		skrol.setBorder(null);
		skrol.getVerticalScrollBar().setUnitIncrement(14);
		d.add(skrol, BorderLayout.CENTER);

		Dugme sacuvaj = Dugme.primarno(novi ? "Dodaj proizvod" : "Sačuvaj izmene");
		Dugme otkazi = Dugme.sekundarno("Otkaži");
		otkazi.addActionListener(e -> d.dispose());
		sacuvaj.addActionListener(e -> {
			String bk = barKod.getText().trim();
			String n = naziv.getText().trim();
			String sas = sastojci.getText().trim();
			String dz = doza.getText().trim();
			String[] brojevi = { kj.getText().trim(), kcal.getText().trim(), masti.getText().trim(),
					zasMasti.getText().trim(), ugljHid.getText().trim(), seceri.getText().trim(),
					vlakna.getText().trim(), proteini.getText().trim(), so.getText().trim() };
			if (!ProizvodKontroler.validirajProizvod(n, bk, sas, posan.isSelected() ? "1" : "0",
					halal.isSelected() ? "1" : "0", brojevi[0], brojevi[1], brojevi[2], brojevi[3], brojevi[4],
					brojevi[5], brojevi[6], brojevi[7], brojevi[8], dz)) {
				app.upozorenje(d, "Sva polja su obavezna, a bar-kod mora imati najmanje 13 cifara.");
				return;
			}
			Proizvod pr;
			try {
				pr = new Proizvod(bk, n, sas, posan.isSelected() ? 1 : 0, halal.isSelected() ? 1 : 0,
						Integer.parseInt(brojevi[0]), Integer.parseInt(brojevi[1]),
						Tema.parsirajBroj(brojevi[2]), Tema.parsirajBroj(brojevi[3]), Tema.parsirajBroj(brojevi[4]),
						Tema.parsirajBroj(brojevi[5]), Tema.parsirajBroj(brojevi[6]), Tema.parsirajBroj(brojevi[7]),
						Tema.parsirajBroj(brojevi[8]), dz);
			} catch (NumberFormatException ex) {
				app.upozorenje(d, "Nutritivne vrednosti moraju biti brojevi.\nEnergija (kJ i kcal) mora biti ceo broj.");
				return;
			}
			boolean uspeh = novi
					? app.uBazi(d, "Greška pri dodavanju proizvoda", () -> app.dao.dodajProizvod(pr))
					: app.uBazi(d, "Greška pri izmeni proizvoda", () -> app.dao.azurirajProizvod(pr));
			if (!uspeh) return;
			d.dispose();
			osveziProizvode();
			app.info(app, "Proizvod \"" + n + "\" je " + (novi ? "dodat." : "izmenjen."));
		});
		d.add(Tema.podnozje(null, otkazi, sacuvaj), BorderLayout.SOUTH);
		d.getRootPane().setDefaultButton(null);
		d.setVisible(true);
	}

	/** Dva polja sa labelama jedno pored drugog (drugo moze biti null). */
	private static JPanel red(String l1, JComponent c1, String l2, JComponent c2) {
		JPanel red = new JPanel(new GridLayout(1, 2, 16, 0));
		red.setOpaque(false);
		red.add(poljeSaLabelom(l1, c1));
		if (l2 != null) {
			red.add(poljeSaLabelom(l2, c2));
		} else {
			JPanel prazno = new JPanel();
			prazno.setOpaque(false);
			red.add(prazno);
		}
		red.setAlignmentX(Component.LEFT_ALIGNMENT);
		red.setMaximumSize(new Dimension(Integer.MAX_VALUE, red.getPreferredSize().height));
		return red;
	}

	private static JPanel poljeSaLabelom(String labela, JComponent polje) {
		JPanel p = new JPanel(new BorderLayout(0, 6));
		p.setOpaque(false);
		p.add(Tema.labelaPolja(labela), BorderLayout.NORTH);
		polje.setPreferredSize(new Dimension(100, 40));
		p.add(polje, BorderLayout.CENTER);
		return p;
	}

	private static JComponent levo(JComponent c) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		return c;
	}

	// ═══════════════════════════════════════════════════════════
	// ZAHTEVI ZA SARADNJU
	// ═══════════════════════════════════════════════════════════

	private JPanel ekranZahteva() {
		JTable t = new JTable(zahteviModel);
		Tema.stilizujTabelu(t);
		Tema.pretraga(t, pretragaZahteva);
		Tema.sirineKolona(t, 50, 150, 150, 110, 300, 300, 130);
		t.getColumnModel().getColumn(6).setCellRenderer(new Tema.StatusCelija());
		JLabel brojac = Tema.brojac(t, "zahteva");

		filterStatusa.addItem("Svi statusi");
		for (String s : Tema.STATUSI_ZAHTEVA) filterStatusa.addItem(Tema.nazivStatusa(s));
		filterStatusa.setFont(Tema.font(Font.PLAIN, 13));
		filterStatusa.setPreferredSize(new Dimension(190, 38));
		filterStatusa.addActionListener(e -> osveziZahteve());

		t.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					int red = Tema.izabraniRed(t);
					if (red >= 0) detaljiZahteva(red);
				}
			}
		});

		JLabel filterLbl = Tema.labelaPolja("Status:");
		Dugme osvezi = Dugme.sekundarno("Osveži");
		osvezi.addActionListener(e -> osveziZahteve());

		return ekranSaTabelom("Zahtevi za saradnju", "Svi zahtevi korisnika prema nutricionistima - dupli klik za detalje",
				t, Tema.trakaAlata(pretragaZahteva, filterLbl, filterStatusa), brojac, osvezi);
	}

	private void osveziZahteve() {
		int izbor = filterStatusa.getSelectedIndex();
		String status = izbor <= 0 ? null : Tema.STATUSI_ZAHTEVA[izbor - 1];
		ArrayList<ZahtevZaSaradnju> lista = app.izBaze(app, "Greška pri učitavanju zahteva",
				() -> status == null ? app.dao.vratiSveZahteve() : app.dao.vratiSveZahtevePoStatusu(status));
		if (lista == null) return;

		// Umesto ID-jeva prikazujemo imena (ranije su u tabeli bili samo brojevi).
		Map<Integer, String> korisnici = new HashMap<>();
		Map<Integer, String> nutricionisti = new HashMap<>();
		ArrayList<Korisnik> k = app.izBaze(app, "Greška pri učitavanju korisnika", app.dao::vratiListuKorisnika);
		ArrayList<Nutricionista> n = app.izBaze(app, "Greška pri učitavanju nutricionista", app.dao::vratiListuNutricionista);
		if (k != null) k.forEach(x -> korisnici.put(x.getIdKorisnika(), x.getIme() + " " + x.getPrezime()));
		if (n != null) n.forEach(x -> nutricionisti.put(x.getIdNutricioniste(), x.getIme() + " " + x.getPrezime()));

		zahteviModel.setRowCount(0);
		for (ZahtevZaSaradnju z : lista) {
			zahteviModel.addRow(new Object[] { z.getIdZahteva(),
					korisnici.getOrDefault(z.getIdKorisnika(), "#" + z.getIdKorisnika()),
					nutricionisti.getOrDefault(z.getIdNutricioniste(), "#" + z.getIdNutricioniste()),
					z.getTip() != null ? z.getTip() : "-",
					z.getOpis() != null ? z.getOpis() : "-",
					z.getOdgovor() != null ? z.getOdgovor() : "-",
					z.getStatus() });
		}
	}

	private void detaljiZahteva(int red) {
		int id = (int) zahteviModel.getValueAt(red, 0);
		String tip = (String) zahteviModel.getValueAt(red, 3);
		String status = (String) zahteviModel.getValueAt(red, 6);
		JDialog d = Tema.dijalog(app, "Zahtev #" + id, tip, 620, 560);

		JPanel info = new JPanel(new GridLayout(0, 2, 12, 10));
		info.setOpaque(false);
		info.add(Tema.labelaPolja("Korisnik"));
		info.add(new JLabel((String) zahteviModel.getValueAt(red, 1)));
		info.add(Tema.labelaPolja("Nutricionista"));
		info.add(new JLabel((String) zahteviModel.getValueAt(red, 2)));
		info.add(Tema.labelaPolja("Tip"));
		info.add(new JLabel(tip));
		info.add(Tema.labelaPolja("Status"));
		Color[] boje = Tema.bojeStatusa(status);
		JPanel cipOmot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		cipOmot.setOpaque(false);
		cipOmot.add(Tema.cip(Tema.nazivStatusa(status), boje[0], boje[1]));
		info.add(cipOmot);
		for (Component c : info.getComponents()) {
			if (c instanceof JLabel l && l.getFont().getStyle() == Font.PLAIN) l.setFont(Tema.font(Font.PLAIN, 14));
		}

		String odgovor = (String) zahteviModel.getValueAt(red, 5);
		JPanel sadrzaj = new JPanel();
		sadrzaj.setLayout(new BoxLayout(sadrzaj, BoxLayout.Y_AXIS));
		sadrzaj.setOpaque(false);
		sadrzaj.add(levo(info));
		sadrzaj.add(Box.createVerticalStrut(18));
		sadrzaj.add(levo(okvirTeksta("Opis zahteva", (String) zahteviModel.getValueAt(red, 4))));
		sadrzaj.add(Box.createVerticalStrut(12));
		sadrzaj.add(levo(okvirTeksta("Odgovor nutricioniste", "-".equals(odgovor) ? "Još nema odgovora." : odgovor)));

		JScrollPane skrol = new JScrollPane(Tema.omotac(sadrzaj, 20, 24, 20, 24));
		skrol.setBorder(null);
		d.add(skrol, BorderLayout.CENTER);
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		d.add(Tema.podnozje(null, zatvori), BorderLayout.SOUTH);
		d.setVisible(true);
	}

	private static JPanel okvirTeksta(String naslov, String tekst) {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 8), 16);
		k.add(Tema.labelaPolja(naslov), BorderLayout.NORTH);
		k.add(Tema.tekstPrikaz(tekst), BorderLayout.CENTER);
		k.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
		return k;
	}

	// ═══════════════════════════════════════════════════════════
	// EVIDENCIJA ZARADE
	// ═══════════════════════════════════════════════════════════

	private void prikaziEvidencijuZarade() {
		ArrayList<RacunDTO> racuni = app.izBaze(app, "Greška pri učitavanju računa", app.dao::vratiSveRacune);
		if (racuni == null) return;
		Double provizija = app.izBaze(app, "Greška pri obračunu provizije", app.dao::obracunajProviziju);

		JDialog d = Tema.dijalog(app, "Evidencija zarade", "Svi računi i obračun provizije (20%)", 1080, 580);
		DefaultTableModel m = Tema.model("ID", "Korisnik", "Nutricionista", "Iznos (RSD)", "Status",
				"Provizija (RSD)", "Datum");
		for (RacunDTO r : racuni) {
			m.addRow(new Object[] { r.getIdRacuna(), r.getImeKorisnika(), r.getImeNutricioniste(), r.getIznos(),
					r.getStatus(), r.getProvizija(), Tema.datum(r.getDatum()) });
		}
		JTable t = new JTable(m);
		Tema.stilizujTabelu(t);
		Tema.sakrijKolonu(t, 0);
		Tema.sirineKolona(t, 0, 200, 200, 120, 130, 130, 160);
		t.getColumnModel().getColumn(4).setCellRenderer(new Tema.StatusCelija());
		t.setAutoCreateRowSorter(true);

		JLabel ukupno = new JLabel(provizija == null ? " "
				: "Ukupna provizija (plaćeni računi):  " + Tema.novac(provizija));
		ukupno.setFont(Tema.font(Font.BOLD, 15));
		ukupno.setForeground(Tema.PRIMARNA_TAMNA);

		d.add(Tema.omotac(Tema.skrolTabele(t), 16, 20, 12, 20), BorderLayout.CENTER);
		Dugme zatvori = Dugme.sekundarno("Zatvori");
		zatvori.addActionListener(e -> d.dispose());
		JLabel broj = Tema.sporedni("Ukupno računa: " + m.getRowCount() + "     ");
		JPanel levo = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		levo.setOpaque(false);
		levo.add(broj);
		levo.add(ukupno);
		d.add(Tema.podnozje(levo, zatvori), BorderLayout.SOUTH);
		d.setVisible(true);
	}
}

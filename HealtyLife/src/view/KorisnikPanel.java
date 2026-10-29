package view;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;

import api.OpenFoodFacts.ApiGreska;
import model.ApiProizvod;
import model.RezultatPretrage;

/**
 * Pretraga proizvoda preko Open Food Facts API-ja.
 *
 * U polje se unosi bar-kod (samo cifre) ili naziv proizvoda. Bar-kod odmah
 * otvara proizvod, a naziv daje listu rezultata iz koje se bira proizvod.
 * Svi pozivi API-ja idu u pozadini (SwingWorker), pa prozor ne "zamrzne"
 * dok se ceka odgovor sa interneta.
 */
class KorisnikPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private static final String PRAZNO = "PRAZNO";
	private static final String UCITAVANJE = "UCITAVANJE";
	private static final String REZULTATI = "REZULTATI";
	private static final String PROIZVOD = "PROIZVOD";
	private static final int MAKS_NEDAVNIH = 8;

	private final GUI app;

	private final JLabel imeKorisnika = Tema.imeUZaglavlju();
	private final Dugme prijavaOdjava = Dugme.naZaglavlju("Odjavi se");
	private final JTextField upitPolje = Tema.polje("Bar-kod (npr. 8600043022551) ili naziv (npr. plazma)");
	private final Dugme trazi = Dugme.primarno("Pretraži");
	private final JCheckBox samoSrbija = new JCheckBox("Pri pretrazi po nazivu prikaži samo proizvode iz Srbije", true);
	private final JPanel nedavnoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
	private final LinkedList<ApiProizvod> nedavno = new LinkedList<>();

	private final CardLayout rezultat = new CardLayout();
	private final JPanel rezultatPanel = new JPanel(rezultat);
	private final JLabel ucitavanjeTekst = Tema.naslov("", 17);
	private JScrollPane skrol;

	// Rezultati pretrage po nazivu
	private final DefaultTableModel rezultatiModel = Tema.model("Bar-kod", "Naziv", "Brend", "Količina", "Nutri-Score");
	private final JTable rezultatiTabela = new JTable(rezultatiModel);
	private final JLabel rezultatiNaslov = Tema.naslov("", 17);
	private boolean imaRezultata;

	// Prikaz proizvoda
	private final JLabel slika = new JLabel("", SwingConstants.CENTER);
	private final JLabel naziv = Tema.naslov("", 26);
	private final JLabel podnaslov = Tema.sporedni("");
	private final JPanel cipovi = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
	private final NutriScore nutriScore = new NutriScore();
	private final JLabel nova = new JLabel();
	private final JPanel nutritivneVrednosti = new JPanel(new GridLayout(0, 1));
	private final JTextArea sastojci = Tema.tekstPrikaz("");
	private final JPanel alergeni = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
	private final Dugme nazadNaRezultate = Dugme.link("← Nazad na rezultate pretrage");
	private int brojUcitavanja; // da se ne prikaze zakasneli odgovor starije pretrage

	KorisnikPanel(GUI app) {
		super(new BorderLayout());
		this.app = app;
		setBackground(Tema.POZADINA);

		prijavaOdjava.addActionListener(e -> {
			if (app.korisnik != null) app.odjava();
			else app.prikaziPrijavu(GUI.PRIJAVA_KORISNIK);
		});
		Dugme pocetna = Dugme.naZaglavlju("Početna");
		pocetna.addActionListener(e -> app.prikaziPocetnu());

		rezultatPanel.setOpaque(false);
		rezultatPanel.add(praznoStanje(), PRAZNO);
		rezultatPanel.add(ucitavanje(), UCITAVANJE);
		rezultatPanel.add(rezultatiPretrage(), REZULTATI);
		rezultatPanel.add(prikazProizvoda(), PROIZVOD);

		Tema.SkrolPanel telo = new Tema.SkrolPanel();
		telo.setBorder(new EmptyBorder(22, 28, 28, 28));
		telo.add(levo(pretraga()));
		telo.add(Box.createVerticalStrut(18));
		telo.add(levo(rezultatPanel));

		add(Tema.zaglavlje("Pretraga proizvoda", "Podaci: Open Food Facts - javna baza proizvoda iz celog sveta",
				imeKorisnika, pocetna, prijavaOdjava), BorderLayout.NORTH);
		add(skrol = Tema.skrol(telo), BorderLayout.CENTER);
	}

	/** Poziva se posle prijave ili kada se nastavi kao gost. */
	void otvori() {
		boolean gost = app.korisnik == null;
		imeKorisnika.setText(gost ? "Gost" : app.korisnik.getIme() + " " + app.korisnik.getPrezime());
		prijavaOdjava.setText(gost ? "Prijavi se" : "Odjavi se");
		SwingUtilities.invokeLater(upitPolje::requestFocusInWindow);
	}

	/** Brise prikaz prethodnog korisnika (pri odjavi). */
	void ocisti() {
		upitPolje.setText("");
		nedavno.clear();
		osveziNedavno();
		imaRezultata = false;
		rezultat.show(rezultatPanel, PRAZNO);
	}

	private static <T extends JComponent> T levo(T c) {
		c.setAlignmentX(Component.LEFT_ALIGNMENT);
		return c;
	}

	// ═══════════════════════════════════════════════════════════
	// PRETRAGA
	// ═══════════════════════════════════════════════════════════

	private JPanel pretraga() {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 10), 22);
		k.add(Tema.labelaPolja("Bar-kod ili naziv proizvoda"), BorderLayout.NORTH);

		upitPolje.setFont(Tema.font(Font.PLAIN, 16));
		upitPolje.setPreferredSize(new Dimension(400, 48));
		upitPolje.addActionListener(e -> pretrazi());

		trazi.setFont(Tema.font(Font.BOLD, 15));
		trazi.setPreferredSize(new Dimension(140, 48));
		trazi.addActionListener(e -> pretrazi());

		JPanel red = new JPanel(new BorderLayout(12, 0));
		red.setOpaque(false);
		red.add(upitPolje, BorderLayout.CENTER);
		red.add(trazi, BorderLayout.EAST);

		samoSrbija.setOpaque(false);
		samoSrbija.setFont(Tema.font(Font.PLAIN, 13));
		samoSrbija.setForeground(Tema.TEKST_LABELE);
		samoSrbija.setFocusPainted(false);

		nedavnoPanel.setOpaque(false);
		osveziNedavno();

		JPanel dole = new JPanel();
		dole.setLayout(new BoxLayout(dole, BoxLayout.Y_AXIS));
		dole.setOpaque(false);
		dole.add(levo(samoSrbija));
		dole.add(Box.createVerticalStrut(6));
		dole.add(levo(nedavnoPanel));

		k.add(red, BorderLayout.CENTER);
		k.add(dole, BorderLayout.SOUTH);
		k.setMaximumSize(new Dimension(Integer.MAX_VALUE, k.getPreferredSize().height + 10));
		return k;
	}

	private void pretrazi() {
		String upit = upitPolje.getText().trim();
		String cifre = upit.replaceAll("[\\s-]", "");
		if (cifre.matches("\\d{8,14}")) {
			otvoriProizvod(cifre);
		} else if (cifre.matches("\\d+")) {
			app.upozorenje(this, "Bar-kod mora imati od 8 do 14 cifara (najčešće 13).");
		} else if (upit.length() < 2) {
			app.upozorenje(this, "Unesite bar-kod ili bar dva slova naziva proizvoda.");
		} else {
			pretraziPoNazivu(upit);
		}
	}

	private void pretraziPoNazivu(String upit) {
		boolean srbija = samoSrbija.isSelected();
		prikaziUcitavanje("Tražim \"" + upit + "\"...");
		uPozadini(() -> app.api.pretrazi(upit, srbija), lista -> {
			rezultatiModel.setRowCount(0);
			for (RezultatPretrage r : lista) {
				rezultatiModel.addRow(new Object[] { r.barKod(), r.naziv(), r.brend() != null ? r.brend() : "-",
						r.kolicina() != null ? r.kolicina() : "-", r.nutriScore() });
			}
			imaRezultata = !lista.isEmpty();
			if (lista.isEmpty()) {
				rezultat.show(rezultatPanel, PRAZNO);
				app.info(this, "Nema rezultata za \"" + upit + "\"."
						+ (srbija ? "\nProbajte bez filtera \"samo proizvodi iz Srbije\"." : ""));
				return;
			}
			rezultatiNaslov.setText("Rezultati za \"" + upit + "\" (" + lista.size() + ")");
			rezultat.show(rezultatPanel, REZULTATI);
			naVrh();
		});
	}

	private void otvoriProizvod(String barKod) {
		prikaziUcitavanje("Učitavam proizvod " + barKod + "...");
		uPozadini(() -> app.api.nadjiProizvod(barKod), p -> {
			if (p == null) {
				rezultat.show(rezultatPanel, imaRezultata ? REZULTATI : PRAZNO);
				app.info(this, "Proizvod sa bar-kodom " + barKod + " nije pronađen u Open Food Facts.\n\n"
						+ "Open Food Facts popunjavaju korisnici, pa neki (naročito domaći) proizvodi još nisu uneti.");
				return;
			}
			prikaziProizvod(p);
		});
	}

	private void prikaziUcitavanje(String tekst) {
		ucitavanjeTekst.setText(tekst);
		rezultat.show(rezultatPanel, UCITAVANJE);
	}

	/**
	 * Izvrsava poziv API-ja u pozadini, pa rezultat predaje ekranu.
	 * Ako u medjuvremenu krene nova pretraga, rezultat stare se ignorise.
	 */
	private <T> void uPozadini(Callable<T> posao, Consumer<T> uspeh) {
		int ovo = ++brojUcitavanja;
		trazi.setEnabled(false);
		new SwingWorker<T, Void>() {
			@Override
			protected T doInBackground() throws Exception {
				return posao.call();
			}

			@Override
			protected void done() {
				if (ovo != brojUcitavanja) return;
				trazi.setEnabled(true);
				try {
					uspeh.accept(get());
				} catch (ExecutionException e) {
					rezultat.show(rezultatPanel, imaRezultata ? REZULTATI : PRAZNO);
					Throwable uzrok = e.getCause();
					app.greska(KorisnikPanel.this, uzrok instanceof ApiGreska ? uzrok.getMessage()
							: "Neočekivana greška: " + uzrok);
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		}.execute();
	}

	private void naVrh() {
		SwingUtilities.invokeLater(() -> skrol.getVerticalScrollBar().setValue(0));
	}

	// ── Nedavno pregledani proizvodi (samo dok je aplikacija otvorena) ──

	private void zapamti(ApiProizvod p) {
		nedavno.removeIf(x -> x.barKod().equals(p.barKod()));
		nedavno.addFirst(p);
		while (nedavno.size() > MAKS_NEDAVNIH) nedavno.removeLast();
		osveziNedavno();
	}

	private void osveziNedavno() {
		nedavnoPanel.removeAll();
		if (!nedavno.isEmpty()) {
			JLabel l = Tema.sporedni("Nedavno:");
			nedavnoPanel.add(l);
			for (ApiProizvod p : nedavno) {
				String n = p.naziv().length() > 24 ? p.naziv().substring(0, 22) + "…" : p.naziv();
				Dugme d = Dugme.link(n);
				d.setFont(Tema.font(Font.PLAIN, 13));
				d.setToolTipText(p.naziv() + " (" + p.barKod() + ")");
				d.addActionListener(e -> {
					upitPolje.setText(p.barKod());
					prikaziProizvod(p); // vec ucitan - ne trazimo ponovo sa interneta
				});
				nedavnoPanel.add(d);
			}
		}
		nedavnoPanel.revalidate();
		nedavnoPanel.repaint();
	}

	// ═══════════════════════════════════════════════════════════
	// STANJA: prazno, ucitavanje, rezultati
	// ═══════════════════════════════════════════════════════════

	private JPanel praznoStanje() {
		Tema.Kartica k = Tema.kartica(new BorderLayout(), 30);
		k.add(Tema.prazno("Pretražite proizvod",
				"Unesite bar-kod sa pakovanja ili deo naziva (npr. \"plazma\", \"jogurt\", \"nutella\") i pritisnite Enter."),
				BorderLayout.CENTER);
		return k;
	}

	private JPanel ucitavanje() {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 8), 40);
		ucitavanjeTekst.setHorizontalAlignment(SwingConstants.CENTER);
		ucitavanjeTekst.setForeground(Tema.TEKST_LABELE);
		JLabel opis = Tema.sporedni("Podaci stižu sa interneta (Open Food Facts), ovo može da potraje nekoliko sekundi.");
		opis.setHorizontalAlignment(SwingConstants.CENTER);
		k.add(ucitavanjeTekst, BorderLayout.CENTER);
		k.add(opis, BorderLayout.SOUTH);
		return k;
	}

	private JPanel rezultatiPretrage() {
		Tema.stilizujTabelu(rezultatiTabela);
		Tema.sirineKolona(rezultatiTabela, 130, 330, 170, 100, 100);
		rezultatiTabela.getColumnModel().getColumn(4).setCellRenderer(new NutriScoreCelija());
		Runnable otvori = () -> {
			int red = Tema.izabraniRed(rezultatiTabela);
			if (red >= 0) otvoriProizvod((String) rezultatiModel.getValueAt(red, 0));
		};
		rezultatiTabela.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) otvori.run();
			}
		});
		rezultatiTabela.getInputMap(JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT)
				.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "otvori");
		rezultatiTabela.getActionMap().put("otvori", new javax.swing.AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(java.awt.event.ActionEvent e) {
				otvori.run();
			}
		});

		JScrollPane sp = Tema.skrolTabele(rezultatiTabela);
		sp.setPreferredSize(new Dimension(800, 420));

		Dugme prikazi = Dugme.primarno("Prikaži proizvod");
		prikazi.addActionListener(e -> {
			if (Tema.izabraniRed(rezultatiTabela) < 0) app.upozorenje(this, "Izaberite proizvod u tabeli.");
			else otvori.run();
		});

		JPanel gore = new JPanel(new BorderLayout());
		gore.setOpaque(false);
		gore.add(rezultatiNaslov, BorderLayout.WEST);
		gore.add(prikazi, BorderLayout.EAST);

		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 12), 22);
		k.add(gore, BorderLayout.NORTH);
		k.add(sp, BorderLayout.CENTER);
		k.add(Tema.sporedni("Dupli klik na red (ili Enter) otvara detalje proizvoda."), BorderLayout.SOUTH);
		return k;
	}

	// ═══════════════════════════════════════════════════════════
	// PRIKAZ PROIZVODA
	// ═══════════════════════════════════════════════════════════

	private JPanel prikazProizvoda() {
		JPanel p = new JPanel();
		p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
		p.setOpaque(false);

		nazadNaRezultate.addActionListener(e -> {
			rezultat.show(rezultatPanel, REZULTATI);
			naVrh();
		});
		JPanel nazadRed = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		nazadRed.setOpaque(false);
		nazadRed.add(nazadNaRezultate);

		// ── Naslovna kartica: slika | naziv i oznake | Nutri-Score ──
		Tema.Kartica glava = Tema.kartica(new BorderLayout(22, 0), 22);
		slika.setPreferredSize(new Dimension(150, 150));
		slika.setFont(Tema.font(Font.PLAIN, 12));
		slika.setForeground(Tema.TEKST_SPOREDNI);
		slika.setOpaque(true);
		slika.setBackground(Tema.POZADINA);
		slika.setBorder(BorderFactory.createLineBorder(Tema.IVICA));

		JPanel sredina = new JPanel();
		sredina.setLayout(new BoxLayout(sredina, BoxLayout.Y_AXIS));
		sredina.setOpaque(false);
		cipovi.setOpaque(false);
		sredina.add(Box.createVerticalStrut(6));
		sredina.add(levo(naziv));
		sredina.add(Box.createVerticalStrut(4));
		sredina.add(levo(podnaslov));
		sredina.add(Box.createVerticalStrut(14));
		sredina.add(levo(cipovi));

		nova.setFont(Tema.font(Font.PLAIN, 12));
		nova.setAlignmentX(Component.CENTER_ALIGNMENT);
		nutriScore.setAlignmentX(Component.CENTER_ALIGNMENT);
		JLabel nsLbl = Tema.sporedni("Nutri-Score");
		nsLbl.setAlignmentX(Component.CENTER_ALIGNMENT);
		JPanel desno = new JPanel();
		desno.setLayout(new BoxLayout(desno, BoxLayout.Y_AXIS));
		desno.setOpaque(false);
		desno.add(nsLbl);
		desno.add(Box.createVerticalStrut(6));
		desno.add(nutriScore);
		desno.add(Box.createVerticalStrut(10));
		desno.add(nova);

		glava.add(slika, BorderLayout.WEST);
		glava.add(sredina, BorderLayout.CENTER);
		glava.add(desno, BorderLayout.EAST);

		// ── Nutritivne vrednosti | sastojci i alergeni ──
		Tema.Kartica nutri = Tema.kartica(new BorderLayout(0, 12), 22);
		nutri.add(Tema.naslov("Nutritivne vrednosti (na 100 g/ml)", 17), BorderLayout.NORTH);
		nutritivneVrednosti.setOpaque(false);
		nutri.add(nutritivneVrednosti, BorderLayout.CENTER);

		alergeni.setOpaque(false);
		JPanel sastavTelo = new JPanel();
		sastavTelo.setLayout(new BoxLayout(sastavTelo, BoxLayout.Y_AXIS));
		sastavTelo.setOpaque(false);
		sastavTelo.add(levo(sastojci));
		sastavTelo.add(Box.createVerticalStrut(16));
		sastavTelo.add(levo(Tema.naslov("Alergeni", 15)));
		sastavTelo.add(Box.createVerticalStrut(6));
		sastavTelo.add(levo(alergeni));

		Tema.Kartica sastav = Tema.kartica(new BorderLayout(0, 10), 22);
		sastav.add(Tema.naslov("Sastojci", 17), BorderLayout.NORTH);
		sastav.add(sastavTelo, BorderLayout.CENTER);

		JPanel red = new JPanel(new GridLayout(1, 2, 18, 0));
		red.setOpaque(false);
		red.add(nutri);
		red.add(sastav);

		JLabel izvor = Tema.sporedni("<html>Izvor: <b>Open Food Facts</b> (openfoodfacts.org). Podatke unose korisnici "
				+ "širom sveta, pa mogu biti nepotpuni - uvek proverite deklaraciju na pakovanju.</html>");

		p.add(levo(nazadRed));
		p.add(Box.createVerticalStrut(8));
		p.add(levo(glava));
		p.add(Box.createVerticalStrut(18));
		p.add(levo(red));
		p.add(Box.createVerticalStrut(14));
		p.add(levo(izvor));
		return p;
	}

	private void prikaziProizvod(ApiProizvod p) {
		zapamti(p);
		nazadNaRezultate.setVisible(imaRezultata);

		naziv.setText(p.naziv());
		List<String> pod = new ArrayList<>();
		if (p.brend() != null) pod.add(p.brend());
		if (p.kolicina() != null) pod.add(p.kolicina());
		pod.add("Bar-kod: " + p.barKod());
		podnaslov.setText(String.join("   •   ", pod));

		// Oznake: posno ima tri stanja (da / ne / nepoznato), halal samo ako postoji oznaka
		cipovi.removeAll();
		if (Boolean.TRUE.equals(p.posno())) cipovi.add(Tema.cip("Posno (vegansko)", Tema.CIP_DA_POZADINA, Tema.CIP_DA_TEKST));
		else if (Boolean.FALSE.equals(p.posno())) cipovi.add(Tema.cip("Nije posno", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST));
		else cipovi.add(Tema.cip("Posno: nepoznato", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST));
		cipovi.add(Boolean.TRUE.equals(p.halal())
				? Tema.cip("Halal", Tema.CIP_DA_POZADINA, Tema.CIP_DA_TEKST)
				: Tema.cip("Halal: nema oznake", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST));
		if (!p.alergeni().isEmpty()) {
			cipovi.add(Tema.cip("Sadrži alergene: " + p.alergeni().size(), ALERGEN_POZADINA, ALERGEN_TEKST));
		}

		nutriScore.postavi(p.nutriScore());
		postaviNova(p.nova());

		nutritivneVrednosti.removeAll();
		String energija = p.energijaKj() == null && p.energijaKcal() == null ? null
				: (p.energijaKj() != null ? Math.round(p.energijaKj()) + " kJ" : "?") + " / "
						+ (p.energijaKcal() != null ? Math.round(p.energijaKcal()) + " kcal" : "?");
		redVrednosti("Energija", energija, false);
		redVrednosti("Masti", gram(p.masti()), false);
		redVrednosti("    od toga zasićene masti", gram(p.zasiceneMasti()), true);
		redVrednosti("Ugljeni hidrati", gram(p.ugljeniHidrati()), false);
		redVrednosti("    od toga šećeri", gram(p.seceri()), true);
		redVrednosti("Vlakna", gram(p.vlakna()), false);
		redVrednosti("Proteini", gram(p.proteini()), false);
		redVrednosti("So", gram(p.so()), false);

		sastojci.setText(p.sastojci() != null ? p.sastojci() : "Sastojci nisu uneti u Open Food Facts za ovaj proizvod.");

		alergeni.removeAll();
		if (p.alergeni().isEmpty()) {
			alergeni.add(Tema.sporedni("Nema unetih alergena (to ne znači da ih proizvod sigurno nema)."));
		} else {
			for (String a : p.alergeni()) alergeni.add(Tema.cip(a, ALERGEN_POZADINA, ALERGEN_TEKST));
		}

		ucitajSliku(p);
		rezultat.show(rezultatPanel, PROIZVOD);
		revalidate();
		repaint();
		naVrh();
	}

	private static final Color ALERGEN_POZADINA = new Color(255, 237, 213);
	private static final Color ALERGEN_TEKST = new Color(194, 65, 12);

	private static String gram(Double v) {
		return v == null ? null : Tema.broj(Math.round(v * 100) / 100.0) + " g";
	}

	/** Jedan red tabele nutritivnih vrednosti; null vrednost = "nepoznato". */
	private void redVrednosti(String naziv, String vrednost, boolean podstavka) {
		JPanel red = new JPanel(new BorderLayout());
		red.setOpaque(true);
		red.setBackground(nutritivneVrednosti.getComponentCount() % 2 == 0 ? Tema.POZADINA : Tema.POVRSINA);
		red.setBorder(new EmptyBorder(9, 12, 9, 12));
		JLabel n = new JLabel(naziv);
		n.setFont(Tema.font(Font.PLAIN, 14));
		n.setForeground(podstavka ? Tema.TEKST_SPOREDNI : Tema.TEKST_LABELE);
		JLabel v = new JLabel(vrednost != null ? vrednost : "nepoznato");
		v.setFont(vrednost != null ? Tema.font(podstavka ? Font.PLAIN : Font.BOLD, 14) : Tema.font(Font.ITALIC, 13));
		v.setForeground(vrednost != null ? Tema.TEKST : Tema.TEKST_SPOREDNI);
		red.add(n, BorderLayout.WEST);
		red.add(v, BorderLayout.EAST);
		nutritivneVrednosti.add(red);
	}

	/** NOVA: stepen industrijske obrade hrane, 1 (neobradjeno) do 4 (ultra-obradjeno). */
	private void postaviNova(Integer n) {
		if (n == null || n < 1 || n > 4) {
			nova.setText("NOVA: nepoznato");
			nova.setForeground(Tema.TEKST_SPOREDNI);
			nova.setToolTipText(null);
			return;
		}
		String[] opisi = { "neobrađena hrana", "kulinarski sastojci", "obrađena hrana", "ultra-obrađena hrana" };
		Color[] boje = { new Color(0, 128, 0), new Color(161, 125, 0), new Color(217, 119, 6), new Color(220, 38, 38) };
		nova.setText("<html><div style='text-align:center'><b>NOVA " + n + "</b><br>" + opisi[n - 1] + "</div></html>");
		nova.setForeground(boje[n - 1]);
		nova.setToolTipText("NOVA - stepen industrijske obrade hrane (1 = najmanje, 4 = najviše)");
	}

	/** Slika se ucitava posebno, posle prikaza podataka, da korisnik ne ceka na nju. */
	private void ucitajSliku(ApiProizvod p) {
		slika.setIcon(null);
		slika.setText(p.slikaUrl() == null ? "Nema slike" : "Učitavam sliku...");
		if (p.slikaUrl() == null) return;
		new SwingWorker<Image, Void>() {
			@Override
			protected Image doInBackground() {
				BufferedImage img = app.api.slika(p.slikaUrl());
				return img == null ? null : umanji(img, 146, 146);
			}

			@Override
			protected void done() {
				if (!p.naziv().equals(naziv.getText())) return; // u medjuvremenu je otvoren drugi proizvod
				try {
					Image img = get();
					slika.setText(img == null ? "Nema slike" : "");
					slika.setIcon(img == null ? null : new ImageIcon(img));
				} catch (InterruptedException | ExecutionException e) {
					slika.setText("Nema slike");
				}
			}
		}.execute();
	}

	private static Image umanji(BufferedImage img, int maksW, int maksH) {
		double f = Math.min((double) maksW / img.getWidth(), (double) maksH / img.getHeight());
		int w = Math.max(1, (int) (img.getWidth() * f)), h = Math.max(1, (int) (img.getHeight() * f));
		BufferedImage r = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = r.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.drawImage(img, 0, 0, w, h, null);
		g.dispose();
		return r;
	}

	// ═══════════════════════════════════════════════════════════
	// NUTRI-SCORE (A-E u boji)
	// ═══════════════════════════════════════════════════════════

	private static final Color[] NS_BOJE = { new Color(3, 129, 65), new Color(133, 187, 47), new Color(254, 203, 2),
			new Color(238, 129, 0), new Color(230, 62, 17) };

	/** Velika obojena oznaka: aktivno slovo je istaknuto, ostala su bleda. */
	private static final class NutriScore extends JComponent {
		private static final long serialVersionUID = 1L;
		private int aktivno = -1;

		NutriScore() {
			setPreferredSize(new Dimension(170, 46));
			setMaximumSize(getPreferredSize());
		}

		void postavi(String ocena) {
			aktivno = ocena == null ? -1 : ocena.charAt(0) - 'a';
			setToolTipText(ocena == null ? "Nutri-Score nije izračunat za ovaj proizvod"
					: "Nutri-Score " + ocena.toUpperCase() + " (A = najzdravije, E = najmanje zdravo)");
			repaint();
		}

		@Override
		protected void paintComponent(Graphics g) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			if (aktivno < 0) {
				g2.setColor(Tema.POZADINA_HOVER);
				g2.fillRoundRect(0, 6, getWidth(), getHeight() - 12, 12, 12);
				g2.setColor(Tema.TEKST_SPOREDNI);
				g2.setFont(Tema.font(Font.PLAIN, 13));
				String t = "nije izračunat";
				g2.drawString(t, (getWidth() - g2.getFontMetrics().stringWidth(t)) / 2, getHeight() / 2 + 5);
				g2.dispose();
				return;
			}
			int w = 28, x = 6;
			for (int i = 0; i < 5; i++) {
				boolean akt = i == aktivno;
				int ww = akt ? 40 : w, h = akt ? getHeight() : getHeight() - 14, y = akt ? 0 : 7;
				g2.setColor(akt ? NS_BOJE[i] : Tema.mesaj(NS_BOJE[i], Color.WHITE, 0.55));
				g2.fillRoundRect(x, y, ww, h, 10, 10);
				g2.setColor(Color.WHITE);
				g2.setFont(Tema.font(Font.BOLD, akt ? 22 : 13));
				String s = String.valueOf((char) ('A' + i));
				g2.drawString(s, x + (ww - g2.getFontMetrics().stringWidth(s)) / 2,
						y + (h + g2.getFontMetrics().getAscent()) / 2 - 3);
				x += ww + 2;
			}
			g2.dispose();
		}
	}

	/** Nutri-Score u tabeli rezultata kao mala obojena oznaka. */
	private static final class NutriScoreCelija implements TableCellRenderer {
		private final JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 8));
		private final JLabel cip = Tema.cip("", Color.WHITE, Color.WHITE);

		NutriScoreCelija() {
			cip.setBorder(new EmptyBorder(2, 10, 2, 10));
			panel.add(cip);
		}

		@Override
		public Component getTableCellRendererComponent(JTable t, Object v, boolean sel, boolean foc, int row, int col) {
			String ns = v == null ? null : v.toString();
			if (ns == null) Tema.postaviCip(cip, "-", Tema.CIP_NE_POZADINA, Tema.CIP_NE_TEKST);
			else Tema.postaviCip(cip, ns.toUpperCase(), NS_BOJE[ns.charAt(0) - 'a'], Color.WHITE);
			panel.setBackground(sel ? t.getSelectionBackground() : row % 2 == 0 ? Tema.POVRSINA : Tema.POZADINA);
			return panel;
		}
	}
}

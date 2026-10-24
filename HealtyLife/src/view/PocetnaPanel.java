package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.RenderingHints;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;

/** Pocetna strana: traka sa prijavom, uvodni deo, "kako funkcionise" i prednosti. */
class PocetnaPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private final GUI app;

	PocetnaPanel(GUI app) {
		super(new BorderLayout());
		this.app = app;
		setBackground(Tema.POZADINA);

		JPanel sadrzaj = new JPanel();
		sadrzaj.setLayout(new BoxLayout(sadrzaj, BoxLayout.Y_AXIS));
		sadrzaj.setBackground(Tema.POZADINA);
		sadrzaj.add(uvod());
		sadrzaj.add(kakoFunkcionise());
		sadrzaj.add(prednosti());
		sadrzaj.add(podnozje());

		JScrollPane sp = new JScrollPane(sadrzaj);
		sp.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		sp.getVerticalScrollBar().setUnitIncrement(16);
		sp.setBorder(null);

		add(traka(), BorderLayout.NORTH);
		add(sp, BorderLayout.CENTER);
	}

	private JPanel traka() {
		JPanel traka = new JPanel(new BorderLayout());
		traka.setBackground(Tema.POVRSINA);
		traka.setBorder(BorderFactory.createCompoundBorder(
				new MatteBorder(0, 0, 1, 0, Tema.IVICA), new EmptyBorder(12, 32, 12, 32)));

		JLabel logo = new JLabel("● Healthy Life");
		logo.setFont(Tema.font(Font.BOLD, 22));
		logo.setForeground(Tema.PRIMARNA_TAMNA);

		Dugme admin = Dugme.link("Administrator");
		admin.setForeground(Tema.TEKST_SPOREDNI);
		admin.addActionListener(e -> app.prikaziPrijavu(GUI.PRIJAVA_ADMIN));

		Dugme prijava = Dugme.primarno("Prijavi se");
		prijava.addActionListener(e -> meniPrijave(prijava));

		JPanel desno = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
		desno.setOpaque(false);
		desno.add(admin);
		desno.add(prijava);

		traka.add(logo, BorderLayout.WEST);
		traka.add(desno, BorderLayout.EAST);
		return traka;
	}

	private void meniPrijave(Component ispod) {
		JPopupMenu meni = new JPopupMenu();
		meni.setBorder(BorderFactory.createLineBorder(Tema.IVICA));
		meni.add(stavka("Korisnik", "Pretraga proizvoda i saveti nutricionista", GUI.PRIJAVA_KORISNIK));
		meni.add(stavka("Nutricionista", "Odgovaranje korisnicima i ocene proizvoda", GUI.PRIJAVA_NUTRICIONISTA));
		meni.show(ispod, ispod.getWidth() - meni.getPreferredSize().width, ispod.getHeight() + 4);
	}

	private JMenuItem stavka(String naslov, String opis, String ekran) {
		JMenuItem s = new JMenuItem("<html><b>" + naslov + "</b><br><span style='color:#64748b'>" + opis + "</span></html>");
		s.setFont(Tema.font(Font.PLAIN, 13));
		s.setBorder(new EmptyBorder(8, 12, 8, 16));
		s.addActionListener(e -> app.prikaziPrijavu(ekran));
		return s;
	}

	private JPanel uvod() {
		JPanel uvod = new Tema.GradijentPanel(new GridBagLayout());
		uvod.setPreferredSize(new Dimension(1300, 380));
		uvod.setMaximumSize(new Dimension(Integer.MAX_VALUE, 380));

		JPanel c = new JPanel();
		c.setLayout(new BoxLayout(c, BoxLayout.Y_AXIS));
		c.setOpaque(false);

		JLabel znacka = Tema.cip("Zdrava ishrana, jednostavno", new Color(255, 255, 255, 40), Color.WHITE);
		znacka.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel naslov = new JLabel("<html><div style='text-align:center'>Znaj šta jedeš.<br>Skeniraj bar-kod.</div></html>",
				SwingConstants.CENTER);
		naslov.setFont(Tema.font(Font.BOLD, 46));
		naslov.setForeground(Color.WHITE);
		naslov.setAlignmentX(Component.CENTER_ALIGNMENT);

		JLabel pod = new JLabel("Sastav, nutritivne vrednosti i saveti stručnjaka za svaki proizvod.");
		pod.setFont(Tema.font(Font.PLAIN, 19));
		pod.setForeground(new Color(220, 252, 231));
		pod.setAlignmentX(Component.CENTER_ALIGNMENT);

		Dugme pocni = Dugme.sekundarno("Prijavi se kao korisnik");
		pocni.setFont(Tema.font(Font.BOLD, 15));
		pocni.setForeground(Tema.PRIMARNA_TAMNA);
		pocni.setBorder(new EmptyBorder(12, 26, 12, 26));
		pocni.addActionListener(e -> app.prikaziPrijavu(GUI.PRIJAVA_KORISNIK));

		Dugme registracija = Dugme.naZaglavlju("Napravi nalog");
		registracija.setFont(Tema.font(Font.BOLD, 15));
		registracija.setBorder(new EmptyBorder(12, 26, 12, 26));
		registracija.addActionListener(e -> app.prikaziPrijavu(GUI.REGISTRACIJA_KORISNIK));

		JPanel dugmad = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 0));
		dugmad.setOpaque(false);
		dugmad.add(pocni);
		dugmad.add(registracija);

		c.add(znacka);
		c.add(Box.createVerticalStrut(18));
		c.add(naslov);
		c.add(Box.createVerticalStrut(14));
		c.add(pod);
		c.add(Box.createVerticalStrut(28));
		c.add(dugmad);
		uvod.add(c);
		return uvod;
	}

	private JPanel kakoFunkcionise() {
		JPanel sekcija = sekcija("Kako Healthy Life funkcioniše?", Tema.POVRSINA);
		JPanel kartice = new JPanel(new GridLayout(1, 3, 24, 0));
		kartice.setOpaque(false);
		kartice.add(korak(1, "Unesi bar-kod", "Ukucaj bar-kod sa pakovanja proizvoda i pokreni pretragu."));
		kartice.add(korak(2, "Proveri sastav", "Vidi sastojke, nutritivne vrednosti i da li je proizvod posan ili halal."));
		kartice.add(korak(3, "Donesi odluku", "Pročitaj ocene nutricionista i odluči da li proizvod odgovara tvojoj ishrani."));
		sekcija.add(kartice, BorderLayout.CENTER);
		return sekcija;
	}

	private JPanel prednosti() {
		JPanel sekcija = sekcija("Zašto Healthy Life?", Tema.POZADINA);
		JPanel kartice = new JPanel(new GridLayout(1, 3, 24, 0));
		kartice.setOpaque(false);
		kartice.add(prednost("Zdravija ishrana", "Bolje odluke uz detaljne informacije o svakom proizvodu."));
		kartice.add(prednost("Brzo i jednostavno", "Sve informacije za nekoliko sekundi - samo unesi bar-kod."));
		kartice.add(prednost("Saveti stručnjaka", "Pošalji pitanje nutricionisti ili zatraži personalizovan plan ishrane."));
		sekcija.add(kartice, BorderLayout.CENTER);
		return sekcija;
	}

	private JPanel sekcija(String naslov, Color pozadina) {
		JPanel s = new JPanel(new BorderLayout(0, 32));
		s.setBackground(pozadina);
		s.setBorder(new EmptyBorder(56, 80, 56, 80));
		JLabel n = Tema.naslov(naslov, 30);
		n.setHorizontalAlignment(SwingConstants.CENTER);
		s.add(n, BorderLayout.NORTH);
		return s;
	}

	private JPanel korak(int broj, String naslov, String opis) {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 14), 28);
		JLabel krug = new JLabel(String.valueOf(broj), SwingConstants.CENTER) {
			private static final long serialVersionUID = 1L;

			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g2.setColor(Tema.PRIMARNA_SVETLA);
				g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
				g2.dispose();
				super.paintComponent(g);
			}
		};
		krug.setFont(Tema.font(Font.BOLD, 18));
		krug.setForeground(Tema.PRIMARNA_TAMNA);
		krug.setPreferredSize(new Dimension(44, 44));
		JPanel krugOmot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		krugOmot.setOpaque(false);
		krugOmot.add(krug);

		JPanel tekst = new JPanel(new BorderLayout(0, 8));
		tekst.setOpaque(false);
		tekst.add(Tema.naslov(naslov, 19), BorderLayout.NORTH);
		tekst.add(opis(opis), BorderLayout.CENTER);

		k.add(krugOmot, BorderLayout.NORTH);
		k.add(tekst, BorderLayout.CENTER);
		return k;
	}

	private JPanel prednost(String naslov, String opis) {
		Tema.Kartica k = Tema.kartica(new BorderLayout(0, 8), 28);
		JPanel traka = new JPanel();
		traka.setBackground(Tema.PRIMARNA);
		traka.setPreferredSize(new Dimension(36, 4));
		JPanel trakaOmot = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
		trakaOmot.setOpaque(false);
		trakaOmot.setBorder(new EmptyBorder(0, 0, 8, 0));
		trakaOmot.add(traka);

		JPanel gore = new JPanel(new BorderLayout());
		gore.setOpaque(false);
		gore.add(trakaOmot, BorderLayout.NORTH);
		gore.add(Tema.naslov(naslov, 19), BorderLayout.CENTER);

		k.add(gore, BorderLayout.NORTH);
		k.add(opis(opis), BorderLayout.CENTER);
		return k;
	}

	private JTextArea opis(String tekst) {
		JTextArea d = Tema.tekstPrikaz(tekst);
		d.setForeground(Tema.TEKST_SPOREDNI);
		return d;
	}

	private JPanel podnozje() {
		JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
		p.setBackground(Tema.POVRSINA);
		p.setBorder(new EmptyBorder(18, 0, 18, 0));
		p.add(Tema.sporedni("Healthy Life © 2026"));
		return p;
	}
}

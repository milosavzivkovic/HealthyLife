package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;

import model.Administrator;
import model.KomentarDTO;
import model.Korisnik;
import model.Nutricionista;
import model.Proizvod;
import model.RacunDTO;
import model.ZahtevZaSaradnju;

/**
 * Pristup bazi podataka.
 *
 * Svaka metoda otvara konekciju u try-with-resources bloku, pa se konekcija,
 * upit i rezultat UVEK zatvaraju - i kada dodje do greske. Operacije koje
 * menjaju vise tabela (brisanje, blokiranje, placanje...) izvrsavaju se u
 * transakciji: ili prodju sve izmene, ili nijedna.
 */
public class DAO {

	// ═══════════════════════════════════════════════════════════
	// POMOCNE METODE
	// ═══════════════════════════════════════════════════════════

	/** Pretvara jedan red rezultata u objekat. */
	@FunctionalInterface
	private interface Mapper<T> {
		T mapiraj(ResultSet rs) throws SQLException;
	}

	/** Deo posla koji se izvrsava nad vec otvorenom konekcijom (u transakciji). */
	@FunctionalInterface
	private interface Posao<T> {
		T izvrsi(Connection c) throws SQLException;
	}

	private static void postaviParametre(PreparedStatement ps, Object... parametri) throws SQLException {
		for (int i = 0; i < parametri.length; i++) {
			ps.setObject(i + 1, parametri[i]);
		}
	}

	/** SELECT koji vraca listu objekata. */
	private <T> ArrayList<T> lista(String sql, Mapper<T> mapper, Object... parametri) throws SQLException {
		try (Connection c = Konekcija.otvori()) {
			return lista(c, sql, mapper, parametri);
		}
	}

	private <T> ArrayList<T> lista(Connection c, String sql, Mapper<T> mapper, Object... parametri) throws SQLException {
		ArrayList<T> rezultat = new ArrayList<>();
		try (PreparedStatement ps = c.prepareStatement(sql)) {
			postaviParametre(ps, parametri);
			try (ResultSet rs = ps.executeQuery()) {
				while (rs.next()) {
					rezultat.add(mapper.mapiraj(rs));
				}
			}
		}
		return rezultat;
	}

	/** SELECT koji vraca jedan objekat ili null ako nema rezultata. */
	private <T> T jedan(String sql, Mapper<T> mapper, Object... parametri) throws SQLException {
		ArrayList<T> l = lista(sql, mapper, parametri);
		return l.isEmpty() ? null : l.get(0);
	}

	/** INSERT/UPDATE/DELETE; vraca broj promenjenih redova. */
	private int izmena(String sql, Object... parametri) throws SQLException {
		try (Connection c = Konekcija.otvori()) {
			return izmena(c, sql, parametri);
		}
	}

	private int izmena(Connection c, String sql, Object... parametri) throws SQLException {
		try (PreparedStatement ps = c.prepareStatement(sql)) {
			postaviParametre(ps, parametri);
			return ps.executeUpdate();
		}
	}

	/** INSERT koji vraca automatski generisan ID. */
	private int unosSaId(String sql, Object... parametri) throws SQLException {
		try (Connection c = Konekcija.otvori();
				PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
			postaviParametre(ps, parametri);
			ps.executeUpdate();
			try (ResultSet keys = ps.getGeneratedKeys()) {
				if (!keys.next()) throw new SQLException("Baza nije vratila ID novog reda.");
				return keys.getInt(1);
			}
		}
	}

	/** Izvrsava vise izmena kao jednu celinu: commit ako sve prodje, rollback ako bilo sta pukne. */
	private <T> T transakcija(Posao<T> posao) throws SQLException {
		try (Connection c = Konekcija.otvori()) {
			c.setAutoCommit(false);
			try {
				T rezultat = posao.izvrsi(c);
				c.commit();
				return rezultat;
			} catch (SQLException | RuntimeException e) {
				c.rollback();
				throw e;
			}
		}
	}

	// ── Mapiranje redova u objekte (ranije ponovljeno u svakoj metodi) ──

	private static Proizvod mapirajProizvod(ResultSet rs) throws SQLException {
		return new Proizvod(
				rs.getString("barKod"),
				rs.getString("naziv"),
				rs.getString("sastojci"),
				rs.getInt("posan"),
				rs.getInt("halal"),
				rs.getInt("energijaKj"),
				rs.getInt("energijaKcal"),
				rs.getDouble("masti"),
				rs.getDouble("zasiceneMasti"),
				rs.getDouble("ugljeniHidrati"),
				rs.getDouble("seceri"),
				rs.getDouble("vlakna"),
				rs.getDouble("proteini"),
				rs.getDouble("so"),
				rs.getString("preporucenaDnevnaDoza"));
	}

	private static Nutricionista mapirajNutricionistu(ResultSet rs) throws SQLException {
		return new Nutricionista(
				rs.getInt("idNutricioniste"),
				rs.getString("ime"),
				rs.getString("prezime"),
				rs.getString("licenca"),
				rs.getString("email"),
				rs.getString("lozinka"));
	}

	private static Korisnik mapirajKorisnika(ResultSet rs) throws SQLException {
		return new Korisnik(
				rs.getInt("idKorisnika"),
				rs.getString("ime"),
				rs.getString("prezime"),
				rs.getString("email"),
				rs.getString("lozinka"));
	}

	private static Administrator mapirajAdministratora(ResultSet rs) throws SQLException {
		Administrator a = new Administrator();
		a.setIdAdministratora(rs.getInt("idAdministratora"));
		a.setIme(rs.getString("ime"));
		a.setPrezime(rs.getString("prezime"));
		a.setEmail(rs.getString("email"));
		a.setLozinka(rs.getString("lozinka"));
		return a;
	}

	private static ZahtevZaSaradnju mapirajZahtev(ResultSet rs) throws SQLException {
		ZahtevZaSaradnju z = new ZahtevZaSaradnju();
		z.setIdZahteva(rs.getInt("idZahteva"));
		z.setIdKorisnika(rs.getInt("idKorisnika"));
		z.setIdNutricioniste(rs.getInt("idNutricioniste"));
		z.setTip(rs.getString("tip"));
		z.setOpis(rs.getString("opis"));
		z.setOdgovor(rs.getString("odgovor"));
		z.setStatus(rs.getString("status"));
		return z;
	}

	private static final String KOLONE_PROIZVODA = "barKod, naziv, sastojci, posan, halal, energijaKj, energijaKcal, "
			+ "masti, zasiceneMasti, ugljeniHidrati, seceri, vlakna, proteini, so, preporucenaDnevnaDoza";

	private static final String KOLONE_ZAHTEVA = "idZahteva, idKorisnika, idNutricioniste, tip, opis, odgovor, status";

	// ═══════════════════════════════════════════════════════════
	// PROIZVODI I BAR-KODOVI
	// ═══════════════════════════════════════════════════════════

	public ArrayList<Proizvod> vratiListuProizvoda() throws SQLException {
		return lista("SELECT " + KOLONE_PROIZVODA + " FROM proizvodi ORDER BY naziv", DAO::mapirajProizvod);
	}

	public Proizvod nadjiProizvod(String barKod) throws SQLException {
		return jedan("SELECT " + KOLONE_PROIZVODA + " FROM proizvodi WHERE barKod = ?", DAO::mapirajProizvod, barKod);
	}

	/** Vraca stanje bar-koda ("aktivan"...) ili prazan string ako bar-kod ne postoji. */
	public String proveriBarKod(String barKod) throws SQLException {
		String stanje = jedan("SELECT stanje FROM bar_kodovi WHERE barKod = ?", rs -> rs.getString("stanje"), barKod);
		return stanje != null ? stanje : "";
	}

	public void dodajBarKod(String barKod) throws SQLException {
		izmena("INSERT INTO bar_kodovi (barKod, stanje) VALUES (?, 'aktivan')", barKod);
	}

	/** Dodaje bar-kod i proizvod zajedno - ako unos proizvoda ne uspe, ne ostaje "siroce" bar-kod. */
	public void dodajProizvod(Proizvod p) throws SQLException {
		transakcija(c -> {
			izmena(c, "INSERT INTO bar_kodovi (barKod, stanje) VALUES (?, 'aktivan')", p.getBarKod());
			izmena(c, "INSERT INTO proizvodi (" + KOLONE_PROIZVODA + ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
					p.getBarKod(), p.getNaziv(), p.getSastojci(), p.getPosan(), p.getHalal(),
					p.getEnergijaKJ(), p.getEnergijaKcal(), p.getMasti(), p.getZasiceneMasti(),
					p.getUgljeniHidrati(), p.getSeceri(), p.getVlakna(), p.getProteini(), p.getSo(),
					p.getPreporucenaDnevnaDoza());
			return null;
		});
	}

	public void azurirajProizvod(Proizvod p) throws SQLException {
		izmena("UPDATE proizvodi SET naziv=?, sastojci=?, posan=?, halal=?, energijaKj=?, energijaKcal=?, "
				+ "masti=?, zasiceneMasti=?, ugljeniHidrati=?, seceri=?, vlakna=?, proteini=?, so=?, "
				+ "preporucenaDnevnaDoza=? WHERE barKod=?",
				p.getNaziv(), p.getSastojci(), p.getPosan(), p.getHalal(), p.getEnergijaKJ(), p.getEnergijaKcal(),
				p.getMasti(), p.getZasiceneMasti(), p.getUgljeniHidrati(), p.getSeceri(), p.getVlakna(),
				p.getProteini(), p.getSo(), p.getPreporucenaDnevnaDoza(), p.getBarKod());
	}

	/** Brise proizvod i sve sto je vezano za njega, u jednoj transakciji. */
	public void obrisiProizvod(String barKod) throws SQLException {
		transakcija(c -> {
			izmena(c, "DELETE FROM komentari WHERE barKod = ?", barKod);
			izmena(c, "DELETE FROM skenirani_proizvodi WHERE barKod = ?", barKod);
			izmena(c, "DELETE FROM proizvodi WHERE barKod = ?", barKod);
			izmena(c, "DELETE FROM bar_kodovi WHERE barKod = ?", barKod);
			return null;
		});
	}

	// ═══════════════════════════════════════════════════════════
	// SKENIRANI PROIZVODI
	// ═══════════════════════════════════════════════════════════

	public void unosSkeniranih(int idK, String barKod, LocalDate datum) throws SQLException {
		izmena("INSERT INTO skenirani_proizvodi (idKorisnika, barKod, datum) VALUES (?, ?, ?)",
				idK, barKod, Date.valueOf(datum));
	}

	/** Svaki proizvod koji je korisnik skenirao, jednom (bez obzira koliko puta je skeniran). */
	public ArrayList<Proizvod> vratiListuSkeniranih(int idKorisnika) throws SQLException {
		return lista("SELECT " + KOLONE_PROIZVODA + " FROM proizvodi WHERE barKod IN "
				+ "(SELECT barKod FROM skenirani_proizvodi WHERE idKorisnika = ?) ORDER BY naziv",
				DAO::mapirajProizvod, idKorisnika);
	}

	// ═══════════════════════════════════════════════════════════
	// KOMENTARI / OCENE
	// ═══════════════════════════════════════════════════════════

	public void unesiOcenuProizvoda(int idNutricioniste, String barKod, String komentar, String preporucenaKol, int ocena)
			throws SQLException {
		izmena("INSERT INTO komentari (idNutricioniste, barKod, strucniKomentar, preporucenaKolicina, ocena) "
				+ "VALUES (?, ?, ?, ?, ?)", idNutricioniste, barKod, komentar, preporucenaKol, ocena);
	}

	public ArrayList<KomentarDTO> vratiKomentareZaProizvod(String barKod) throws SQLException {
		// JOIN sa tabelom proizvodi nije potreban - barKod je vec u tabeli komentari.
		return lista("SELECT n.ime, n.prezime, k.ocena, k.strucniKomentar, k.preporucenaKolicina "
				+ "FROM komentari k JOIN nutricionisti n ON n.idNutricioniste = k.idNutricioniste "
				+ "WHERE k.barKod = ?",
				rs -> new KomentarDTO(rs.getString("ime"), rs.getString("prezime"), rs.getInt("ocena"),
						rs.getString("strucniKomentar"), rs.getString("preporucenaKolicina")),
				barKod);
	}

	// ═══════════════════════════════════════════════════════════
	// KORISNICI
	// ═══════════════════════════════════════════════════════════

	public ArrayList<Korisnik> vratiListuKorisnika() throws SQLException {
		return lista("SELECT idKorisnika, ime, prezime, email, lozinka FROM korisnici ORDER BY ime, prezime",
				DAO::mapirajKorisnika);
	}

	/** Vraca korisnika, ili prazan objekat (ID = 0) ako email/lozinka nisu ispravni. */
	public Korisnik nadjiKorisnika(String email, String lozinka) throws SQLException {
		Korisnik k = jedan("SELECT idKorisnika, ime, prezime, email, lozinka FROM korisnici WHERE email = ? AND lozinka = ?",
				DAO::mapirajKorisnika, email, lozinka);
		return k != null ? k : new Korisnik();
	}

	public boolean proveriPostojecegKorisnika(String email) throws SQLException {
		// Ranije se konekcija ovde nije zatvarala kada korisnik postoji.
		return jedan("SELECT 1 FROM korisnici WHERE email = ? LIMIT 1", rs -> true, email) != null;
	}

	public int dodajKorisnika(String ime, String prezime, String email, String lozinka) throws SQLException {
		return unosSaId("INSERT INTO korisnici (ime, prezime, email, lozinka) VALUES (?, ?, ?, ?)",
				ime, prezime, email, lozinka);
	}

	/** Brise korisnika i sve njegove podatke, u jednoj transakciji. */
	public void blokirajKorisnika(int idKorisnika) throws SQLException {
		transakcija(c -> {
			izmena(c, "DELETE FROM skenirani_proizvodi WHERE idKorisnika = ?", idKorisnika);
			izmena(c, "DELETE FROM racuni WHERE idKorisnika = ?", idKorisnika);
			izmena(c, "DELETE FROM zahtevi_za_saradnju WHERE idKorisnika = ?", idKorisnika);
			izmena(c, "DELETE FROM korisnici WHERE idKorisnika = ?", idKorisnika);
			return null;
		});
	}

	// ═══════════════════════════════════════════════════════════
	// NUTRICIONISTI
	// ═══════════════════════════════════════════════════════════

	public ArrayList<Nutricionista> vratiListuNutricionista() throws SQLException {
		return lista("SELECT idNutricioniste, ime, prezime, licenca, email, lozinka FROM nutricionisti ORDER BY ime, prezime",
				DAO::mapirajNutricionistu);
	}

	/** Vraca nutricionistu, ili prazan objekat (ID = 0) ako email/lozinka nisu ispravni. */
	public Nutricionista nadjiNutricionistu(String email, String lozinka) throws SQLException {
		Nutricionista n = jedan("SELECT idNutricioniste, ime, prezime, licenca, email, lozinka FROM nutricionisti "
				+ "WHERE email = ? AND lozinka = ?", DAO::mapirajNutricionistu, email, lozinka);
		return n != null ? n : new Nutricionista();
	}

	public boolean proveriPostojecegNutricionistu(String email) throws SQLException {
		return jedan("SELECT 1 FROM nutricionisti WHERE email = ? LIMIT 1", rs -> true, email) != null;
	}

	public int dodajNutricionistu(String ime, String prezime, String licenca, String email, String lozinka)
			throws SQLException {
		return unosSaId("INSERT INTO nutricionisti (ime, prezime, licenca, email, lozinka) VALUES (?, ?, ?, ?, ?)",
				ime, prezime, licenca, email, lozinka);
	}

	/** Brise nutricionistu i sve njegove podatke, u jednoj transakciji. */
	public void blokirajNutricionistu(int idNutricioniste) throws SQLException {
		transakcija(c -> {
			izmena(c, "DELETE FROM komentari WHERE idNutricioniste = ?", idNutricioniste);
			izmena(c, "DELETE FROM racuni WHERE idNutricioniste = ?", idNutricioniste);
			izmena(c, "DELETE FROM zahtevi_za_saradnju WHERE idNutricioniste = ?", idNutricioniste);
			izmena(c, "DELETE FROM nutricionisti WHERE idNutricioniste = ?", idNutricioniste);
			return null;
		});
	}

	/** Nutricionisti kojima je korisnik slao zahteve (za Inbox korisnika). */
	public ArrayList<Nutricionista> vratiSagovornikeKorisnika(int idKorisnika) throws SQLException {
		return lista("SELECT DISTINCT n.idNutricioniste, n.ime, n.prezime, n.licenca, n.email, n.lozinka "
				+ "FROM zahtevi_za_saradnju z JOIN nutricionisti n ON z.idNutricioniste = n.idNutricioniste "
				+ "WHERE z.idKorisnika = ? ORDER BY n.ime",
				DAO::mapirajNutricionistu, idKorisnika);
	}

	// ═══════════════════════════════════════════════════════════
	// ADMINISTRATORI
	// ═══════════════════════════════════════════════════════════

	/** Vraca administratora, ili prazan objekat (ID = 0) ako email/lozinka nisu ispravni. */
	public Administrator nadjiAdministratora(String email, String lozinka) throws SQLException {
		Administrator a = jedan("SELECT idAdministratora, ime, prezime, email, lozinka FROM administratori "
				+ "WHERE email = ? AND lozinka = ?", DAO::mapirajAdministratora, email, lozinka);
		return a != null ? a : new Administrator();
	}

	// ═══════════════════════════════════════════════════════════
	// ZAHTEVI ZA SARADNJU
	// ═══════════════════════════════════════════════════════════

	public void posaljiZahtevNutricionisti(int idK, int idN, String tip, String opis) throws SQLException {
		izmena("INSERT INTO zahtevi_za_saradnju (idKorisnika, idNutricioniste, tip, opis, status) "
				+ "VALUES (?, ?, ?, ?, 'poslat')", idK, idN, tip, opis);
	}

	public void prihvatiZahtev(int idZahteva) throws SQLException {
		izmena("UPDATE zahtevi_za_saradnju SET status = 'prihvacen' WHERE idZahteva = ?", idZahteva);
	}

	public void odbijZahtev(int idZahteva) throws SQLException {
		izmena("UPDATE zahtevi_za_saradnju SET status = 'neprihvacen' WHERE idZahteva = ?", idZahteva);
	}

	/** Prepiska jednog korisnika i jednog nutricioniste. */
	public ArrayList<ZahtevZaSaradnju> vratiZahteve(int idKorisnika, int idNutricioniste) throws SQLException {
		return lista("SELECT " + KOLONE_ZAHTEVA + " FROM zahtevi_za_saradnju "
				+ "WHERE idKorisnika = ? AND idNutricioniste = ? ORDER BY idZahteva",
				DAO::mapirajZahtev, idKorisnika, idNutricioniste);
	}

	/** Korisnici koji su slali zahteve datom nutricionisti (za Inbox nutricioniste). */
	public ArrayList<Korisnik> vratiZahteve(int idNutricioniste) throws SQLException {
		return lista("SELECT DISTINCT k.idKorisnika, k.ime, k.prezime, k.email, k.lozinka "
				+ "FROM zahtevi_za_saradnju z JOIN korisnici k ON z.idKorisnika = k.idKorisnika "
				+ "WHERE z.idNutricioniste = ? ORDER BY k.ime",
				DAO::mapirajKorisnika, idNutricioniste);
	}

	public ArrayList<ZahtevZaSaradnju> vratiSveZahteve() throws SQLException {
		return lista("SELECT " + KOLONE_ZAHTEVA + " FROM zahtevi_za_saradnju ORDER BY idZahteva DESC", DAO::mapirajZahtev);
	}

	public ArrayList<ZahtevZaSaradnju> vratiSveZahtevePoStatusu(String status) throws SQLException {
		return lista("SELECT " + KOLONE_ZAHTEVA + " FROM zahtevi_za_saradnju WHERE status = ? ORDER BY idZahteva DESC",
				DAO::mapirajZahtev, status);
	}

	/**
	 * Cuva odgovor nutricioniste. Za "Plan ishrane" sa iznosom pravi i racun
	 * (zahtev ceka placanje); konsultacija je odmah zavrsena.
	 * Sve se radi u jednoj transakciji i sa jednom konekcijom (ranije tri).
	 */
	public void dodajOdgovorNaZahtev(int idZahteva, String odgovor, int iznos) throws SQLException {
		transakcija(c -> {
			ZahtevZaSaradnju z = lista(c, "SELECT " + KOLONE_ZAHTEVA + " FROM zahtevi_za_saradnju WHERE idZahteva = ?",
					DAO::mapirajZahtev, idZahteva).stream().findFirst()
					.orElseThrow(() -> new SQLException("Zahtev #" + idZahteva + " ne postoji."));

			boolean naplata = "Plan ishrane".equals(z.getTip()) && iznos > 0;
			String noviStatus = naplata ? "odgovoren" : "dostavljen_odgovor";

			izmena(c, "UPDATE zahtevi_za_saradnju SET odgovor = ?, status = ? WHERE idZahteva = ?",
					odgovor, noviStatus, idZahteva);

			if (naplata) {
				izmena(c, "INSERT INTO racuni (idZahteva, idKorisnika, idNutricioniste, iznos, status, provizija, datum) "
						+ "VALUES (?, ?, ?, ?, 'neplacen', ?, NOW())",
						idZahteva, z.getIdKorisnika(), z.getIdNutricioniste(), iznos, iznos * 0.2);
			}
			return null;
		});
	}

	// ═══════════════════════════════════════════════════════════
	// RACUNI
	// ═══════════════════════════════════════════════════════════

	public ArrayList<RacunDTO> vratiRacuneZaKorisnika(int idKorisnika) throws SQLException {
		return lista("SELECT r.idRacuna, r.idZahteva, r.iznos, r.status, r.datum, n.ime AS nIme, n.prezime AS nPrezime "
				+ "FROM racuni r JOIN nutricionisti n ON r.idNutricioniste = n.idNutricioniste "
				+ "WHERE r.idKorisnika = ? ORDER BY r.datum DESC",
				rs -> {
					RacunDTO dto = new RacunDTO();
					dto.setIdRacuna(rs.getInt("idRacuna"));
					dto.setIdZahteva(rs.getInt("idZahteva"));
					dto.setIznos(rs.getInt("iznos"));
					dto.setStatus(rs.getString("status"));
					dto.setDatum(rs.getString("datum"));
					dto.setImeNutricioniste(rs.getString("nIme") + " " + rs.getString("nPrezime"));
					return dto;
				}, idKorisnika);
	}

	public ArrayList<RacunDTO> vratiSveRacune() throws SQLException {
		return lista("SELECT r.idRacuna, r.idZahteva, r.iznos, r.status, r.provizija, r.datum, "
				+ "k.ime AS kIme, k.prezime AS kPrezime, n.ime AS nIme, n.prezime AS nPrezime "
				+ "FROM racuni r "
				+ "JOIN korisnici k ON r.idKorisnika = k.idKorisnika "
				+ "JOIN nutricionisti n ON r.idNutricioniste = n.idNutricioniste "
				+ "ORDER BY r.datum DESC",
				rs -> {
					RacunDTO dto = new RacunDTO();
					dto.setIdRacuna(rs.getInt("idRacuna"));
					dto.setIdZahteva(rs.getInt("idZahteva"));
					dto.setIznos(rs.getInt("iznos"));
					dto.setStatus(rs.getString("status"));
					dto.setProvizija(rs.getDouble("provizija"));
					dto.setDatum(rs.getString("datum"));
					dto.setImeKorisnika(rs.getString("kIme") + " " + rs.getString("kPrezime"));
					dto.setImeNutricioniste(rs.getString("nIme") + " " + rs.getString("nPrezime"));
					return dto;
				});
	}

	/** Ukupna provizija od placenih racuna. (Ranije se citala kao int, pa su se decimale gubile.) */
	public double obracunajProviziju() throws SQLException {
		Double suma = jedan("SELECT COALESCE(SUM(provizija), 0) AS provizija FROM racuni WHERE status = 'placen'",
				rs -> rs.getDouble("provizija"));
		return suma != null ? suma : 0;
	}

	/** Oznacava racun kao placen i zahtev kao zavrsen, u jednoj transakciji. */
	public void platiRacun(int idRacuna) throws SQLException {
		transakcija(c -> {
			Integer idZahteva = lista(c, "SELECT idZahteva FROM racuni WHERE idRacuna = ?",
					rs -> rs.getInt("idZahteva"), idRacuna).stream().findFirst().orElse(0);

			izmena(c, "UPDATE racuni SET status = 'placen' WHERE idRacuna = ?", idRacuna);
			if (idZahteva > 0) {
				izmena(c, "UPDATE zahtevi_za_saradnju SET status = 'dostavljen_odgovor' WHERE idZahteva = ?", idZahteva);
			}
			return null;
		});
	}
}

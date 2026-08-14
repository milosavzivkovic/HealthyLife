package dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Administrator;
import model.Komentar;
import model.KomentarDTO;
import model.Korisnik;
import model.Nutricionista;
import model.Proizvod;
import model.Racun;
import model.RacunDTO;
import model.ZahtevZaSaradnju;

public class DAO {

	private Connection connect = null;
	private Statement statement = null;
	private PreparedStatement preparedStatement = null;
	private ResultSet resultSet = null;

	private void connect() throws ClassNotFoundException, SQLException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connect = DriverManager.getConnection("jdbc:mysql://localhost/healthylifebaza1", "root", "");
	}
	
	
	
	public ArrayList<Proizvod> vratiListuSkeniranih(int id_korisnika) throws ClassNotFoundException, SQLException {

		ArrayList<Proizvod> lista = new ArrayList<Proizvod>();

		connect();
		preparedStatement = connect.prepareStatement("SELECT * FROM proizvodi WHERE barKod IN ("
				+ "	SELECT barKod from skenirani_proizvodi WHERE idKorisnika=?)");
		preparedStatement.setInt(1,id_korisnika);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();

		while (resultSet.next()) {
			String barKod=resultSet.getString("barKod");
			String naziv=resultSet.getString("naziv");
			String sastojci=resultSet.getString("sastojci");
			int posan=resultSet.getInt("posan");
			int halal=resultSet.getInt("halal");
			int energijKJ=resultSet.getInt("energijaKj");
			int energijaKcal=resultSet.getInt("energijaKcal");
			double masti=resultSet.getDouble("masti");
			double zasiceneMasti=resultSet.getDouble("zasiceneMasti");
			double ugljeniHidrati=resultSet.getDouble("ugljeniHidrati");
			double seceri=resultSet.getDouble("seceri");
			double vlakna=resultSet.getDouble("vlakna");
			double proteini=resultSet.getDouble("proteini");
			double so=resultSet.getDouble("so");
			String preporucenaDnevnaDoza=resultSet.getString("preporucenaDnevnaDoza");
			
			Proizvod p=new Proizvod(barKod, naziv, sastojci, posan, halal, energijKJ, energijaKcal, masti, zasiceneMasti, ugljeniHidrati, seceri, vlakna, proteini, so, preporucenaDnevnaDoza);

			lista.add(p);
		}
		close();
		return lista;
		
    }
	
	public ArrayList<Nutricionista> vratiSagovornikeKorisnika(int idKorisnika)
            throws ClassNotFoundException, SQLException {
        ArrayList<Nutricionista> lista = new ArrayList<>();
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT DISTINCT n.idNutricioniste, n.ime, n.prezime, n.licenca, n.email, n.lozinka " +
            "FROM zahtevi_za_saradnju z " +
            "JOIN nutricionisti n ON z.idNutricioniste = n.idNutricioniste " +
            "WHERE z.idKorisnika = ? " +
            "ORDER BY n.ime");
        preparedStatement.setInt(1, idKorisnika);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            lista.add(new Nutricionista(
                resultSet.getInt("idNutricioniste"),
                resultSet.getString("ime"),
                resultSet.getString("prezime"),
                resultSet.getString("licenca"),
                resultSet.getString("email"),
                resultSet.getString("lozinka")
            ));
        }
        close();
        return lista;
    }
	
	
	public ArrayList<Proizvod> vratiListuProizvoda() throws ClassNotFoundException, SQLException {
		ArrayList<Proizvod> lista = new ArrayList<Proizvod>();

		connect();
		preparedStatement = connect.prepareStatement("select * from proizvodi");
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();

		while (resultSet.next()) {
			String barKod=resultSet.getString("barKod");
			String naziv=resultSet.getString("naziv");
			String sastojci=resultSet.getString("sastojci");
			int posan=resultSet.getInt("posan");
			int halal=resultSet.getInt("halal");
			int energijKJ=resultSet.getInt("energijaKj");
			int energijaKcal=resultSet.getInt("energijaKcal");
			double masti=resultSet.getDouble("masti");
			double zasiceneMasti=resultSet.getDouble("zasiceneMasti");
			double ugljeniHidrati=resultSet.getDouble("ugljeniHidrati");
			double seceri=resultSet.getDouble("seceri");
			double vlakna=resultSet.getDouble("vlakna");
			double proteini=resultSet.getDouble("proteini");
			double so=resultSet.getDouble("so");
			String preporucenaDnevnaDoza=resultSet.getString("preporucenaDnevnaDoza");
			
			Proizvod p=new Proizvod(barKod, naziv, sastojci, posan, halal, energijKJ, energijaKcal, masti, zasiceneMasti, ugljeniHidrati, seceri, vlakna, proteini, so, preporucenaDnevnaDoza);

			lista.add(p);
		}
		close();
		return lista;
    }

    public void obrisiProizvod(String barKod) throws ClassNotFoundException, SQLException {
    	connect();
    	
    	preparedStatement = connect.prepareStatement("DELETE FROM komentari WHERE barKod=?");
		preparedStatement.setString(1,barKod);
		preparedStatement.executeUpdate();
		
		preparedStatement = connect.prepareStatement("DELETE FROM skenirani_proizvodi WHERE barKod=?");
		preparedStatement.setString(1,barKod);
		preparedStatement.executeUpdate();
    	
		preparedStatement = connect.prepareStatement("DELETE FROM proizvodi WHERE barKod=?");
		preparedStatement.setString(1,barKod);
		preparedStatement.executeUpdate();
		
		preparedStatement = connect.prepareStatement("DELETE FROM bar_kodovi WHERE barKod=?");
		preparedStatement.setString(1,barKod);
		preparedStatement.executeUpdate();
		
		close();
    }

    public void azurirajProizvod(Proizvod p) throws ClassNotFoundException, SQLException {
    	
    	connect();
    	
    	String sql = "UPDATE proizvodi SET naziv=?, sastojci=?, posan=?, halal=?, energijaKj=?, " +
    	             "energijaKcal=?, masti=?, zasiceneMasti=?, ugljeniHidrati=?, seceri=?, " +
    	             "vlakna=?, proteini=?, so=?, preporucenaDnevnaDoza=? WHERE barKod=?";

    	PreparedStatement preparedStatement = connect.prepareStatement(sql);

    	preparedStatement.setString(1, p.getNaziv());
    	preparedStatement.setString(2, p.getSastojci());
    	preparedStatement.setInt(3, p.getPosan());
    	preparedStatement.setInt(4, p.getHalal());
    	preparedStatement.setInt(5, p.getEnergijaKJ());
    	preparedStatement.setInt(6, p.getEnergijaKcal());
    	preparedStatement.setDouble(7, p.getMasti());
    	preparedStatement.setDouble(8, p.getZasiceneMasti());
    	preparedStatement.setDouble(9, p.getUgljeniHidrati());
    	preparedStatement.setDouble(10, p.getSeceri());
    	preparedStatement.setDouble(11, p.getVlakna());
    	preparedStatement.setDouble(12, p.getProteini());
    	preparedStatement.setDouble(13, p.getSo());
    	preparedStatement.setString(14, p.getPreporucenaDnevnaDoza());
    	preparedStatement.setString(15, p.getBarKod());

    	preparedStatement.executeUpdate();
		close();
    }

    // vraćanje liste nutricionista
    public ArrayList<Nutricionista> vratiListuNutricionista() throws ClassNotFoundException, SQLException {
    	ArrayList<Nutricionista> lista = new ArrayList<Nutricionista>();

		connect();
		preparedStatement = connect.prepareStatement("select * from nutricionisti");
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();

		while (resultSet.next()) {
			int idNutricioniste=resultSet.getInt("idNutricioniste");
			String ime = resultSet.getString("ime");
			String prezime = resultSet.getString("prezime");
			String licenca = resultSet.getString("licenca");
			String email = resultSet.getString("email");
			String lozinka = resultSet.getString("lozinka");

			Nutricionista n = new Nutricionista(idNutricioniste, ime, prezime, licenca, email, lozinka);

			lista.add(n);
		}
		close();
		return lista;
    }

    public void unosSkeniranih(int idK, String barKod, LocalDate datum) throws ClassNotFoundException, SQLException{
    	connect();
    	preparedStatement = connect.prepareStatement("insert into skenirani_proizvodi (idKorisnika, barKod, datum) values (?, ?, ?)");

		preparedStatement.setInt(1, idK);
		preparedStatement.setString(2,barKod);
		preparedStatement.setDate(3, Date.valueOf(datum));
		preparedStatement.executeUpdate();
		close();
    }
    
    public Proizvod nadjiProizvod(String barKod) throws ClassNotFoundException, SQLException {
        Proizvod p=null;
        
        connect();
		preparedStatement = connect.prepareStatement("select * from proizvodi where barKod=?");
		preparedStatement.setString(1,barKod);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		while (resultSet.next()) {
			String bk=resultSet.getString("barKod");
			String naziv=resultSet.getString("naziv");
			String sastojci=resultSet.getString("sastojci");
			int posan=resultSet.getInt("posan");
			int halal=resultSet.getInt("halal");
			int energijKJ=resultSet.getInt("energijaKj");
			int energijaKcal=resultSet.getInt("energijaKcal");
			double masti=resultSet.getDouble("masti");
			double zasiceneMasti=resultSet.getDouble("zasiceneMasti");
			double ugljeniHidrati=resultSet.getDouble("ugljeniHidrati");
			double seceri=resultSet.getDouble("seceri");
			double vlakna=resultSet.getDouble("vlakna");
			double proteini=resultSet.getDouble("proteini");
			double so=resultSet.getDouble("so");
			String preporucenaDnevnaDoza=resultSet.getString("preporucenaDnevnaDoza");
			
			p=new Proizvod(bk, naziv, sastojci, posan, halal, energijKJ, energijaKcal, masti, zasiceneMasti, ugljeniHidrati, seceri, vlakna, proteini, so, preporucenaDnevnaDoza);
		}
		
		close();
        return p;
    }

    public boolean proveriPostojecegKorisnika(String email) throws ClassNotFoundException, SQLException {

		connect();
		preparedStatement = connect.prepareStatement("SELECT COUNT(*) FROM korisnici WHERE email=?");
		preparedStatement.setString(1, email);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		
		if (resultSet.next()) {
	        return resultSet.getInt(1) > 0;  // true ako korisnik postoji, false ako ne
	    }
	    close();
	    return false;
    }

    //dodavanje korisnika u bazu
    public int dodajKorisnika(String ime, String prezime, String email, String lozinka) throws ClassNotFoundException, SQLException {
    	connect();
		preparedStatement = connect.prepareStatement("insert into korisnici (ime, prezime, email, lozinka) values (?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		preparedStatement.setString(1, ime);
		preparedStatement.setString(2,prezime);
		preparedStatement.setString(3,email);
		preparedStatement.setString(4,lozinka);
		preparedStatement.executeUpdate();
		
		resultSet = preparedStatement.getResultSet();
		ResultSet keys = preparedStatement.getGeneratedKeys();    
		keys.next();  
		int id = keys.getInt(1);
		
		close();
		return id;
    }

    // vraćanje liste korisnika
    public ArrayList<Korisnik> vratiListuKorisnika() throws ClassNotFoundException, SQLException {
    	ArrayList<Korisnik> lista = new ArrayList<Korisnik>();

		connect();
		preparedStatement = connect.prepareStatement("select * from korisnici");
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();

		while (resultSet.next()) {
			int idKorisnika=resultSet.getInt("idKorisnika");
			String ime = resultSet.getString("ime");
			String prezime = resultSet.getString("prezime");
			String email = resultSet.getString("email");
			String lozinka = resultSet.getString("lozinka");

			Korisnik k = new Korisnik(idKorisnika, ime, prezime, email, lozinka);

			lista.add(k);
		}
		close();
		return lista;
    }

    public void blokirajKorisnika(int id_korisnika) throws ClassNotFoundException, SQLException {
    	connect();
    	
    	preparedStatement = connect.prepareStatement("DELETE FROM skenirani_proizvodi WHERE idKorisnika=?");
    	preparedStatement.setInt(1,id_korisnika);
		preparedStatement.executeUpdate();
		
		preparedStatement = connect.prepareStatement("DELETE FROM racuni WHERE idKorisnika=?");
    	preparedStatement.setInt(1,id_korisnika);
		preparedStatement.executeUpdate();
    	
    	preparedStatement = connect.prepareStatement("DELETE FROM zahtevi_za_saradnju WHERE idKorisnika=?");
    	preparedStatement.setInt(1,id_korisnika);
		preparedStatement.executeUpdate();
    	
		preparedStatement = connect.prepareStatement("DELETE FROM korisnici WHERE idKorisnika=?");
		preparedStatement.setInt(1,id_korisnika);
		preparedStatement.executeUpdate();
		close();
    }

    public void blokirajNutricionistu(int id_nutricioniste) throws ClassNotFoundException, SQLException {
    	connect();
    	preparedStatement = connect.prepareStatement("DELETE FROM komentari WHERE idNutricioniste=?");
    	preparedStatement.setInt(1,id_nutricioniste);
		preparedStatement.executeUpdate();
		
		preparedStatement = connect.prepareStatement("DELETE FROM racuni WHERE idNutricioniste=?");
    	preparedStatement.setInt(1,id_nutricioniste);
		preparedStatement.executeUpdate();
		
		preparedStatement = connect.prepareStatement("DELETE FROM zahtevi_za_saradnju WHERE idNutricioniste=?");
    	preparedStatement.setInt(1,id_nutricioniste);
		preparedStatement.executeUpdate();
		
		preparedStatement = connect.prepareStatement("DELETE FROM nutricionisti WHERE idNutricioniste=?");
		preparedStatement.setInt(1,id_nutricioniste);
		preparedStatement.executeUpdate();
		close();
    }


    public ArrayList<RacunDTO> vratiRacuneZaKorisnika(int idKorisnika)
            throws ClassNotFoundException, SQLException {
        ArrayList<RacunDTO> lista = new ArrayList<>();
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT r.idRacuna, r.idZahteva, r.iznos, r.status, r.datum, " +
            "       n.ime AS nIme, n.prezime AS nPrezime " +
            "FROM racuni r " +
            "JOIN nutricionisti n ON r.idNutricioniste = n.idNutricioniste " +
            "WHERE r.idKorisnika = ? ORDER BY r.datum DESC");
        preparedStatement.setInt(1, idKorisnika);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            RacunDTO dto = new RacunDTO();
            dto.setIdRacuna(resultSet.getInt("idRacuna"));
            dto.setIdZahteva(resultSet.getInt("idZahteva"));
            dto.setIznos(resultSet.getInt("iznos"));
            dto.setStatus(resultSet.getString("status"));
            dto.setDatum(resultSet.getString("datum"));
            dto.setImeNutricioniste(resultSet.getString("nIme") + " " + resultSet.getString("nPrezime"));
            lista.add(dto);
        }
        close();
        return lista;
    }
    
    public double obracunajProviziju() throws ClassNotFoundException, SQLException {
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT SUM(provizija) AS 'provizija' FROM racuni WHERE status = 'placen'");
        resultSet = preparedStatement.executeQuery();
        double iznos = 0;
        if (resultSet.next()) 
        	iznos = resultSet.getInt("provizija");
        close();
 
        return iznos;
    }
    
    public ArrayList<RacunDTO> vratiSveRacune()
            throws ClassNotFoundException, SQLException {
        ArrayList<RacunDTO> lista = new ArrayList<>();
        connect();
        statement = connect.createStatement();
        resultSet = statement.executeQuery(
            "SELECT r.idRacuna, r.idZahteva, r.iznos, r.status, r.provizija, r.datum, " +
            "       k.ime AS kIme, k.prezime AS kPrezime, " +
            "       n.ime AS nIme, n.prezime AS nPrezime " +
            "FROM racuni r " +
            "JOIN korisnici k ON r.idKorisnika = k.idKorisnika " +
            "JOIN nutricionisti n ON r.idNutricioniste = n.idNutricioniste " +
            "ORDER BY r.datum DESC");
        while (resultSet.next()) {
            RacunDTO dto = new RacunDTO();
            dto.setIdRacuna(resultSet.getInt("idRacuna"));
            dto.setIdZahteva(resultSet.getInt("idZahteva"));
            dto.setIznos(resultSet.getInt("iznos"));
            dto.setStatus(resultSet.getString("status"));
            dto.setProvizija(resultSet.getDouble("provizija"));
            dto.setDatum(resultSet.getString("datum"));
            dto.setImeKorisnika(resultSet.getString("kIme") + " " + resultSet.getString("kPrezime"));
            dto.setImeNutricioniste(resultSet.getString("nIme") + " " + resultSet.getString("nPrezime"));
            lista.add(dto);
        }
        close();
        return lista;
    }
    
    public void unesiOcenuProizvoda(int id_nutricioniste, String barKod, String komentar, String preporucenaKol, int ocena) throws ClassNotFoundException, SQLException {
    	connect();
		preparedStatement = connect.prepareStatement("insert into komentari (idNutricioniste, barKod, strucniKomentar, preporucenaKolicina, ocena) values (?, ?, ?, ?, ?)");

		preparedStatement.setInt(1, id_nutricioniste);
		preparedStatement.setString(2,barKod);
		preparedStatement.setString(3,komentar);
		preparedStatement.setString(4,preporucenaKol);
		preparedStatement.setInt(5, ocena);
		preparedStatement.executeUpdate();
		
		close();
    }

    public void dodajBarKod(String barKod) throws ClassNotFoundException, SQLException {
    	connect();
    	PreparedStatement preparedStatement = connect.prepareStatement("INSERT INTO bar_kodovi (barKod,stanje) VALUES (?,'aktivan')");
    	preparedStatement.setString(1, barKod);
    	preparedStatement.executeUpdate();
    	close();
    }

    public ArrayList<KomentarDTO> vratiKomentareZaProizvod(String barKod) throws ClassNotFoundException, SQLException{
    	
    	ArrayList<KomentarDTO> lista= new ArrayList<KomentarDTO>();
    	
    	connect();
    	preparedStatement = connect.prepareStatement("SELECT n.ime, n.prezime, k.ocena, k.strucniKomentar, k.preporucenaKolicina"
    			+ " FROM nutricionisti n JOIN komentari k ON n.idNutricioniste=k.idNutricioniste JOIN proizvodi p ON k.barKod=p.barKod WHERE k.barKod=?");
    	preparedStatement.setString(1, barKod);
    	preparedStatement.execute();
    	
    	resultSet = preparedStatement.getResultSet();
    	while (resultSet.next()) {
			String ime=resultSet.getString("ime");
			String prezime = resultSet.getString("prezime");
			int ocena=resultSet.getInt("ocena");
			String strucniKom=resultSet.getString("strucniKomentar");
			String preporucenaKol=resultSet.getString("preporucenaKolicina");
			
			KomentarDTO kom=new KomentarDTO(ime, prezime, ocena, strucniKom, preporucenaKol);

			lista.add(kom);
		}
    	close();
    	return lista;
    }
    
    public String proveriBarKod(String barKod) throws ClassNotFoundException, SQLException {
    	connect();
    	String stanje="";
    	preparedStatement = connect.prepareStatement("SELECT stanje FROM bar_kodovi WHERE barKod=?");
    	preparedStatement.setString(1, barKod);
    	preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		if (resultSet.next()) {
			stanje=resultSet.getString("stanje");
		}
		close();
		return stanje;
		
    }

    public Korisnik nadjiKorisnika(String email, String lozinka) throws ClassNotFoundException, SQLException {
    	Korisnik k=new Korisnik();
    	connect();
		preparedStatement = connect.prepareStatement("SELECT * FROM korisnici WHERE email=? AND lozinka=?");
		preparedStatement.setString(1, email);
		preparedStatement.setString(2, lozinka);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		
		if (resultSet.next()) {
			k.setIdKorisnika(resultSet.getInt("idKorisnika"));
			k.setIme(resultSet.getString("ime"));
			k.setPrezime(resultSet.getString("prezime"));
			k.setEmail(resultSet.getString("email"));
			k.setLozinka(resultSet.getString("lozinka"));
	    }
		
	    close();
    	return k;
    }

    public void dodajProizvod(Proizvod p) throws ClassNotFoundException, SQLException {
    	
    	dodajBarKod(p.getBarKod());
    	connect();
    	String sql = "INSERT INTO proizvodi (barKod, naziv, sastojci, posan, halal, " + 
    	             "energijaKj, energijaKcal, masti, zasiceneMasti, ugljeniHidrati, " + 
    	             "seceri, vlakna, proteini, so, preporucenaDnevnaDoza) " +
    	             "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    	preparedStatement = connect.prepareStatement(sql);

    	preparedStatement.setString(1, p.getBarKod());
    	preparedStatement.setString(2, p.getNaziv());
    	preparedStatement.setString(3, p.getSastojci());
    	preparedStatement.setInt(4, p.getPosan());
    	preparedStatement.setInt(5, p.getHalal());
    	preparedStatement.setInt(6, p.getEnergijaKJ());
    	preparedStatement.setInt(7, p.getEnergijaKcal());
    	preparedStatement.setDouble(8, p.getMasti());
    	preparedStatement.setDouble(9, p.getZasiceneMasti());
    	preparedStatement.setDouble(10, p.getUgljeniHidrati());
    	preparedStatement.setDouble(11, p.getSeceri());
    	preparedStatement.setDouble(12, p.getVlakna());
    	preparedStatement.setDouble(13, p.getProteini());
    	preparedStatement.setDouble(14, p.getSo());
    	preparedStatement.setString(15, p.getPreporucenaDnevnaDoza());

    	preparedStatement.executeUpdate();
		close();
    }

    public boolean proveriPostojecegNutricionistu(String email) throws ClassNotFoundException, SQLException {
    	connect();
		preparedStatement = connect.prepareStatement("SELECT COUNT(*) FROM nutricionisti WHERE email=?");
		preparedStatement.setString(1, email);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		
		if (resultSet.next()) {
	        return resultSet.getInt(1) > 0;  // true ako nutricionista postoji, false ako ne
	    }
	    close();
	    return false;
    }

    //dodaj nutricionistu u bazu
    public int dodajNutricionistu(String ime, String prezime, String licenca, String email, String lozinka) throws ClassNotFoundException, SQLException {
		connect();
		preparedStatement = connect.prepareStatement("insert into nutricionisti (ime, prezime,licenca, email, lozinka) values (?, ?, ?, ?, ?)", Statement.RETURN_GENERATED_KEYS);

		preparedStatement.setString(1,ime);
		preparedStatement.setString(2,prezime);
		preparedStatement.setString(3,licenca);
		preparedStatement.setString(4,email);
		preparedStatement.setString(5,lozinka);
		preparedStatement.executeUpdate();
		
		resultSet = preparedStatement.getResultSet();
		ResultSet keys = preparedStatement.getGeneratedKeys();    
		keys.next();  
		int id = keys.getInt(1);
		
		close();
		return id;
    }

    public void posaljiZahtevNutricionisti(int idK, int idN, String tip, String opis) throws ClassNotFoundException, SQLException {
    	connect();
		preparedStatement = connect.prepareStatement("insert into zahtevi_za_saradnju (idKorisnika, idNutricioniste,tip, opis, status) values (?, ?, ?, ?, 'poslat')");

		preparedStatement.setInt(1,idK);
		preparedStatement.setInt(2,idN);
		preparedStatement.setString(3,tip);
		preparedStatement.setString(4,opis);
		preparedStatement.executeUpdate();
		
		close();
    }
    
    public void prihvatiZahtev(int idZahteva) throws ClassNotFoundException, SQLException {
        connect();
        preparedStatement = connect.prepareStatement(
            "UPDATE zahtevi_za_saradnju SET status = 'prihvacen' WHERE idZahteva = ?");
        preparedStatement.setInt(1, idZahteva);
        preparedStatement.executeUpdate();
        close();
    }
    
    public void odbijZahtev(int idZahteva)
            throws ClassNotFoundException, SQLException {
        connect();
        preparedStatement = connect.prepareStatement(
            "UPDATE zahtevi_za_saradnju SET status = 'neprihvacen' WHERE idZahteva = ?");
        preparedStatement.setInt(1, idZahteva);
        preparedStatement.executeUpdate();
        close();
    }
    
   
    public ArrayList<ZahtevZaSaradnju> vratiZahteve(int idKorisnika, int idNutricioniste)
            throws ClassNotFoundException, SQLException {
        ArrayList<ZahtevZaSaradnju> lista = new ArrayList<>();
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT idZahteva, idKorisnika, idNutricioniste, tip, opis, odgovor, status " +
            "FROM zahtevi_za_saradnju " +
            "WHERE idKorisnika = ? AND idNutricioniste = ? " +
            "ORDER BY idZahteva ASC");
        preparedStatement.setInt(1, idKorisnika);
        preparedStatement.setInt(2, idNutricioniste);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ZahtevZaSaradnju z = new ZahtevZaSaradnju();
            z.setIdZahteva(resultSet.getInt("idZahteva"));
            z.setIdKorisnika(resultSet.getInt("idKorisnika"));
            z.setIdNutricioniste(resultSet.getInt("idNutricioniste"));
            z.setTip(resultSet.getString("tip"));
            z.setOpis(resultSet.getString("opis"));
            z.setOdgovor(resultSet.getString("odgovor"));
            z.setStatus(resultSet.getString("status"));
            lista.add(z);
        }
        close();
        return lista;
    }
    
    public ArrayList<ZahtevZaSaradnju> vratiSveZahteve()
            throws ClassNotFoundException, SQLException {
        ArrayList<ZahtevZaSaradnju> lista = new ArrayList<>();
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT idZahteva, idKorisnika, idNutricioniste, tip, opis, odgovor, status " +
            "FROM zahtevi_za_saradnju ");

        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ZahtevZaSaradnju z = new ZahtevZaSaradnju();
            z.setIdZahteva(resultSet.getInt("idZahteva"));
            z.setIdKorisnika(resultSet.getInt("idKorisnika"));
            z.setIdNutricioniste(resultSet.getInt("idNutricioniste"));
            z.setTip(resultSet.getString("tip"));
            z.setOpis(resultSet.getString("opis"));
            z.setOdgovor(resultSet.getString("odgovor"));
            z.setStatus(resultSet.getString("status"));
            lista.add(z);
        }
        close();
        return lista;
    }
    
    public ArrayList<ZahtevZaSaradnju> vratiSveZahtevePoStatusu(String status) throws ClassNotFoundException, SQLException {
        ArrayList<ZahtevZaSaradnju> lista = new ArrayList<>();
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT idZahteva, idKorisnika, idNutricioniste, tip, opis, odgovor, status " +
            "FROM zahtevi_za_saradnju WHERE status=?");
        preparedStatement.setString(1, status);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            ZahtevZaSaradnju z = new ZahtevZaSaradnju();
            z.setIdZahteva(resultSet.getInt("idZahteva"));
            z.setIdKorisnika(resultSet.getInt("idKorisnika"));
            z.setIdNutricioniste(resultSet.getInt("idNutricioniste"));
            z.setTip(resultSet.getString("tip"));
            z.setOpis(resultSet.getString("opis"));
            z.setOdgovor(resultSet.getString("odgovor"));
            z.setStatus(resultSet.getString("status"));
            lista.add(z);
        }
        close();
        return lista;
    }
    

    
    public ArrayList<Korisnik> vratiZahteve(int idNutricioniste)
            throws ClassNotFoundException, SQLException {
        ArrayList<Korisnik> lista = new ArrayList<>();
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT DISTINCT k.idKorisnika, k.ime, k.prezime, k.email, k.lozinka " +
            "FROM zahtevi_za_saradnju z " +
            "JOIN korisnici k ON z.idKorisnika = k.idKorisnika " +
            "WHERE z.idNutricioniste = ? " +
            "ORDER BY k.ime");
        preparedStatement.setInt(1, idNutricioniste);
        resultSet = preparedStatement.executeQuery();
        while (resultSet.next()) {
            lista.add(new Korisnik(
                resultSet.getInt("idKorisnika"),
                resultSet.getString("ime"),
                resultSet.getString("prezime"),
                resultSet.getString("email"),
                resultSet.getString("lozinka")
            ));
        }
        close();
        return lista;
    }
    public void dodajOdgovorNaZahtev(int idZahteva, String odgovor, int iznos) throws ClassNotFoundException, SQLException {

        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT idKorisnika, idNutricioniste, tip FROM zahtevi_za_saradnju WHERE idZahteva = ?");
        preparedStatement.setInt(1, idZahteva);
        resultSet = preparedStatement.executeQuery();
        int idKorisnika = 0, idNutricioniste = 0;
        String tip = "";
        if (resultSet.next()) {
            idKorisnika     = resultSet.getInt("idKorisnika");
            idNutricioniste = resultSet.getInt("idNutricioniste");
            tip             = resultSet.getString("tip");
        }
        close();
 
        // Odredi novi status
        String noviStatus = "Plan ishrane".equals(tip) && iznos > 0
            ? "odgovoren"          // ceka placanje
            : "dostavljen_odgovor"; // konsultacija — odmah gotovo
 
        // Sacuvaj odgovor i status
        connect();
        preparedStatement = connect.prepareStatement(
            "UPDATE zahtevi_za_saradnju SET odgovor = ?, status = ? WHERE idZahteva = ?");
        preparedStatement.setString(1, odgovor);
        preparedStatement.setString(2, noviStatus);
        preparedStatement.setInt(3, idZahteva);
        preparedStatement.executeUpdate();
        close();
 
        // Kreiraj racun samo za Plan ishrane sa iznosom
        if ("Plan ishrane".equals(tip) && iznos > 0) {
            connect();
            preparedStatement = connect.prepareStatement(
                "INSERT INTO racuni (idZahteva, idKorisnika, idNutricioniste, iznos, status, provizija, datum) " +
                "VALUES (?, ?, ?, ?, 'neplacen', ?, NOW())");
            preparedStatement.setInt(1, idZahteva);
            preparedStatement.setInt(2, idKorisnika);
            preparedStatement.setInt(3, idNutricioniste);
            preparedStatement.setInt(4, iznos);
            preparedStatement.setDouble(5, iznos*0.2);
            preparedStatement.executeUpdate();
            close();
        }
    	
    }
    
    public void platiRacun(int idRacuna)
            throws ClassNotFoundException, SQLException {
        // Uzmi idZahteva iz racuna
        connect();
        preparedStatement = connect.prepareStatement(
            "SELECT idZahteva FROM racuni WHERE idRacuna = ?");
        preparedStatement.setInt(1, idRacuna);
        resultSet = preparedStatement.executeQuery();
        int idZahteva = 0;
        if (resultSet.next()) idZahteva = resultSet.getInt("idZahteva");
        close();
 
        // Plati racun
        connect();
        preparedStatement = connect.prepareStatement(
            "UPDATE racuni SET status = 'placen' WHERE idRacuna = ?");
        preparedStatement.setInt(1, idRacuna);
        preparedStatement.executeUpdate();
        close();
 
        // Zahtev → 'dostavljen_odgovor'
        if (idZahteva > 0) {
            connect();
            preparedStatement = connect.prepareStatement(
                "UPDATE zahtevi_za_saradnju SET status = 'dostavljen_odgovor' WHERE idZahteva = ?");
            preparedStatement.setInt(1, idZahteva);
            preparedStatement.executeUpdate();
            close();
        }
    }

    public Administrator nadjiAdministratora(String email, String lozinka) throws ClassNotFoundException, SQLException {
    	Administrator a=new Administrator();
    	connect();
		preparedStatement = connect.prepareStatement("SELECT * FROM administratori WHERE email=? AND lozinka=?");
		preparedStatement.setString(1, email);
		preparedStatement.setString(2, lozinka);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		
		if (resultSet.next()) {
			a.setIdAdministratora(resultSet.getInt("idAdministratora"));
			a.setIme(resultSet.getString("ime"));
			a.setPrezime(resultSet.getString("prezime"));
			a.setEmail(resultSet.getString("email"));
			a.setLozinka(resultSet.getString("lozinka"));
	    }
		
	    close();
    	return a;
    }

    public Nutricionista nadjiNutricionistu(String email, String lozinka) throws ClassNotFoundException, SQLException {
    	Nutricionista n=new Nutricionista();
    	connect();
		preparedStatement = connect.prepareStatement("SELECT * FROM nutricionisti WHERE email=? AND lozinka=?");
		preparedStatement.setString(1, email);
		preparedStatement.setString(2, lozinka);
		preparedStatement.execute();
		resultSet = preparedStatement.getResultSet();
		
		if (resultSet.next()) {
			n.setIdNutricioniste(resultSet.getInt("idNutricioniste"));
			n.setIme(resultSet.getString("ime"));
			n.setPrezime(resultSet.getString("prezime"));
			n.setLicenca(resultSet.getString("licenca"));
			n.setEmail(resultSet.getString("email"));
			n.setLozinka(resultSet.getString("lozinka"));
	    }
		
	    close();
    	return n;
    }
    
    
    
    private void close() {
		try {
			if (resultSet != null) {
				resultSet.close();
			}

			if (statement != null) {
				statement.close();
			}

			if (connect != null) {
				connect.close();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}

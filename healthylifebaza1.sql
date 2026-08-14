-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Apr 01, 2026 at 09:47 PM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `healthylifebaza1`
--

-- --------------------------------------------------------

--
-- Table structure for table `administratori`
--

CREATE TABLE `administratori` (
  `idAdministratora` int(11) NOT NULL,
  `ime` varchar(60) NOT NULL,
  `prezime` varchar(80) NOT NULL,
  `email` varchar(60) NOT NULL,
  `lozinka` varchar(60) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `administratori`
--

INSERT INTO `administratori` (`idAdministratora`, `ime`, `prezime`, `email`, `lozinka`) VALUES
(1, 'Milosav', 'Živković', 'milosavadmin@gmail.com', 'milosav123'),
(2, 'Ana', 'Luković', 'anaadmin@gmail.com', 'ana123');

-- --------------------------------------------------------

--
-- Table structure for table `bar_kodovi`
--

CREATE TABLE `bar_kodovi` (
  `barKod` varchar(20) NOT NULL,
  `stanje` varchar(30) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `bar_kodovi`
--

INSERT INTO `bar_kodovi` (`barKod`, `stanje`) VALUES
('3830001210022', 'aktivan'),
('3850102128030', 'aktivan'),
('3850102128078', 'aktivan'),
('3850102322359', 'aktivan'),
('3850104263715', 'aktivan'),
('3850105144518', 'aktivan'),
('3850334005059', 'aktivan'),
('3850374044957', 'aktivan'),
('3856020224250', 'aktivan'),
('4001686301524', 'aktivan'),
('4001686301555', 'aktivan'),
('5449000000996', 'aktivan'),
('5449000111678', 'aktivan'),
('7613034626844', 'aktivan'),
('7613034626875', 'aktivan'),
('7622210449557', 'aktivan'),
('7622210449601', 'aktivan'),
('8001505001306', 'aktivan'),
('8600043000533', 'aktivan'),
('8600043001349', 'aktivan'),
('8600043015480', 'aktivan'),
('8600043022242', 'aktivan'),
('8600101614030', 'aktivan'),
('8600101784535', 'aktivan'),
('8600101784542', 'aktivan'),
('8600197005118', 'aktivan'),
('8600939304634', 'aktivan'),
('8601500111554', 'aktivan'),
('8601500111585', 'aktivan'),
('8606006082497', 'aktivan'),
('8606012160538', 'aktivan'),
('8606012162600', 'aktivan'),
('8606014400236', 'aktivan'),
('8606015061322', 'aktivan'),
('8606108139419', 'aktivan');

-- --------------------------------------------------------

--
-- Table structure for table `komentari`
--

CREATE TABLE `komentari` (
  `idNutricioniste` int(11) NOT NULL,
  `barKod` varchar(20) NOT NULL,
  `strucniKomentar` text DEFAULT NULL,
  `preporucenaKolicina` text DEFAULT NULL,
  `ocena` int(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `komentari`
--

INSERT INTO `komentari` (`idNutricioniste`, `barKod`, `strucniKomentar`, `preporucenaKolicina`, `ocena`) VALUES
(7, '5449000000996', 'Visok glikemijski indeks: Coca-Cola sadrži isključivo proste šećere (saharoza ili glukozno-fruktozni sirup). Nutricionista bi naglasio da ovakav napitak izaziva nagli skok insulina, što opterećuje pankreas i dugoročno može dovesti do insulinske rezistencije.', 'Povremena konzumacija po 250ml', 1),
(1, '3830001210022', 'Sadrži prerađeno meso, koristiti umereno.', 'Jedan namaz (30g)', 3),
(2, '3830001210022', 'Visok sadržaj masti za ovu vrstu proizvoda.', 'Do 50g dnevno', 2),
(3, '3850102128078', 'Izuzetno visok sadržaj soli. Koristiti minimalno.', '5g po obroku', 2),
(1, '3850104263715', 'Pečeni kikiriki je dobar izvor proteina, ali ima previše soli.', '30g', 3),
(2, '3850105144518', 'Multivitaminski napitak, ali sa dosta šećera.', 'Jedna čaša dnevno', 4),
(3, '3850105144518', 'Osvežavajuće, ali nije zamena za pravo voće.', '15g praha u 250ml', 3),
(6, '4001686301524', 'Čist šećer i želatin, bez nutritivne vrednosti.', 'Maksimalno 5 gumenih bombona', 1),
(7, '4001686301524', 'Visok glikemijski indeks, oprez kod dijabetičara.', 'Izbegavati često', 1),
(7, '5449000000996', 'Visok glikemijski indeks, sadrži isključivo prazne kalorije.', 'Povremena konzumacija 250ml', 1),
(1, '5449000000996', 'Prevelika količina šećera u maloj zapremini.', 'Izbegavati potpuno', 1),
(3, '7622210449601', 'Visoko prerađen proizvod sa mnogo mlečne masti.', '15g', 2),
(6, '7622210449601', 'Konzumirati samo kao \"cheat meal\".', 'Maksimalno 3 kockice', 2),
(1, '8600043000533', 'Dobar izvor vitamina B grupe, ali sadrži dosta ugljenih hidrata.', '50g uz mleko', 4),
(7, '8600043000533', 'Kvalitetan keks, ali paziti na ukupni dnevni unos šećera.', 'Doručak ili užina', 4),
(2, '8600043001349', 'Sadrže dosta biljnih masti i šećera. Kalorično.', '2-3 napolitanke', 2),
(3, '8600101784535', 'Standardno rafinisano ulje, bolje koristiti maslinovo za salate.', '10ml po obroku', 3),
(6, '8600101784535', 'Visoka kalorijska vrednost (čiste masti).', 'Koristiti za termičku obradu', 3),
(7, '8600197005118', 'Kombinacija šećerne pene i čokolade je veoma kalorična.', '1 komad', 2),
(2, '8600197005118', 'Puno aditiva i veštačkih aroma.', 'Retka konzumacija', 1),
(6, '8601500111585', 'Tradicionalni slatkiš, visok udeo šećera.', 'Povremeno', 3),
(7, '8606012160538', 'Kafa bez kalorija, ali kofein utiče na krvni pritisak.', '2 šoljice dnevno', 4),
(2, '8606012160538', 'Kvalitetna mešavina pržene kafe.', '7g po šoljici', 5),
(3, '8606014400236', 'Dobar izvor kalcijuma i proteina.', '250ml dnevno', 5),
(9, '8606012160538', 'Kafu bih ocenio visokom ocenom, prvenstveno zbog bogatstva antioksidanata i činjenice da je reč o prirodnom napitku bez veštačkih dodataka, ali uz napomenu da njena vrednost direktno zavisi od toga da li je pijete čistu ili je \"kvarite\" šećerom i punomasnim mlekom. Njene prednosti su ubrzavanje metabolizma, poboljšanje kognitivnih funkcija i zaštita jetre, dok su mane potencijalno podizanje nivoa holesterola zbog kofestola (ulja koja se zadržavaju jer kafa nije filtrirana) i mogućnost iritacije želuca kod osetljivih osoba.', 'Preporučena dnevna doza kofeina za zdravu odraslu osobu je do 400 mg, što u prevodu znači 2 do 3 šoljice domaće kafe dnevno', 5),
(10, '8606014400236', 'Odlično za decu i odrasle.', 'Do 0.5l dnevno.', 5),
(21, '8600043022242', 'Hranjiv integralni keks, mnogo bolji od običnih punih šećera.', '100 do 150 grama dnevno', 5),
(21, '3850334005059', 'Dobar izbor za zdrave obroke.', 'do 450g dnevno', 5),
(11, '3850102322359', 'Odličan izbor za podršku imunom sistemu zahvaljujući vitaminu C, B6 i cinku. Niska kalorijska vrednost čini ga pogodnim i za osobe na redukcijskoj dijeti.', '1 boca dnevno (500ml)', 5),
(12, '3850102322359', 'Funkcionalna voda sa dokazanim mikronutrijentima. Preporučujem kao dopunu unosu vitamina, posebno u zimskim mesecima kada je imunitet oslabljen.', '500ml dnevno', 4),
(13, '3850102322359', 'Sadrži minimalnu količinu šećera (3.7g/100ml) i korisne vitamine. Mnogo bolji izbor od zaslađenih sokova i gaziranih pića.', '500ml, idealno između obroka', 4),
(14, '3850374044957', 'Grčki tip jogurta sa visokim udelom mlečne masti (9.7%) i solidnim sadržajem proteina (5.3g/100g). Odličan za sitost i oporavak mišića.', '150g dnevno', 5),
(15, '3850374044957', 'Preporučujem kao zdravu užinu ili doručak uz voće. Visok udeo masti znači i više kalorija, pa treba paziti kod osoba na redukcijskoj dijeti.', '150g uz šaku bobičastog voća', 4),
(16, '3850374044957', 'Prirodan fermentisan mlečni proizvod bogat probioticima koji podržavaju crevnu floru. Bez veštačkih aditiva — veoma dobar izbor.', '150g jednom dnevno', 5),
(17, '3856020224250', 'Jetrena pašteta je bogat izvor gvožđa i vitamina B12, ali i zasićenih masti i soli. Konzumirati umereno, posebno osobe sa kardiovaskularnim problemima.', '30g na sendviču, ne više od 2x nedeljno', 3),
(18, '3856020224250', 'Svinjska džigerica kao osnova daje dobar nutritivni profil u pogledu mikronutrijenata. Ipak, visok sadržaj masti i soli zahteva oprez.', '30g uz integralni hleb', 3),
(11, '3856020224250', 'Nije idealan svakodnevni izbor, ali povremeno može biti korisna zbog sadržaja gvožđa. Trudnicama i osobama sa anemijom može biti od koristi uz lekarsku preporuku.', 'Maksimalno 30g, 2-3 puta nedeljno', 3),
(19, '4001686301555', 'Slatkiš bez nutritivne vrednosti — čisti šećeri, minimalne masti i bez vlakana. Izaziva nagli skok glikemije i treba ga izbegavati kod dece i dijabetičara.', 'Maksimalno 25g povremeno', 1),
(20, '4001686301555', 'Sadrži želatin životinjskog porekla i veštačke arome. Ovo nije prehrambeni proizvod koji bih ikome preporučio kao redovan deo ishrane.', 'Izbegavati što je moguće više', 1),
(22, '4001686301555', 'Kalorije bez hranljivih materija. Jedino što može biti prihvatljivo je povremena mala porcija kao \"treat\", ali ne kao svakodnevna navika.', 'Retko, do 25g', 2),
(23, '7613034626844', 'Veoma visok sadržaj šećera (75g/100g). Uprkos dodatom kakaou i vitaminima, nije pravi supstitut za zdravi doručak — pre svega je zaslađen prašak.', '1 čaša (15g u 200ml mleka) max jednom dnevno', 2),
(24, '7613034626844', 'Za decu koja odbijaju mleko može biti privremena pomoć da unesu kalcijum, ali dugoročno treba smanjiti šećer i navikavati ih na prirodan kakao.', '15g u 200ml mleka, uz obrok', 3),
(25, '7613034626844', 'Bogat vlaknima od kakaa (7.7g/100g) što je pozitivno, ali ogromna količina šećera neutrališe ovu prednost. Preporučujem prirodni nesladeni kakao u prahu kao alternativu.', 'Ograničiti na 1 obrok dnevno', 2),
(12, '7613034626875', 'Visok udio šećera (58g/100g) i hidrogenizovane masti čine ovaj proizvod lošim izborom. Nescafe 3in1 je instant mešavina koja nutritivno ne nudi ništa korisno.', 'Izbegavati, preći na crnu kafu bez šećera', 1),
(13, '7613034626875', 'Hidrogenizovano ulje je trans-mast koja povećava rizik od kardiovaskularnih bolesti. Strogo ne preporučujem ovaj proizvod ni kao povremenu konzumaciju.', 'Ne preporučujem', 1),
(14, '7613034626875', 'Kalorijski gusto, a nutritivno siromašno. Ako vam treba kafa sa mlekom, napravite je od neslađene kafe i malo mleka — daleko zdravije.', 'Smanjiti na minimum ili eliminiasati', 1),
(15, '8001505001306', 'Energetski veoma gust proizvod (603 kcal/100g) bogat zasićenim mastima i šećerom. Lešnici daju vredne mononezasićene masti, ali ih nadmašuje sadržaj palmovog ulja i šećera.', 'Maksimalno 2 komada (oko 25g)', 2),
(16, '8001505001306', 'Palmino ulje kao jedan od sastojaka nije nutritivno preporučljivo. Pogodan kao prigodna poslastica, ali ne kao redovan deo ishrane.', '1-2 komada povremeno', 2),
(17, '8001505001306', 'Visok sadržaj lešnika (28.5%) donosi vlakna i dobre masti, što ga čini nešto boljim izborom u odnosu na druge čokoladne praline. Ipak, ukupni nutricionistički profil ostaje loš.', 'Nije za svakodnevnu konzumaciju, max 2 komada', 2),
(18, '8600043015480', 'Mlevena Plazma je praktična kao dodatak jogurtu ili kašici, ali treba imati na umu visok sadržaj šećera i ugljenih hidrata. Med i vitamini su pozitivni dodatak.', '1-2 kašike (20-30g) uz jogurt ili voće', 3),
(19, '8600043015480', 'Solidni sadržaj proteina (11.9g/100g) i vlakana (3g) u poređenju sa sličnim prozvodima. Korisno kao energetski dodatak obroku, ali ne preterivati.', '50g uz obrok kao izvor energije', 4),
(20, '8600043015480', 'Kao baza za domaće energetske kuglice sa orasima i suvim voćem može biti dobar izbor. Sam po sebi sadrži više šećera nego što bih voleo.', 'Do 50g dnevno, kombinovati sa zdravijim namirnicama', 3),
(22, '8600101614030', 'Sličan Nutelinom profilu — visok šećer (56g/100g), biljna mast i kakao. Lešnik pasta je pozitivna stvar, ali je u premaloj količini da neutrališe ostale nedostatke.', 'Maksimalno 20g na parčetu hleba', 2),
(23, '8600101614030', 'Nije preporučljivo deci za svakodnevni doručak. Bolje alternative su maslac od kikirikija ili tahini sa malo meda koji nude zdrave masti i manje šećera.', 'Povremeno, do 20g', 2),
(24, '8600101614030', 'Visoke kalorije i šećer bez nutritivne gustine. Preporučujem koristiti samo kao povremenu poslasticu, nikako kao svakodnevni namaz.', 'Retko, 1 kašičica (10g)', 1),
(25, '8600101784542', 'Slično standardnim napolitankama — visok šećer i biljna mast, ali lešnik (5%) donosi malo dobrih masti i mikronutrijenata. Ipak, kalorički gust i ne previše hranljiv.', '1-2 napolitanke uz čaj', 2),
(11, '8600101784542', 'Zamena za obrok nije, ali kao sitna užina uz kafu ili čaj može proći. Pšenično brašno i šećer dominiraju sastavom. Umerena konzumacija je ključna.', '31g (1 porcija) povremeno', 3),
(12, '8600101784542', 'Biljna mast i šećer kao vodeći sastojci govore sami za sebe. Nutritivno siromašan proizvod koji treba konzumirati što ređe.', 'Jednom nedeljno, max 31g', 2),
(13, '8606006082497', 'Kombinacija kikirikija, kukuruzne krupice i čokoladnog preliva — kalorično i masno, ali kikiriki daje protein (8.5g/100g) i vlakna. Svejedno, ovo je grickalica za povremenu upotrebu.', '30g kao užina', 2),
(14, '8606006082497', 'Nije halal sertifikovan, što treba imati na umu. Sa nutritivne strane — visoke masti i šećer, ali i solidni proteini od kikirikija. Umereno.', 'Jedna porcija (30g)', 2),
(15, '8606006082497', 'Čokoladni preliv dodaje još šećera i zasićenih masti. U poređenju sa klasičnim Smokim, ovo je nutritivno lošiji izbor. Retko konzumirati.', 'Do 30g, ne svakodnevno', 2),
(16, '8606012162600', 'Delimično hidrogenizovana biljna mast znači prisustvo trans-masti koje su direktno povezane sa rizikom od kardiovaskularnih bolesti. Preporučujem zamenu maslacem ili maslinovim uljem.', 'Izbegavati, koristiti maslac ili maslinovo ulje', 1),
(17, '8606012162600', 'Trans-masti u margarinu su nutritivni neprijatelj broj jedan. Nikome ne preporučujem ovaj margarin kao redovni namaz — čak ni u malim količinama.', 'Ne preporučujem konzumaciju', 1),
(18, '8606012162600', 'Visok kalorijski sadržaj (720 kcal/100g) i loš profil masti. Jedina prednost je ekonomska pristupačnost, ali to ne opravdava loš uticaj na zdravlje.', 'Zameniti zdravijim alternativama', 1),
(19, '8606015061322', 'Odličan prirodni jogurt sa niskim kalorijskim sadržajem i dobrim udelom proteina (3.4g/100g). Korisni probiotici podržavaju zdravlje creva i imunitet.', '200g dnevno, idealno uz obrok', 5),
(20, '8606015061322', 'Fermentisani mlečni proizvod bez aditiva — upravo kakav treba da bude jogurt. Preporučujem kao bazu za smoothie ili uz integralne žitarice.', '150-200g dnevno', 5),
(22, '8606015061322', 'Nizak sadržaj masti i šećera uz solidne proteine čine ovaj jogurt jednim od boljih opcija na tržištu. Idealan za osobe na dijeti i sportiste.', '200g uz doručak ili kao užina', 5),
(23, '8600939304634', 'Gaziran napitak sa 10% voćnog soka od kruške. Daleko bolji od bezalkoholnih pića poput Cole, ali šećer (8.8g/100ml) i dalje nije zanemariv.', '1 čaša (250ml) kao osveženje', 3),
(24, '8600939304634', 'Prirodna aroma i realni voćni sok su pozitivni, ali ovaj napitak ne treba smatrati zdravim pićem. Uglavnom je to zaslađena gazirana voda sa malo soka.', '250ml povremeno, uz obrok', 3),
(25, '8600939304634', 'Za osobe koje žele smanjiti unos Cole ili Fante, ovo može biti prelazno rešenje. Ipak, cilj treba biti prelazak na čistu vodu ili prirodne sokove bez dodanog šećera.', 'Umereno, do 250ml dnevno', 3),
(11, '8601500111554', 'Mešavina arabike i robuste daje punu aromu i dobar sadržaj antioksidanata. Kafa sama po sebi nema kalorija, ali načini pripreme i dodaci mogu to promeniti.', '2 šoljice dnevno, bez šećera i mleka', 5),
(12, '8601500111554', 'Kvalitetna mlevena kafa bez aditiva. Preporučujem konzumaciju bez šećera za maksimalnu nutritivnu korist i zaštitu od insulinskog odgovora.', '1-2 šoljice (7g po šoljici)', 5),
(13, '8601500111554', 'Prirodni napitak bogat polifenolima. Istraživanja pokazuju da umeren unos kafe smanjuje rizik od dijabetesa tipa 2 i određenih karcinoma. Preporučujem kao zdravu jutarnju rutinu.', '2 šoljice dnevno (jutro i popodne)', 5),
(14, '5449000111678', 'Svega 3% soka od pomorandže uz visok šećer (9.2g/100ml) i ugljični dioksid. Nutricionistički nema smisla — bolje popiti pravu pomorandžu.', 'Izbegavati, zameniti svežim sokom', 1),
(15, '5449000111678', 'Visok glikemijski indeks usled prostih šećera u gaziranom piću. Nisu dobra opcija ni za decu ni za odrasle u svakodnevnoj ishrani.', 'Retko, max 250ml', 2),
(16, '5449000111678', 'Arome i bojila, minimalni prirodni sok i puno šećera — klasičan primer praznih kalorija u tečnom obliku. Štetno naročito za zdravlje zuba i glikemiju.', 'Ne preporučujem kao redovni napitak', 1),
(17, '8606108139419', 'Rafinisano suncokretovo ulje je dobro za termičku obradu zbog visokog tačke dimljenja. Sadrži omega-6 masti, ali ih konzumiramo previše u modernoj ishrani — treba balansirati sa omega-3 iz ribe.', '10ml po obroku za prženje', 3),
(18, '8606108139419', 'Nutritivno neutralno — čiste masti bez ugljenih hidrata i proteina. Za hladna jela preporučujem maslinovo ulje zbog visokog sadržaja oleinske kiseline. Premia je prihvatljivo za kuvanje.', 'Do 20ml dnevno', 3),
(19, '8606108139419', 'Visok sadržaj vitamina E koji je antioksidant. Ipak, kao i svako ulje, treba koristiti umereno zbog energetske gustine (900 kcal/100ml).', 'Do 10-15ml po obroku', 3),
(20, '7622210449557', 'Lešnici (13%) su dobrodošao dodatak koji donosi nezasićene masti i vlakna. Ipak, dominantni sastojci su šećer i kakao-maslac, pa se radi o slatkom zadovoljstvu, ne o zdravoj užini.', 'Max 16.7g (1 porcija) povremeno', 2),
(22, '7622210449557', 'Mlečna čokolada sa lešnicima — ukusna, ali kalorički gusta (552 kcal/100g). Preporučujem tamnu čokoladu sa 70% kakaa kao zdraviju alternativu.', '1-2 kockice, ne više od 20g', 2),
(23, '7622210449557', 'Visok sadržaj zasićenih masti (16g/100g) utiče negativno na lipidni profil krvi. Za čokoladne poslastice preporučujem tamnu čokoladu bez mleka.', 'Povremeno, ograničiti na 1 porciju', 2),
(24, '8601500111585', 'Keks sa džemom od narandže obložen čokoladom — kombinacija jednostavnih šećera i rafinisanog brašna. Džem donosi malo voćnih polifenola, ali ih šećer potire.', 'Maksimalno 2 keksa kao retka poslastica', 2),
(25, '8601500111585', 'Jaffa je ikonski domaći proizvod, ali nutritivno nije preporučljiv za svakodnevnu konzumaciju. Visok šećer i rafinisano brašno = brze kalorije bez nutritivne vrednosti.', 'Povremeno, max 3 keksa', 2),
(11, '3850102128030', 'Gazirana voda sa aromom zove i šećerom — osvežavajuće, ali bez nutritivne vrednosti. Kalorijska vrednost je niska (19 kcal/100ml), što je jedina prednost.', '1 čaša (250ml) povremeno', 3),
(12, '3850102128030', 'Bolja alternativa od Cole ili Fante, ali ni izdaleka nije zamena za čistu vodu. Šećer i arome su dodaci koje tijelu ne trebaju.', '250ml maksimalno dnevno', 3),
(13, '3850102128030', 'Za osobe koje ne mogu da piju čistu vodu, ovo može biti privremeno rešenje. Cilj treba biti postepeno prelaženje na negaziranu ili gaziranu vodu bez aroma i šećera.', 'Umereno, do 250ml dnevno', 3),
(14, '3850334005059', 'Zamrznuti grašak je izuzetno nutritivno vredna namirnica — bogat je biljnim proteinima (5.5g/100g), vlaknima i vitaminima. Zamrzavanjem se čuvaju hranljive materije.', '150g uz glavni obrok', 5),
(15, '3850334005059', 'Jedan od retkih zamrznutih proizvoda koje bez rezerve preporučujem. Bez aditiva, niski kalorijski sadržaj i visoka nutritivna vrednost. Odlično za supe, variva i salate.', '150-200g uz obrok', 5),
(16, '3830001210022', 'Pasterizovana pileća pasteta sa biljnim uljem — prihvatljiv proteinski namaz, ali visok sadržaj masti i soli zahteva umerenost. Bolja alternativa od svinjske paštete.', 'Do 30g na integralnom hlebu', 3),
(17, '3850104263715', 'Kikiriki (33%) obezbeđuje solidne proteine i vlakna, što ga čini boljim od čisto škrobnih grickalica. Ipak, visoka so (2g/100g) je ograničavajući faktor.', 'Do 30g kao užina', 3),
(18, '3850104263715', 'Jedno od ređih slanih grickalica sa realnim sadržajem kikirikija. Preporučujem kao bolji izbor grickalice, ali ipak ne svakodnevno.', 'Do 50g povremeno', 3),
(19, '8600043001349', 'Visok sadržaj biljne masti i šećera u napolitankama ih svrstava u kategoriju grickalica koje treba ograničiti. Vlakna (3g/100g) su jedina pozitivna stavka.', '2-3 napolitanke uz čaj', 2),
(20, '8600043001349', 'Palmina i biljna mast dominiraju sastavom. Preporučujem smanjiti konzumaciju i zameniti integralnim keksima koji imaju bolji nutritivni profil.', 'Retko, max 50g', 2),
(22, '8600101784535', 'Rafinisano suncokretovo ulje je prihvatljivo za prženje, ali za hladna jela preporučujem maslinovo ulje zbog antiinflamatornih svojstava oleinske kiseline.', 'Do 15ml dnevno', 3),
(23, '8600197005118', 'Marshmallow obložen čokoladom — kombinacija šećerne pene i kakaoa nema nutritivno opravdanje. Povremeni \"treat\" je prihvatljiv, ali ne kao redovna užina.', 'Jedan komad (35g) retko', 2),
(24, '7622210449601', 'Mlečna čokolada sa visokim sadržajem zasićenih masti (18g/100g) i šećera (58g/100g). Lep ukus, ali bez nutritivne vrednosti. Preporučujem kao retku poslasticu.', 'Max 1-2 kockice dnevno (10-15g)', 2),
(25, '5449000000996', 'Nema masti, proteina ni vlakana — samo prazne kalorije od šećera. Kiseline u Coca-Coli oštećuju gleđ zuba. Sve preporuke su prema eliminaciji iz redovne ishrane.', 'Izbegavati potpuno, zameniti vodom', 1),
(22, '8606012160538', 'Prirodna mlevena kafa bez aditiva — preporučujem je kao osnovu zdrave jutarnje rutine. Bogata je antioksidantima koji smanjuju oksidativni stres u organizmu.', '2 šoljice dnevno bez šećera', 5),
(23, '8606014400236', 'Pastizovano mleko sa 2.8% masti je uravnotežen izbor za odrasle — dovoljno masti za apsorpciju vitamina D i A, a ne prekalorično. Dobar izvor kalcijuma za kosti.', '250ml 1-2 puta dnevno', 5),
(24, '8600043000533', 'Plazma je jedan od retkih keksa koji ima solidno nutritivno opravdanje — visoki proteini (11g/100g), vlakna i dodati vitamini. Preporučujem uz čašu mleka kao doručak ili užinu.', '50g uz 200ml mleka', 4),
(25, '8600043022242', 'Integralno brašno i ovsene pahuljice povećavaju sadržaj vlakana, a lešnik donosi dobre masti. Jedno od boljih keks opcija na tržištu za svakodnevnu konzumaciju.', '42g (1 porcija) uz čaj ili kafu', 5),
(22, '8600043022242', 'Dobar glikemijski indeks zahvaljujući integralnim žitaricama i vlaknima (7.2g/100g). Preporučujem osobama na dijeti i dijabetičarima kao prihvatljivu grickalicu.', '42-84g kao užina ili doručak', 4),
(11, '3850102128078', 'Natrijum u Vegeti (56.9g soli/100g) je alarm za sve koji paze na krvni pritisak. Preporučujem prelazak na mešavine suvog povrća bez dodate soli kao zdraviju alternativu.', 'Do 2-3g dnevno u jelima', 1),
(12, '3850102128078', 'Pojačivači ukusa (glutamat) su sporni aditivi. Za osobe osetljive na glutamat mogu izazvati glavobolje i nelagodu. Preporučujem sveže bilje i začine umesto Vegete.', 'Minimalna količina, do 2g', 2),
(13, '3850105144518', 'Cedevita je bogata vitaminom C, ali njen šećer (84g/100g praška) dominira sastavom. Treba je tretirati kao napitak sa vitaminima, a ne kao zdravi sok. Bolje je pojesti pravu narandžu.', '15g u 250ml vode, povremeno', 3),
(11, '4001686301524', 'Glukozni sirup i šećer kao prva dva sastojka govore sve. Želatin je jedini izvor proteina, ali ni on nije nutritivno vredan. Gumene bombone su čist šećer u tvrdom obliku.', 'Izbegavati, posebno kod dece', 1),
(14, '8600101784535', 'Suncokretovo ulje ima visok udio omega-6 masnih kiselina. Previše omega-6 u ishrani narušava balans sa omega-3 i promoviše inflamaciju u organizmu. Koristiti umereno.', 'Do 15ml dnevno, kombinovati sa maslinovim uljem', 3),
(25, '7613034626875', 'Dobar izvor kofeina, uz umerenu upotrebu.', 'Jedna dnevno', 4);

-- --------------------------------------------------------

--
-- Table structure for table `korisnici`
--

CREATE TABLE `korisnici` (
  `idKorisnika` int(11) NOT NULL,
  `ime` varchar(60) DEFAULT NULL,
  `prezime` varchar(80) DEFAULT NULL,
  `email` varchar(60) DEFAULT NULL,
  `lozinka` varchar(60) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `korisnici`
--

INSERT INTO `korisnici` (`idKorisnika`, `ime`, `prezime`, `email`, `lozinka`) VALUES
(1, 'Milosav', 'Živković', 'milosav@gmail.com', 'milosav123'),
(2, 'Ana', 'Luković', 'ana@gmail.com', 'ana123'),
(3, 'Nikola', 'Krsmanović', 'nikola@email.com', 'nikola123'),
(4, 'Sara', 'Spasojević', 'sara@email.com', 'sara123'),
(5, 'Jovana', 'Jovanović', 'jovana@email.com', 'jovana123'),
(6, 'Nikola', 'Nikolić', 'nikolic@email.com', 'nikolic123'),
(7, 'Petar', 'Petrović', 'petar.petrovic@email.com', 'petar123'),
(8, 'Iva', 'Ivanović', 'iva@gmail.com', 'iva123'),
(13, 'Lazar', 'Nedeljković', 'lazar@gmail.com', 'lazar123'),
(14, 'Uroš', 'Makić', 'uroš@gmail.com', 'uros123'),
(15, 'Marko', 'Nikolić', 'nikolić@gmail.com', 'nikola123'),
(16, 'Nemanja', 'Nikolić', 'nemanja@gmail.com', 'nemanja123'),
(17, 'Milena', 'Živković', 'milena@gmail.com', 'milena123'),
(19, 'Milutin', 'Milivojević', 'milutin@gmail.com', 'milutin123'),
(20, 'Miloš', 'Milošević', 'milos@gmail.com', 'milos123'),
(22, 'Saša', 'Zdjelar', 'sasa@gmail.com', 'sasa123'),
(23, 'Marko', 'Stanković', 'marko@gmail.com', 'marko123'),
(24, 'Jelena', 'Milić', 'jelena@gmail.com', 'jelena123'),
(25, 'Stefan', 'Đorđević', 'stefan@gmail.com', 'stefan123'),
(26, 'Milica', 'Pavlović', 'milica@gmail.com', 'milica123'),
(27, 'Dušan', 'Ilić', 'dusan@gmail.com', 'dusan123'),
(28, 'Marija', 'Vasić', 'marija@gmail.com', 'marija123'),
(29, 'Aleksandar', 'Lukić', 'aleksandar@gmail.com', 'aleksandar123'),
(30, 'Katarina', 'Popović', 'katarina@gmail.com', 'katarina123'),
(31, 'Nemanja', 'Kostić', 'nemanja@gmail.com', 'nemanja123'),
(32, 'Sofija', 'Nedeljković', 'sofija@gmail.com', 'sofija123'),
(33, 'Luka', 'Vučićević', 'luka@gmail.com', 'luka123');

-- --------------------------------------------------------

--
-- Table structure for table `nutricionisti`
--

CREATE TABLE `nutricionisti` (
  `idNutricioniste` int(11) NOT NULL,
  `ime` varchar(50) NOT NULL,
  `prezime` varchar(50) NOT NULL,
  `licenca` varchar(100) NOT NULL,
  `email` varchar(100) NOT NULL,
  `lozinka` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `nutricionisti`
--

INSERT INTO `nutricionisti` (`idNutricioniste`, `ime`, `prezime`, `licenca`, `email`, `lozinka`) VALUES
(1, 'Marko', 'Marković', 'KZR-NUT-2021-1547', 'markonut@gmail.com', 'marko123'),
(2, 'Andjela', 'Kodić', 'HKMS-NUT-L-098/22', 'andjelanut@gmail.com', 'andjela123'),
(3, 'Andrija', 'Đorić', 'NPI: 1023456789', 'andrijanut@gmail.com', 'andrija123'),
(6, 'Aleksandar', 'Knežević', 'NPI: 1023456743', 'aleksandarnut@gmail.com', 'aleksandar123'),
(7, 'Nebojša', 'Lakić', 'KZR-NUT-2021-4308', 'nebojsanut@gmail.com', 'nebojsa123'),
(9, 'Milica', 'Marković', 'HKMS-NUT-L-078/3', 'milicanut@gmail.com', 'milica123'),
(10, 'Sandra', 'Belošević', 'KZR-NUT-2015-4311', 'sandranut@gmail.com', 'sandra123'),
(11, 'Jovana', 'Ristić', 'KZR-NUT-2022-8841', 'jovananut@gmail.com', 'jovana123'),
(12, 'Nikola', 'Antić', 'HKMS-NUT-L-044/21', 'nikolanut@gmail.com', 'nikola123'),
(13, 'Ivana', 'Simić', 'NPI: 9876543210', 'ivananut@gmail.com', 'ivana123'),
(14, 'Darko', 'Mladenović', 'KZR-NUT-2019-2210', 'darkonut@gmail.com', 'darko123'),
(15, 'Maja', 'Bogdanović', 'HKMS-NUT-L-112/23', 'majanut@gmail.com', 'maja123'),
(16, 'Luka', 'Tomić', 'NPI: 5566778899', 'lukanut@gmail.com', 'luka123'),
(17, 'Tamara', 'Radić', 'KZR-NUT-2020-3305', 'tamaranut@gmail.com', 'tamara123'),
(18, 'Bojan', 'Vuković', 'HKMS-NUT-L-015/19', 'bojannut@gmail.com', 'bojan123'),
(19, 'Elena', 'Petković', 'NPI: 4433221100', 'elenanut@gmail.com', 'elena123'),
(20, 'Pavle', 'Stanojević', 'KZR-NUT-2023-7712', 'pavlenut@gmail.com', 'pavle123'),
(21, 'Filip', 'Kostić', 'KZR-NUT-2024-1122', 'filipnut@gmail.com', 'filip123'),
(22, 'Sanja', 'Vesić', 'HKMS-NUT-L-033/20', 'sanjanut@gmail.com', 'sanja123'),
(23, 'Ognjen', 'Lazić', 'NPI: 1212343456', 'ognjennut@gmail.com', 'ognjen123'),
(24, 'Marta', 'Jović', 'KZR-NUT-2018-9901', 'martanut@gmail.com', 'marta123'),
(25, 'Uroš', 'Dukić', 'HKMS-NUT-L-055/18', 'urosnut@gmail.com', 'uros123');

-- --------------------------------------------------------

--
-- Table structure for table `proizvodi`
--

CREATE TABLE `proizvodi` (
  `barKod` varchar(20) NOT NULL,
  `naziv` varchar(255) NOT NULL,
  `sastojci` text DEFAULT NULL,
  `posan` tinyint(1) DEFAULT 0,
  `halal` tinyint(1) DEFAULT 0,
  `energijaKj` int(11) DEFAULT NULL,
  `energijaKcal` int(11) DEFAULT NULL,
  `masti` decimal(10,2) DEFAULT NULL,
  `zasiceneMasti` decimal(10,2) DEFAULT NULL,
  `ugljeniHidrati` decimal(10,2) DEFAULT NULL,
  `seceri` decimal(10,2) DEFAULT NULL,
  `vlakna` decimal(10,2) DEFAULT NULL,
  `proteini` decimal(10,2) DEFAULT NULL,
  `so` decimal(10,2) DEFAULT NULL,
  `preporucenaDnevnaDoza` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `proizvodi`
--

INSERT INTO `proizvodi` (`barKod`, `naziv`, `sastojci`, `posan`, `halal`, `energijaKj`, `energijaKcal`, `masti`, `zasiceneMasti`, `ugljeniHidrati`, `seceri`, `vlakna`, `proteini`, `so`, `preporucenaDnevnaDoza`) VALUES
('3850104263715', 'Stark Smoki 50g', 'Kukuruzna krupica, kikiriki (33%), biljna mast, so', 1, 1, 2269, 544, 33.00, 13.00, 46.00, 2.00, 4.00, 14.00, 2.00, '50g'),
('7622210449601', 'Milka Alpine Milk 100g', 'Secer, kakao-puter, obrano mleko u prahu, kakao masa', 0, 1, 2213, 530, 29.00, 18.00, 59.00, 58.00, 1.80, 6.30, 0.37, '33g'),
('3850105144518', 'Cedevita Narandža 200g', 'Secer, limunska kiselina, regulator kiselosti, aroma', 1, 1, 1530, 365, 0.00, 0.00, 84.00, 84.00, 0.00, 0.00, 0.70, '19g'),
('5449000000996', 'Coca-Cola 0.5l', 'Voda, secer, ugljen-dioksid, boja E150d, kiselina', 1, 1, 180, 42, 0.00, 0.00, 10.60, 10.60, 0.00, 0.00, 0.00, '250ml'),
('3830001210022', 'Argeta kokosija pasteta 95g', 'Voda, kuvano kokosije meso, biljno ulje, mlecni proteini', 0, 1, 1279, 309, 29.00, 3.50, 0.50, 0.50, 0.00, 10.00, 1.30, '95g'),
('3850102128078', 'Vegeta 250g', 'Kuhinjska so, suseno povrce (15.5%), pojacivaci ukusa, secer', 1, 1, 697, 164, 0.50, 0.20, 32.00, 22.00, 0.00, 8.50, 56.90, '5g'),
('8600043001349', 'Bambi Napolitanke 800g', 'Psenicno brasno, biljna mast, secer, kakao prah', 1, 1, 2150, 514, 26.00, 12.00, 64.00, 32.00, 3.00, 5.00, 0.30, '100g'),
('8600101784535', 'Dijamant Ulje 1l', 'Rafinisano suncokretovo ulje', 1, 1, 3700, 900, 100.00, 11.00, 0.00, 0.00, 0.00, 0.00, 0.00, '15ml'),
('4001686301524', 'Haribo Goldbaren 100g', 'Glukozni sirup, secer, zelatin, dekstroza, voce', 0, 1, 1459, 343, 0.50, 0.10, 77.00, 46.00, 0.00, 6.90, 0.07, '25g'),
('8606014400236', 'Moja Kravica Mleko 1l', 'Pasterizovano mleko sa 2.8% mlecne masti', 0, 1, 239, 57, 2.80, 1.90, 4.70, 4.70, 0.00, 3.20, 0.10, '200ml'),
('8606012160538', 'Grand Kafa Gold 200g', 'Mesavina przene mlevene kafe', 1, 1, 0, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '7g'),
('8600197005118', 'Munchmallow 105g', 'Secer, psenicno brasno, biljna mast, kakao prah', 0, 1, 1878, 447, 17.00, 10.00, 69.00, 45.00, 2.00, 3.50, 0.10, '35g'),
('7613034626844', 'Nesquik Kakao 200g', 'Secer, kakao prah sa redukovanim sadrzajem masti', 1, 1, 1614, 381, 3.60, 1.50, 78.00, 75.00, 7.70, 5.10, 0.40, '15g'),
('8601500111585', 'Jaffa keks 150g', 'Secer, psenicno brasno, glukozni sirup, jaja, kakao maslac', 0, 1, 1279, 309, 29.00, 3.50, 0.50, 0.50, 0.00, 10.00, 1.30, '110g'),
('8600043000533', 'Bambi Plazma 150g', 'Psenicno brasno, secer, maslac, sojino brasno, biljna mast', 0, 1, 1855, 441, 12.00, 6.30, 70.00, 20.00, 3.00, 11.00, 0.80, '50g'),
('3850374044957', 'Dukatos Natur grčki tip jogurta 150g', 'Pasterizovano mleko sa 9.7% mlečne masti, pavlaka, jogurtna kultura', 0, 1, 510, 123, 9.70, 6.30, 3.60, 3.60, 0.00, 5.30, 0.10, '150g'),
('3850102322359', 'Jana Vitamin Immuno 0.5L', 'Voda, voćni sok limuna (1%), vitamini C i B6, mineral cink, prirodne arome.', 1, 1, 65, 15, 0.00, 0.00, 3.80, 3.70, 0.00, 0.00, 0.01, '500ml'),
('8606015061322', 'Moja Kravica Gusti jogurt 1kg', 'Pasterizovano mleko sa 2.8% mlečne masti, jogurtna kultura.', 0, 1, 235, 56, 2.80, 1.80, 4.30, 4.30, 0.00, 3.40, 0.10, '200g'),
('8600939304634', 'Knjaz Miloš Remix Kruška 1.5L', 'Gazirana voda, voćni sok kruške (10%), šećer, limunska kiselina.', 1, 1, 155, 37, 0.00, 0.00, 9.00, 8.80, 0.00, 0.00, 0.02, '250ml'),
('8600043015480', 'Plazma Mlevena 300g', 'Pšenično brašno, šećer, maslac, sojin protein, med, vitamini.', 0, 1, 1855, 441, 12.00, 6.30, 70.00, 20.00, 3.00, 11.90, 0.80, '50g'),
('7622210449557', 'Milka Noisette 100g', 'Šećer, kakao-maslac, lešnici (13%), obrano mleko u prahu, surutka.', 0, 1, 2304, 552, 34.00, 16.00, 53.00, 51.00, 2.60, 7.30, 0.33, '16.7g'),
('8606006082497', 'Smoki Čoko Štark 80g', 'Kukuruzna krupica, kikiriki, biljna mast, čokoladni preliv (šećer, kakao).', 0, 0, 2180, 521, 28.00, 14.00, 58.00, 22.00, 3.50, 8.50, 0.50, '30g'),
('8001505001306', 'Ferrero Rocher T16', 'Mlečna čokolada, lešnici (28.5%), šećer, palmino ulje, pšenično brašno.', 0, 1, 2506, 603, 42.70, 14.10, 44.40, 39.90, 4.00, 8.20, 0.15, '37.5g'),
('8606108139419', 'Premia Suncokretovo ulje 1L', '100% rafinisano suncokretovo ulje.', 1, 1, 3700, 900, 100.00, 10.00, 0.00, 0.00, 0.00, 0.00, 0.00, '10ml'),
('3850334005059', 'Ledo Grašak 450g', 'Zamrznuti mladi grašak.', 1, 1, 335, 80, 0.50, 0.10, 11.00, 2.50, 5.00, 5.50, 0.01, '150g'),
('8606012162600', 'Dijamant Margarin Stoni 250g', 'Jestiva delimično hidrogenizovana biljna mast, suncokretovo ulje, voda.', 1, 1, 2960, 720, 80.00, 24.00, 0.00, 0.00, 0.00, 0.00, 0.40, '10g'),
('3856020224250', 'Gavrilović Pašteta Jetrena 100g', 'Svinjsko meso, voda, jetra (15%), začini, so.', 0, 0, 1285, 311, 29.00, 11.00, 1.50, 0.50, 0.00, 11.00, 1.40, '30g'),
('8600101784542', 'Jaffa Napolitanke lešnik 187g', 'Pšenično brašno, šećer, biljna mast, pečeni lešnik (5%), kakao.', 1, 1, 2220, 531, 29.00, 13.00, 61.00, 35.00, 2.50, 5.50, 0.25, '31g'),
('4001686301555', 'Haribo Tropifrutti 100g', 'Šećer, glukozni sirup, želatin, voćni koncentrati, limunska kiselina.', 0, 0, 1459, 343, 0.50, 0.10, 77.00, 46.00, 0.10, 6.90, 0.07, '25g'),
('8600101614030', 'Cipiripi krem 400g', 'Šećer, biljna mast, obrano mleko u prahu, kakao, lešnik pasta.', 0, 1, 2330, 558, 34.00, 11.00, 58.00, 56.00, 1.50, 4.50, 0.15, '20g'),
('5449000111678', 'Fanta Orange 0.5L', 'Voda, šećer, sok od pomorandže (3%), ugljen-dioksid.', 1, 1, 160, 38, 0.00, 0.00, 9.20, 9.20, 0.00, 0.00, 0.01, '250ml'),
('8600043022242', 'Wellness keks sa lešnikom 210g', 'Integralno brašno, ovsene pahuljice, šećer, lešnik (4%), vitamini.', 1, 1, 1920, 458, 18.00, 7.50, 63.00, 19.00, 7.20, 8.00, 0.70, '42g'),
('7613034626875', 'Nescafe 3in1 Classic 16.5g', 'Šećer, glukozni sirup, kafa (7.9%), potpuno hidrogenizovano ulje.', 0, 1, 1860, 442, 12.00, 11.00, 80.00, 58.00, 3.20, 2.10, 0.90, '16.5g'),
('3850102128030', 'Jamnica Sensation Zova 1.5L', 'Gazirana voda, aroma zove i limuna, šećer.', 1, 1, 80, 19, 0.00, 0.00, 4.50, 4.50, 0.00, 0.00, 0.02, '250ml'),
('8601500111554', 'Doncafe Minas 200g', 'Mlevena pržena kafa (mešavina Minas i Robusta).', 1, 1, 0, 0, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, 0.00, '7g');

-- --------------------------------------------------------

--
-- Table structure for table `racuni`
--

CREATE TABLE `racuni` (
  `idRacuna` int(11) NOT NULL,
  `idZahteva` int(11) NOT NULL,
  `idKorisnika` int(11) NOT NULL,
  `idNutricioniste` int(11) NOT NULL,
  `iznos` int(11) NOT NULL,
  `status` varchar(20) DEFAULT 'neplacen',
  `provizija` double NOT NULL,
  `datum` datetime DEFAULT current_timestamp()
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `racuni`
--

INSERT INTO `racuni` (`idRacuna`, `idZahteva`, `idKorisnika`, `idNutricioniste`, `iznos`, `status`, `provizija`, `datum`) VALUES
(1, 36, 1, 1, 2500, 'placen', 250, '2026-03-26 22:11:00'),
(2, 38, 5, 15, 3500, 'placen', 350, '2026-03-26 23:03:22'),
(3, 40, 33, 13, 5000, 'placen', 1000, '2026-03-26 23:52:27'),
(4, 41, 2, 2, 5000, 'placen', 1000, '2026-03-28 00:30:41'),
(6, 23, 28, 14, 1500, 'neplacen', 300, '2026-03-30 21:10:37');

-- --------------------------------------------------------

--
-- Table structure for table `skenirani_proizvodi`
--

CREATE TABLE `skenirani_proizvodi` (
  `idKorisnika` int(11) NOT NULL,
  `barKod` varchar(20) NOT NULL,
  `datum` date NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `skenirani_proizvodi`
--

INSERT INTO `skenirani_proizvodi` (`idKorisnika`, `barKod`, `datum`) VALUES
(1, '3830001210022', '2026-03-17'),
(1, '3850104263715', '2026-03-17'),
(1, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '3830001210022', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(2, '3850104263715', '2026-03-17'),
(1, '5449000000996', '2026-03-17'),
(1, '5449000000996', '2026-03-17'),
(1, '7622210449601', '2026-03-17'),
(1, '7622210449601', '2026-03-17'),
(1, '7622210449601', '2026-03-17'),
(1, '7622210449601', '2026-03-17'),
(1, '7622210449601', '2026-03-17'),
(2, '7622210449601', '2026-03-17'),
(1, '7622210449601', '2026-03-17'),
(2, '5449000000996', '2026-03-17'),
(1, '3830001210022', '2026-03-18'),
(1, '8600101784535', '2026-03-18'),
(2, '8600101784535', '2026-03-18'),
(22, '7622210449601', '2026-03-18'),
(2, '8606012160538', '2026-03-18'),
(22, '8600043000533', '2026-03-18'),
(13, '8606014400236', '2026-03-18'),
(27, '8600043022242', '2026-03-18'),
(3, '7622210449557', '2026-03-01'),
(5, '8600043000533', '2026-03-01'),
(7, '5449000000996', '2026-03-02'),
(8, '8606014400236', '2026-03-02'),
(13, '8606012160538', '2026-03-03'),
(14, '4001686301524', '2026-03-03'),
(15, '7613034626844', '2026-03-03'),
(16, '8600043001349', '2026-03-04'),
(17, '3856020224250', '2026-03-04'),
(19, '8601500111585', '2026-03-04'),
(20, '8600197005118', '2026-03-05'),
(22, '5449000111678', '2026-03-05'),
(23, '3850374044957', '2026-03-05'),
(24, '8606015061322', '2026-03-06'),
(25, '7622210449601', '2026-03-06'),
(26, '8600939304634', '2026-03-06'),
(27, '3850102322359', '2026-03-07'),
(28, '8001505001306', '2026-03-07'),
(29, '8600043015480', '2026-03-07'),
(30, '4001686301555', '2026-03-08'),
(31, '7613034626875', '2026-03-08'),
(32, '8606108139419', '2026-03-08'),
(3, '8600101614030', '2026-03-09'),
(5, '3850334005059', '2026-03-09'),
(6, '8606012162600', '2026-03-09'),
(7, '8600101784542', '2026-03-10'),
(8, '3830001210022', '2026-03-10'),
(13, '3850102128030', '2026-03-11'),
(14, '8600043022242', '2026-03-11'),
(15, '8601500111554', '2026-03-11'),
(16, '5449000000996', '2026-03-12'),
(17, '8606014400236', '2026-03-12'),
(19, '3850105144518', '2026-03-12'),
(20, '8600101784535', '2026-03-13'),
(22, '3850104263715', '2026-03-13'),
(23, '8600043000533', '2026-03-13'),
(24, '7622210449557', '2026-03-14'),
(25, '8606015061322', '2026-03-14'),
(26, '5449000111678', '2026-03-14'),
(27, '8600197005118', '2026-03-15'),
(28, '3850374044957', '2026-03-15'),
(29, '8601500111585', '2026-03-15'),
(30, '8600939304634', '2026-03-16'),
(31, '7613034626844', '2026-03-16'),
(32, '3850102322359', '2026-03-16'),
(6, '8001505001306', '2026-03-17'),
(4, '8606012160538', '2026-03-17'),
(4, '8600043001349', '2026-03-18'),
(1, '3850102128078', '2026-03-18'),
(1, '4001686301524', '2026-03-18'),
(33, '3850105144518', '2026-03-26'),
(1, '5449000111678', '2026-03-28'),
(1, '8600043001349', '2026-03-28'),
(1, '3850104263715', '2026-03-30'),
(1, '8600043001349', '2026-04-01'),
(1, '7622210449557', '2026-04-01'),
(2, '3850102322359', '2026-04-01');

-- --------------------------------------------------------

--
-- Table structure for table `zahtevi_za_saradnju`
--

CREATE TABLE `zahtevi_za_saradnju` (
  `idZahteva` int(11) NOT NULL,
  `idKorisnika` int(11) NOT NULL,
  `idNutricioniste` int(11) NOT NULL,
  `tip` varchar(50) NOT NULL,
  `opis` text NOT NULL,
  `odgovor` text DEFAULT NULL,
  `status` varchar(50) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `zahtevi_za_saradnju`
--

INSERT INTO `zahtevi_za_saradnju` (`idZahteva`, `idKorisnika`, `idNutricioniste`, `tip`, `opis`, `odgovor`, `status`) VALUES
(3, 1, 6, 'Konsultacija', 'Koje namirnice preporučujete sa visokim sadržajem  proteina?', NULL, 'prihvacen'),
(20, 25, 11, 'Konsultacija', 'Da li postoji veza između ishrane i kvaliteta sna? Koje namirnice bi mi pomogle da bolje spavam?', NULL, 'poslat'),
(21, 26, 12, 'Plan ishrane', 'Imam problema sa kožom — akne i suva koža. Da li ishrana može da pomogne i šta da jedem a šta da izbegavam?', NULL, 'neprihvacen'),
(22, 27, 13, 'Konsultacija', 'Treniram dizanje tegova i zanima me da li treba da jedem pre ili posle treninga i šta tačno?', NULL, 'poslat'),
(23, 28, 14, 'Plan ishrane', 'Imam hroničan umor i nedostatak energije tokom dana. Može li ishrana da mi pomogne i šta bih trebala da promenim?', 'Ishrana može značajno uticati na nivo vitalnosti, a ključ je u stabilizaciji šećera u krvi i adekvatnom unosu mikronutrijenata. Fokusirajte se na složene ugljene hidrate poput ovsenih pahuljica ili integralnog pirinča koji postepeno oslobađaju energiju, uz obavezan unos proteina iz nemasnog mesa, ribe ili mahunarki kako biste sprečili nagle padove snage. Povećajte unos zelenog lisnatog povrća bogatog gvožđem i magnezijumom, dodajte zdrave masti iz orašastih plodova ili maslinovog ulja, a istovremeno pokušajte da smanjite prerađene šećere i kofein koji često uzrokuju kratkotrajan nalet, a zatim još dublji osećaj umora.', 'odgovoren'),
(24, 29, 15, 'Konsultacija', 'Koliko obroka dnevno je optimalno i da li je bolje jesti češće u manjim količinama ili ređe u većim?', NULL, 'poslat'),
(25, 30, 16, 'Plan ishrane', 'Veganski sam već godinu dana ali osećam se slabo i umorno. Može li mi neko napraviti plan koji pokriva sve nutritivne potrebe?', NULL, 'poslat'),
(26, 31, 17, 'Konsultacija', 'Da li su suplementi vitamina D neophodni i u kojoj dozi ih uzimati tokom zime?', NULL, 'prihvacen'),
(27, 32, 18, 'Plan ishrane', 'Imam 60 godina i želim da sačuvam mišićnu masu i koštanu gustinu što duže. Koji plan ishrane mi preporučujete?', NULL, 'neprihvacen'),
(28, 3, 19, 'Konsultacija', 'Primetio sam da mi je loša koncentracija na poslu, posebno posle ručka. Šta bih mogao da promenim u ishrani?', NULL, 'poslat'),
(29, 4, 20, 'Plan ishrane', 'Imam sindrom policističnih jajnika (PCOS) i čitala sam da ishrana može da pomogne. Možete li mi napraviti odgovarajući plan?', NULL, 'prihvacen'),
(31, 7, 23, 'Plan ishrane', 'Radim noćne smene i teško mi je da održavam zdravu ishranu. Kako da organizujem obroke kada mi je raspored potpuno poremećen?', NULL, 'poslat'),
(32, 8, 24, 'Konsultacija', 'Zanima me da li je mediteranska ishrana zaista toliko zdrava koliko se priča i kako da je prilagodim srpskim namirnicama?', NULL, 'poslat'),
(33, 5, 25, 'Plan ishrane', 'Imam alergiju na gluten i teško mi je da nađem ukusne i hranljive alternative. Možete li mi pomoći sa planom ishrane?', NULL, 'neprihvacen'),
(34, 16, 9, 'Konsultacija', 'Koliko je opasno preskakati doručak i da li je tačno da je najvažniji obrok u danu?', NULL, 'prihvacen'),
(36, 1, 1, 'Plan ishrane', 'Želim da smršam. Potreban mi je plan ishrane, hvala unapred.', 'Za efikasno mršavljenje ključno je da dan započnete doručkom bogatim proteinima i vlaknima, poput omleta sa povrćem ili ovsene kaše sa borovnicama, dok bi ručak trebalo da se sastoji od nemasnog mesa ili ribe uz obilnu porciju sezonske salate i malu količinu složenih ugljenih hidrata poput integralnog pirinča ili kinoe. Večeru fokusirajte na lakše opcije kao što su grilovano belo meso ili mladi sir sa barenim brokolijem, pazeći da poslednji obrok bude bar tri sata pre spavanja, uz obavezan unos minimum dve litre vode dnevno i potpuno izbacivanje sokova, grickalica i prerađenih šećera.', 'dostavljen_odgovor'),
(37, 2, 9, 'Plan ishrane', 'Želim da se hranim zdravije, pa mi je potreban plan ishrane sa što više zdrave hrane.', NULL, 'neprihvacen'),
(38, 5, 15, 'Plan ishrane', 'Želim da smršam, potreban mi je plan ishrane, ako je moguće bez ribe, s obzirom da ne volim ribu.', 'Za efikasno mršavljenje bez konzumacije ribe, fokusirajte se na visok unos proteina iz piletine, ćuretine, nemasnog junećeg mesa, jaja i mlečnih proizvoda poput mladog sira ili grčkog jogurta, kombinujući ih sa velikim porcijama zelenog i sezonskog povrća koje obezbeđuje sitost uz malo kalorija. Doručak bazirajte na jajima ili ovsenim pahuljicama sa bobičastim voćem, za ručak birajte grilovano meso uz baren krompir ili integralni pirinač, dok bi večera trebala da bude lagana, poput obrok salate sa piletinom ili leblebijama, uz obavezno izbacivanje rafinisanih šećera, peciva i sokova, te održavanje adekvatne hidratacije čistom vodom i biljnim čajevima tokom celog dana.', 'dostavljen_odgovor'),
(39, 5, 10, 'Konsultacija', 'Koliko je štetno jesti svaki dan slatko.', NULL, 'neprihvacen'),
(40, 33, 13, 'Plan ishrane', 'Želim da se ugojim, potreban mi je plan ishrane.', 'Da biste na zdrav način povećali telesnu masu, fokusirajte se na energetski gustu hranu i veći broj obroka, počevši od doručka koji kombinuje jaja, punomasni sir i integralni hleb ili ovsenu kašu sa puterom od kikirikija, bananama i orašastim plodovima. Ručak i večera treba uvek da sadrže obilan izvor proteina poput piletine, teletine ili ćuretine, uz značajne porcije složenih ugljenih hidrata kao što su pirinač, krompir, kinoa ili testenina, sve uz dodatak zdravih masti poput maslinovog ulja i avokada. Između glavnih obroka obavezno uvedite dve do tri užine koje mogu biti u obliku proteinskih šejkova sa voćem i ovsenim pahuljicama ili miksa sirovih badema i lešnika, uz stalnu hidrataciju vodom i prirodnim sokovima, izbegavajući pritom prazne kalorije iz rafinisanog šećera i brze hrane.', 'dostavljen_odgovor'),
(41, 2, 2, 'Plan ishrane', 'potreban plan ishrane', 'evo plana ishraneeeee', 'dostavljen_odgovor'),
(43, 1, 25, 'Konsultacija', 'Šta jesti zdravo za užine u školi?', NULL, 'prihvacen');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `administratori`
--
ALTER TABLE `administratori`
  ADD PRIMARY KEY (`idAdministratora`);

--
-- Indexes for table `bar_kodovi`
--
ALTER TABLE `bar_kodovi`
  ADD PRIMARY KEY (`barKod`);

--
-- Indexes for table `komentari`
--
ALTER TABLE `komentari`
  ADD KEY `barKod` (`barKod`),
  ADD KEY `idNutricioniste` (`idNutricioniste`);

--
-- Indexes for table `korisnici`
--
ALTER TABLE `korisnici`
  ADD PRIMARY KEY (`idKorisnika`);

--
-- Indexes for table `nutricionisti`
--
ALTER TABLE `nutricionisti`
  ADD PRIMARY KEY (`idNutricioniste`),
  ADD UNIQUE KEY `email` (`email`);

--
-- Indexes for table `proizvodi`
--
ALTER TABLE `proizvodi`
  ADD KEY `barKod` (`barKod`);

--
-- Indexes for table `racuni`
--
ALTER TABLE `racuni`
  ADD PRIMARY KEY (`idRacuna`);

--
-- Indexes for table `skenirani_proizvodi`
--
ALTER TABLE `skenirani_proizvodi`
  ADD KEY `barKod` (`barKod`),
  ADD KEY `idKorisnika` (`idKorisnika`);

--
-- Indexes for table `zahtevi_za_saradnju`
--
ALTER TABLE `zahtevi_za_saradnju`
  ADD PRIMARY KEY (`idZahteva`),
  ADD KEY `idNutricioniste` (`idNutricioniste`),
  ADD KEY `idKorisnika` (`idKorisnika`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `administratori`
--
ALTER TABLE `administratori`
  MODIFY `idAdministratora` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `korisnici`
--
ALTER TABLE `korisnici`
  MODIFY `idKorisnika` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=34;

--
-- AUTO_INCREMENT for table `nutricionisti`
--
ALTER TABLE `nutricionisti`
  MODIFY `idNutricioniste` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=26;

--
-- AUTO_INCREMENT for table `racuni`
--
ALTER TABLE `racuni`
  MODIFY `idRacuna` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `zahtevi_za_saradnju`
--
ALTER TABLE `zahtevi_za_saradnju`
  MODIFY `idZahteva` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=44;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `komentari`
--
ALTER TABLE `komentari`
  ADD CONSTRAINT `komentari_ibfk_1` FOREIGN KEY (`barKod`) REFERENCES `bar_kodovi` (`barKod`),
  ADD CONSTRAINT `komentari_ibfk_2` FOREIGN KEY (`idNutricioniste`) REFERENCES `nutricionisti` (`idNutricioniste`);

--
-- Constraints for table `proizvodi`
--
ALTER TABLE `proizvodi`
  ADD CONSTRAINT `proizvodi_ibfk_1` FOREIGN KEY (`barKod`) REFERENCES `bar_kodovi` (`barKod`);

--
-- Constraints for table `skenirani_proizvodi`
--
ALTER TABLE `skenirani_proizvodi`
  ADD CONSTRAINT `skenirani_proizvodi_ibfk_1` FOREIGN KEY (`barKod`) REFERENCES `bar_kodovi` (`barKod`),
  ADD CONSTRAINT `skenirani_proizvodi_ibfk_2` FOREIGN KEY (`idKorisnika`) REFERENCES `korisnici` (`idKorisnika`);

--
-- Constraints for table `zahtevi_za_saradnju`
--
ALTER TABLE `zahtevi_za_saradnju`
  ADD CONSTRAINT `zahtevi_za_saradnju_ibfk_1` FOREIGN KEY (`idNutricioniste`) REFERENCES `nutricionisti` (`idNutricioniste`),
  ADD CONSTRAINT `zahtevi_za_saradnju_ibfk_2` FOREIGN KEY (`idKorisnika`) REFERENCES `korisnici` (`idKorisnika`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;

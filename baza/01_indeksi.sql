-- ═══════════════════════════════════════════════════════════════════
-- HealthyLife - indeksi za brze pretrage
-- ═══════════════════════════════════════════════════════════════════
--
-- Sta radi: dodaje indekse na kolone po kojima aplikacija pretrazuje
-- (email pri prijavi, idKorisnika, status...). Indeks je kao sadrzaj u
-- knjizi: baza ne mora da pregleda svaki red da bi nasla trazeni.
--
-- Bezbedno je: NE menja i NE brise podatke, samo dodaje indekse.
-- Obe verzije aplikacije (stara i nova) rade isto i posle ove skripte.
--
-- Kako se pokrece (phpMyAdmin):
--   1. Prvo napravi rezervnu kopiju: izaberi bazu healthylifebaza1 -> Export -> Go
--   2. Izaberi bazu healthylifebaza1 -> kartica SQL
--   3. Nalepi ceo sadrzaj ovog fajla -> Go
--
-- Skripta se pokrece jednom. Ako je pokrenes ponovo, prva naredba javlja
-- gresku "Can't DROP INDEX `barKod`" - to je u redu, znaci da je skripta
-- vec primenjena.
-- ═══════════════════════════════════════════════════════════════════

-- proizvodi.barKod je jedinstven za svaki proizvod, ali je bio obican
-- indeks. Kao primarni kljuc baza garantuje da nema duplikata.
ALTER TABLE `proizvodi`
  ADD PRIMARY KEY (`barKod`),
  DROP INDEX `barKod`;

-- Prijava: WHERE email = ? AND lozinka = ?
ALTER TABLE `korisnici`      ADD INDEX IF NOT EXISTS `idx_email` (`email`);
ALTER TABLE `administratori` ADD INDEX IF NOT EXISTS `idx_email` (`email`);
-- (nutricionisti.email vec ima UNIQUE indeks)

-- "Moji racuni" (WHERE idKorisnika = ?), obracun provizije (WHERE status = 'placen')
ALTER TABLE `racuni`
  ADD INDEX IF NOT EXISTS `idx_korisnik` (`idKorisnika`),
  ADD INDEX IF NOT EXISTS `idx_nutricionista` (`idNutricioniste`),
  ADD INDEX IF NOT EXISTS `idx_status` (`status`);

-- Admin filter zahteva po statusu
ALTER TABLE `zahtevi_za_saradnju`
  ADD INDEX IF NOT EXISTS `idx_status` (`status`);

-- "Pregled skeniranih": indeks pokriva ceo podupit (idKorisnika + barKod)
ALTER TABLE `skenirani_proizvodi`
  ADD INDEX IF NOT EXISTS `idx_korisnik_barkod` (`idKorisnika`, `barKod`);

-- ───────────────────────────────────────────────────────────────────
-- NAPOMENA (nije deo skripte): u tabeli korisnici email
-- 'nemanja@gmail.com' postoji dva puta. Aplikacija pri registraciji
-- proverava duplikate, ali baza to ne garantuje. Kada obrises duplikat,
-- mozes da pokrenes i:
--
--   ALTER TABLE `korisnici` DROP INDEX `idx_email`, ADD UNIQUE INDEX `uq_email` (`email`);
-- ───────────────────────────────────────────────────────────────────

# HealthyLife – unapređenje aplikacije

## API verzija (grana `api-verzija`, folder `HealthyLifeApp-api`)

Eksperimentalna verzija u kojoj se **podaci o proizvodima uzimaju sa Open Food Facts**
(besplatna javna baza proizvoda, openfoodfacts.org), a ne iz naše baze.

- **Pretraga po bar-kodu ili nazivu** (npr. „plazma"), uz opciju „samo proizvodi iz Srbije"
- prikaz: slika, nutritivne vrednosti, sastojci, **alergeni**, **Nutri-Score (A–E)**, **NOVA** (stepen obrade)
- posno (vegansko) i halal kada postoji podatak; inače piše „nepoznato"
- **radi bez prijave** („Pretraži proizvode" na početnoj) – tada nije potreban ni XAMPP
- prijava/registracija i dalje koriste našu bazu (tabela `korisnici`)
- za sada **nema** administratora ni nutricionista (ostali su u granama `unapredjenje` i `main`)
- potreban je internet; pretraga ide u pozadini, pa se prozor ne zamrzava

Kod: `src/api/OpenFoodFacts.java` (pozivi API-ja), `src/api/Json.java` (čitanje odgovora),
`src/view/KorisnikPanel.java` (ekran pretrage). Nisu potrebne dodatne biblioteke.

U Eclipse-u: **File → Switch Workspace → Other...** → folder `HealthyLifeApp-api`, pa
**File → Import → Existing Projects into Workspace** → izaberi isti folder (samo prvi put).

## Verzije

| Verzija | Gde je | Opis |
|---|---|---|
| Originalna | grana `main`, oznaka `v1-original` | Aplikacija pre izmena |
| Unapređena | grana `unapredjenje` | Optimizacija baze i koda, novi dizajn |

Obe verzije rade sa **istom bazom** (`healthylifebaza1`) i ne smetaju jedna drugoj.

### Gde je koja verzija na disku

Svaka verzija ima svoj folder, pa nije potrebno prebacivati se između grana:

| Folder | Verzija |
|---|---|
| `softversko inzenjerstvo\HealthyLifeApp` | unapređena (`unapredjenje`) |
| `softversko inzenjerstvo\HealthyLifeApp-original` | originalna (`v1-original`) |

U Eclipse-u: **File → Switch Workspace → Other...** i izaberi folder verzije koju želiš da pokreneš.
Pokreće se isto kao i do sada (`view.GUI`).

> Ne koristi `git checkout main` u folderu `HealthyLifeApp`: originalna verzija u Git-u čuva i
> Eclipse podešavanja (`.metadata`), pa bi prebacivanje tamo-vamo prepisivalo podešavanja radnog
> prostora. Zato postoji poseban folder za originalnu verziju (napravljen sa `git worktree`).
> Ako ti originalni folder više ne treba: `git worktree remove ../HealthyLifeApp-original`

### Pregled izmena

Na GitHub-u: **Commits** na grani `unapredjenje`. Svaki commit ima opis šta je i zašto promenjeno.
Ili u terminalu:

```
git log --oneline main..unapredjenje   # spisak izmena
git diff main unapredjenje --stat      # koji fajlovi su promenjeni
```

## Šta je urađeno

1. **`.gitignore`** – kompajlirani fajlovi (`bin/`) i Eclipse podešavanja (`.metadata/`) više nisu u Git-u.
2. **Sloj za bazu (`dao`)**
   - konekcije se uvek zatvaraju (i kada dođe do greške) – ranije su neke ostajale otvorene
   - brisanje/blokiranje/plaćanje rade u **transakciji** – nema polovično upisanih podataka
   - podešavanja baze su u `src/db.properties`, ne u kodu
   - ispravljen obračun provizije (gubile su se decimale)
3. **SQL skripta `baza/01_indeksi.sql`** – indeksi za brže pretrage (vidi dole)
4. **Novi dizajn i podela `GUI.java`** (3047 linija) na manje klase po ekranima

## Baza – šta treba da uradiš ti

Aplikacija radi i **bez** ovog koraka. Indeksi samo ubrzavaju pretragu kada bude više podataka.

1. phpMyAdmin → baza `healthylifebaza1` → **Export** → Go (rezervna kopija)
2. Kartica **SQL** → nalepi sadržaj fajla `baza/01_indeksi.sql` → **Go**

Skripta ne menja i ne briše podatke, a stara verzija aplikacije radi i posle nje.

## Ako tvoja baza ima lozinku

Napravi fajl `HealtyLife/db.local.properties` (ne šalje se na GitHub):

```
db.korisnik=root
db.lozinka=tvoja_lozinka
```

## Predlozi za sledeći korak

- **Heširanje lozinki** – sada se čuvaju kao običan tekst. Nije urađeno namerno: posle toga stara
  verzija ne bi mogla da prijavi korisnike, a obe verzije koriste istu bazu.
- **Duplikat u bazi** – email `nemanja@gmail.com` postoji dva puta u tabeli `korisnici`.

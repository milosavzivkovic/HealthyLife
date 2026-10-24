# HealthyLife – unapređenje aplikacije

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

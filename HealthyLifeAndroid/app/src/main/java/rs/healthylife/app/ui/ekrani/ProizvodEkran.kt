package rs.healthylife.app.ui.ekrani

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.healthylife.app.R
import rs.healthylife.app.data.Granice
import rs.healthylife.app.data.IstorijaStavka
import rs.healthylife.app.data.Nivo
import rs.healthylife.app.data.Podesavanja
import rs.healthylife.app.data.Proizvod
import rs.healthylife.app.data.broj
import rs.healthylife.app.data.licnaUpozorenja
import rs.healthylife.app.ui.StanjeProizvoda
import rs.healthylife.app.ui.komponente.Cip
import rs.healthylife.app.ui.komponente.Ikona
import rs.healthylife.app.ui.komponente.Kartica
import rs.healthylife.app.ui.komponente.NutriScoreSkala
import rs.healthylife.app.ui.komponente.OkruglaIkona
import rs.healthylife.app.ui.komponente.Poruka
import rs.healthylife.app.ui.komponente.SlikaProizvoda
import rs.healthylife.app.ui.komponente.Ucitavanje
import rs.healthylife.app.ui.komponente.opisProizvoda
import rs.healthylife.app.ui.theme.Boje
import kotlin.math.roundToInt

/** Ekran 4: svi podaci o proizvodu i licna upozorenja. */
@Composable
fun ProizvodEkran(
    stanje: StanjeProizvoda,
    podesavanja: Podesavanja,
    omiljeni: List<IstorijaStavka>,
    onNazad: () -> Unit,
    onPonovo: (String) -> Unit,
    onOmiljeni: (Proizvod) -> Unit,
    onProfil: () -> Unit,
) {
    when (stanje) {
        is StanjeProizvoda.Ucitan -> SadrzajProizvoda(
            p = stanje.proizvod,
            podesavanja = podesavanja,
            omiljen = omiljeni.any { it.barKod == stanje.proizvod.barKod },
            onNazad = onNazad,
            onOmiljeni = { onOmiljeni(stanje.proizvod) },
            onProfil = onProfil,
        )
        else -> Column(Modifier.fillMaxSize().statusBarsPadding()) {
            IconButton(onClick = onNazad, modifier = Modifier.padding(8.dp)) { Ikona(R.drawable.ic_nazad, Boje.Tekst, "Nazad") }
            when (stanje) {
                StanjeProizvoda.Ucitavanje -> Ucitavanje("Učitavam proizvod…")
                is StanjeProizvoda.NijePronadjen -> Poruka(
                    "Proizvod nije pronađen",
                    "Bar-kod ${stanje.barKod} nije u Open Food Facts. Bazu popunjavaju korisnici, pa neki " +
                        "(naročito domaći) proizvodi još nisu uneti. Probaj pretragu po nazivu.",
                    "Nazad", onNazad,
                )
                is StanjeProizvoda.Greska -> Poruka("Nije uspelo učitavanje", stanje.poruka, "Pokušaj ponovo") { onPonovo(stanje.barKod) }
                else -> Poruka("Nema izabranog proizvoda", "Skeniraj bar-kod ili pretraži proizvod po nazivu.")
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SadrzajProizvoda(
    p: Proizvod,
    podesavanja: Podesavanja,
    omiljen: Boolean,
    onNazad: () -> Unit,
    onOmiljeni: () -> Unit,
    onProfil: () -> Unit,
) {
    Column(Modifier.fillMaxSize().verticalScroll(rememberScrollState())) {

        // ── Fotografija sa dugmadima "nazad" i "omiljeni" ──
        Box(Modifier.fillMaxWidth().background(Boje.Ivica)) {
            SlikaProizvoda(
                url = p.slikaUrl,
                opis = "Fotografija proizvoda ${p.naziv}",
                modifier = Modifier.fillMaxWidth().statusBarsPadding().height(270.dp),
            )
            Row(
                Modifier.fillMaxWidth().statusBarsPadding().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                OkruglaIkona(R.drawable.ic_nazad, "Nazad", onClick = onNazad)
                OkruglaIkona(
                    if (omiljen) R.drawable.ic_srce_puno else R.drawable.ic_srce,
                    if (omiljen) "Ukloni iz omiljenih" else "Dodaj u omiljene",
                    boja = if (omiljen) Color(0xFFDC2626) else Boje.Tekst,
                    onClick = onOmiljeni,
                )
            }
        }

        Column(
            modifier = Modifier
                .offset(y = (-26).dp)
                .clip(RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp))
                .background(Boje.Pozadina)
                .padding(start = 20.dp, end = 20.dp, top = 24.dp, bottom = 8.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(p.naziv, fontSize = 28.sp, fontWeight = FontWeight.ExtraBold, lineHeight = 34.sp)
                val opis = opisProizvoda(p.brend, p.kolicina)
                if (opis.isNotEmpty()) Text(opis, fontSize = 15.sp, color = Boje.TekstLabele)
                Text("Bar-kod ${p.barKod}", fontSize = 13.sp, color = Boje.TekstSporedni)
            }

            // ── Licna upozorenja (moji alergeni, post, palmino ulje) ──
            licnaUpozorenja(p, podesavanja).forEach { u ->
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(Boje.UpozorenjePozadina)
                        .border(1.dp, Boje.UpozorenjeIvica, RoundedCornerShape(16.dp))
                        .clickable(role = Role.Button, onClick = onProfil)
                        .padding(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Ikona(R.drawable.ic_upozorenje, Boje.UpozorenjeIkona, velicina = 22.dp)
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        Text(u.naslov, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = Boje.UpozorenjeTekst)
                        Text(u.opis, fontSize = 13.sp, lineHeight = 18.sp, color = Boje.UpozorenjeTekst)
                    }
                }
            }

            // ── Nutri-Score i NOVA ──
            Row(Modifier.height(IntrinsicSize.Min), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Kartica(Modifier.weight(1f).fillMaxHeight()) {
                    Text("Nutri-Score", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Boje.TekstLabele)
                    NutriScoreSkala(p.nutriScore)
                    Text(opisNutriScore(p.nutriScore), fontSize = 12.sp, color = Boje.TekstSporedni)
                }
                Kartica(Modifier.weight(1f).fillMaxHeight()) {
                    Text("Stepen obrade (NOVA)", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = Boje.TekstLabele)
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        val n = p.nova
                        Box(
                            Modifier.size(38.dp).clip(RoundedCornerShape(9.dp))
                                .background(if (n == null) Boje.Siva else Boje.Nova[n - 1]),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(n?.toString() ?: "?", color = if (n == null) Boje.TekstSporedni else Color.White,
                                fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                        }
                        Text(opisNova(n), fontSize = 13.sp, fontWeight = FontWeight.SemiBold, lineHeight = 17.sp,
                            color = if (n == null) Boje.TekstSporedni else Boje.Nova[n - 1])
                    }
                    Text("Skala 1 – 4", fontSize = 12.sp, color = Boje.TekstSporedni)
                }
            }

            // ── Oznake ──
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                when (p.posno) {
                    true -> Cip("Posno (vegansko)", Boje.ZelenaSvetla, Boje.ZelenaTamna)
                    false -> Cip("Nije posno")
                    null -> Cip("Posno: nepoznato")
                }
                if (p.halal == true) Cip("Halal", Boje.ZelenaSvetla, Boje.ZelenaTamna) else Cip("Halal: nema oznake")
                when (p.palminoUlje) {
                    true -> Cip("Sadrži palmino ulje")
                    false -> Cip("Bez palminog ulja", Boje.ZelenaSvetla, Boje.ZelenaTamna)
                    null -> Unit
                }
            }

            // ── Nutritivne vrednosti ──
            Kartica {
                Text(
                    buildAnnotatedString {
                        append("Nutritivne vrednosti ")
                        withStyle(SpanStyle(fontWeight = FontWeight.Medium, color = Boje.TekstSporedni)) { append("na 100 g") }
                    },
                    fontSize = 17.sp, fontWeight = FontWeight.Bold,
                )
                Column {
                    RedVrednosti("Energija", energija(p), null)
                    RedVrednosti("Masti", gram(p.masti), Granice.masti(p.masti))
                    RedVrednosti("od toga zasićene", gram(p.zasiceneMasti), Granice.zasiceneMasti(p.zasiceneMasti), podstavka = true)
                    RedVrednosti("Ugljeni hidrati", gram(p.ugljeniHidrati), null)
                    RedVrednosti("od toga šećeri", gram(p.seceri), Granice.seceri(p.seceri), podstavka = true)
                    RedVrednosti("Vlakna", gram(p.vlakna), null)
                    RedVrednosti("Proteini", gram(p.proteini), null)
                    RedVrednosti("So", gram(p.so), Granice.so(p.so), poslednji = true)
                }
                Text(
                    "Nivoi (nisko / srednje / visoko) po preporukama za označavanje hrane, na 100 g.",
                    fontSize = 12.sp, lineHeight = 17.sp, color = Boje.TekstSporedni,
                )
            }

            // ── Alergeni ──
            Kartica {
                Text("Alergeni", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                val svi = p.alergeni.map { it.naziv } + p.ostaliAlergeni.map { it.replaceFirstChar(Char::uppercase) }
                if (svi.isEmpty()) {
                    Text("Nema unetih alergena (to ne znači da ih proizvod sigurno nema).", fontSize = 14.sp, color = Boje.TekstSporedni)
                } else {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        svi.forEach { Cip(it, Boje.AlergenPozadina, Boje.AlergenTekst) }
                    }
                }
            }

            // ── Sastojci ──
            Kartica {
                Text("Sastojci", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                val sastojci = p.sastojci
                if (sastojci == null) {
                    Text("Sastojci nisu uneti u Open Food Facts za ovaj proizvod.", fontSize = 14.sp, color = Boje.TekstSporedni)
                } else {
                    var sve by rememberSaveable(p.barKod) { mutableStateOf(false) }
                    Text(
                        sastojci, fontSize = 14.sp, lineHeight = 21.sp, color = Boje.TekstLabele,
                        maxLines = if (sve) Int.MAX_VALUE else 4, overflow = TextOverflow.Ellipsis,
                    )
                    if (sastojci.length > 180) {
                        TextButton(onClick = { sve = !sve }, modifier = Modifier.heightIn(min = 44.dp)) {
                            Text(if (sve) "Prikaži manje" else "Prikaži ceo tekst", color = Boje.Zelena, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Text(
                "Izvor: Open Food Facts. Podatke unose korisnici širom sveta i mogu biti nepotpuni – " +
                    "uvek proveri deklaraciju na pakovanju.",
                fontSize = 12.sp, lineHeight = 18.sp, color = Boje.TekstSporedni,
            )
        }
    }
}

@Composable
private fun RedVrednosti(naziv: String, vrednost: String?, nivo: Nivo?, podstavka: Boolean = false, poslednji: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().heightIn(min = 46.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Text(
            naziv,
            fontSize = if (podstavka) 14.sp else 15.sp,
            color = if (podstavka) Boje.TekstSporedni else Boje.Tekst,
            modifier = Modifier.weight(1f).padding(start = if (podstavka) 14.dp else 0.dp),
        )
        if (nivo != null) {
            val (pozadina, boja) = when (nivo) {
                Nivo.NISKO -> Boje.NiskoPozadina to Boje.NiskoTekst
                Nivo.SREDNJE -> Boje.SrednjePozadina to Boje.SrednjeTekst
                Nivo.VISOKO -> Boje.VisokoPozadina to Boje.VisokoTekst
            }
            Text(
                nivo.naziv, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = boja,
                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(pozadina).padding(horizontal = 9.dp, vertical = 3.dp),
            )
        }
        Text(
            vrednost ?: "nepoznato",
            fontSize = if (vrednost == null) 13.sp else 15.sp,
            fontWeight = if (vrednost == null) FontWeight.Normal else FontWeight.Bold,
            fontStyle = if (vrednost == null) FontStyle.Italic else FontStyle.Normal,
            color = if (vrednost == null) Boje.TekstSporedni else Boje.Tekst,
        )
    }
    if (!poslednji) HorizontalDivider(color = Boje.Siva)
}

private fun gram(v: Double?): String? = v?.let { broj(it) + " g" }

private fun energija(p: Proizvod): String? =
    if (p.energijaKj == null && p.energijaKcal == null) null
    else "${p.energijaKj?.roundToInt() ?: "?"} kJ / ${p.energijaKcal?.roundToInt() ?: "?"} kcal"

private fun opisNutriScore(ocena: String?) = when (ocena) {
    "a" -> "Najviša ocena kvaliteta"
    "b" -> "Dobar izbor"
    "c" -> "Srednja ocena"
    "d" -> "Slabija ocena"
    "e" -> "Najniža ocena kvaliteta"
    else -> "Podatak ne postoji"
}

private fun opisNova(n: Int?) = when (n) {
    1 -> "Neprerađena\nhrana"
    2 -> "Kulinarski\nsastojci"
    3 -> "Prerađena\nhrana"
    4 -> "Ultra-\nprerađena"
    else -> "Nepoznato"
}

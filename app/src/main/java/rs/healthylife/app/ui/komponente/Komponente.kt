package rs.healthylife.app.ui.komponente

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.SubcomposeAsyncImage
import rs.healthylife.app.R
import rs.healthylife.app.ui.theme.Boje

// ═══════════════════════════════════════════════════════════
// NUTRI-SCORE
// ═══════════════════════════════════════════════════════════

/** Mala oznaka sa slovom A-E; "?" kada ocena nije izracunata. */
@Composable
fun NutriScoreZnak(ocena: String?, velicina: Dp = 32.dp) {
    val i = ocena?.firstOrNull()?.let { it - 'a' }?.takeIf { it in 0..4 }
    val (pozadina, slovo) = if (i == null) Boje.Siva to Boje.TekstSporedni else Boje.NutriScore[i]
    Box(
        modifier = Modifier
            .size(velicina)
            .clip(RoundedCornerShape(9.dp))
            .background(pozadina)
            .semantics {
                contentDescription = if (i == null) "Nutri-Score nije izračunat" else "Nutri-Score ${ocena.uppercase()}"
            },
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = if (i == null) "?" else ocena.uppercase(),
            color = slovo,
            fontWeight = FontWeight.ExtraBold,
            fontSize = (velicina.value / 2).sp,
        )
    }
}

/** Skala A-E na kojoj je istaknuta ocena proizvoda. */
@Composable
fun NutriScoreSkala(ocena: String?) {
    val aktivno = ocena?.firstOrNull()?.let { it - 'a' }?.takeIf { it in 0..4 }
    if (aktivno == null) {
        Box(
            Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Boje.Siva)
                .padding(horizontal = 12.dp, vertical = 9.dp)
        ) {
            Text("nije izračunat", fontSize = 13.sp, color = Boje.TekstSporedni)
        }
        return
    }
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        modifier = Modifier.semantics { contentDescription = "Nutri-Score ${ocena.uppercase()}, na skali od A do E" },
    ) {
        Boje.NutriScore.forEachIndexed { i, (pozadina, slovo) ->
            val akt = i == aktivno
            Box(
                modifier = Modifier
                    .size(width = if (akt) 34.dp else 22.dp, height = if (akt) 38.dp else 26.dp)
                    .clip(RoundedCornerShape(if (akt) 9.dp else 6.dp))
                    // Neaktivna slova su bleda (pomesana sa belom), kao na pakovanjima
                    .background(if (akt) pozadina else pozadina.copy(alpha = 0.45f).kompozitNaBelo()),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = ('A' + i).toString(),
                    color = if (akt) slovo else Boje.Tekst.copy(alpha = 0.6f),
                    fontWeight = if (akt) FontWeight.ExtraBold else FontWeight.Bold,
                    fontSize = if (akt) 20.sp else 12.sp,
                )
            }
        }
    }
}

/** Providna boja "naslikana" preko bele - da tekst na njoj ima stabilan kontrast. */
private fun Color.kompozitNaBelo(): Color =
    Color(red * alpha + (1 - alpha), green * alpha + (1 - alpha), blue * alpha + (1 - alpha))

// ═══════════════════════════════════════════════════════════
// PROIZVOD U LISTI
// ═══════════════════════════════════════════════════════════

/** Slika proizvoda sa Open Food Facts, ili siv okvir kada slike nema. */
@Composable
fun SlikaProizvoda(url: String?, modifier: Modifier = Modifier, opis: String? = null) {
    val okvir: @Composable () -> Unit = {
        Box(modifier.background(Boje.Siva), contentAlignment = Alignment.Center) {
            Text("slika", fontSize = 10.sp, color = Boje.TekstSporedni)
        }
    }
    if (url == null) {
        okvir()
    } else {
        SubcomposeAsyncImage(
            model = url,
            contentDescription = opis,
            contentScale = ContentScale.Fit,
            modifier = modifier.background(Color.White),
            loading = { okvir() },
            error = { okvir() },
        )
    }
}

/** Kartica proizvoda u listama (nedavno, rezultati, istorija). */
@Composable
fun ProizvodRed(
    naziv: String,
    opis: String,
    nutriScore: String?,
    slikaUrl: String?,
    onClick: () -> Unit,
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Boje.Povrsina)
            .border(1.dp, Boje.Ivica, RoundedCornerShape(16.dp))
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
    ) {
        SlikaProizvoda(slikaUrl, Modifier.size(50.dp).clip(RoundedCornerShape(12.dp)))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(naziv, fontSize = 15.sp, fontWeight = FontWeight.SemiBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (opis.isNotBlank()) {
                Text(opis, fontSize = 13.sp, color = Boje.TekstSporedni, maxLines = 1, overflow = TextOverflow.Ellipsis)
            }
        }
        NutriScoreZnak(nutriScore)
    }
}

/** "Bambi · 600 g" */
fun opisProizvoda(brend: String?, kolicina: String?): String = listOfNotNull(brend, kolicina).joinToString(" · ")

// ═══════════════════════════════════════════════════════════
// OSNOVNI ELEMENTI
// ═══════════════════════════════════════════════════════════

@Composable
fun Kartica(modifier: Modifier = Modifier, sadrzaj: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(18.dp))
            .background(Boje.Povrsina)
            .border(1.dp, Boje.Ivica, RoundedCornerShape(18.dp))
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        content = sadrzaj,
    )
}

@Composable
fun Cip(tekst: String, pozadina: Color = Boje.Siva, boja: Color = Boje.TekstLabele) {
    Text(
        text = tekst,
        color = boja,
        fontSize = 13.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(pozadina)
            .padding(horizontal = 12.dp, vertical = 7.dp),
    )
}

@Composable
fun Ikona(@DrawableRes id: Int, boja: Color, opis: String? = null, velicina: Dp = 24.dp) {
    Icon(painterResource(id), contentDescription = opis, tint = boja, modifier = Modifier.size(velicina))
}

/** Okruglo belo dugme sa ikonicom (preko fotografije proizvoda). */
@Composable
fun OkruglaIkona(@DrawableRes id: Int, opis: String, boja: Color = Boje.Tekst, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = Boje.Povrsina,
        modifier = Modifier.size(46.dp).shadow(4.dp, CircleShape),
    ) {
        Box(contentAlignment = Alignment.Center) { Ikona(id, boja, opis, 22.dp) }
    }
}

/** Polje za pretragu sa lupom; Enter na tastaturi pokrece pretragu. */
@Composable
fun PoljePretrage(
    vrednost: String,
    onPromena: (String) -> Unit,
    onTrazi: () -> Unit,
    modifier: Modifier = Modifier,
    uputstvo: String = "npr. plazma, jogurt, nutella",
    opis: String = "Pretraga po nazivu",
) {
    OutlinedTextField(
        value = vrednost,
        onValueChange = onPromena,
        modifier = modifier.semantics { contentDescription = opis },
        singleLine = true,
        placeholder = { Text(uputstvo, color = Boje.TekstSporedni) },
        leadingIcon = { Ikona(R.drawable.ic_pretraga, Boje.TekstSporedni, velicina = 20.dp) },
        shape = RoundedCornerShape(14.dp),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
        keyboardActions = KeyboardActions(onSearch = { onTrazi() }),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Boje.Povrsina,
            unfocusedContainerColor = Boje.Povrsina,
            unfocusedBorderColor = Boje.IvicaPolja,
            focusedBorderColor = Boje.Zelena,
        ),
    )
}

/** Krug koji se vrti i tekst ispod - dok podaci stizu sa interneta. */
@Composable
fun Ucitavanje(tekst: String) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        CircularProgressIndicator(color = Boje.Zelena)
        Spacer(Modifier.height(16.dp))
        Text(tekst, fontSize = 16.sp, fontWeight = FontWeight.SemiBold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(6.dp))
        Text("Podaci stižu sa Open Food Facts.", fontSize = 13.sp, color = Boje.TekstSporedni, textAlign = TextAlign.Center)
    }
}

/** Poruka preko celog ekrana (nije pronadjeno, greska...) sa opcionim dugmetom. */
@Composable
fun Poruka(naslov: String, opis: String, dugme: String? = null, onDugme: () -> Unit = {}) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(naslov, fontSize = 18.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text(opis, fontSize = 14.sp, color = Boje.TekstSporedni, textAlign = TextAlign.Center, lineHeight = 20.sp)
        if (dugme != null) {
            Spacer(Modifier.height(12.dp))
            TextButton(onClick = onDugme) { Text(dugme, fontWeight = FontWeight.SemiBold, color = Boje.Zelena) }
        }
    }
}

// ═══════════════════════════════════════════════════════════
// DONJI MENI
// ═══════════════════════════════════════════════════════════

/** Donji meni: Pocetna, istaknuto dugme za skeniranje u sredini, Istorija. */
@Composable
fun DonjiMeni(naPocetnoj: Boolean, onPocetna: () -> Unit, onSkeniraj: () -> Unit, onIstorija: () -> Unit) {
    // Box ne sece sadrzaj, pa okruglo dugme moze da "izviri" iznad trake (Surface bi ga odsekao).
    Box(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .shadow(8.dp, clip = false)
                .background(Boje.Povrsina)
                .navigationBarsPadding()
                .height(72.dp)
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            StavkaMenija("Početna", R.drawable.ic_pocetna, izabrana = naPocetnoj, onClick = onPocetna)
            Spacer(Modifier.size(60.dp))
            StavkaMenija("Istorija", R.drawable.ic_istorija, izabrana = !naPocetnoj, onClick = onIstorija)
        }
        Surface(
            onClick = onSkeniraj,
            shape = CircleShape,
            color = Boje.Zelena,
            shadowElevation = 6.dp,
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-18).dp)
                .size(60.dp)
                .semantics { contentDescription = "Skeniraj bar-kod" },
        ) {
            Box(contentAlignment = Alignment.Center) { Ikona(R.drawable.ic_skeniraj, Color.White, velicina = 28.dp) }
        }
    }
}

@Composable
private fun StavkaMenija(tekst: String, @DrawableRes ikona: Int, izabrana: Boolean, onClick: () -> Unit) {
    val boja = if (izabrana) Boje.Zelena else Boje.TekstSporedni
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier
            .widthIn(min = 72.dp)
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(vertical = 6.dp),
    ) {
        Box(
            Modifier
                .clip(RoundedCornerShape(14.dp))
                .background(if (izabrana) Boje.ZelenaSvetla else Color.Transparent)
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) { Ikona(ikona, boja, velicina = 22.dp) }
        Text(tekst, fontSize = 12.sp, color = boja, fontWeight = if (izabrana) FontWeight.SemiBold else FontWeight.Medium)
    }
}

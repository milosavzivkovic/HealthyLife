package rs.healthylife.app.ui.ekrani

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
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
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.selected
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.healthylife.app.data.IstorijaStavka
import rs.healthylife.app.ui.komponente.Poruka
import rs.healthylife.app.ui.komponente.ProizvodRed
import rs.healthylife.app.ui.komponente.opisProizvoda
import rs.healthylife.app.ui.theme.Boje
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId

/** Ekran 5: istorija pregledanih proizvoda (po danima) i omiljeni. */
@Composable
fun IstorijaEkran(
    istorija: List<IstorijaStavka>,
    omiljeni: List<IstorijaStavka>,
    onProizvod: (String) -> Unit,
    onObrisiIstoriju: () -> Unit,
) {
    var naOmiljenima by rememberSaveable { mutableStateOf(false) }
    var pitajZaBrisanje by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize().statusBarsPadding()) {
        Column(Modifier.padding(start = 20.dp, end = 12.dp, top = 24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Istorija", fontSize = 26.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                if (!naOmiljenima && istorija.isNotEmpty()) {
                    TextButton(onClick = { pitajZaBrisanje = true }) {
                        Text("Obriši sve", color = Boje.Crvena, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
            Row(
                modifier = Modifier
                    .padding(end = 8.dp)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Boje.Ivica)
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                TabDugme("Skenirano", izabrana = !naOmiljenima, Modifier.weight(1f)) { naOmiljenima = false }
                TabDugme("Omiljeni", izabrana = naOmiljenima, Modifier.weight(1f)) { naOmiljenima = true }
            }
        }

        val lista = if (naOmiljenima) omiljeni else istorija
        if (lista.isEmpty()) {
            Poruka(
                if (naOmiljenima) "Nemaš omiljene proizvode" else "Istorija je prazna",
                if (naOmiljenima) "Dodaj proizvod u omiljene dugmetom sa srcem na ekranu proizvoda."
                else "Proizvodi koje skeniraš ili otvoriš pojaviće se ovde.",
            )
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                if (naOmiljenima) {
                    items(lista) { s -> Red(s, onProizvod) }
                } else {
                    // Grupisanje po danima: Danas, Juce, Ranije
                    lista.groupBy { dan(it.vreme) }.forEach { (dan, stavke) ->
                        item {
                            Text(
                                dan.uppercase(), fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Boje.TekstSporedni,
                                letterSpacing = 0.4.sp, modifier = Modifier.padding(start = 4.dp, top = 6.dp),
                            )
                        }
                        items(stavke) { s -> Red(s, onProizvod) }
                    }
                }
            }
        }
    }

    if (pitajZaBrisanje) {
        AlertDialog(
            onDismissRequest = { pitajZaBrisanje = false },
            title = { Text("Obrisati istoriju?") },
            text = { Text("Biće obrisani svi pregledani proizvodi. Omiljeni ostaju.") },
            confirmButton = {
                TextButton(onClick = { pitajZaBrisanje = false; onObrisiIstoriju() }) {
                    Text("Obriši", color = Boje.Crvena, fontWeight = FontWeight.SemiBold)
                }
            },
            dismissButton = { TextButton(onClick = { pitajZaBrisanje = false }) { Text("Otkaži") } },
        )
    }
}

@Composable
private fun Red(s: IstorijaStavka, onProizvod: (String) -> Unit) =
    ProizvodRed(s.naziv, opisProizvoda(s.brend, s.kolicina), s.nutriScore, s.slikaUrl) { onProizvod(s.barKod) }

@Composable
private fun TabDugme(tekst: String, izabrana: Boolean, modifier: Modifier, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(11.dp),
        color = if (izabrana) Boje.Povrsina else Boje.Ivica,
        shadowElevation = if (izabrana) 1.dp else 0.dp,
        modifier = modifier
            .heightIn(min = 44.dp)
            .semantics {
                role = Role.Tab
                selected = izabrana
            },
    ) {
        Text(
            tekst, fontSize = 14.sp, fontWeight = FontWeight.SemiBold,
            color = if (izabrana) Boje.Tekst else Boje.TekstLabele,
            modifier = Modifier.padding(vertical = 12.dp),
            textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        )
    }
}

private fun dan(vreme: Long): String {
    val datum = Instant.ofEpochMilli(vreme).atZone(ZoneId.systemDefault()).toLocalDate()
    val danas = LocalDate.now()
    return when (datum) {
        danas -> "Danas"
        danas.minusDays(1) -> "Juče"
        else -> "Ranije"
    }
}

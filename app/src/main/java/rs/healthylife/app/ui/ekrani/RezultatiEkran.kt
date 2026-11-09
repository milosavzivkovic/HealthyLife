package rs.healthylife.app.ui.ekrani

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.healthylife.app.R
import rs.healthylife.app.data.Podesavanja
import rs.healthylife.app.ui.StanjePretrage
import rs.healthylife.app.ui.komponente.Ikona
import rs.healthylife.app.ui.komponente.PoljePretrage
import rs.healthylife.app.ui.komponente.Poruka
import rs.healthylife.app.ui.komponente.ProizvodRed
import rs.healthylife.app.ui.komponente.Ucitavanje
import rs.healthylife.app.ui.komponente.opisProizvoda
import rs.healthylife.app.ui.theme.Boje

/** Ekran 3: rezultati pretrage po nazivu, sa brzim filterima. */
@Composable
fun RezultatiEkran(
    stanje: StanjePretrage,
    podesavanja: Podesavanja,
    onNazad: () -> Unit,
    onTrazi: (String) -> Unit,
    onSamoSrbija: (Boolean) -> Unit,
    onProizvod: (String) -> Unit,
) {
    val trenutniUpit = when (stanje) {
        is StanjePretrage.Ucitavanje -> stanje.upit
        is StanjePretrage.Rezultati -> stanje.upit
        is StanjePretrage.Greska -> stanje.upit
        StanjePretrage.Prazno -> ""
    }
    var upit by rememberSaveable(trenutniUpit) { mutableStateOf(trenutniUpit) }
    var samoAB by rememberSaveable { mutableStateOf(false) }
    var bezMojihAlergena by rememberSaveable { mutableStateOf(false) }

    Column(Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(Boje.Povrsina)
                .statusBarsPadding()
                .padding(start = 8.dp, end = 16.dp, top = 12.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(onClick = onNazad) { Ikona(R.drawable.ic_nazad, Boje.Tekst, "Nazad") }
                PoljePretrage(upit, { upit = it }, { onTrazi(upit) }, Modifier.weight(1f), uputstvo = "Pretraži proizvode")
            }
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.horizontalScroll(rememberScrollState()).padding(start = 8.dp),
            ) {
                Filter("Samo Srbija", podesavanja.samoSrbija) { onSamoSrbija(!podesavanja.samoSrbija) }
                Filter("Nutri-Score A–B", samoAB) { samoAB = !samoAB }
                if (podesavanja.alergeni.isNotEmpty()) {
                    Filter("Bez mojih alergena", bezMojihAlergena) { bezMojihAlergena = !bezMojihAlergena }
                }
            }
        }
        HorizontalDivider(color = Boje.Ivica)

        when (stanje) {
            StanjePretrage.Prazno -> Poruka("Pretraži proizvode", "Unesi naziv proizvoda ili brenda, na primer \"plazma\".")
            is StanjePretrage.Ucitavanje -> Ucitavanje("Tražim \"${stanje.upit}\"…")
            is StanjePretrage.Greska -> Poruka("Pretraga nije uspela", stanje.poruka, "Pokušaj ponovo") { onTrazi(stanje.upit) }
            is StanjePretrage.Rezultati -> {
                // Filteri "A-B" i "bez mojih alergena" rade na telefonu, nad vec ucitanim rezultatima
                val prikaz = stanje.lista.filter { r ->
                    (!samoAB || r.nutriScore == "a" || r.nutriScore == "b") &&
                        (!bezMojihAlergena || r.alergeni.none { it in podesavanja.alergeni })
                }
                if (prikaz.isEmpty()) {
                    Poruka(
                        "Nema rezultata",
                        if (stanje.lista.isEmpty()) {
                            "Za \"${stanje.upit}\" nema proizvoda" + (if (podesavanja.samoSrbija) " iz Srbije. Probaj bez filtera \"Samo Srbija\"." else ".")
                        } else {
                            "Nijedan od ${stanje.lista.size} proizvoda ne prolazi izabrane filtere."
                        },
                    )
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        item {
                            Text(
                                "${prikaz.size} proizvoda · izvor: Open Food Facts",
                                fontSize = 14.sp, color = Boje.TekstSporedni,
                                modifier = Modifier.padding(horizontal = 4.dp),
                            )
                        }
                        // Bez "key": Open Food Facts ponekad vrati isti bar-kod dva puta
                        items(prikaz) { r ->
                            ProizvodRed(r.naziv, opisProizvoda(r.brend, r.kolicina), r.nutriScore, r.slikaUrl) { onProizvod(r.barKod) }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Filter(tekst: String, izabran: Boolean, onClick: () -> Unit) {
    FilterChip(
        selected = izabran,
        onClick = onClick,
        label = { Text(tekst, fontWeight = FontWeight.SemiBold) },
        leadingIcon = if (izabran) {
            { Ikona(R.drawable.ic_kvacica, Boje.ZelenaTamna, velicina = 16.dp) }
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = Boje.ZelenaSvetla,
            selectedLabelColor = Boje.ZelenaTamna,
        ),
    )
}

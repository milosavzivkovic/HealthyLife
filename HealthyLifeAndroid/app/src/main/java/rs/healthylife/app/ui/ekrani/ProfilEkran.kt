package rs.healthylife.app.ui.ekrani

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import rs.healthylife.app.R
import rs.healthylife.app.data.Alergen
import rs.healthylife.app.data.Podesavanja
import rs.healthylife.app.ui.komponente.Ikona
import rs.healthylife.app.ui.komponente.Kartica
import rs.healthylife.app.ui.theme.Boje

/** Ekran 6: moji alergeni i podesavanja ishrane (cuvaju se samo na telefonu). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfilEkran(
    podesavanja: Podesavanja,
    onNazad: () -> Unit,
    onAlergen: (Alergen) -> Unit,
    onPostim: (Boolean) -> Unit,
    onPalmino: (Boolean) -> Unit,
    onSamoSrbija: (Boolean) -> Unit,
) {
    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(bottom = 24.dp)
    ) {
        Row(Modifier.padding(start = 8.dp, top = 12.dp, end = 16.dp), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onNazad) { Ikona(R.drawable.ic_nazad, Boje.Tekst, "Nazad") }
            Text("Moj profil", fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
        }

        Column(Modifier.padding(horizontal = 20.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            Kartica {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text("Moji alergeni", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                    Text("Upozorićemo te kada proizvod sadrži nešto od označenog.", fontSize = 13.sp, color = Boje.TekstSporedni)
                }
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(0.dp)) {
                    Alergen.entries.forEach { a ->
                        val izabran = a in podesavanja.alergeni
                        FilterChip(
                            selected = izabran,
                            onClick = { onAlergen(a) },
                            label = { Text(a.naziv, fontWeight = FontWeight.SemiBold) },
                            leadingIcon = if (izabran) {
                                { Ikona(R.drawable.ic_kvacica, Boje.ZelenaTamna, velicina = 16.dp) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Boje.ZelenaSvetla,
                                selectedLabelColor = Boje.ZelenaTamna,
                            ),
                        )
                    }
                }
            }

            Kartica {
                Text("Ishrana i pretraga", fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Column {
                    Prekidac("Postim – upozori na proizvode koji nisu posni", podesavanja.postim, onPostim)
                    HorizontalDivider(color = Boje.Siva)
                    Prekidac("Izbegavam palmino ulje", podesavanja.izbegavamPalmino, onPalmino)
                    HorizontalDivider(color = Boje.Siva)
                    Prekidac("U pretrazi prikaži samo proizvode iz Srbije", podesavanja.samoSrbija, onSamoSrbija)
                }
            }

            Column(
                Modifier.fillMaxWidth().clip(RoundedCornerShape(18.dp)).background(Boje.Siva).padding(horizontal = 18.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text("Podešavanja se čuvaju samo na ovom telefonu.", fontSize = 14.sp, fontWeight = FontWeight.SemiBold, color = Boje.TekstLabele)
                Text("Nalog i prijava stižu u sledećoj verziji, zajedno sa nutricionistima.", fontSize = 13.sp, color = Boje.TekstLabele)
            }

            Text(
                "Podaci o proizvodima: Open Food Facts (openfoodfacts.org), licenca ODbL · Healthy Life 1.0",
                fontSize = 12.sp, lineHeight = 18.sp, color = Boje.TekstSporedni,
            )
        }
    }
}

@Composable
private fun Prekidac(tekst: String, ukljuceno: Boolean, onPromena: (Boolean) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 56.dp)
            .toggleable(value = ukljuceno, role = Role.Switch, onValueChange = onPromena),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(tekst, fontSize = 15.sp, modifier = Modifier.weight(1f))
        Switch(
            checked = ukljuceno,
            onCheckedChange = null,
            colors = SwitchDefaults.colors(checkedTrackColor = Boje.Zelena),
        )
    }
}

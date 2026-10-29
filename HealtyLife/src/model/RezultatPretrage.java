package model;

/** Jedan rezultat pretrage po nazivu (skraceni podaci, detalji se ucitavaju klikom). */
public record RezultatPretrage(String barKod, String naziv, String brend, String kolicina, String nutriScore) {
}

import java.time.LocalDate;

/** Deník: vlastní obousměrný spojový seznam dnů. */
public class Denik {
    private Den zacatek;
    private Den konec;
    private Den aktualni;
    private int pocetDnu;

    public boolean jePrazdny() {
        return aktualni == null;
    }

    public int getPocetDnu() {
        return pocetDnu;
    }

    public Den getAktualni() {
        return aktualni;
    }

    public Den getZacatek() {
        return zacatek;
    }

    // ---- navigace ----

    public void predchozi() {
        if (aktualni != null && aktualni.predchozi != null) {
            aktualni = aktualni.predchozi;
        }
    }

    public void dalsi() {
        if (aktualni != null && aktualni.dalsi != null) {
            aktualni = aktualni.dalsi;
        }
    }

    public void zacatekDen() {
        aktualni = zacatek;
    }

    public void konecDen() {
        aktualni = konec;
    }

    // ---- přidávání ----

    /**
     * Přidá záznam podle pravidel zadání.
     * @return true při úspěchu, false pokud je datum odmítnuto
     */
    public boolean pridejZaznam(LocalDate datum, String text) {
        Zaznam zaznam = new Zaznam(text);

        if (aktualni == null) {
            Den den = new Den(datum);
            den.getZaznamy().pridej(zaznam);
            zacatek = den;
            konec = den;
            aktualni = den;
            pocetDnu = 1;
            return true;
        }

        if (datum.equals(aktualni.getDatum())) {
            aktualni.getZaznamy().pridej(zaznam);
            return true;
        }

        Den nasledujici = aktualni.dalsi;
        boolean poAktualnim = datum.isAfter(aktualni.getDatum());
        boolean predNasledujicim = nasledujici == null || datum.isBefore(nasledujici.getDatum());

        if (poAktualnim && predNasledujicim) {
            Den novy = new Den(datum);
            novy.getZaznamy().pridej(zaznam);
            novy.predchozi = aktualni;
            novy.dalsi = nasledujici;
            if (nasledujici != null) {
                nasledujici.predchozi = novy;
            } else {
                konec = novy;
            }
            aktualni.dalsi = novy;
            aktualni = novy;
            pocetDnu++;
            return true;
        }
        return false;
    }

    /** Pro načítání ze souboru: přidá nový den na konec (datum musí být po posledním dni). */
    public Den pridejDenNaKonec(LocalDate datum) {
        if (konec != null && !datum.isAfter(konec.getDatum())) {
            throw new IllegalArgumentException("Dny v souboru nejsou vzestupně seřazené: " + datum.format(Den.FORMAT));
        }
        Den novy = new Den(datum);
        if (konec == null) {
            zacatek = novy;
            aktualni = novy;
        } else {
            konec.dalsi = novy;
            novy.predchozi = konec;
        }
        konec = novy;
        pocetDnu++;
        return novy;
    }

    // ---- mazání ----

    /**
     * Smaže záznam (index od 0) aktuálního dne. Prázdný den zmizí;
     * aktuální se pak stane následující, jinak předchozí.
     */
    public void smazZaznam(int index) {
        aktualni.getZaznamy().odeber(index);
        if (aktualni.getZaznamy().jePrazdny()) {
            odeberAktualniDen();
        }
    }

    private void odeberAktualniDen() {
        Den den = aktualni;
        if (den.predchozi != null) {
            den.predchozi.dalsi = den.dalsi;
        } else {
            zacatek = den.dalsi;
        }
        if (den.dalsi != null) {
            den.dalsi.predchozi = den.predchozi;
        } else {
            konec = den.predchozi;
        }
        aktualni = (den.dalsi != null) ? den.dalsi : den.predchozi;
        pocetDnu--;
    }
}
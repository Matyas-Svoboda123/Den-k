/**
 * Vlastní seznam (List) postavený nad polem pevné velikosti.
 * Když se pole zaplní, vytvoří se větší a prvky se ručně zkopírují.
 */
public class SeznamZaznamu {
    private static final int VYCHOZI_KAPACITA = 4;

    private Zaznam[] pole = new Zaznam[VYCHOZI_KAPACITA];
    private int pocet = 0;

    /** Přidá záznam na konec. */
    public void pridej(Zaznam zaznam) {
        if (pocet == pole.length) {
            zvetsiPole();
        }
        pole[pocet] = zaznam;
        pocet++;
    }

    /** Vrátí záznam na indexu (od 0). */
    public Zaznam get(int index) {
        zkontrolujIndex(index);
        return pole[index];
    }

    /** Odebere záznam na indexu (od 0), ostatní posune doleva, vrátí odebraný. */
    public Zaznam odeber(int index) {
        zkontrolujIndex(index);
        Zaznam odebrany = pole[index];
        for (int i = index; i < pocet - 1; i++) {
            pole[i] = pole[i + 1];
        }
        pole[pocet - 1] = null;
        pocet--;
        return odebrany;
    }

    public int velikost() {
        return pocet;
    }

    public boolean jePrazdny() {
        return pocet == 0;
    }

    private void zvetsiPole() {
        Zaznam[] nove = new Zaznam[pole.length * 2];
        for (int i = 0; i < pocet; i++) {
            nove[i] = pole[i];
        }
        pole = nove;
    }

    private void zkontrolujIndex(int index) {
        if (index < 0 || index >= pocet) {
            throw new IndexOutOfBoundsException("Index " + index + ", velikost " + pocet);
        }
    }
}
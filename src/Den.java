import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;

/** Uzel obousměrného spojového seznamu dnů: datum + seznam záznamů. */
public class Den {
    public static final DateTimeFormatter FORMAT =
            DateTimeFormatter.ofPattern("dd.MM.uuuu").withResolverStyle(ResolverStyle.STRICT);

    private final LocalDate datum;
    private final SeznamZaznamu zaznamy = new SeznamZaznamu();
    Den dalsi;
    Den predchozi;

    public Den(LocalDate datum) {
        this.datum = datum;
    }

    public LocalDate getDatum() {
        return datum;
    }

    public SeznamZaznamu getZaznamy() {
        return zaznamy;
    }

    public Den getDalsi() {
        return dalsi;
    }

    public Den getPredchozi() {
        return predchozi;
    }
}
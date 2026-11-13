import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Textový formát:
 *   DEN dd.MM.yyyy
 *   ZAZNAM
 *   > řádek textu      (každý řádek záznamu má prefix "> ", takže se nemůže splést s řídicími řádky)
 *   KONEC
 */
public class DenikUloziste {

    public static void uloz(Denik denik, Path soubor) throws IOException {
        try (BufferedWriter out = Files.newBufferedWriter(soubor, StandardCharsets.UTF_8)) {
            for (Den den = denik.getZacatek(); den != null; den = den.getDalsi()) {
                out.write("DEN " + den.getDatum().format(Den.FORMAT));
                out.newLine();
                SeznamZaznamu zaznamy = den.getZaznamy();
                for (int i = 0; i < zaznamy.velikost(); i++) {
                    out.write("ZAZNAM");
                    out.newLine();
                    for (String radek : zaznamy.get(i).getText().split("\n", -1)) {
                        out.write("> " + radek);
                        out.newLine();
                    }
                    out.write("KONEC");
                    out.newLine();
                }
            }
        }
    }

    /** Načte nový deník; při jakékoli chybě vyhodí výjimku a nic se nepřepíše. */
    public static Denik nacti(Path soubor) throws IOException {
        Denik denik = new Denik();
        try (BufferedReader in = Files.newBufferedReader(soubor, StandardCharsets.UTF_8)) {
            Den den = null;
            StringBuilder text = null;
            boolean prvniRadek = true;
            String radek;
            int cislo = 0;
            while ((radek = in.readLine()) != null) {
                cislo++;
                if (text != null) {
                    if (radek.equals("KONEC")) {
                        den.getZaznamy().pridej(new Zaznam(text.toString()));
                        text = null;
                    } else if (radek.startsWith("> ")) {
                        if (!prvniRadek) {
                            text.append('\n');
                        }
                        text.append(radek.substring(2));
                        prvniRadek = false;
                    } else {
                        throw chyba(cislo, "očekáván řádek záznamu nebo KONEC");
                    }
                } else if (radek.startsWith("DEN ")) {
                    if (den != null && den.getZaznamy().jePrazdny()) {
                        throw chyba(cislo, "předchozí den nemá žádný záznam");
                    }
                    try {
                        den = denik.pridejDenNaKonec(LocalDate.parse(radek.substring(4), Den.FORMAT));
                    } catch (DateTimeParseException e) {
                        throw chyba(cislo, "neplatné datum");
                    } catch (IllegalArgumentException e) {
                        throw chyba(cislo, e.getMessage());
                    }
                } else if (radek.equals("ZAZNAM")) {
                    if (den == null) {
                        throw chyba(cislo, "záznam mimo den");
                    }
                    text = new StringBuilder();
                    prvniRadek = true;
                } else if (!radek.isBlank()) {
                    throw chyba(cislo, "neznámý řádek");
                }
            }
            if (text != null) {
                throw new IOException("Soubor končí uprostřed záznamu");
            }
            if (den != null && den.getZaznamy().jePrazdny()) {
                throw new IOException("Poslední den nemá žádný záznam");
            }
        }
        return denik;
    }

    private static IOException chyba(int cislo, String zprava) {
        return new IOException("Chyba v souboru na řádku " + cislo + ": " + zprava);
    }
}
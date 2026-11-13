import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class Main {
    private static final String VYCHOZI_SOUBOR = "denik.txt";
    private static final Scanner in = new Scanner(System.in);
    private static Denik denik = new Denik();

    public static void main(String[] args) {
        boolean bezi = true;
        while (bezi) {
            vypisObrazovku();
            System.out.print("> ");
            if (!in.hasNextLine()) {
                break;
            }
            String prikaz = in.nextLine().trim().toLowerCase();
            switch (prikaz) {
                case "predchozi" -> denik.predchozi();
                case "dalsi" -> denik.dalsi();
                case "zacatek" -> denik.zacatekDen();
                case "konec" -> denik.konecDen();
                case "novy" -> novyZaznam();
                case "smaz" -> smazZaznam();
                case "uloz" -> uloz();
                case "nacti" -> nacti();
                case "zavri" -> bezi = false;
                default -> System.out.println("Neznámá operace: " + prikaz);
            }
        }
        System.out.println("Na shledanou.");
    }

    private static void vypisObrazovku() {
        System.out.println();
        System.out.println("==================== DENÍK ====================");
        if (denik.jePrazdny()) {
            System.out.println("Deník je prázdný. Přidej záznam příkazem 'novy'.");
            System.out.println("Počet dnů: 0");
        } else {
            Den den = denik.getAktualni();
            SeznamZaznamu zaznamy = den.getZaznamy();
            System.out.println("Den: " + den.getDatum().format(Den.FORMAT));
            for (int i = 0; i < zaznamy.velikost(); i++) {
                System.out.println(" " + (i + 1) + ". " + odsad(zaznamy.get(i).getText()));
            }
            System.out.println("Počet dnů: " + denik.getPocetDnu()
                    + " | Záznamů v tomto dni: " + zaznamy.velikost());
        }
        System.out.println("Operace: predchozi, dalsi, zacatek, konec, novy, smaz, uloz, nacti, zavri");
    }

    /** Další řádky víceřádkového záznamu se odsadí pod číslo. */
    private static String odsad(String text) {
        return text.replace("\n", "\n    ");
    }

    private static void novyZaznam() {
        LocalDate datum = nactiDatum();
        if (datum == null) {
            return;
        }
        String text = nactiText();
        if (text == null) {
            System.out.println("Prázdný záznam se neukládá.");
            return;
        }
        if (denik.pridejZaznam(datum, text)) {
            System.out.println("Záznam přidán.");
        } else {
            System.out.println("Zápis odmítnut: datum musí být aktuální den, "
                    + "nebo ležet mezi aktuálním a následujícím dnem.");
        }
    }

    /** Opakuje dotaz při špatném formátu; prázdný vstup = zrušení. */
    private static LocalDate nactiDatum() {
        while (true) {
            System.out.print("Datum (dd.MM.yyyy, prázdné = zrušit): ");
            String vstup = in.nextLine().trim();
            if (vstup.isEmpty()) {
                return null;
            }
            try {
                return LocalDate.parse(vstup, Den.FORMAT);
            } catch (DateTimeParseException e) {
                System.out.println("Špatný formát data, zkus to znovu.");
            }
        }
    }

    /** Text končí řádkem obsahujícím jen tečku. Vrací null, pokud je text prázdný. */
    private static String nactiText() {
        System.out.println("Text záznamu (konec: řádek s jedinou tečkou):");
        StringBuilder sb = new StringBuilder();
        boolean prvni = true;
        while (in.hasNextLine()) {
            String radek = in.nextLine();
            if (radek.equals(".")) {
                break;
            }
            if (!prvni) {
                sb.append('\n');
            }
            sb.append(radek);
            prvni = false;
        }
        return sb.toString().isBlank() ? null : sb.toString();
    }

    private static void smazZaznam() {
        if (denik.jePrazdny()) {
            System.out.println("Není co mazat.");
            return;
        }
        SeznamZaznamu zaznamy = denik.getAktualni().getZaznamy();
        System.out.print("Číslo záznamu ke smazání (1-" + zaznamy.velikost() + "): ");
        int cislo;
        try {
            cislo = Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Neplatné číslo.");
            return;
        }
        if (cislo < 1 || cislo > zaznamy.velikost()) {
            System.out.println("Záznam s tímto číslem neexistuje.");
            return;
        }
        System.out.println("Smazat tento záznam?");
        System.out.println(" " + cislo + ". " + odsad(zaznamy.get(cislo - 1).getText()));
        System.out.print("Potvrdit (a/n): ");
        if (in.nextLine().trim().equalsIgnoreCase("a")) {
            denik.smazZaznam(cislo - 1);
            System.out.println("Záznam smazán.");
        } else {
            System.out.println("Smazání zrušeno.");
        }
    }

    private static String nactiNazevSouboru() {
        System.out.print("Soubor (prázdné = " + VYCHOZI_SOUBOR + "): ");
        String vstup = in.nextLine().trim();
        return vstup.isEmpty() ? VYCHOZI_SOUBOR : vstup;
    }

    private static void uloz() {
        try {
            DenikUloziste.uloz(denik, Path.of(nactiNazevSouboru()));
            System.out.println("Deník uložen.");
        } catch (IOException e) {
            System.out.println("Uložení selhalo: " + e.getMessage());
        }
    }

    private static void nacti() {
        try {
            denik = DenikUloziste.nacti(Path.of(nactiNazevSouboru()));
            System.out.println("Deník načten.");
        } catch (IOException e) {
            System.out.println("Načtení selhalo, deník zůstal beze změny: " + e.getMessage());
        }
    }
}
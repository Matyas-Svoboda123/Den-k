/** Jeden záznam v deníku: víceřádkový text (řádky oddělené '\n'). */
public class Zaznam {
    private final String text;

    public Zaznam(String text) {
        this.text = text;
    }

    public String getText() {
        return text;
    }
}
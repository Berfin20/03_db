import java.text.SimpleDateFormat;
import java.util.Date;

public class Person {
    private final String email;
    private final String fornavn;
    private final String efternavn;
    private final String koen;
    private final Date foedselsdato;
    private final int tlf;
    private final int husnummer;
    private final String vejnavn;
    private final int postnummer;

    public Person(String email, String fornavn, String efternavn, String koen, Date foedselsdato, int tlf, int husnummer, String vejnavn, int postnummer) {
        this.email = email;
        this.fornavn = fornavn;
        this.efternavn = efternavn;
        this.koen = koen;
        this.foedselsdato = foedselsdato;
        this.tlf = tlf;
        this.husnummer = husnummer;
        this.vejnavn = vejnavn;
        this.postnummer = postnummer;
    }

    public String getEmail() {
        return email;
    }

    public String getFornavn() {
        return fornavn;
    }

    public String getEfternavn() {
        return efternavn;
    }

    public String getKoen() {
        return koen;
    }

    public Date getFoedselsdato() {
        return foedselsdato;
    }

    public int getTlf() {
        return tlf;
    }

    public int getHusnummer() {
        return husnummer;
    }

    public int getPostnummer() {
        return postnummer;
    }

    public String getVejnavn() {
        return vejnavn;
    }

    @Override
    public String toString() {
        final String D = ";";
        final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyyMMdd");

        return getEmail() +D +getFornavn() +D +getEfternavn() +D +getKoen() +D +dateFormatter.format(getFoedselsdato());
    }
}


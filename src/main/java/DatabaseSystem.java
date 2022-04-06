import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;

public class DatabaseSystem {
    private Connector connector;
    List<PersonOgTilmelding> personerOgTilmeldinger;
    IndlaesPersonerOgTilmeldinger laeser;

    DatabaseSystem(Connector connector){
        this.connector = connector;
    }

    public void CSVToDB(){
        try {
            readCSV();
            for (PersonOgTilmelding personOgTilmelding : personerOgTilmeldinger) {
                String email = personOgTilmelding.getPerson().getEmail();
                if(!personExistsInDB(email)){
                    createPersonInDB(personOgTilmelding);
                }
                updatePersonInDB(personOgTilmelding, email);
            }
            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  void readCSV(){
        laeser = new IndlaesPersonerOgTilmeldinger();
        personerOgTilmeldinger = null;

        try {
            personerOgTilmeldinger = laeser.indlaesPersonerOgTilmeldinger("src/main/resources/tilmeldinger.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean personExistsInDB(String email){
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("SELECT * FROM person WHERE email = ?");
            stmt.setString(1,email);
            ResultSet rs = stmt.executeQuery();

            readCSV();
                if (rs.next()) {
                    if (email.equals(rs.getString("email"))){
                        return true;
                    }
            }
            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void createPersonInDB(PersonOgTilmelding personOgTilmelding){
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO person(email, firstName, lastName, phoneNumber, streetName, postalCode, city, birthday, gender, isContestant) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?)");
                    ps.setString(1, personOgTilmelding.getPerson().getEmail());
                    ps.setString(2, personOgTilmelding.getPerson().getFornavn());
                    ps.setString(3, personOgTilmelding.getPerson().getEfternavn());
                    ps.setString(4, null);
                    ps.setString(5, null);
                    ps.setString(6, null);
                    ps.setString(7, null);
                    String birthdate = (new SimpleDateFormat("yyyyMMdd").format(personOgTilmelding.getPerson().getFoedselsdato()));
                    ps.setString(8, birthdate);
                    ps.setString(9, personOgTilmelding.getPerson().getKoen());
                    ps.setInt(10, 0);
                    ps.execute();
                    createContestantsInDB(personOgTilmelding);
                    updateIsContestant(personOgTilmelding, personOgTilmelding.getPerson().getEmail());

            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createContestantsInDB (PersonOgTilmelding personOgTilmelding){
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO contestant(contestantEmail, startingNumber, time) VALUES (?, ?, ?)");
            if(personOgTilmelding.getTilmelding() != null) {
                ps.setString(1,personOgTilmelding.getPerson().getEmail());
                ps.setInt(2, Integer.parseInt(personOgTilmelding.getPerson().getEmail().substring(4, personOgTilmelding.getPerson().getEmail().lastIndexOf('@'))));
                ps.setNull(3,-1);
                ps.execute();
            }
            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public  void updateIsContestant(PersonOgTilmelding personOgTilmelding, String email) {
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("UPDATE person SET isContestant = ? WHERE email = ?");
            if (personOgTilmelding.getTilmelding() != null) {
                stmt.setString(2, email);
                stmt.setInt(1, 1);
            }
            else {
                stmt.setString(2, email);
                stmt.setInt(1,0);
            }
            stmt.execute();
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public  void updatePersonInDB(PersonOgTilmelding personOgTilmelding, String email) {
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("UPDATE person SET firstName = ?, lastName =?, phoneNumber =?, streetName =?, postalCode=?, city=?, birthday=?, gender=?,isContestant=? WHERE email = ?");
                stmt.setString(10, email);
                stmt.setString(1, personOgTilmelding.getPerson().getFornavn());
                stmt.setString(2, personOgTilmelding.getPerson().getEfternavn());
                stmt.setString(3, null);
                stmt.setString(4, null);
                stmt.setString(5, null);
                stmt.setString(6, null);
                String birthdate = (new SimpleDateFormat("yyyyMMdd").format(personOgTilmelding.getPerson().getFoedselsdato()));
                stmt.setString(7, birthdate);
                stmt.setString(8, personOgTilmelding.getPerson().getKoen());
                stmt.setInt(9, 0);
                stmt.execute();
                updateIsContestant(personOgTilmelding, personOgTilmelding.getPerson().getEmail());
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }
}

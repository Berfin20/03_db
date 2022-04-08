import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

public class DatabaseSystem {
    private Connector connector;
    List<PersonOgTilmelding> personerOgTilmeldinger;
    IndlaesPersonerOgTilmeldinger laeser;

    DatabaseSystem(Connector connector) {
        this.connector = connector;
    }

    public void CSVToDB() {
        try {
            readCSV();
            for (PersonOgTilmelding personOgTilmelding : personerOgTilmeldinger) {
                String email = personOgTilmelding.getPerson().getEmail();
                if (!personExistsInDB(email) && !contestantExistsInDB(email)) {
                    createPersonInDB(personOgTilmelding);
                } else if (personExistsInDB(email) && !contestantExistsInDB(email)) {
                    updatePersonInDB(personOgTilmelding, email);
                } else if (personExistsInDB(email) && contestantExistsInDB(email)) {
                    updatePersonInDB(personOgTilmelding, email);
                    if (isContestant(email)) {
                        updateContestantInDB(personOgTilmelding, email);
                    } else {
                        deleteContestant(email);
                    }

                }
            }
            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void deleteContestant(String email) {
        try {
            PreparedStatement st = connector.getConnection().prepareStatement("DELETE FROM contestant WHERE contestantEmail =  ?");
            st.setString(1, email);
            st.executeUpdate();

            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void readCSV() {
        laeser = new IndlaesPersonerOgTilmeldinger();
        personerOgTilmeldinger = null;

        try {
            personerOgTilmeldinger = laeser.indlaesPersonerOgTilmeldinger("src/main/resources/tilmeldinger.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean personExistsInDB(String email) {
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("SELECT * FROM person WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            readCSV();
            if (rs.next()) {
                if (email.equals(rs.getString("email"))) {
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

    public boolean contestantExistsInDB(String email) {
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("SELECT * FROM contestant WHERE contestantEmail = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (email.equals(rs.getString("contestantEmail"))) {
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

    public boolean isContestant(String email) {
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("SELECT * FROM person WHERE email = ?");
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                if (email.equals(rs.getString("email"))) {
                    if (rs.getBoolean("isContestant"))
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


    public void createPersonInDB(PersonOgTilmelding personOgTilmelding) {
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO person VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?)");
            ps.setString(1, personOgTilmelding.getPerson().getEmail());
            ps.setString(2, personOgTilmelding.getPerson().getFornavn());
            ps.setString(3, personOgTilmelding.getPerson().getEfternavn());
            ps.setInt(4, personOgTilmelding.getPerson().getTlf());
            ps.setInt(5, personOgTilmelding.getPerson().getHusnummer());
            ps.setString(6, personOgTilmelding.getPerson().getVejnavn());
            ps.setInt(7, personOgTilmelding.getPerson().getPostnummer());
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

    public void createContestantsInDB(PersonOgTilmelding personOgTilmelding) {
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO contestant VALUES (?, ?)");
            if (personOgTilmelding.getTilmelding() != null) {
                String email = personOgTilmelding.getPerson().getEmail();
                ps.setString(1, email);
                int startingNumber = createStartingNumber(email);
                ps.setInt(2, startingNumber);
                ps.execute();
                createResultInDB(personOgTilmelding, startingNumber);
            }
            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void createResultInDB(PersonOgTilmelding personOgTilmelding, int startingNumber) {
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO result VALUES (?, ?, ?, ?, ?)");
            if (personOgTilmelding.getTilmelding() != null) {
                ps.setInt(1, startingNumber);
                ps.setString(2, personOgTilmelding.getTilmelding().getEventTypeId());
                String date = (new SimpleDateFormat("yyyyMMdd").format(personOgTilmelding.getTilmelding().getEventDate()));
                ps.setString(3, date);
                ps.setString(4, personOgTilmelding.getTilmelding().getForeningsId());
                ps.setString(5, createRandomTime());
                ps.execute();
            }
            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public int createStartingNumber (String email) {
        int startingNumber = Integer.parseInt(email.substring(4, email.lastIndexOf('@')));
     return startingNumber;
    }

    public String createRandomTime () {
        final Random random = new Random();

        SimpleDateFormat sdf = new SimpleDateFormat("00:mm:ss");

        Date time = new Date(random.nextLong());

        return sdf.format(time);
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

    public  void updateContestantInDB(PersonOgTilmelding personOgTilmelding, String email) {
        try {
            readCSV();
            PreparedStatement stmt = connector.getConnection().prepareStatement("UPDATE contestant SET startingNumber=? WHERE contestantEmail = ?");
            stmt.setString(2, email);
            stmt.setInt(1, createStartingNumber(email));
            stmt.execute();
            updateIsContestant(personOgTilmelding, personOgTilmelding.getPerson().getEmail());
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public  void updateResultInDB(PersonOgTilmelding personOgTilmelding, int startingNumber) {
        try {
            readCSV();
            PreparedStatement stmt = connector.getConnection().prepareStatement("UPDATE result SET eventType=?, date = ?, club = ?, time=? WHERE startingNumber = ?");
            stmt.setInt(5, startingNumber);
            stmt.setString(1, personOgTilmelding.getTilmelding().getEventTypeId());
            String date = (new SimpleDateFormat("yyyyMMdd").format(personOgTilmelding.getTilmelding().getEventDate()));
            stmt.setString(2, date);
            stmt.setString(3, personOgTilmelding.getTilmelding().getForeningsId());
            stmt.setString(4, createRandomTime());
            stmt.execute();
            updateIsContestant(personOgTilmelding, personOgTilmelding.getPerson().getEmail());
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }

    public  void updatePersonInDB(PersonOgTilmelding personOgTilmelding, String email) {
        try {
            PreparedStatement stmt = connector.getConnection().prepareStatement("UPDATE person SET firstName = ?, lastName =?, phoneNumber =?,houseNumber =?, streetName =?, postalCode=?, birthday=?, gender=?,isContestant=? WHERE email = ?");
                stmt.setString(10, email);
                stmt.setString(1, personOgTilmelding.getPerson().getFornavn());
                stmt.setString(2, personOgTilmelding.getPerson().getEfternavn());
                stmt.setInt(3, personOgTilmelding.getPerson().getTlf());
                stmt.setInt(4, personOgTilmelding.getPerson().getHusnummer());
                stmt.setString(5, personOgTilmelding.getPerson().getVejnavn());
                stmt.setInt(6,personOgTilmelding.getPerson().getPostnummer());
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

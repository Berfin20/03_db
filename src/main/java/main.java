import java.io.IOException;
import java.sql.*;
import java.util.List;

public class main {
    static List<PersonOgTilmelding> personerOgTilmeldinger;
    static IndlaesPersonerOgTilmeldinger laeser;

        public static void main(String[] args) {
         Connector connector = new Connector();
         truncateRegistrationTable(connector.getConnection());
         insertPeople(connector);

    }

    public static void readCSV(){
        laeser = new IndlaesPersonerOgTilmeldinger();
        personerOgTilmeldinger = null;

        try {
            personerOgTilmeldinger = laeser.indlaesPersonerOgTilmeldinger("src/main/resources/tilmeldinger.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void truncateRegistrationTable(Connection connection){
        Statement statement = null;
        try {
            statement = connection.createStatement();

            statement.executeUpdate("TRUNCATE registration");
            System.out.println("Successfully truncated test_table");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void insertPeople(Connector connector){
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO person(email, firstName, lastName, gender, birthday, isContestant) VALUES (?, ?, ?, ?, ?, ?)");

            readCSV();

                for(PersonOgTilmelding personOgTilmelding : personerOgTilmeldinger) {
                    ps.setString(1,personOgTilmelding.getPerson().getEmail());
                    ps.setString(2,personOgTilmelding.getPerson().getFornavn());
                    ps.setString(3,personOgTilmelding.getPerson().getEfternavn());
                    ps.setString(4,personOgTilmelding.getPerson().getKoen());
                    ps.setString(5, String.valueOf(personOgTilmelding.getPerson().getFoedselsdato()));
                    ps.setInt(6,0);
                    ps.execute();
                    insertRegistrations(personOgTilmelding, connector);
                    updateIsContestant(personOgTilmelding, connector, personOgTilmelding.getPerson().getEmail());
            }

            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void insertRegistrations(PersonOgTilmelding personOgTilmelding, Connector connector){
        try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO registration(clubId, eventType, eventDate, memberMail) VALUES (?, ?, ?, ?)");

                if(personOgTilmelding.getTilmelding() != null) {
                    ps.setString(1, personOgTilmelding.getTilmelding().getForeningsId());
                    ps.setString(2, personOgTilmelding.getTilmelding().getEventTypeId());
                    ps.setString(3, String.valueOf(personOgTilmelding.getTilmelding().getEventDate()));
                    ps.setString(4,personOgTilmelding.getPerson().getEmail());
                    ps.execute();
                }

            connector.getConnection().commit();
            connector.getConnection().setAutoCommit(true);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateIsContestant(PersonOgTilmelding personOgTilmelding, Connector connector, String email) {
        try {
            if (personOgTilmelding.getTilmelding() != null) {
                PreparedStatement stmt = connector.getConnection().prepareStatement("UPDATE person SET isContestant = ? WHERE email = ?");
                stmt.setString(2, email);
                stmt.setInt(1, 1);
                stmt.execute();
            }
        }
            catch(SQLException ex){
                ex.printStackTrace();
            }
        }
    }




import java.io.IOException;
import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class main {
    static List<PersonOgTilmelding> personerOgTilmeldinger;
    static IndlaesPersonerOgTilmeldinger laeser;

        public static void main(String[] args) {
         Connector connector = new Connector();

//         truncateRegistrationTable(connector.getConnection());
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


//    public static void truncateRegistrationTable(Connection connection){
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//
//            statement.executeUpdate("TRUNCATE registration");
//            System.out.println("Successfully truncated test_table");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
//
//    }

    public static void insertPeople(Connector connector){
        try {

            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO person(email, firstName, lastName, phoneNumber, streetName, postalCode, city, birthday, gender, isContestant) VALUES (?, ?, ?, ?, ?, ?, ?,?,?,?)");

            readCSV();

                for(PersonOgTilmelding personOgTilmelding : personerOgTilmeldinger) {
                    ps.setString(1,personOgTilmelding.getPerson().getEmail());
                    ps.setString(2,personOgTilmelding.getPerson().getFornavn());
                    ps.setString(3,personOgTilmelding.getPerson().getEfternavn());
                    ps.setString(4,null);
                    ps.setString(5,null);
                    ps.setString(6,null);
                    ps.setString(7,null);
                    String birthdate = (new SimpleDateFormat("yyyyMMdd").format(personOgTilmelding.getPerson().getFoedselsdato()));
                    ps.setString(8, birthdate);
                    ps.setString(9,personOgTilmelding.getPerson().getKoen());
                    ps.setInt(10,0);
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
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO contestant(contestantEmail, startingNumber, time, event, ageGroup) VALUES (?, ?, ?, ?,?)");
                if(personOgTilmelding.getTilmelding() != null) {
                    ps.setString(1,personOgTilmelding.getPerson().getEmail());
                    ps.setInt(2, Integer.parseInt(personOgTilmelding.getPerson().getEmail().substring(4, personOgTilmelding.getPerson().getEmail().lastIndexOf('@'))));
                    ps.setNull(3,-1);
                    ps.setString(4, "hjulmtb");
//                    ps.setString(4, personOgTilmelding.getTilmelding().getEventTypeId());
                    ps.setString(5, "f1825");
                    ps.execute();
                    updateContestantTime(personOgTilmelding,connector);
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
    public static void updateContestantTime(PersonOgTilmelding personOgTilmelding, Connector connector) {
        try {
            if (personOgTilmelding.getTilmelding() != null) {
                PreparedStatement stmt = connector.getConnection().prepareStatement("SELECT * FROM contestant where time=-1",ResultSet.TYPE_FORWARD_ONLY,
                        ResultSet.CONCUR_UPDATABLE);
                ResultSet rs = stmt.executeQuery();

                while(rs.next()) {
                    rs.updateNull("time");
                };
            }
        }
        catch(SQLException ex){
            ex.printStackTrace();
        }
    }
    }




import java.io.IOException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class DatabaseSystem {
    private Connector connector;
    DatabaseSystem(Connector connector){
        this.connector = connector;
    }


    public void insertPersonInDB(List<PersonOgTilmelding> poaliste) throws SQLException {
            Connection connection = connector.getConnection();
                connection.setAutoCommit(false);
            try {
            PreparedStatement ps = connector.getConnection().prepareStatement("INSERT INTO person(email, firstName, lastName, gender, birthday, isContestant) VALUES (?, ?, ?, ?, ?, ?)");
            IndlaesPersonerOgTilmeldinger laeser = new IndlaesPersonerOgTilmeldinger();
                List<PersonOgTilmelding> personerOgTilmeldinger = null;
                try {
                    personerOgTilmeldinger = laeser.indlaesPersonerOgTilmeldinger("/Users/payammadani/Documents/GitHub/09_del1/03_db/src/main/resources/tilmeldinger.csv");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(PersonOgTilmelding personOgTilmelding : personerOgTilmeldinger) {
                        System.out.print("Person: " +personOgTilmelding.getPerson());
                        ps.setString(1,personOgTilmelding.getPerson().getEmail());
                        ps.setString(2,personOgTilmelding.getPerson().getFornavn());
                        ps.setString(3,personOgTilmelding.getPerson().getEfternavn());
                        ps.setString(4,personOgTilmelding.getPerson().getKoen());
                        ps.setDate(5, (Date) personOgTilmelding.getPerson().getFoedselsdato());
                        ps.setInt(6,0);






                    if(personOgTilmelding.getTilmelding() != null)
                            System.out.println("\tTilmelding: " +personOgTilmelding.getTilmelding());
                        else
                            System.out.println("\t Ingen tilhørende tilmelding");
                    }
                connection.commit();
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }
    }

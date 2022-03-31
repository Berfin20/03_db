import java.io.IOException;
import java.sql.SQLException;
import java.util.Scanner;

        public class main {
        public static void main(String[] args) throws IOException, SQLException {
            IndlaesPersonerOgTilmeldinger laeser = new IndlaesPersonerOgTilmeldinger();
       DatabaseSystem databaseSystem = new DatabaseSystem();
       databaseSystem.insertPersonInDB( laeser.indlaesPersonerOgTilmeldinger("/Users/payammadani/Documents/GitHub/09_del1/03_db/src/main/resources/tilmeldinger.csv"));
    }
            public static Connector getRepository() {
                if(repository == null) {
                    repository = new Repository(new Connector());
                }
                return repository;
            }
}


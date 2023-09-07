import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static final String DATABASE = "jdbc:mysql://localhost:3306/nation";
    public static final String USER = "jaita91";
    public static final String PASSWORD = "JAITA91";

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try (Connection connection = DriverManager.getConnection(DATABASE, USER, PASSWORD)) {

            System.out.print("Cerca una nazione: ");
            String choice = scanner.nextLine();

            // ID, CountryName, RegionName, ContinentName | ORDERED ASC BY CountryName
            String query = "SELECT c.country_id, c.name as country_name , r.name as region_name , c2.name as continent_name \n" +
                    "FROM countries c \n" +
                    "LEFT JOIN regions r ON r.region_id = c.region_id \n" +
                    "LEFT JOIN continents c2 ON c2.continent_id = r.continent_id \n" +
                    "WHERE LOWER(c.name) LIKE LOWER(?) \n" +
                    "ORDER BY c.name";

            PreparedStatement prepareStatement = connection.prepareStatement(query);
            prepareStatement.setString(1, "%" + choice + "%");

            List<Country> countries = new ArrayList<>();

            try (ResultSet rs = prepareStatement.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("country_id");
                    String countryName = rs.getString("country_name");
                    String regionName = rs.getString("region_name");
                    String continentName = rs.getString("continent_name");

                    Country country = new Country(id, countryName, regionName, continentName);
                    countries.add(country);
                }
            } catch (SQLException e) {
                System.out.println("Errore nell'iterazione del ResultSet.");
            }

            if (countries.size() > 0) {
                String columnNames = printColumns("ID", "Country Name", "Region Name", "Continent Name");
                System.out.println(columnNames);

                for (Country c :
                        countries) {
                    System.out.println(printColumns(String.valueOf(c.getId()), c.getCountryName(), c.getRegionName(), c.getContinentName()));
                }


                System.out.print("Inserisci ID della nazione per ulteriori informazioni: ");
                choice = scanner.nextLine();


                // CountryName, Language, Year, Population, GDP
                query = "SELECT c.name, l.`language`, cs.`year`, cs.population, cs.gdp  \n" +
                        "FROM countries c \n" +
                        "LEFT JOIN country_stats cs ON cs.country_id = c.country_id \n" +
                        "LEFT JOIN country_languages cl ON cl.country_id = c.country_id\n" +
                        "LEFT JOIN languages l ON l.language_id = cl.language_id \n" +
                        "WHERE cs.`year` = (SELECT MAX(`year`) FROM country_stats)\n" +
                        "AND c.country_id = ? \n" +
                        "ORDER BY c.name \n";

                prepareStatement = connection.prepareStatement(query);
                prepareStatement.setString(1, choice);

                List<String> languages = new ArrayList<>();

                String countryName = "";
                int mostRecentYear = 0;
                int mostRecentPopulation= 0;
                long mostRecentGdp= 0;

                try (ResultSet rs = prepareStatement.executeQuery()) {
                    if (rs.next()) {
                        languages.add(rs.getString("language"));

                        countryName = rs.getString("name");
                        mostRecentYear = rs.getInt("year");
                        mostRecentPopulation = rs.getInt("population");
                        mostRecentGdp = rs.getLong("gdp");
                    }

                    while (rs.next()) {
                        languages.add(rs.getString("language"));
                    }
                } catch (SQLException e) {
                    System.out.println("Errore nell'iterazione del ResultSet.");
                }

                if (languages.size() > 0) {
                    StringBuilder languagesBuilder = new StringBuilder();
                    for (String lang :
                            languages) {
                        languagesBuilder.append(lang).append(", ");
                    }
                    languagesBuilder.delete(languagesBuilder.length() - 2, languagesBuilder.length() - 1);

                    System.out.println("Informazioni su " + countryName);
                    System.out.println("Lingue: " + languagesBuilder);
                    System.out.println("Informazioni più recenti: ");
                    System.out.println("\tAnno: " + mostRecentYear);
                    System.out.println("\tPopulation: " + mostRecentPopulation);
                    System.out.println("\tGDP: " + mostRecentGdp);
                }
            } else {
                System.out.println("Non ho trovato nessuna nazione con il parametro di ricerca: " + choice);
            }
        } catch (SQLException e) {
            System.out.println("Connessione al database non riuscita.");
        }

        scanner.close();
    }

    public static String printColumns(String... args) {
        StringBuilder stringBuilder = new StringBuilder();

        for (String name :
                args) {
            stringBuilder.append(name);
            stringBuilder.append(" ".repeat(30 - name.length()));
        }

        return stringBuilder.toString();
    }
}

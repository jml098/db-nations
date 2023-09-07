public class Country {
    private int id;
    private String countryName;
    private String regionName;
    private String continentName;

    public Country(int id, String countryName, String regionName, String continentName) {
        this.id = id;
        this.countryName = countryName;
        this.regionName = regionName;
        this.continentName = continentName;
    }

    public int getId() {
        return id;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getRegionName() {
        return regionName;
    }

    public String getContinentName() {
        return continentName;
    }
}

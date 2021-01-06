package se.kth.iv1351.bankjdbc.model;

/**
 * An instrument in the db.
 */
public class Instrument implements InstrumentDTO {
    private int instrumentId;
    private String type;
    private String brand;
    private int monthlyPrice;
    private int stock;

    /**
     * Creates an instrument object with the specified ID, type, brand, monthly price and
     * number in stock.
     *
     * @param instrumentId    The instrument ID.
     * @param type            The type of instrument.
     * @param brand           The instrument's brand.
     * @param monthlyPrice    The monthly price of the instrument.
     * @param stock           The amount of the instrument in stock.
     */
    public Instrument(int instrumentId, String type, String brand, int monthlyPrice, int stock) {
        this.instrumentId = instrumentId;
        this.type = type;
        this.brand = brand;
        this.monthlyPrice = monthlyPrice;
        this.stock = stock;
    }

    /**
     * @return The instrument id.
     */
    public int getInstrumentId() {
        return instrumentId;
    }

    /**
     * @return The type.
     */
    public String getType() {
        return type;
    }

    /**
     * @return The instrument's brand.
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return The instrument's monthly price.
     */
    public int getMonthlyPrice() {
        return monthlyPrice;
    }

    /**
     * @return The number of the instrument in stock.
     */
    public int getStock() {
        return stock;
    }

    /**
     * Updates the instrument stock after a rental.
     */
    public void decrementStock() {
        this.stock--;
    }

    /**
     * Updates the instrument stock after a return.
     */
    public void incrementStock() {
        this.stock++;
    }

    /**
     * @return A string representation of all fields in this object.
     */
    @Override
    public String toString() {
        StringBuilder stringRepresentation = new StringBuilder();
        stringRepresentation.append("Instrument: [");
        stringRepresentation.append("id: ");
        stringRepresentation.append(instrumentId);
        stringRepresentation.append(", type: ");
        stringRepresentation.append(type);
        stringRepresentation.append(", brand: ");
        stringRepresentation.append(brand);
        stringRepresentation.append(", monthly price: ");
        stringRepresentation.append(monthlyPrice);
        stringRepresentation.append(", stock: ");
        stringRepresentation.append(stock);
        stringRepresentation.append("]");
        return stringRepresentation.toString();
    }
}
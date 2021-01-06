package se.kth.iv1351.bankjdbc.model;

/**
 * Specifies a read-only view of an instrument.
 */
public interface InstrumentDTO {
    /**
     * @return The instrument type.
     */
    public String getType();

    /**
     * @return The brand.
     */
    public String getBrand();

    /**
     * @return The instrument's month price.
     */
    public int getMonthlyPrice();

    /**
     * @return The number of the instrument in stock.
     */
    public int getStock();
}
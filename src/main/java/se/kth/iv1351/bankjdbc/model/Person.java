package se.kth.iv1351.bankjdbc.model;

/**
 * A person in the db.
 */
public class Person {
    private int personId;
    private String personNumber;
    private String firstName;
    private String lastName;

    /**
     * Creates a person object with the specified person ID, person number, first name
     * and last name.
     *
     * @param personId          The person's person ID.
     * @param personNumber      The person number of the person.
     * @param firstName         The person's first name.
     * @param lastName          The person's last name.
     */
    public Person(int personId, String personNumber, String firstName, String lastName){
        this.personId = personId;
        this.personNumber = personNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    /**
     * @return The person's id.
     */
    public int getPersonId() {
        return personId;
    }

    /**
     * @return Person number
     */
    public String getPersonNumber() {
        return personNumber;
    }

    /**
     * @return Person's first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @return Person's last name
     */
    public String getLastName() {
        return lastName;
    }

}
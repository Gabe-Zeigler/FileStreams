import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Objects;

/**
 * Represents a Product with an ID, name, description, and cost.
 */
public class Product
{
    // ---- fixed field sizes for the RandomAccessFile (Ass 02) ----
    public static final int NAME_LEN = 35;
    public static final int DESC_LEN = 75;
    public static final int ID_LEN = 6;

    // writeChars() uses 2 bytes per char; a double is 8 bytes -> 240 bytes per record
    public static final int RECORD_SIZE = (NAME_LEN + DESC_LEN + ID_LEN) * 2 + Double.BYTES;

    private String ID;
    private String name;
    private String description;
    private double cost;

    /**
     * Full constructor for a Product.
     *
     * @param ID the product's ID
     * @param name the product's name
     * @param description the product's description
     * @param cost the product's cost
     */
    public  Product (String ID, String name, String description, double cost)
    {
        this.ID=ID;
        this.name=name;
        this.description=description;
        this.cost=cost;
    }

    /**
     * Overloaded constructor for when no description is known yet.
     * The description defaults to an empty String.
     *
     * @param ID the product's ID
     * @param name the product's name
     * @param cost the product's cost
     */
    public Product(String ID, String name, double cost)
    {
        this(ID, name, "", cost);
    }

    /**
     * Default constructor.
     */
    public Product()
    {
        this("", "", "", 0.0);
    }

    /**
     * Gets the ID.
     *
     * @return the ID
     */
    public String getID()
    {
        return ID;
    }
    /**
     * Gets the name.
     *
     * @return the name
     */
    public String getName()
    {
        return name;
    }
    /**
     * Gets the description.
     *
     * @return the description
     */
    public String getDescription()
    {
        return description;
    }
    /**
     * Gets the cost.
     *
     * @return the cost
     */
    public double getCost()
    {
        return cost;
    }

    /**
     * Sets the ID.
     *
     * @param ID the new ID
     */
    public void setID(String ID) {
        this.ID = ID;
    }

    /**
     * Sets the name.
     *
     * @param name the new name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the description.
     *
     * @param description the new description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Sets the cost.
     *
     * @param cost the new cost
     */
    public void setCost(double cost) {
        this.cost = cost;
    }

    /**
     * Returns the Product as a CSV record.
     *
     * @return a comma-separated String
     */
    public String toCSV()
    {
        return ID + "," + name + "," + description + "," + cost;
    }

    /**
     * Returns the Product as a JSON record.
     *
     * @return a JSON-formatted String
     */
    public String toJSON()
    {
        return "{\"ID\":\"" + ID + "\","
                + "\"Name\":\"" + name + "\","
                + "\"Description\":\"" + description + "\","
                + "\"Cost\":" + cost + "}";
    }

    /**
     * Returns a String representation of the Product.
     *
     * @return a String describing the Product
     */
    @Override
    public String toString()
    {
        return "Product{" +
                "ID='" + ID + '\'' +
                ", Name='" + name + '\'' +
                ", Description='" + description + '\'' +
                ", Cost=" + cost +
                '}';
    }

    /**
     * Returns the Product as an XML record.
     *
     * @return an XML-formatted String
     */
    public String toXML()
    {
        return "<Product>"
                + "<ID>" + ID + "</ID>"
                + "<Name>" + name + "</Name>"
                + "<Description>" + description + "</Description>"
                + "<Cost>" + cost + "</Cost>"
                + "</Product>";
    }

    /**
     * Determines whether two Product objects contain the same data.
     *
     * @param o the object to compare
     * @return true if the objects contain the same data
     */
    @Override
    public boolean equals(Object o)
    {
        if (this == o)
            return true;

        if (!(o instanceof Product))
            return false;

        Product product = (Product) o;

        return Double.compare(product.cost, cost) == 0 &&
                Objects.equals(ID, product.ID) &&
                Objects.equals(name, product.name) &&
                Objects.equals(description, product.description);
    }

    // ---------- Ass 02: fixed-length record utilities ----------

    /**
     * Pads a String with spaces (or truncates it) to exactly len characters.
     *
     * @param s the String to format
     * @param len the required length
     * @return a String of exactly len characters
     */
    public static String pad(String s, int len)
    {
        StringBuilder sb = new StringBuilder(s == null ? "" : s);
        if (sb.length() > len)
        {
            sb.setLength(len);
        }
        while (sb.length() < len)
        {
            sb.append(' ');
        }
        return sb.toString();
    }

    /** @return the name padded to NAME_LEN characters */
    public String getPaddedName()
    {
        return pad(name, NAME_LEN);
    }

    /** @return the description padded to DESC_LEN characters */
    public String getPaddedDescription()
    {
        return pad(description, DESC_LEN);
    }

    /** @return the ID padded to ID_LEN characters */
    public String getPaddedID()
    {
        return pad(ID, ID_LEN);
    }

    /**
     * Writes this Product as a fixed-size record at the file's current position.
     *
     * @param file an open RandomAccessFile
     * @throws IOException if the write fails
     */
    public void writeRecord(RandomAccessFile file) throws IOException
    {
        file.writeChars(getPaddedName());
        file.writeChars(getPaddedDescription());
        file.writeChars(getPaddedID());
        file.writeDouble(cost);
    }

    /**
     * Reads one fixed-size record from the file's current position.
     *
     * @param file an open RandomAccessFile
     * @return the Product read, with the padding trimmed off
     * @throws IOException if the read fails
     */
    public static Product readRecord(RandomAccessFile file) throws IOException
    {
        String name = readFixedString(file, NAME_LEN);
        String desc = readFixedString(file, DESC_LEN);
        String id = readFixedString(file, ID_LEN);
        double cost = file.readDouble();
        return new Product(id, name, desc, cost);
    }

    private static String readFixedString(RandomAccessFile file, int len) throws IOException
    {
        StringBuilder sb = new StringBuilder(len);
        for (int i = 0; i < len; i++)
        {
            sb.append(file.readChar());
        }
        return sb.toString().trim();
    }
}
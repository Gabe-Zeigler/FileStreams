import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Swing form that collects Product data, validates it, pads it to the fixed
 * field lengths and appends it to a RandomAccessFile.
 */
public class RandProductMaker extends JFrame
{
    public static final String FILE_NAME = "ProductData.dat";

    private final JTextField nameField = new JTextField(Product.NAME_LEN);
    private final JTextField descField = new JTextField(30);
    private final JTextField idField = new JTextField(Product.ID_LEN);
    private final JTextField costField = new JTextField(10);
    private final JTextField countField = new JTextField(5);

    private final JButton addButton = new JButton("Add");
    private final JButton quitButton = new JButton("Quit");

    private int recordCount = 0;

    public RandProductMaker()
    {
        super("Random Access Product Maker");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        addRow(form, 0, "Name (max " + Product.NAME_LEN + "):", nameField);
        addRow(form, 1, "Description (max " + Product.DESC_LEN + "):", descField);
        addRow(form, 2, "ID (max " + Product.ID_LEN + "):", idField);
        addRow(form, 3, "Cost:", costField);
        countField.setEditable(false);
        addRow(form, 4, "Record count:", countField);
        add(form, BorderLayout.CENTER);

        JPanel buttons = new JPanel(new GridLayout(1, 2, 5, 5));
        buttons.add(addButton);
        buttons.add(quitButton);
        add(buttons, BorderLayout.SOUTH);

        addButton.addActionListener(e -> addRecord());
        quitButton.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
    }

    private void addRow(JPanel p, int row, String label, JComponent field)
    {
        GridBagConstraints c = new GridBagConstraints();
        c.gridy = row;
        c.insets = new Insets(4, 4, 4, 4);
        c.anchor = GridBagConstraints.WEST;
        c.gridx = 0;
        p.add(new JLabel(label), c);
        c.gridx = 1;
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        p.add(field, c);
    }

    /** If a data file already exists, let the user choose to append or start over. */
    private void initFile()
    {
        File f = new File(FILE_NAME);
        if (f.exists() && f.length() > 0)
        {
            int choice = JOptionPane.showOptionDialog(this,
                    FILE_NAME + " already exists with " + (f.length() / Product.RECORD_SIZE) + " record(s).\n"
                            + "Append to it or start a new file?",
                    "Existing Data File", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                    null, new String[]{"Append", "Start New File"}, "Append");
            if (choice == 1)
            {
                try (RandomAccessFile raf = new RandomAccessFile(f, "rw"))
                {
                    raf.setLength(0);
                }
                catch (IOException ex)
                {
                    showError("Could not reset file: " + ex.getMessage());
                }
            }
        }
        recordCount = (int) (new File(FILE_NAME).length() / Product.RECORD_SIZE);
        countField.setText(String.valueOf(recordCount));
    }

    private void addRecord()
    {
        String name = nameField.getText().trim();
        String desc = descField.getText().trim();
        String id = idField.getText().trim();
        String costText = costField.getText().trim();

        // Confirm everything was entered
        if (name.isEmpty() || desc.isEmpty() || id.isEmpty() || costText.isEmpty())
        {
            showError("Please fill in all of the fields.");
            return;
        }
        // Validate lengths
        if (name.length() > Product.NAME_LEN)
        {
            showError("Name may be at most " + Product.NAME_LEN + " characters.");
            return;
        }
        if (desc.length() > Product.DESC_LEN)
        {
            showError("Description may be at most " + Product.DESC_LEN + " characters.");
            return;
        }
        if (id.length() > Product.ID_LEN)
        {
            showError("ID may be at most " + Product.ID_LEN + " characters.");
            return;
        }
        // Validate cost
        double cost;
        try
        {
            cost = Double.parseDouble(costText);
        }
        catch (NumberFormatException ex)
        {
            showError("Cost must be a number, e.g. 12.99");
            return;
        }
        if (cost < 0)
        {
            showError("Cost cannot be negative.");
            return;
        }

        Product p = new Product(id, name, desc, cost);
        try (RandomAccessFile raf = new RandomAccessFile(FILE_NAME, "rw"))
        {
            raf.seek(raf.length());   // append at the end
            p.writeRecord(raf);
        }
        catch (IOException ex)
        {
            showError("Could not write record: " + ex.getMessage());
            return;
        }

        recordCount++;
        countField.setText(String.valueOf(recordCount));
        clearFields();
    }

    private void clearFields()
    {
        nameField.setText("");
        descField.setText("");
        idField.setText("");
        costField.setText("");
        nameField.requestFocusInWindow();
    }

    private void showError(String msg)
    {
        JOptionPane.showMessageDialog(this, msg, "Input Error", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() ->
        {
            RandProductMaker m = new RandProductMaker();
            m.setVisible(true);
            m.initFile();
        });
    }
}
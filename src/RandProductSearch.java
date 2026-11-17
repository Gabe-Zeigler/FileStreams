import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * Swing program that reads the random access Product file and displays every
 * product whose name contains the (partial) text the user typed.
 */
public class RandProductSearch extends JFrame
{
    private final JTextField searchField = new JTextField(20);
    private final JTextArea resultArea = new JTextArea(20, 60);
    private final JButton searchButton = new JButton("Search");
    private final JButton quitButton = new JButton("Quit");
    private final JLabel statusLabel = new JLabel(" ");

    public RandProductSearch()
    {
        super("Random Access Product Search");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(5, 5));

        JPanel top = new JPanel(new FlowLayout(FlowLayout.LEFT));
        top.add(new JLabel("Partial product name:"));
        top.add(searchField);
        top.add(searchButton);
        top.add(statusLabel);
        add(top, BorderLayout.NORTH);

        resultArea.setEditable(false);
        resultArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        JPanel bottom = new JPanel(new GridLayout(1, 1));
        bottom.add(quitButton);
        add(bottom, BorderLayout.SOUTH);

        searchButton.addActionListener(e -> search());
        searchField.addActionListener(e -> search());   // Enter key also searches
        quitButton.addActionListener(e -> System.exit(0));

        pack();
        setLocationRelativeTo(null);
    }

    private void search()
    {
        String target = searchField.getText().trim().toLowerCase();
        if (target.isEmpty())
        {
            JOptionPane.showMessageDialog(this, "Please enter part of a product name.",
                    "No Search Text", JOptionPane.WARNING_MESSAGE);
            return;
        }
        File f = new File(RandProductMaker.FILE_NAME);
        if (!f.exists())
        {
            JOptionPane.showMessageDialog(this,
                    RandProductMaker.FILE_NAME + " was not found. Run RandProductMaker first.",
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        StringBuilder sb = new StringBuilder();
        int matches = 0;
        try (RandomAccessFile raf = new RandomAccessFile(f, "r"))
        {
            long records = raf.length() / Product.RECORD_SIZE;
            for (long i = 0; i < records; i++)
            {
                raf.seek(i * Product.RECORD_SIZE);
                Product p = Product.readRecord(raf);
                if (p.getName().toLowerCase().contains(target))
                {
                    matches++;
                    sb.append(String.format("ID: %-6s  Name: %s%n    Description: %s%n    Cost: $%.2f%n%n",
                            p.getID(), p.getName(), p.getDescription(), p.getCost()));
                }
            }
        }
        catch (IOException ex)
        {
            JOptionPane.showMessageDialog(this, "Could not read file:\n" + ex.getMessage(),
                    "File Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultArea.setText(sb.toString());
        resultArea.setCaretPosition(0);
        statusLabel.setText(matches + " match(es)");
    }

    public static void main(String[] args)
    {
        SwingUtilities.invokeLater(() -> new RandProductSearch().setVisible(true));
    }
}
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import java.net.*;
import java.io.*;

public class Server implements ActionListener {

    JTextField text;
    JPanel a1;
    static Box vertical = Box.createVerticalBox();
    static DataOutputStream dout;

    static JFrame f = new JFrame();
    static JScrollPane scrollPane; // ⬅️ Added for scroll support

    Server() {
        f.setLayout(null);

        // Top green panel
        JPanel p1 = new JPanel();
        p1.setBackground(new Color(7, 94, 84));
        p1.setBounds(0, 0, 450, 80);
        p1.setLayout(null);
        f.add(p1);

        // BACK button
        ImageIcon backIcon = new ImageIcon(ClassLoader.getSystemResource("Icons/3.png"));
        Image backImg = backIcon.getImage().getScaledInstance(25, 25, Image.SCALE_SMOOTH);
        JLabel back = new JLabel(new ImageIcon(backImg));
        back.setBounds(10, 27, 25, 25);
        p1.add(back);
        back.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent ae) {
                System.exit(0);
            }
        });

        // Profile image
        ImageIcon profileIcon = new ImageIcon(ClassLoader.getSystemResource("Icons/1.png"));
        Image profileImg = profileIcon.getImage().getScaledInstance(45, 45, Image.SCALE_SMOOTH);
        JLabel profile = new JLabel(new ImageIcon(profileImg));
        profile.setBounds(45, 17, 45, 45);
        p1.add(profile);

        // Name
        JLabel name = new JLabel("Neha");
        name.setBounds(100, 20, 150, 20);
        name.setForeground(Color.WHITE);
        name.setFont(new Font("SAN SERIF", Font.BOLD, 18));
        p1.add(name);

        // Status
        JLabel status = new JLabel("Active Now");
        status.setBounds(100, 40, 150, 20);
        status.setForeground(Color.WHITE);
        status.setFont(new Font("SAN SERIF", Font.PLAIN, 14));
        p1.add(status);

        // Start placing icons from right to left with 40px spacing
        int iconY = 27;
        int iconSize = 25;
        int rightMargin = 410; // start placing from right edge

        // MOREVERT (3 dots)
        ImageIcon moreIcon = new ImageIcon(ClassLoader.getSystemResource("Icons/3icon.png"));
        Image moreImg = moreIcon.getImage().getScaledInstance(15, 20, Image.SCALE_SMOOTH); // smaller and sharper
        JLabel more = new JLabel(new ImageIcon(moreImg));
        more.setBounds(rightMargin, 30, 15, 20); // y = 30 for centering
        p1.add(more);

        // PHONE
        rightMargin -= 40;
        ImageIcon phoneIcon = new ImageIcon(ClassLoader.getSystemResource("Icons/phone.png"));
        Image phoneImg = phoneIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        JLabel phone = new JLabel(new ImageIcon(phoneImg));
        phone.setBounds(rightMargin, iconY, iconSize, iconSize);
        p1.add(phone);

        // VIDEO
        rightMargin -= 40;
        ImageIcon videoIcon = new ImageIcon(ClassLoader.getSystemResource("Icons/video.png"));
        Image videoImg = videoIcon.getImage().getScaledInstance(iconSize, iconSize, Image.SCALE_SMOOTH);
        JLabel video = new JLabel(new ImageIcon(videoImg));
        video.setBounds(rightMargin, iconY, iconSize, iconSize);
        p1.add(video);

        // Chat area
        a1 = new JPanel();
        a1.setBounds(0, 70, 450, 585); // Full width and height
        scrollPane = new JScrollPane(a1);
        a1.setLayout(null);
        f.add(a1);

        // Text field
        text = new JTextField();
        text.setBounds(5, 665, 310, 40); // Align with padding
        text.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        f.add(text);

        // Send button
        JButton send = new JButton("Send");
        send.setBounds(325, 665, 120, 40); // Equal height, aligned to the right
        send.setBackground(new Color(7, 94, 84));
        send.setForeground(Color.WHITE);
        send.setFont(new Font("SAN_SERIF", Font.PLAIN, 16));
        send.setBorderPainted(false);
        send.addActionListener(this);
        f.add(send);

        // Frame settings
        f.setSize(450, 750);
        f.setLocation(200, 10);
        f.getContentPane().setBackground(Color.WHITE);
        f.setUndecorated(true);
        f.setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        try {
            String out = text.getText();

            JPanel p2 = formatLabel(out);

            a1.setLayout(new BorderLayout());

            JPanel right = new JPanel(new BorderLayout());
            right.add(p2, BorderLayout.LINE_END);
            vertical.add(right);
            vertical.add(Box.createVerticalStrut(15));

            a1.add(vertical, BorderLayout.PAGE_START);

            dout.writeUTF(out);

            text.setText("");

            a1.revalidate();

            // ✅ Scroll to bottom after sending
            SwingUtilities.invokeLater(
                    () -> scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static JPanel formatLabel(String out) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        JLabel output = new JLabel("<html><p style=\"width: 150px\">" + out + "</p></html>");
        output.setFont(new Font("Tahoma", Font.PLAIN, 16));
        output.setBackground(new Color(37, 211, 102));
        output.setOpaque(true);
        output.setBorder(new EmptyBorder(15, 15, 15, 50));

        panel.add(output);

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        JLabel time = new JLabel();
        time.setText(sdf.format(cal.getTime()));

        panel.add(time);

        return panel;
    }

    public static void main(String[] args) {
        new Server();

        try {

            ServerSocket skt = new ServerSocket(6001);

            while (true) {
                Socket s = skt.accept();

                DataInputStream din = new DataInputStream(s.getInputStream());

                dout = new DataOutputStream(s.getOutputStream());

                while (true) {
                    String msg = din.readUTF();

                    // ✅ Beep on receiving message
                    Toolkit.getDefaultToolkit().beep();

                    JPanel panel = formatLabel(msg);

                    JPanel left = new JPanel(new BorderLayout());
                    left.add(panel, BorderLayout.LINE_START);
                    vertical.add(left);
                    f.validate();

                    // ✅ Scroll to bottom after receiving
                    SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar()
                            .setValue(scrollPane.getVerticalScrollBar().getMaximum()));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package  client;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class GameUI extends JPanel {
    private JLabel playerScoreLabel, opponentScoreLabel, timeLabel;
    private int playerScore = 0;
    private int opponentScore = 0;
    private int timeRemaining = 60;
    private TrashBin[] bins = new TrashBin[4];
    public int currentBinIndex = 1; // Thùng ở giữa (nhựa) bắt đầu là chỉ số 1
    private Trash trash;
    private Timer countdownTimer; // Khai báo biến ở cấp độ lớp
    private Timer gameTimer; // Khai báo biến ở cấp độ lớp



    private Image background;
    public GameUI() {


        try {
            background = ImageIO.read(getClass().getResource("/resources/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLayout(null);
        setPreferredSize(new Dimension(800, 600));


        // Thiết lập font chữ lớn hơn
        Font largeFont = new Font("Arial", Font.BOLD, 24);

        // Thông tin người chơi
        playerScoreLabel = new JLabel("Player: 0");
        playerScoreLabel.setFont(largeFont); // Thiết lập font chữ lớn hơn
        playerScoreLabel.setForeground(Color.WHITE); // Đổi màu chữ thành trắng
        playerScoreLabel.setBounds(50, 20, 300, 50); // Điều chỉnh kích thước và vị trí
        add(playerScoreLabel);

        // Thông tin đối thủ
        opponentScoreLabel = new JLabel("Opponent: 0");
        opponentScoreLabel.setFont(largeFont); // Thiết lập font chữ lớn hơn
        opponentScoreLabel.setForeground(Color.WHITE); // Đổi màu chữ thành trắng
        opponentScoreLabel.setBounds(500, 20, 300, 50); // Điều chỉnh kích thước và vị trí
        add(opponentScoreLabel);

        // Thời gian
        timeLabel = new JLabel("Time: 60s");
        timeLabel.setFont(largeFont); // Thiết lập font chữ lớn hơn
        timeLabel.setForeground(Color.WHITE); // Đổi màu chữ thành trắng
        timeLabel.setBounds(350, 20, 200, 50); // Điều chỉnh kích thước và vị trí
        add(timeLabel);

        // Tạo 4 thùng rác với các loại và tên khác nhau
        String[] types = {"paper", "plastic", "metal", "organic"};
        String[] names = {"Giấy", "Nhựa", "Kim loại", "Hữu cơ"};
        for (int i = 0; i < 4; i++) {
            bins[i] = new TrashBin(i * 150 + 100, 500, types[i], names[i]);
            add(bins[i]);
        }

        // Khởi tạo rác
        trash = new Trash();
        add(trash);

        // Nút thoát
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(largeFont); // Thiết lập font chữ lớn hơn
        exitButton.setForeground(Color.WHITE); // Đổi màu chữ thành trắng
        exitButton.setBounds(20, 540, 150, 50); // Điều chỉnh kích thước và vị trí
        exitButton.addActionListener(e -> System.exit(0));
        add(exitButton);

        // Bắt đầu trò chơi
        startGame();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (background != null) {
            g.drawImage(background, 0, 0, getWidth(), getHeight(), this);
        }
    }
//  tat de sua trash

    public void startGame() {
        // Timer để điều khiển rơi của rác
        gameTimer = new Timer(50, new ActionListener() {
            private int timeElapsed = 0; // Biến để đếm thời gian
            @Override
            public void actionPerformed(ActionEvent e) {
                trash.moveDown(timeElapsed);
                if (trash.reachedBottom()) {
                    boolean correct = bins[currentBinIndex].isCorrectBin(trash);
                    updateScore(correct);
                    trash.resetPosition();
                }
            }
        });
        gameTimer.start();

        // Timer để giảm thời gian còn lại
        countdownTimer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                timeRemaining--;
                timeLabel.setText("Time: " + timeRemaining + "s");
                if (timeRemaining <= 0) {
                    countdownTimer.stop();
                    gameTimer.stop();
                    endGame();
                }
            }
        });
        countdownTimer.start();
    }

    public void endGame() {
        JOptionPane.showMessageDialog(this, "Game Over! Final Score: " + playerScore);
    }

    public void updateScore(boolean correct) {
        if (correct) {
            playerScore++;
        } else {
            playerScore--;
        }
        playerScoreLabel.setText("Player: " + playerScore);
    }

    // Hàm di chuyển thùng rác và thay đổi thứ tự
    public void moveBin(int direction) {
        // Di chuyển sang trái hoặc phải
        bins[currentBinIndex].reset(); // Trở lại màu mặc định

        if (direction == -1) { // Sang trái
            currentBinIndex = (currentBinIndex - 1 + bins.length) % bins.length;
        } else if (direction == 1) { // Sang phải
            currentBinIndex = (currentBinIndex + 1) % bins.length;
        }

        // Đặt thùng hiện tại về giữa
        for (int i = 0; i < bins.length; i++) {
            bins[i].setBounds((i - currentBinIndex + 2) * 150 + 100, 500, 100, 100);
        }
        bins[currentBinIndex].rotate(); // Đánh dấu thùng rác mới được chọn
    }

    public void createAndShowUI() {
        JFrame frame = new JFrame("Trash Sorting Game");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        frame.addKeyListener(new GameController(this));
        frame.setFocusable(true);
        frame.requestFocusInWindow();
    }
}

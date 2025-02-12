package fit.nlu.utils;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Util {

    private static final Set<String> existingUsernames = new HashSet<>();
    private static final Random random = new Random();

    public static String generateUniqueUsername() {
        String username;
        do {
            int number = 100 + random.nextInt(900); // Thêm số ngẫu nhiên 100-999 để tăng tính duy nhất
            username = "Player" + number;
        } while (existingUsernames.contains(username)); // Kiểm tra nếu đã tồn tại

        existingUsernames.add(username); // Lưu lại username đã sử dụng
        return username;
    }


    public static String maskWord(String word) {
        if (word == null || word.isEmpty()) {
            return "";
        }

        StringBuilder masked = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            if (word.charAt(i) == ' ') {
                masked.append("   "); // 3 khoảng trắng để giữ format
            } else {
                masked.append("_ ");
            }
        }

        // Xóa khoảng trắng cuối cùng nếu có
        return masked.toString().trim();
    }


}

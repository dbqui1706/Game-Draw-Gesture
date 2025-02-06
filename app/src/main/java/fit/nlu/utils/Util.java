package fit.nlu.utils;

public class Util {
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

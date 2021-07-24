/**
 *  633.
 *  给定一个非负整数 c ，你要判断是否存在两个整数 a 和 b，使得 a^2 + b^2 = c 。
 */
public class SquareSum {

    /**
     * 使用暴力解
     *
     * @param c
     * @return
     */
    public static boolean bruteSolution(int c) {
        for (long i = 0; i * i <= c; i++) {
            double b = Math.sqrt(c - i * i);
            if (b == (int)b) {
                return true;
            }
        }
        return false;
    }

    /**
     * 使用双指针
     *
     * @param c
     * @return
     */
    public static boolean doublePointer(int c) {
        int left = 0;
        int right = (int) Math.sqrt(c);
        while (left <= right) {
            int sum = left * left + right * right;
            if (sum == c) {
                return true;
            } else if (sum > c) {
                right--;
            } else {
                left++;
            }
        }

        return false;
    }

    /**
     * 费马平方和定理告诉我们：
     *
     * 一个非负整数 c 如果能够表示为两个整数的平方和，当且仅当 c 的所有形如 4k + 3的质因子的幂均为偶数。
     *
     * @param c
     * @return
     */
    public static boolean fermatLaw(int c) {
        for (int base = 2; base * base <= c; base++) {
            if (c % base != 0) {
                continue;
            }

            int exp = 0;
            while (c % base == 0) {
                c /= base;
                exp++;
            }

            // 根据 Sum of two squares theorem 验证
            if (base % 4 == 3 && exp % 2 != 0) {
                return false;
            }
        }

        return c % 4 != 3;
    }
}

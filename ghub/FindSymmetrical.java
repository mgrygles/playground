public class FindSymmetrical {

    public static int atIndex(String S) {
        
        S = S.replaceAll("[\\W]+", "");

        // Returns 0 when length of given String is 1
        if (S.length() == 1) {
            return 0;
        }

        char[] chars = S.toCharArray();

        int minMatch;
        boolean isProceed;
        boolean isDone = false;

        for (int i=0; i<S.length(); i++) {

            minMatch = 0;
            isProceed = false;
            for (int j=i, m=i; ((j>0) && ((m+1) < S.length())); j--, m++) {
                
                if (((j == i) && (m == i)) || isProceed){
                    if (chars[j-1] == chars[m+1]) {
                        minMatch++;
                        isProceed = true;
                    } else {
                        isProceed = false;
                    }
                }
                if (minMatch > 0) {
                    isDone = true;
                    return i;
                }
            }
            if (isDone) {
                break;
            }
        }
        return -1;
    }


    public static void main(String args[]) {
        
        String s = "abcdefghijklmnopqrstuvwxyzracecar";

        if (args.length > 0) {
            s = args[0];
        }

        System.out.println("FindSymmetrical atIndex is " + atIndex(s));

    }
}

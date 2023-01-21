public class Solution3 {
    public static void main(String[] args) {
        String[] arr = {
                "3:1",
                "2:2",
                "1:3"
        };

        Solution3 solution3 = new Solution3();
        System.out.println(solution3.solution(arr));
    }

    private int solution(String[] arr){

        int me = 0;
        int other = 0;
        int result = 0;

        for (String data : arr) {
            String[] score = data.split(":");
            me = Integer.parseInt(score[0]);
            other = Integer.parseInt(score[1]);

            result += getScore(me, other);
        }

        return result;
    }

    private int getScore(int a, int b){
        if(a > b){
            return 3;
        }else if(a == b) {
            return 1;
        }else return 0;
    }
}

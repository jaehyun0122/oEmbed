public class Solution1 {

    public static void main(String[] args) {
        Solution1 solution1 = new Solution1();
        for(int i=12; i>=1; i--){
            System.out.println(solution1.solution(i));
        }
    }

    private int solution(int month){
        if(month <= 3){
            return 1;
        }else if(month <= 6){
            return 2;
        }else if(month <= 9){
            return 3;
        }else return 4;
    }
}

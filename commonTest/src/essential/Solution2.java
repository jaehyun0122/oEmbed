import java.util.*;

public class Solution2 {
    public static void main(String[] args) {
        Solution2 solution2 = new Solution2();
        int[] arr = new int[]{0,1,2,4};

        System.out.println(solution2.solution(arr));
    }

    private int solution(int[] arr){
        Arrays.sort(arr);

        int max = arr[arr.length - 1];
        boolean[] check = new boolean[max+1];
        int answer = max+1;

        for (int num : arr) {
            if(!check[num]) {
                check[num] = true;
            }
        }

        for(int i=0; i<= max; i++){
            if(!check[i]){
                answer = i;
                break;
            }
        }

        return answer;
    }
}

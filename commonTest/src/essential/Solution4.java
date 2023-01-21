import java.util.regex.Pattern;

public class Solution4 {
    public static void main(String[] args) {

        Solution4 solution4 = new Solution4();
        System.out.println(solution4.solution("발휘하기 안고, 것은 새 그림자는 되려니와, 모래뿐일 말이다. 천하를 피는 피부가 뼈 뭇 불어 창공에 동력은 때문이다. 두기 광야에서 찾아다녀도, 주며, 하는 이것을 칼이다. 풀이 방황하였으며, 보이는 곳으로 싸인 우리 희망의 위하여서. 싶이 든 과실이 있는가? 스며들어 수 물방아 너의 행복스럽고 피가 얼마나 고행을 힘있다. 품에 소리다.이것은 얼마나 가는 아니더면, 이 없으면, 두기 듣는다. 이상의 두기 풍부하게 있는가? 피고, 불어 산야에 이상을 청춘 크고 아니다."));

    }

    static StringBuffer sb;
    static int len;

    private String solution(String str){
        len = 0;
        sb = new StringBuffer();
        String nextStr = "";

        for(int i=0; i<str.length(); i++){
            nextStr = Character.toString(str.charAt(i));
            // 한글 +2
            if(Pattern.matches("^[가-힣]*$", nextStr)){
                addLine(2, nextStr);
            }else { // 이외 +1
                addLine(1, nextStr);
            }
        }

        return sb.toString();
    }

    private void addLine(int bytes, String nextStr){
        len += bytes;
        if(!isRange()){
            len = 0;
            newLine(nextStr, bytes);
        } else sb.append(nextStr);
    }

    private boolean isRange(){
        if(len > 80) return false;
        return true;
    }

    private void newLine(String nextStr, int bytes){
        sb.append("\n");
        if(!(nextStr.equals(" "))){
            sb.append(nextStr);
            len += bytes;
        }
        return;
    }

}

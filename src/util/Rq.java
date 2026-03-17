package util;

import java.util.ArrayList;
import java.util.List;

public class Rq {
    private final String command;
    private final List<String> params = new ArrayList<>();

    public Rq(String cmd) throws IndexOutOfBoundsException {
        // 공통적으로, '와 "는 단어를 묶는 역할을 함.
        // 만약 ' 내에 "가 있다면 " 그대로 출력. 반대도 동일
        // " 단일로 입력하려고 한다면 \"로 입력. '는 \'
        // " 또는 '로 시작한다면 다음 " 또는 '를 만날때까지의 모든 단어를 그대로 저장
        cmd = cmd.strip();

        StringBuilder sb = new StringBuilder();

        char myChar = '\0';
        char c;
        for (int i = 0; i < cmd.length(); i++) {
            c = cmd.charAt(i);
            if (c == '\\') {
                sb.append(cmd.charAt(++i));
            } else if (myChar == '\'' || myChar == '"') {
                if (c == myChar) myChar = '\0';
                else sb.append(c);
            } else if (c == '\'' || c == '"') {
                myChar = c;
            } else if (c == ' ') {
                if (sb.length() != 0) {
                    params.add(sb.toString());
                    sb.setLength(0);
                }
            } else {
                sb.append(c);
            }
        }

        if (sb.length() != 0) {
            params.add(sb.toString());
            sb.setLength(0);
        }

        command = params.get(0);
        params.remove(0);
    }

    public String getCommand() {
        return this.command;
    }

    public List<String> getParams() {
        return this.params;
    }

    @Override
    public String toString() {
        return "command: " + command +
            "\nparams: " + params;
    }
}

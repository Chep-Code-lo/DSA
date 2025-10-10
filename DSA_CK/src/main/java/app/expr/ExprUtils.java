package app.expr;

import java.util.*;
import java.util.function.Function;

public class ExprUtils {
  private static final Set<String> OPS = Set.of("+", "-", "*", "/", "^");

  // Step models for visualization
  public static class ConversionStep {
    public final String token; // current token processed
    public final String action; // description
    public final List<String> stackState; // top-first
    public final List<String> outputState; // left-to-right

    public ConversionStep(String token, String action, List<String> stackState, List<String> outputState) {
      this.token = token;
      this.action = action;
      this.stackState = List.copyOf(stackState);
      this.outputState = List.copyOf(outputState);
    }
  }

  public static class ConversionResult {
    public final List<ConversionStep> steps;
    public final List<String> result;

    public ConversionResult(List<ConversionStep> steps, List<String> result) {
      this.steps = List.copyOf(steps);
      this.result = List.copyOf(result);
    }
  }

  public static boolean isOperator(String s) {
    return OPS.contains(s);
  }

  public static int precedence(String op) {
    return switch (op) {
      case "+", "-" -> 1;
      case "*", "/" -> 2;
      case "^" -> 3;
      default -> -1;
    };
  }

  public static boolean rightAssoc(String op) {
    return "^".equals(op);
  }

  // Tokenizer with unary handling
  public static List<String> tokenize(String s) {
    ArrayList<String> out = new ArrayList<>();
    int n = s.length();
    for (int i = 0; i < n; ) {
      char c = s.charAt(i);
      if (Character.isWhitespace(c)) {
        i++;
        continue;
      }
      if (c == '-'
          && (out.isEmpty()
              || isOperator(out.get(out.size() - 1))
              || "(".equals(out.get(out.size() - 1)))) {
        int j = i + 1;
        boolean any = false;
        while (j < n && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) {
          any = true;
          j++;
        }
        if (any) {
          out.add(s.substring(i, j));
          i = j;
          continue;
        }
      }
      if (Character.isDigit(c)) {
        int j = i;
        while (j < n && (Character.isDigit(s.charAt(j)) || s.charAt(j) == '.')) j++;
        out.add(s.substring(i, j));
        i = j;
        continue;
      }
      if (Character.isLetter(c)) {
        int j = i;
        while (j < n && (Character.isLetterOrDigit(s.charAt(j)) || s.charAt(j) == '_')) j++;
        out.add(s.substring(i, j));
        i = j;
        continue;
      }
      if ("()+-*/^".indexOf(c) >= 0) {
        out.add(String.valueOf(c));
        i++;
        continue;
      }
      throw new IllegalArgumentException("Ký tự không hợp lệ: '" + c + "'");
    }
    return out;
  }

  public static void validateTokens(List<String> tks) {
    if (tks.isEmpty()) throw new IllegalArgumentException("Biểu thức rỗng");
    int bal = 0;
    String prev = null;
    for (String tk : tks) {
      if ("(".equals(tk)) bal++;
      else if (")".equals(tk)) {
        bal--;
        if (bal < 0) throw new IllegalArgumentException("Ngoặc đóng dư");
      }
      if (isOperator(tk) && (prev == null || isOperator(prev) || "(".equals(prev)))
        throw new IllegalArgumentException("Toán tử đứng sai vị trí: '" + tk + "'");
      if (")".equals(tk) && prev != null && "(".equals(prev))
        throw new IllegalArgumentException("Ngoặc rỗng '()' không hợp lệ");
      prev = tk;
    }
    if (isOperator(tks.get(tks.size() - 1)))
      throw new IllegalArgumentException("Kết thúc bằng toán tử");
    if (bal != 0) throw new IllegalArgumentException("Ngoặc không cân bằng");
  }

  public static List<String> toPostfix(List<String> tokens) {
    validateTokens(tokens);
    ArrayList<String> out = new ArrayList<>();
    MyStack<String> ops = new MyStack<>();
  for (String tk : tokens) {
      if (isNumber(tk) || isIdentifier(tk)) {
        out.add(tk);
      } else if ("(".equals(tk)) {
        ops.push(tk);
      } else if (")".equals(tk)) {
        while (!ops.isEmpty() && !"(".equals(ops.peek())) out.add(ops.pop());
        if (!ops.isEmpty() && "(".equals(ops.peek())) ops.pop();
      } else if (isOperator(tk)) {
        while (!ops.isEmpty()) {
          String top = ops.peek();
          if (isOperator(top)
              && (precedence(top) > precedence(tk)
                  || (precedence(top) == precedence(tk) && !rightAssoc(tk)))) out.add(ops.pop());
          else break;
        }
        ops.push(tk);
      } else throw new IllegalArgumentException("Token không hợp lệ: " + tk);
    }
    while (!ops.isEmpty()) out.add(ops.pop());
    return out;
  }

  // Return detailed steps for visualization while producing postfix result
  public static ConversionResult toPostfixWithSteps(List<String> tokens) {
    validateTokens(tokens);
    ArrayList<String> out = new ArrayList<>();
    MyStack<String> ops = new MyStack<>();
    ArrayList<ConversionStep> steps = new ArrayList<>();

    for (String tk : tokens) {
      if (isNumber(tk) || isIdentifier(tk)) {
        out.add(tk);
  steps.add(new ConversionStep(tk, "Emit operand to output", ops.snapshotTopFirst(), List.copyOf(out)));
      } else if ("(".equals(tk)) {
        ops.push(tk);
  steps.add(new ConversionStep(tk, "Push '(' to stack", ops.snapshotTopFirst(), List.copyOf(out)));
      } else if (")".equals(tk)) {
        // pop until '('
        while (!ops.isEmpty() && !"(".equals(ops.peek())) {
          String p = ops.pop();
          out.add(p);
          steps.add(new ConversionStep(p, "Pop operator to output (')' handling)", ops.snapshotTopFirst(), List.copyOf(out)));
        }
        if (!ops.isEmpty() && "(".equals(ops.peek())) {
          ops.pop();
          steps.add(new ConversionStep("", "Pop '(' from stack", ops.snapshotTopFirst(), List.copyOf(out)));
        }
      } else if (isOperator(tk)) {
        while (!ops.isEmpty()) {
          String top = ops.peek();
          if (isOperator(top)
              && (precedence(top) > precedence(tk)
                  || (precedence(top) == precedence(tk) && !rightAssoc(tk)))) {
            String p = ops.pop();
            out.add(p);
            steps.add(new ConversionStep(p, "Pop operator to output (precedence)", ops.snapshotTopFirst(), List.copyOf(out)));
          } else break;
        }
        ops.push(tk);
  steps.add(new ConversionStep(tk, "Push operator to stack", ops.snapshotTopFirst(), List.copyOf(out)));
      } else throw new IllegalArgumentException("Token không hợp lệ: " + tk);
    }

    while (!ops.isEmpty()) {
      String p = ops.pop();
      out.add(p);
      steps.add(new ConversionStep(p, "Pop remaining operator to output", ops.snapshotTopFirst(), List.copyOf(out)));
    }

    return new ConversionResult(steps, out);
  }

  // removed direct internal access; use MyStack.snapshotTopFirst()

  public static List<String> toPrefix(List<String> tokens) {
    validateTokens(tokens);
    ArrayList<String> rev = new ArrayList<>(tokens);
    Collections.reverse(rev);
    for (int i = 0; i < rev.size(); i++) {
      if ("(".equals(rev.get(i))) rev.set(i, ")");
      else if (")".equals(rev.get(i))) rev.set(i, "(");
    }
    List<String> post = toPostfix(rev);
    Collections.reverse(post);
    return post;
  }

  public static double evalPostfix(List<String> postfix, Function<String, Double> varResolver) {
    MyStack<Double> st = new MyStack<>();
    for (String tk : postfix) {
      if (isNumber(tk)) st.push(Double.parseDouble(tk));
      else if (isIdentifier(tk)) st.push(varResolver.apply(tk));
      else if (isOperator(tk)) {
        double b = st.pop();
        double a = st.pop();
        switch (tk) {
          case "+" -> st.push(a + b);
          case "-" -> st.push(a - b);
          case "*" -> st.push(a * b);
          case "/" -> {
            if (b == 0.0) throw new ArithmeticException("Chia cho 0");
            st.push(a / b);
          }
          case "^" -> st.push(Math.pow(a, b));
        }
      } else throw new IllegalArgumentException("Token không hợp lệ khi tính: " + tk);
    }
    return st.pop();
  }

  public static boolean isNumber(String s) {
    if (s == null || s.isEmpty()) return false;
    int dots = 0, i = 0;
    if (s.charAt(0) == '-' && s.length() > 1) i = 1;
    for (; i < s.length(); i++) {
      char c = s.charAt(i);
      if (c == '.') {
        dots++;
        if (dots > 1) return false;
      } else if (!Character.isDigit(c)) return false;
    }
    return true;
  }

  public static boolean isIdentifier(String s) {
    if (s == null || s.isEmpty()) return false;
    if (!Character.isLetter(s.charAt(0))) return false;
    for (int i = 1; i < s.length(); i++) {
      char c = s.charAt(i);
      if (!(Character.isLetterOrDigit(c) || c == '_')) return false;
    }
    return true;
  }
}

package reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import reachability.OverwriteRuleLambda.OPERATION;

public abstract class AConcreteRewriteRule implements IConcreteRewriteRule {
  protected final TriFunction<Integer, Integer, Integer, Boolean> applicable;
  protected final Function<Integer, Integer> globalFunc;
  protected final Function<Integer, Integer> localFunc;
  protected final Optional<Integer> localTo;

  AConcreteRewriteRule(TriFunction<Integer, Integer, Integer, Boolean> applicable,
      Function<Integer, Integer> globalFunc, Function<Integer, Integer> localFunc,
      Optional<Integer> localTo) {
    this.applicable = applicable;
    this.globalFunc = globalFunc;
    this.localFunc = localFunc;
    this.localTo = localTo;
  }

  public static class RuleBuilder {
    public static IConcreteRewriteRule build(String ruleAsString) {

      Matcher m1 = Pattern.compile(
          "\\(?\\s?(!?)(\\d+|~),\\s?(\\d+)\\s(!?)(\\d+|~)\\s?\\)?\\s?->\\s?\\(?\\s?g\\s?(\\+|-|\\*|/|=)\\s?(\\d+),(\\d+)\\s?l\\s?(\\+|-|\\*|/|=)\\s?(\\d+)\\s?\\)?")
          .matcher(ruleAsString);

      if (m1.find()) {
        // System.out.println(Pattern.matches("\\(?\\s?(\\d+|~),?\\s(\\d+|~)\\s?\\)?\\s?->\\s?",
        // "(1, 4) ->");
        boolean globalNot = false;
        boolean localNot = true;
        Optional<Integer> globalFrom = Optional.empty();
        Optional<Integer> topFrom = Optional.empty();
        OPERATION globalOp = null;
        OPERATION localOp = null;
        int globalMod;
        int localMod;
        int localFrom;
        int localTo;

        globalNot = !m1.group(1).equals("");
        if (!m1.group(2).equals("~")) {
          globalFrom = Optional.of(Integer.parseInt(m1.group(2)));
        }
        localFrom = Integer.parseInt(m1.group(3));
        localNot = !m1.group(4).equals("");
        if (!m1.group(5).equals("~")) {
          topFrom = Optional.of(Integer.parseInt(m1.group(4)));
        }
        globalOp = parseOp(m1.group(6));
        globalMod = Integer.parseInt(m1.group(7));
        localOp = parseOp(m1.group(9));
        localTo = Integer.parseInt(m1.group(8));
        localMod = Integer.parseInt(m1.group(10));
        return ConcreteOverwriteRule.makeConcreteOverwriteRule(globalNot, globalOp, localNot,
            localOp, globalFrom, topFrom, localFrom, globalMod, localMod, localTo);

      }

      Matcher m2 = Pattern.compile(
          "\\(?\\s?(!?)(\\d+|~),(\\d+)\\s(!?)(\\d+|~)\\s?\\)?\\s?->\\s?\\(?\\s?g\\s?(\\+|-|\\*|/|=)\\s?(\\d+),\\s?e\\s?\\)?")
          .matcher(ruleAsString);

      if (m1.find()) {
        // System.out.println(Pattern.matches("\\(?\\s?(\\d+|~),?\\s(\\d+|~)\\s?\\)?\\s?->\\s?",
        // "(1, 4) ->");
        boolean globalNot = false;
        boolean localNot = true;
        Optional<Integer> globalFrom = Optional.empty();
        Optional<Integer> topFrom = Optional.empty();
        OPERATION globalOp = null;
        int globalMod;
        int localFrom;

        globalNot = !m1.group(1).equals("");
        if (!m1.group(2).equals("~")) {
          globalFrom = Optional.of(Integer.parseInt(m1.group(2)));
        }
        localFrom = Integer.parseInt(m1.group(3));
        localNot = !m1.group(4).equals("");
        if (!m1.group(5).equals("~")) {
          topFrom = Optional.of(Integer.parseInt(m1.group(4)));
        }
        globalOp = parseOp(m1.group(6));
        globalMod = Integer.parseInt(m1.group(7));
        return ConcretePopRule.makeConcretePopRule(globalNot, globalOp, localNot, globalFrom,
            topFrom, localFrom, globalMod);

      }
      
      Matcher m3 = Pattern.compile(
          "\\(?\\s?(!?)(\\d+|~),(\\d+)\\s(!?)(\\d+|~)\\s?\\)?\\s?->\\s?\\(?\\s?g\\s?(\\+|-|\\*|/|=)\\s?(\\d+),\\s?(\\d+)\\s?g\\s?(\\+|-|\\*|/|=)\\s?(\\d+),\\s?\\s?(\\d+)\\s?l\\s?(\\+|-|\\*|/|=)\\s?(\\d+)\\s?\\)?")
          .matcher(ruleAsString);

      if (m1.find()) {
        // System.out.println(Pattern.matches("\\(?\\s?(\\d+|~),?\\s(\\d+|~)\\s?\\)?\\s?->\\s?",
        // "(1, 4) ->");
        boolean globalNot = false;
        boolean localNot = true;
        Optional<Integer> globalFrom = Optional.empty();
        Optional<Integer> topFrom = Optional.empty();
        OPERATION globalOp = null;
        OPERATION localOp = null;
        int globalMod;
        int localMod;
        int localFrom;
        int localTo;
        int newTopLocal;
        int topMod;
        OPERATION topOp;

        globalNot = !m1.group(1).equals("");
        if (!m1.group(2).equals("~")) {
          globalFrom = Optional.of(Integer.parseInt(m1.group(2)));
        }
        localFrom = Integer.parseInt(m1.group(3));
        localNot = !m1.group(4).equals("");
        if (!m1.group(5).equals("~")) {
          topFrom = Optional.of(Integer.parseInt(m1.group(4)));
        }
        globalOp = parseOp(m1.group(6));
        globalMod = Integer.parseInt(m1.group(7));
        newTopLocal = Integer.parseInt(m1.group(8));
        topOp = parseOp(m1.group(9));
        topMod = Integer.parseInt(m1.group(10));
        localTo = Integer.parseInt(m1.group(11));
        localOp = parseOp(m1.group(12));
        localMod = Integer.parseInt(m1.group(13));
        return ConcretePushRule.makeConcretePushRule(globalNot, globalOp, localNot, localOp,
            globalFrom, topFrom, localFrom, globalMod, localMod, localTo, newTopLocal, topOp,
            topMod);

      }
      
      throw new IllegalArgumentException(ruleAsString + " Didn't match anything. Check syntax.");
    }
  }

  @Override
  public boolean canRewrite(int global, Stack<Pair<Integer, Integer>> local) {
    return !local.isEmpty()
        && applicable.apply(global, local.peek().getFirst(), local.peek().getSecond());
  }


  public static List<Stack<Pair<Integer, Integer>>> cloneList(
      List<Stack<Pair<Integer, Integer>>> list) {
    List<Stack<Pair<Integer, Integer>>> ans = new ArrayList<>();
    for (Stack<Pair<Integer, Integer>> stack : list) {
      ans.add(cloneStack(stack));
    }
    return ans;
  }

  private static Stack<Pair<Integer, Integer>> cloneStack(Stack<Pair<Integer, Integer>> stack) {
    Stack<Pair<Integer, Integer>> newStack = new Stack<>();
    for (int i = 0; i < stack.size(); i += 1) {
      newStack.push(new Pair<Integer, Integer>(stack.get(i).getFirst(), stack.get(i).getSecond()));
    }
    return newStack;
  }

  public static OPERATION parseOp(String op) {
    switch (op) {
    case "+":
      return OPERATION.PLUS;

    case "-":
      return OPERATION.MINUS;

    case "*":
      return OPERATION.MULT;

    case "/":
      return OPERATION.DIVIDE;

    case "=":
      return OPERATION.OVERWRITE;
    default:
      throw new IllegalArgumentException("Not an operation:" + op);
    }
  }

  protected static Function<Integer, Integer> funcMachine(OPERATION op, int modifier) {
    Function<Integer, Integer> func = null;
    switch (op) {
    case PLUS:
      func = (x) -> x + modifier;
      break;
    case MINUS:
      func = (x) -> x - modifier;
      break;
    case MULT:
      func = (x) -> x * modifier;
      break;
    case DIVIDE:
      func = (x) -> x / modifier;
      break;
    case OVERWRITE:
      func = (x) -> modifier;
      break;
    }
    return func;
  }

}

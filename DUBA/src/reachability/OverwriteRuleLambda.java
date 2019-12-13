package reachability;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

import State.IState;
import State.StateWrapper;

/**
 * For testing, a rewrite rule to be used for overwrite rules that go from and
 * to infinite domains. DO NOT USE WITH EMPTY LOCAL STACK. This will just ignore
 * your functions if the stack is empty. Do not use the overapproximation
 * funcitons with this, it doesnt make sense.
 * 
 * @author Andrew
 *
 */
public class OverwriteRuleLambda implements IRewriteRule {
  enum OPERATION {
    PLUS, MINUS, MULT, DIVIDE, OVERWRITE
  }

  BiFunction<Integer, Integer, Boolean> applicable;
  Function<Integer, Integer> globalFunc;
  Function<Integer, Integer> localFunc;

  private OverwriteRuleLambda(BiFunction<Integer, Integer, Boolean> applicable,
      Function<Integer, Integer> globalFunc, Function<Integer, Integer> localFunc) {
    this.applicable = applicable;
    this.globalFunc = globalFunc;
    this.localFunc = localFunc;
  }

  /**
   * parameters for a lambda rule.
   * @param globalOpn which operation to use for the global updater.
   * @param localOp which operation to use for the local updater
   * @param args these should be: [GLOBALFROM, TOPFROM, GLOBALMODIFIER, TOPMODIFIER]
   * @return a new rule
   */
  static IRewriteRule makeOverwriteLambda(boolean globalNot, OPERATION globalOp, boolean localNot,
      OPERATION localOp,
      Optional<Integer> globalFrom, Optional<Integer> topFrom, int globalModifier, int localModifier) {
//    if (args.length == 4) {
//      return new OverwriteRuleLambda(args[0], args[1], args[2], args[3]);
//    }

    Function<Integer, Boolean> globalChecker;
    if (globalFrom.isPresent()) {
      if (globalNot) {
        globalChecker = (x) -> x != globalFrom.get();
      }
      else {
        globalChecker = (x) -> x == globalFrom.get();
      }
    }
    else {
      globalChecker = (x) -> true;
    }

    Function<Integer, Boolean> localChecker;
    if (topFrom.isPresent()) {
      if (localNot) {
        localChecker = (x) -> x != topFrom.get();
      }
      else {
        localChecker = (x) -> x == topFrom.get();
      }
    }
    else {
      localChecker = (x) -> true;
    }
    BiFunction<Integer, Integer, Boolean> applicable = (g, l) -> globalChecker.apply(g)
        && localChecker.apply(l);
    
    Function<Integer, Integer> globalFunc = funcMachine(globalOp, globalModifier);
      
    Function<Integer, Integer> localFunc = funcMachine(localOp, localModifier);
//    else {
//      throw new IllegalArgumentException(
//          "Input must be all integers, and the array must be of length 4");
//    }
    return new OverwriteRuleLambda(applicable, globalFunc, localFunc);
  }



  private static Function<Integer, Integer> funcMachine(OPERATION op, int modifier) {
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

  @Override
  public boolean canRewrite(int global, Stack<Integer> local) {
    return !local.isEmpty() && applicable.apply(global, local.peek());
  }

  @Override
  public IState rewrite(int global, List<Stack<Integer>> stacks, int machineNum, int delays) {
    List<Stack<Integer>> nextList = StateWrapper.cloneList(stacks);
    Stack<Integer> toRewrite = nextList.get(machineNum);
    int prevLocal = toRewrite.pop();
    toRewrite.push(this.localFunc.apply(prevLocal));

    return new StateWrapper(this.globalFunc.apply(global), nextList, delays);
  }

  /**
   * Not implemented.
   */
  @Override
  public List<IRewriteRule> overapproxRewrite(int bound, Set<Integer> emergingSymbols) {
    // This should never be used. The domain of these rules is intended to be
    // infinite
    return null;
  }

  /**
   * Not implemented.
   */
  @Override
  public void addEmergingSymbols(Set<Integer> acc) {
    // TODO Auto-generated method stub

  }

  /**
   * Not implemented.
   */
  @Override
  public void addGlobalToIfPop(Set<Integer> acc) {
    // TODO Auto-generated method stub
    
  }

}

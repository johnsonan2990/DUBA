package reachability;

import java.util.List;
import java.util.Optional;
import java.util.Stack;
import java.util.function.BiFunction;
import java.util.function.Function;

import State.ConcreteState;
import State.IConcreteState;
import reachability.OverwriteRuleLambda.OPERATION;

public class ConcretePushRule extends AConcreteRewriteRule {

  BiFunction<Integer, Pair<Integer, Integer>, Pair<Integer, Integer>> newTopGen;

  ConcretePushRule(TriFunction<Integer, Integer, Integer, Boolean> applicable,
      Function<Integer, Integer> globalFunc, Function<Integer, Integer> localFunc,
      Optional<Integer> localTo,
      BiFunction<Integer, Pair<Integer, Integer>, Pair<Integer, Integer>> newTopGen) {
    super(applicable, globalFunc, localFunc, localTo);
    this.newTopGen = newTopGen;
    // TODO Auto-generated constructor stub
  }

  /**
   * parameters for a lambda rule.
   * 
   * @param globalOpn which operation to use for the global updater.
   * @param localOp   which operation to use for the local updater
   * @param args      these should be: [GLOBALFROM, TOPFROM, GLOBALMODIFIER,
   *                  TOPMODIFIER]
   * @return a new rule
   */
  static IConcreteRewriteRule makeConcretePushRule(boolean globalNot, OPERATION globalOp,
      boolean localNot, OPERATION localOp, Optional<Integer> globalFrom, Optional<Integer> topFrom,
      int localFrom, int globalModifier, int localModifier, int newlocal, int newTopLocal,
      OPERATION topOp, int topModifier) {
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
    TriFunction<Integer, Integer, Integer, Boolean> applicable = (g, l, v) -> globalChecker.apply(g)
        && l == localFrom && localChecker.apply(l);

    Function<Integer, Integer> globalFunc = funcMachine(globalOp, globalModifier);

    Function<Integer, Integer> localFunc = funcMachine(localOp, localModifier);
    Function<Integer, Integer> topFunc = funcMachine(topOp, topModifier);

    BiFunction<Integer, Pair<Integer, Integer>, Pair<Integer, Integer>> newTopGen = (g,
        p) -> new Pair<>(newTopLocal, topFunc.apply(g));

    Optional<Integer> localTo = Optional.of(newlocal);
//    else {
//      throw new IllegalArgumentException(
//          "Input must be all integers, and the array must be of length 4");
//    }
    return new ConcretePushRule(applicable, globalFunc, localFunc, localTo, newTopGen);
  }

  @Override
  public IConcreteState rewrite(int global, List<Stack<Pair<Integer, Integer>>> stacks,
      int machineNum, int delays) {
    List<Stack<Pair<Integer, Integer>>> nextList = cloneList(stacks);
    Stack<Pair<Integer, Integer>> toRewrite = nextList.get(machineNum);
    Pair<Integer, Integer> prevLocal = toRewrite.pop();
    toRewrite.push(new Pair<>(this.localTo.get(), this.localFunc.apply(prevLocal.getSecond())));
    toRewrite.push(newTopGen.apply(global, prevLocal));
    return new ConcreteState(this.globalFunc.apply(global), nextList);
  }

}

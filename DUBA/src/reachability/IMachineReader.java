package reachability;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The format for a machine file should be:
 * Initial Global: #
 * 
 * [MachineName] initial local: #
 * [List of rules in the format:]
 * ( #, # ) -> ( #, # ) //Overwrite
 * ( #, # ) -> (#, # # ) //Push, with the stack's bottom on the right.
 * ( #, # ) -> ( #, e ) //Pop
 * ( #, e ) -> ( #, e ) //Overwrite
 * ( #, e ) -> ( #, # ) //Push
 * 
 *  ...
 */
public interface IMachineReader {

  /**
   * Reads a given readable and returns the list of machines contained therein.
   * @param r the readable
   * @return a list with all the machines inside the readable and their rules, and t he initial global state.
   * @throws IllegalArgumentException if the reader stops working or is improperly formatted.
   */
  static Pair<Integer, List<IMachine>> read(Readable r) {
    List<IMachine> ans = new ArrayList<>();
    int initGlobal = 0;
    Pattern machAndInit = Pattern.compile("[a-zA-z0-9]+:\\s?(\\d+)");

    Scanner s = new Scanner(r);
    while (s.hasNextLine()) {
      String line = s.nextLine();
      Matcher m = machAndInit.matcher(line);
      if (m.find()) {
        initGlobal = Integer.parseInt(m.group(1));
        break;
      }
    }

    List<IRewriteRule> rules = null;
    int initLocal = -1;
    while (s.hasNextLine()) {
      String line = s.nextLine();

      Matcher m = machAndInit.matcher(line);
      if (m.find()) {
        if (rules != null) {
          ans.add(new Machine(rules, initLocal));
        }
        rules = new ArrayList<>();
        initLocal = Integer.parseInt(m.group(1));
      }
      else {
        if (line.contains("->")) {
          rules.add(ARewriteRule.RuleBuilder.build(line));
        }
      }
    }
    if (rules != null) {
      ans.add(new Machine(rules, initLocal));
    }
    s.close();
    return new Pair<>(initGlobal, ans);
  }
}

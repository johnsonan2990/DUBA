package reachability;

import java.io.FileNotFoundException;
import java.util.List;

/*
 * The format for a machine file should be:
 * Initial Global: #
 * 
 * Machine a- Initial Local: #
 * [List of rules in the format:]
 * (#, #) -> (#, #) //Overwrite
 * (#, #) -> (#, # #) //Push, with the stack's bottom on the left.
 * (#, #) -> (#,) //Pop
 * 
 *  ...
 */
public interface IFileToMachineReader {

  /**
   * Reads a file and parses it into a list of machines with rules.
   * 
   * @param filepath The location of the file.
   * @return A list of machines with the given rules.
   * @throws FileNotFoundException if the given filepath can't be found.
   */
  List<IMachine> readFile(String filepath) throws FileNotFoundException;

}

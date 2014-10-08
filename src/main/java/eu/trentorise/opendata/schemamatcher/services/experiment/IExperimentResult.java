package eu.trentorise.opendata.schemamatcher.services.experiment;

public interface IExperimentResult {

	
	/** Returns the time spend on the matching process
	 * @return time in milliseconds
	 */
	long getTimeSpent();
	
	/**Returns the memory used for the experiment
	 * @return memory in bytes
	 */
	long getMemoryUsed();
	
	/** Return the precision of the algorithm in the experiment
	 * @return
	 */
	double getPrecision();
	
	/** Returns the recall of the algorithm in the experiment.
	 * @return
	 */
	double getRecall();
	
	/** Returns the mean average precision of an algorithm in the experiment.
	 * @return
	 */
	double getMeanAveragePrecision();
	

	
	
	
}

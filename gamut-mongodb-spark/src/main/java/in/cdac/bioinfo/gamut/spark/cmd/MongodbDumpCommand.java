/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package in.cdac.bioinfo.gamut.spark.cmd;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 *
 * @author ramki
 */
@Data
@NoArgsConstructor
@Parameters(separators = "=", commandDescription = "Record inserted/updated into the repository")
public class MongodbDumpCommand {

	@ParametersDelegate
	private MongoDBInfo mongoDBInfo = new MongoDBInfo();

	@Parameter(names = { "--inputpath", "-i" }, required = true, description = "Input directory Path")
	private String path;

	@Parameter(names = { "--processors", "-proc" }, description = "Number of Processors")
	private int processors = 1;

}
